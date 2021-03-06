package me.proxer.app.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotterknife.bindView
import me.proxer.app.R
import me.proxer.app.util.compat.TaskDescriptionCompat
import me.proxer.app.util.data.PreferenceHelper
import me.proxer.app.util.extension.getSafeStringExtra
import me.proxer.app.util.extension.startActivity
import org.koin.android.ext.android.inject

/**
 * @author Ruben Gees
 */
class WebViewActivity : AppCompatActivity() {

    companion object {
        private const val URL_EXTRA = "url"

        fun navigateTo(context: Activity, url: String) = context.startActivity<WebViewActivity>(URL_EXTRA to url)
    }

    private val url: String
        get() = intent.getSafeStringExtra(URL_EXTRA)

    private val preferenceHelper by inject<PreferenceHelper>()

    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val webView: WebView by bindView(R.id.webview)

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        val theme = preferenceHelper.themeContainer.theme

        getTheme().applyStyle(theme.main, true)
        TaskDescriptionCompat.setTaskDescription(this, theme.primaryColor(this))

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_web_view)
        setSupportActionBar(toolbar)

        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = url

        webView.loadUrl(url)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }
}
