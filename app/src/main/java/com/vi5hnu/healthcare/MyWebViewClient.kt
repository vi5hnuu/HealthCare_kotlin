import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient


class MyWebViewClient() : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return false
    }
}