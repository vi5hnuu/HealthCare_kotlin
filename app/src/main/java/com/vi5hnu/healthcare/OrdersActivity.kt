package com.vi5hnu.healthcare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.SimpleCursorAdapter

class OrdersActivity : AppCompatActivity() {
    private lateinit var vOrdersContainer:ListView
    private lateinit var adp:SimpleCursorAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        vOrdersContainer=findViewById(R.id.vorders_container)

        val db=Database(this,getString(R.string.dbName),null,1)
        val cursor=db.getOrders()

//        adp= SimpleCursorAdapter(this,
//            R.layout.order_tile,
//            cursor,
//            arrayOf("_id","name","exp","mobile"),
//            intArrayOf(R.id.vdoctor_id,R.id.vdoctor_name,R.id.vdoctor_exp,R.id.vdoctor_phone),0)
//        doc_cont.adapter=adp
    }
}