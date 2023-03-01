package com.vi5hnu.healthcare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class AppointmentActivity : AppCompatActivity() {
    private lateinit var vfees:TextView
    private lateinit var vFullName:EditText
    private lateinit var vEmail:EditText
    private lateinit var vAddress:EditText
    private lateinit var vContact:EditText
    private lateinit var btnBook:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment)

        vfees=findViewById(R.id.vfees)
        vFullName=findViewById(R.id.tf_fullname)
        vEmail=findViewById(R.id.tf_email)
        vAddress=findViewById(R.id.tf_address)
        vContact=findViewById(R.id.tf_contact)
        btnBook=findViewById(R.id.btn_book)
        val fees=intent.extras?.getInt("fees").toString()
        vfees.text="${fees}$"

        /////////
        btnBook.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                //ToDo store appointment in db
                Toast.makeText(this@AppointmentActivity,"Appointment Booked.",Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }
}