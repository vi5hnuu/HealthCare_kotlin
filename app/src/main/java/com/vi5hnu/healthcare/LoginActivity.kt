package com.vi5hnu.healthcare

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    private lateinit var btnLogin:Button
    private lateinit var tfUsername:EditText
    private lateinit var tfPassword:EditText
    private lateinit var tvRegNewUser:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin=findViewById(R.id.btn_login)
        tfUsername=findViewById(R.id.tf_username)
        tfPassword=findViewById(R.id.tf_password)
        tvRegNewUser=findViewById(R.id.tv_reg_new_user)

        val sp=getSharedPreferences(getString(R.string.sp_user_info), MODE_PRIVATE)
        val usrName=sp.getString(getString(R.string.key_username),"NULL")
        if(usrName!="NULL"){
            Database.username=usrName!!  //required in db
            startActivity(Intent(this,HomeActivity::class.java))
            finish()
        }

        btnLogin.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val username=tfUsername.text.toString().trim()
                val pasword=tfPassword.text.toString().trim()
                if(username.isEmpty() or pasword.isEmpty()){
                    Toast.makeText(this@LoginActivity,"Invalid username or password.",Toast.LENGTH_SHORT).show()
                    return
                }
                val db=Database(this@LoginActivity,getString(R.string.dbName),null,1)
                if(!db.login(username,pasword)){
                    Toast.makeText(this@LoginActivity,"user does not exist.",Toast.LENGTH_SHORT).show()
                    db.close()
                    return
                }
                /////////Log In Success////////////////////
                val sp: SharedPreferences =getSharedPreferences(getString(R.string.sp_user_info), Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor=sp.edit()
                editor.putString(getString(R.string.key_username),username)
                editor.apply()
                startActivity(Intent(this@LoginActivity,HomeActivity::class.java))
                finish()
                Toast.makeText(this@LoginActivity,"Logging",Toast.LENGTH_SHORT).show()
            }
        })

        tvRegNewUser.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@LoginActivity,RegisterActivity::class.java))
                finish()
            }
        })
    }
}