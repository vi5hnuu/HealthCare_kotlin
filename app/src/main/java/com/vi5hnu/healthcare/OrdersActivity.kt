package com.vi5hnu.healthcare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView

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
        btnPlaceOrder=findViewById(R.id.btn_place_order)

        val db=Database(this,getString(R.string.dbName),null,1)

        vorderTotal.text=db.getOrderTotal()
        //TODO : Error if orders is empty
        adp= SimpleCursorAdapter(this,
            R.layout.order_tile,
            db.getOrders(),
            arrayOf("name","amount","order_id","type"),
            intArrayOf(R.id.vorder_name,R.id.vorder_price,R.id.vorder_id,R.id.vorder_type),0)
        vOrdersContainer.adapter=adp

        vOrdersContainer.setOnItemLongClickListener(object:AdapterView.OnItemLongClickListener{
            override fun onItemLongClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ): Boolean {
                val order_id=view!!.findViewById<TextView>(R.id.vorder_id).text.toString()
                val name=view!!.findViewById<TextView>(R.id.vorder_name).text.toString()
                val type=view!!.findViewById<TextView>(R.id.vorder_type).text.toString()
                Toast.makeText(this@OrdersActivity,"Removing item ${name} from cart",Toast.LENGTH_SHORT).show()
                val db=Database(this@OrdersActivity,getString(R.string.dbName),null,1)
                db.removeFromOrders(order_id,if(type=="T") Database.ORDER_TYPE.LAB_TEST else Database.ORDER_TYPE.MEDICINE)
                view.findViewById<CardView>(R.id.tile).setCardBackgroundColor(resources.getColor(R.color.colorRed))
//                adp.notifyDataSetChanged()//TODO :: NOT WORKING SO CHANGED COLOR BG TO RED
                return true;
            }
        })
        /////////////////////////////////////
        btnPlaceOrder.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                //TODO : create confirmation activity
            }
        })
    }
}