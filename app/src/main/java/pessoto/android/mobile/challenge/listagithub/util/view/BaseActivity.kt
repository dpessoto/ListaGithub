package pessoto.android.mobile.challenge.listagithub.util.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import pessoto.android.mobile.challenge.listagithub.R

open class BaseActivity : AppCompatActivity() {

    private lateinit var layout: ConstraintLayout
    private var goingToBackground = true

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = layoutInflater.inflate(R.layout.activity_base, null) as ConstraintLayout

        supportActionBar?.hide()
    }

    override fun onResume() {
        super.onResume()
        goingToBackground = true
        try {
            (window.decorView as ViewGroup).removeView(layout)
        } catch (e: Exception) {
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            if (goingToBackground)
                (window.decorView as ViewGroup).addView(layout)
        } catch (e: Exception) {
        }
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        goingToBackground = false
    }

    override fun finish() {
        super.finish()
        goingToBackground = false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        goingToBackground = false
    }
}