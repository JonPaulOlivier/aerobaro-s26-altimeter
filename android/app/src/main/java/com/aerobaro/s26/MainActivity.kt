package com.aerobaro.s26

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.webkit.GeolocationPermissions
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebViewAssetLoader
import java.util.Locale

class MainActivity : Activity(), SensorEventListener {

    private lateinit var webView: WebView
    private lateinit var sensorManager: SensorManager
    private var pressureSensor: Sensor? = null

    @Volatile
    private var currentPressureHpa: Float = 1013.25f

    @Volatile
    private var hasHardwareReading: Boolean = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.parseColor("#07090E")
        window.navigationBarColor = Color.parseColor("#07090E")

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

        webView = WebView(this).apply {
            setBackgroundColor(Color.parseColor("#07090E"))
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = false
            settings.setGeolocationEnabled(true)
            addJavascriptInterface(BarometerJsBridge(), "AndroidBarometer")
        }

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                val url = request.url
                if (url.host == "appassets.androidplatform.net") {
                    val path = if (url.path.isNullOrEmpty() || url.path == "/") {
                        "/assets/public/index.html"
                    } else {
                        "/assets/public" + url.path
                    }
                    return assetLoader.shouldInterceptRequest(
                        url.buildUpon().path(path).build()
                    )
                }
                return super.shouldInterceptRequest(view, request)
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(
                origin: String,
                callback: GeolocationPermissions.Callback
            ) {
                callback.invoke(origin, true, false)
            }
        }

        setContentView(webView)
        webView.loadUrl("https://appassets.androidplatform.net/index.html")
    }

    override fun onResume() {
        super.onResume()
        pressureSensor?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_PRESSURE) {
            val rawHpa = event.values[0]
            if (rawHpa in 250f..1100f) {
                currentPressureHpa = if (!hasHardwareReading) {
                    hasHardwareReading = true
                    rawHpa
                } else {
                    (currentPressureHpa * 0.80f) + (rawHpa * 0.20f)
                }
                val js = String.format(
                    Locale.US,
                    "window.dispatchEvent(new CustomEvent('s26-barometer',{detail:{pressureHpa:%.3f}}));",
                    currentPressureHpa
                )
                webView.post {
                    webView.evaluateJavascript(js, null)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    inner class BarometerJsBridge {
        @JavascriptInterface
        fun getPressureHpa(): Float = currentPressureHpa

        @JavascriptInterface
        fun isHardwareBarometerAvailable(): Boolean = pressureSensor != null

        @JavascriptInterface
        fun getSensorModel(): String = pressureSensor?.name ?: "Samsung Galaxy S26 Barometer"
    }
}
