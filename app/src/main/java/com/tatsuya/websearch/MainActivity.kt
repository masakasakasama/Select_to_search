package com.tatsuya.websearch

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : Activity() {
    private lateinit var message: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        message = TextView(this).apply {
            text = getString(R.string.checking_update_message)
            gravity = Gravity.CENTER
            textSize = 16f
            setPadding(32, 32, 32, 32)
        }

        val accessibilityButton = Button(this).apply {
            text = getString(R.string.open_accessibility_settings)
            setOnClickListener {
                try {
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                } catch (_: ActivityNotFoundException) {
                    showMessage(getString(R.string.installed_message))
                }
            }
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
            addView(message)
            addView(accessibilityButton)
        }
        setContentView(content)

        checkForUpdate()
    }

    private fun checkForUpdate() {
        Thread {
            try {
                val marker = fetchRemoteMarker()
                if (marker.isNullOrEmpty()) {
                    showMessage(getString(R.string.installed_message))
                    return@Thread
                }

                val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                val knownMarker = prefs.getString(KEY_RELEASE_MARKER, null)

                if (knownMarker == null) {
                    prefs.edit().putString(KEY_RELEASE_MARKER, marker).apply()
                    showMessage(getString(R.string.auto_update_enabled_message))
                    return@Thread
                }

                if (knownMarker == marker) {
                    showMessage(getString(R.string.installed_message))
                    return@Thread
                }

                showMessage(getString(R.string.downloading_update_message))
                val apkFile = downloadApk()
                prefs.edit().putString(KEY_RELEASE_MARKER, marker).apply()
                openInstaller(apkFile)
            } catch (_: Exception) {
                showMessage(getString(R.string.installed_message))
            }
        }.start()
    }

    private fun fetchRemoteMarker(): String? {
        val connection = openConnection(APK_URL, "HEAD")
        return try {
            val responseCode = connection.responseCode
            if (responseCode !in 200..299) return null

            connection.getHeaderField("ETag")
                ?: connection.getHeaderField("Last-Modified")
                ?: connection.getHeaderField("Content-Length")
        } finally {
            connection.disconnect()
        }
    }

    private fun downloadApk(): File {
        val apkFile = File(cacheDir, APK_FILE_NAME)
        val connection = openConnection(APK_URL, "GET")

        try {
            if (connection.responseCode !in 200..299) {
                throw IllegalStateException("APK download failed")
            }

            connection.inputStream.use { input ->
                apkFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        } finally {
            connection.disconnect()
        }

        return apkFile
    }

    private fun openInstaller(apkFile: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !packageManager.canRequestPackageInstalls()) {
            showMessage(getString(R.string.allow_install_message))
            val settingsIntent = Intent(
                Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                Uri.parse("package:$packageName")
            )

            try {
                startActivity(settingsIntent)
            } catch (_: ActivityNotFoundException) {
                showMessage(getString(R.string.installed_message))
            }
            return
        }

        val apkUri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority("$packageName.apkprovider")
            .appendPath(apkFile.name)
            .build()

        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, APK_MIME_TYPE)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            startActivity(installIntent)
            showMessage(getString(R.string.install_update_message))
        } catch (_: ActivityNotFoundException) {
            showMessage(getString(R.string.installed_message))
        }
    }

    private fun openConnection(url: String, method: String): HttpURLConnection {
        return (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            instanceFollowRedirects = true
            connectTimeout = 10_000
            readTimeout = 30_000
            setRequestProperty("User-Agent", "WebSearchAndroid")
        }
    }

    private fun showMessage(text: String) {
        runOnUiThread {
            message.text = text
        }
    }

    private companion object {
        const val APK_URL = "https://github.com/masakasakasama/Select_to_search/raw/refs/heads/main/dist/web-search-debug.apk"
        const val APK_FILE_NAME = "web-search-update.apk"
        const val APK_MIME_TYPE = "application/vnd.android.package-archive"
        const val PREFS_NAME = "auto_update"
        const val KEY_RELEASE_MARKER = "release_marker"
    }
}
