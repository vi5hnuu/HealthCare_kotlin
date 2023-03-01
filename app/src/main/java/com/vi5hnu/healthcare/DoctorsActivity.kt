package com.vi5hnu.healthcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.TextView

class DoctorsActivity : AppCompatActivity() {
    private lateinit var docTitle:TextView
    private lateinit var doc_cont:ListView
    private lateinit var adp:SimpleCursorAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctors)
        doc_cont=findViewById(R.id.doctors_container)
        docTitle=findViewById<TextView>(R.id.vdoctor_type)
        docTitle.text=intent.extras?.getString(getString(R.string.activityHKey))


//        val doctors_items=ArrayList<Map<String,String>>()
//        //Demo data
//        repeat(10){
//            doctors_items.add(mapOf(Pair("name","Dr. Ashok Goel${it}"),
//                Pair("exp","Exp : ${it} years"),
//                Pair("phone","Phone : 9814339065")))
//        }
        //
        val db=Database(this,getString(R.string.dbName),null,1)
        val doc_type=when(docTitle.text.toString()){
            getString(R.string.ah_cd)->Database.DOCTOR_TYPE.CARDIOLOGIST
            getString(R.string.ah_de)->Database.DOCTOR_TYPE.DENTIST
            getString(R.string.ah_di)->Database.DOCTOR_TYPE.DIETICIAN
            getString(R.string.ah_fd)->Database.DOCTOR_TYPE.FAMILY_DOCTOR
            else->Database.DOCTOR_TYPE.SURGEON
        }
        val cursor=db.getDoctorsCursor(doc_type,Database.DOCTORSCOLUMN.NAME,Database.DOCTORSCOLUMN.EXP,Database.DOCTORSCOLUMN.MOBILE)

        adp=SimpleCursorAdapter(this,
            R.layout.doctor_list_tile,
            cursor,
            arrayOf("_id","name","exp","mobile"),
            intArrayOf(R.id.vdoctor_id,R.id.vdoctor_name,R.id.vdoctor_exp,R.id.vdoctor_phone),0)
        doc_cont.adapter=adp

        /////////////////////
        doc_cont.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val doc_detail_intent =Intent(this@DoctorsActivity,DoctorDetail::class.java);
                val id=view?.findViewById<TextView>(R.id.vdoctor_id)?.text.toString().toInt()
                doc_detail_intent.putExtra("_id",id );
                startActivity(doc_detail_intent)
            }
        });
        ////////////////////
    }
}