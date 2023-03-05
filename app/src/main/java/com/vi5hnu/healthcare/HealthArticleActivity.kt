package com.vi5hnu.healthcare

import MyWebViewClient
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebView
import android.webkit.WebViewClient

class HealthArticleActivity : AppCompatActivity() {
    private lateinit var vwebview:WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_article)

        vwebview=findViewById(R.id.vwebview)
        vwebview.settings.javaScriptEnabled=true
        vwebview.webViewClient= MyWebViewClient()
        vwebview.loadUrl("https://www.happiesthealth.com/articles");
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && this.vwebview.canGoBack()) {
            this.vwebview.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}