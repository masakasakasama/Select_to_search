package com.tatsuya.websearch

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val message = TextView(this).apply {
            text = getString(R.string.installed_message)
            gravity = Gravity.CENTER
            textSize = 16f
            setPadding(32, 32, 32, 32)
        }
        setContentView(message)
    }
}
