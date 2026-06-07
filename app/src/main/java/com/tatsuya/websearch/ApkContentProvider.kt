package com.tatsuya.websearch

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import java.io.File

class ApkContentProvider : ContentProvider() {
    override fun onCreate(): Boolean = true

    override fun getType(uri: Uri): String = APK_MIME_TYPE

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor {
        val fileName = uri.lastPathSegment ?: throw IllegalArgumentException("Missing APK file name")
        if (fileName != APK_FILE_NAME) {
            throw IllegalArgumentException("Invalid APK file name")
        }

        val apkFile = File(requireNotNull(context).cacheDir, APK_FILE_NAME)
        return ParcelFileDescriptor.open(apkFile, ParcelFileDescriptor.MODE_READ_ONLY)
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

    private companion object {
        const val APK_FILE_NAME = "web-search-update.apk"
        const val APK_MIME_TYPE = "application/vnd.android.package-archive"
    }
}
