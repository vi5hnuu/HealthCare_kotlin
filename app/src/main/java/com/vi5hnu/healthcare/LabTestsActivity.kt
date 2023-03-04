package com.vi5hnu.healthcare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.SimpleCursorAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LabTestsActivity : AppCompatActivity() {
    private lateinit var vlab_tests:RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab_tests)

        vlab_tests=findViewById(R.id.vlab_tests)
        vlab_tests.layoutManager=LinearLayoutManager(this)

        val db=Database(this,getString(R.string.dbName),null,1)
        val labTestsCursor=db.getLabTestCursor()
        vlab_tests.adapter=MyRAdapter(this,labTestsCursor)
    }
}


























