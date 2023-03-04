package com.vi5hnu.healthcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlin.properties.Delegates

class DoctorDetail : AppCompatActivity() {
    private lateinit var btnBookAppointMent:Button
    private lateinit var vDocDesc:TextView
    private lateinit var vDocMob:TextView
    private lateinit var vDocExp:TextView
    private lateinit var vDocAdd:TextView
    private lateinit var vDocName:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_detail)

        vDocAdd=findViewById(R.id.vhospital_address)
        vDocMob=findViewById(R.id.vdoctor_phone)
        vDocExp=findViewById(R.id.vdoctor_exp)
        vDocName=findViewById(R.id.vdoctor_name)
        vDocDesc=findViewById(R.id.vdoctor_description)

        val id=intent.extras?.getInt("_id",0)

        val db=Database(this,getString(R.string.dbName),null,1)
        val cursor= db.getDocDetails(id!!)
        var fees=0
        if(cursor.moveToFirst()){
            fees=cursor.getInt(6)
            val name=cursor.getString(2)
            val hosp_addr=cursor.getString(3)
            val exp=cursor.getInt(4)
            val mobile=cursor.getString(5)
            val desc=cursor.getString(7)

            vDocAdd.text="Address : ${hosp_addr}"
            vDocMob.text=mobile
            vDocExp.text="Experience : ${exp}"
            vDocName.text=name
            vDocDesc.text=desc
            cursor.close()
        }


        btnBookAppointMent=findViewById(R.id.btnbookappointment)
        btnBookAppointMent.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val appointIntent=Intent(this@DoctorDetail,AppointmentActivity::class.java)
                appointIntent.putExtra("fees",fees)
                startActivity(appointIntent)
            }
        })
    }
}