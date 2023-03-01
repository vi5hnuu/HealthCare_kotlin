package com.vi5hnu.healthcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import androidx.core.view.children

class FindDoctor : AppCompatActivity() {
    private lateinit var category:GridLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_doctor)

        category=findViewById(R.id.menu)
        category.children.forEach {
            it.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View?) {
                    when(it.id){
                        R.id.vFamilyDoctor->loadDoctorsActivity(getString(R.string.ah_fd))
                        R.id.vCardiologist->loadDoctorsActivity(getString(R.string.ah_cd))
                        R.id.vDentist->loadDoctorsActivity(getString(R.string.ah_de))
                        R.id.vDietician->loadDoctorsActivity(getString(R.string.ah_di))
                        R.id.vSurgeon->loadDoctorsActivity(getString(R.string.ah_su))
                        else -> loadSupportActivity()
                    }
                }
            })
        }
    }

    private fun loadDoctorsActivity(activityHeading:String){
        val intent=Intent(this,DoctorsActivity::class.java)
        intent.putExtra(getString(R.string.activityHKey),activityHeading)
        startActivity(intent)
    }
    private fun loadSupportActivity(){
        val intent=Intent(this,SupportActivity::class.java)
        intent.putExtra(getString(R.string.activityHKey),"Support")
        startActivity(intent)
    }
}