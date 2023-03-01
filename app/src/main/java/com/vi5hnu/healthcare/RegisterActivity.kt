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

class RegisterActivity : AppCompatActivity() {
    private lateinit var btnRegister: Button
    private lateinit var tfUsername: EditText
    private lateinit var tfPassword: EditText
    private lateinit var tfEmail: EditText
    private lateinit var tfConfirmPassword: EditText
    private lateinit var tvExistingUser: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegister=findViewById(R.id.btn_register)
        tfUsername=findViewById(R.id.tf_username)
        tfPassword=findViewById(R.id.tf_password)
        tfEmail=findViewById(R.id.tf_email)
        tfConfirmPassword=findViewById(R.id.tf_passwordConfirm)
        tvExistingUser=findViewById(R.id.tv_existingUser)

        btnRegister.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val username=tfUsername.text.toString().trim()
                val pasword=tfPassword.text.toString().trim()
                val paswordConfirm=tfConfirmPassword.text.toString().trim()
                val email=tfEmail.text.toString().trim()
                if(username.isEmpty() or pasword.isEmpty() or (pasword!=paswordConfirm) or !email.matches(Regex(".+@gmail.com$"))){
                    Toast.makeText(this@RegisterActivity,"Invalid username or password.", Toast.LENGTH_SHORT).show()
                    return
                }
                val db=Database(this@RegisterActivity,getString(R.string.dbName),null,1)
                if(!db.registerUser(username,email,pasword)){
                    Toast.makeText(this@RegisterActivity,"User already exist...", Toast.LENGTH_SHORT).show()
                    return
                }

                ///////////////////LOGIN SUCCESS///////////////////
                Toast.makeText(this@RegisterActivity,"Registration successfully", Toast.LENGTH_SHORT).show()
                val sp:SharedPreferences=getSharedPreferences(getString(R.string.sp_user_info), Context.MODE_PRIVATE)
                val editor:SharedPreferences.Editor=sp.edit()
                editor.putString(getString(R.string.key_username),username)
                editor.apply()
                startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
                finish()
            }
        })
        tvExistingUser.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
                finish()
            }
        })
    }
}