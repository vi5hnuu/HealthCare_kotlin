package com.vi5hnu.healthcare

import android.content.Context
import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class MyRAdapter(private val Context:Context,private val cursor:Cursor) : RecyclerView.Adapter<MyRAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        Log.d("Cursor","Created")//only 11 created
        val view=LayoutInflater.from(parent.context).inflate(R.layout.lab_test_tile,parent,false)
        return MyViewHolder(view)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if(!cursor.moveToPosition(position)){
            return
        }
        val price=cursor.getString(2)
        val testName=cursor.getString(1)
        val uid=cursor.getString(0)
        holder.vprice.text=price
        holder.vTestName.text=testName
        holder.vUid.text=uid

        holder.checkBox.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val db=Database(this@MyRAdapter.Context,Context.getString(R.string.dbName),null,1)
                val chkBox=v as CheckBox
                if(chkBox.isChecked){
                    Toast.makeText(this@MyRAdapter.Context,"Adding ${holder.vTestName.text} to cart.",Toast.LENGTH_SHORT).show()
                    db.addToOrders(uid,testName,price,Database.ORDER_TYPE.LAB_TEST)
                }else{
                    db.removeFromOrders(uid,Database.ORDER_TYPE.LAB_TEST)
                    Toast.makeText(this@MyRAdapter.Context,"Removing ${holder.vTestName.text} from cart.",Toast.LENGTH_SHORT).show()
                }
                db.close()
            }
        })
    }
    override fun getItemCount(): Int {
        return cursor.count
    }
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox=view.findViewById(R.id.vtest_check);
        val vprice:TextView=view.findViewById(R.id.vtest_price)
        val vUid:TextView=view.findViewById(R.id.vudi)
        val vTestName:TextView=view.findViewById(R.id.vtest_name)
    }
}