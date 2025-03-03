package com.alplabs.barcodescanner.extension

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log

/**
 * Created by Alfredo Lima Porfirio on 21/05/23.
 */
fun Uri.getFilename(context: Context): String {
    var fileName = ""

    if (scheme == "file") {
        fileName = lastPathSegment ?: ""

    } else if (scheme == "content") {
        val cursor = context.contentResolver.query(this, null, null, null, null)

        try {
            if (cursor != null && cursor.moveToFirst()) {
                fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        } catch (e: Exception) {
            Log.e("pdfToImage", "cursor error", e)
        }

        cursor?.close()
    }

    return fileName
}