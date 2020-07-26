package tink.co.weatherforecast.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import tink.co.weatherforecast.R

class SplashActivity : AppCompatActivity() {

    private val handler: Handler = Handler()
    private val runnable: Runnable = Runnable {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }
    private val SPLASH_DELAY = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, SPLASH_DELAY)
    }

    override fun onPause() {
        handler.removeCallbacks(runnable)
        super.onPause()
    }
}