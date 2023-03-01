package com.vi5hnu.healthcare

import android.content.ContentValues
import android.content.Context
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
        db?.execSQL(tableDoctorsQuery)
        db?.execSQL(tableUserQuery)
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

    fun login(username: String,password: String):Boolean{
        if(!userExist(username, password)){
            return false
        }
        //TODO : proceed for login
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
        cursor.moveToFirst()
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
}