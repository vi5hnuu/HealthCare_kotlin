package com.vi5hnu.healthcare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton

class OrdersActivity : AppCompatActivity() {
    private lateinit var vOrdersContainer:ListView
    private lateinit var adp:SimpleCursorAdapter
    private lateinit var vorderTotal:TextView
    private lateinit var btnPlaceOrder:AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        vOrdersContainer=findViewById(R.id.vorders_container)
        vorderTotal=findViewById(R.id.vorder_total)

        val db=Database(this,getString(R.string.dbName),null,1)

        vorderTotal.text=db.getOrderTotal()

        val cursor=db.getOrders()
        adp= SimpleCursorAdapter(this,
            R.layout.order_tile,
            cursor,
            arrayOf("name","amount","order_id"),
            intArrayOf(R.id.vorder_name,R.id.vorder_price,R.id.vorder_id),0)
        vOrdersContainer.adapter=adp

        /////////////////////////////////////
        btnPlaceOrder.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                //TODO : create confirmation activity
            }
        })
    }
}