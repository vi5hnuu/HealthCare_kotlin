package com.vi5hnu.healthcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.cardview.widget.CardView

class HomeActivity : AppCompatActivity() {
    private lateinit var vlogOut:CardView
    private lateinit var vFindDoctor:CardView
    private lateinit var vAboutDevp:CardView
    private lateinit var vlab_test:CardView
    private lateinit var vorder_details:CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        vlogOut=findViewById(R.id.logout_app)
        vFindDoctor=findViewById(R.id.vfind_doctor)
        vAboutDevp=findViewById(R.id.vabout_devp)
        vlab_test=findViewById(R.id.vlab_test)
        vorder_details=findViewById(R.id.vorder_details)

        welcomeUser()

        vlogOut.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val spEditor=getSharedPreferences(getString(R.string.sp_user_info), MODE_PRIVATE).edit()
                spEditor.remove(getString(R.string.key_username))
                spEditor.apply()

                startActivity(Intent(this@HomeActivity,LoginActivity::class.java))
                finish()
            }
        })
        vFindDoctor.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@HomeActivity,FindDoctor::class.java))
            }
        })

        vAboutDevp.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@HomeActivity,AboutDeveloperActivity::class.java))
            }
        })
        vlab_test.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@HomeActivity,LabTestsActivity::class.java))
            }
        })
        vorder_details.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@HomeActivity,OrdersActivity::class.java))
            }
        })
    }
    private fun welcomeUser(){
        val sp=getSharedPreferences(getString(R.string.sp_user_info), MODE_PRIVATE)
        val username=sp.getString(getString(R.string.key_username),"User")
        Toast.makeText(this,"Welcome ${username}!",Toast.LENGTH_SHORT).show()
    }
}