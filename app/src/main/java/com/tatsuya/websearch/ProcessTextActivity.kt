package com.tatsuya.websearch

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle

class ProcessTextActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedText = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
            ?.toString()
            ?.trim()

        if (!selectedText.isNullOrEmpty()) {
            openGoogleSearch(selectedText)
        }

        finish()
    }

    private fun openGoogleSearch(query: String) {
        val encodedQuery = Uri.encode(query)
        val searchUri = Uri.parse("https://www.google.com/search?q=$encodedQuery")
        val browserIntent = Intent(Intent.ACTION_VIEW, searchUri)

        try {
            startActivity(browserIntent)
        } catch (_: ActivityNotFoundException) {
            // No browser is available. Exit silently.
        }
    }
}
