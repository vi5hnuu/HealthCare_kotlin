package com.vi5hnu.healthcare

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper

/*
*  SQLiteOpenHelper(@Nullable Context context, @Nullable String name,
            @Nullable CursorFactory factory, int version) {
        this(context, name, factory, version, null);
* */
class Database(context: Context, dbName: String, factory: CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, dbName, factory, version) {
    companion object{
        lateinit var username:String
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val tableUserQuery="create table  IF NOT EXISTS users(username text primary key,email text not null,password text not null)";
        val tableDoctorsQuery="create table  IF NOT EXISTS doctors(" +
                "_id integer primary key, AUTO_INCREMENT," +
                "name text not null," +
                "hospital_address text," +
                "exp integer," +
                "mobile text not null," +
                "fee integer," +
                "description text," +
                "type text check(type in ('FD','DE','SU','CA','DI')) not null);"
        val statesTable="create table  IF NOT EXISTS states(" +
                "_id integer primary key," +
                "name text not null);"
        val citiesTable="create table  IF NOT EXISTS cities(" +
                "_id integer," +
                "name text not null," +
                "FOREIGN KEY (_id) REFERENCES states(_id));"
        val lab_testsTable="create table  IF NOT EXISTS lab_tests (" +
                "_id integer primary key ,AUTO_INCREMENT," +
                "name text not null," +
                "price double check(price >= 0));"
        val orderTable="create table  IF NOT EXISTS orders(" +
                "_id integer , AUTO_INCREMENT," +
                "name text,"+
                "amount number,"+
                "username text primary key," +
                "order_id integer check(order_id>=0)," +
                "type text check(type in (\"T\",\"M\"))," +
                "FOREIGN KEY(username) REFERENCES users(username));"
        //in orderTable orderID is uid of labTest
        db?.execSQL(tableDoctorsQuery)
        db?.execSQL(tableUserQuery)
        db?.execSQL(statesTable)
        db?.execSQL(citiesTable)
        db?.execSQL(lab_testsTable)
        db?.execSQL(orderTable)
    }
    enum class DOCTORSCOLUMN(val identifier:String){
        NAME("name"),
        HOSPITAL_ADDRESS("hospital_address"),
        EXP("exp"),
        MOBILE("mobile"),
        FEE("fee"),
        DESCRIPTION("description"),
        TYPE("type"),
    }
    enum class DOCTOR_TYPE(val identifier:String){
        FAMILY_DOCTOR("FD"),
        DIETICIAN("DI"),
        SURGEON("SU"),
        CARDIOLOGIST("CA"),
        DENTIST("DE"),
    }
    enum class ORDER_TYPE(val identifier:String){
        LAB_TEST("T"),
        MEDICINE("M"),
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
    fun registerUser(username:String,email:String,password:String):Boolean{
        if(userExist(username)){
            return false
        }
        val cv=ContentValues()
        cv.put("username",username)
        cv.put("email",email)
        cv.put("password",password)
        val db:SQLiteDatabase=writableDatabase
        db.insert("users",null,cv)
        db.close()
        return true
    }

    fun login(usrnme: String,password: String):Boolean{
        if(!userExist(username, password)){
            return false
        }
        username=usrnme
        return true
    }
    fun getDoctorsCursor(type:DOCTOR_TYPE,vararg cols: DOCTORSCOLUMN):Cursor{
        val db:SQLiteDatabase=readableDatabase
        val columns = Array<String>(cols.size+1){"-"}
        columns[0]="_id"
        var i=0
        while(i<cols.size){
            columns[i+1]=cols[i].identifier
            i++
        }

        val cursor: Cursor = db.query("doctors",columns,"type=?", arrayOf(type.identifier),null,null,null)
        return cursor
    }
    fun getLabTestCursor():Cursor{
        val db:SQLiteDatabase=readableDatabase
        val columns= arrayOf("_id","name","price")
        val cursor=db.query("lab_tests",columns,null,null,null,null,null)
        return cursor
    }
    fun getDocDetails(id: Int): Cursor {
        val db: SQLiteDatabase = readableDatabase
        return db.rawQuery("select * from doctors where _id=?", arrayOf(id.toString()))
    }
    private fun userExist(username: String):Boolean{
        val db:SQLiteDatabase=readableDatabase
        val credentials = arrayOf(username)

        val cursor=db.rawQuery("select username from users where username=?",credentials)
        val result:Boolean=cursor.moveToFirst();
        cursor.close()
        return  result
    }
    private fun userExist(username: String,password: String):Boolean{
        val db:SQLiteDatabase=readableDatabase
        val credentials = arrayOf(username,password)
        val cursor=db.rawQuery("select username from users where username=? and password=?",credentials)
        val result:Boolean=cursor.moveToFirst();
        cursor.close()
        return  result
    }
    fun addToOrders(order_id:String,name:String,amount:String,type:ORDER_TYPE){//order_id is id of test/medicine
        if(orderAlreadyExist(username,order_id,type)){
            return
        }
        val cv=ContentValues()
        cv.put("name",name)
        cv.put("amount",amount)
        cv.put("username",username)
        cv.put("order_id",order_id)
        cv.put("type",type.identifier)
        val db:SQLiteDatabase=writableDatabase
        db.insert("orders",null,cv)
        db.close()
    }
    fun getOrders():Cursor{
        val db:SQLiteDatabase=readableDatabase
        val cursor=db.query("orders", arrayOf("_id","name","amount","order_id","type"),null,null,null,null,null)
        return cursor
    }
    fun getOrderTotal():String{
        val db:SQLiteDatabase=readableDatabase
        val cursor=db.rawQuery("select sum(amount) from orders;", null)
        var total="0"
        if(cursor.moveToFirst()){
            total= cursor.getString(0)
        }
        cursor.close()
        return total;
    }
    fun removeFromOrders(order_id:String,type:ORDER_TYPE){
        if(!orderAlreadyExist(username,order_id,type)){
            return
        }
        val db:SQLiteDatabase=writableDatabase
        db.delete("orders","username=? and order_id=? and type=?", arrayOf(username,order_id,type.identifier))
        db.close()
    }
    private fun orderAlreadyExist(username: String,order_id: String,type:ORDER_TYPE):Boolean{
        val db:SQLiteDatabase=readableDatabase
        val cursor=db.query("orders",null,"username=? and order_id=? and type=?", arrayOf(username,order_id,type.identifier),null,null,null)
        if(cursor.moveToFirst()){
            cursor.close()
            return true;
        }
        return false;
    }
}