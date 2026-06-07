package com.tatsuya.websearch

import android.app.Activity
import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle

class ProcessTextActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedText = extractQuery(intent)

        if (!selectedText.isNullOrEmpty()) {
            openGoogleSearch(selectedText)
        }

        finish()
    }

    private fun extractQuery(sourceIntent: Intent): String? {
        val text = when (sourceIntent.action) {
            Intent.ACTION_PROCESS_TEXT -> sourceIntent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
            Intent.ACTION_SEND -> sourceIntent.getCharSequenceExtra(Intent.EXTRA_TEXT)
            Intent.ACTION_WEB_SEARCH -> sourceIntent.getStringExtra(SearchManager.QUERY)
            else -> null
        }

        return text?.toString()?.trim()
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
