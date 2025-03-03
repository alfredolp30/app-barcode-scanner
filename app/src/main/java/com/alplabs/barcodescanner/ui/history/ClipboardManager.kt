package com.alplabs.barcodescanner.ui.history

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat.getSystemService


/**
 * Created by Alfredo Lima Porfirio on 30/03/23.
 */
class ClipboardManager {
    companion object {
        const val TITLE = "barcode"
    }

    fun copy(context: Context, text: String) {
        val clipboard = getSystemService(context, ClipboardManager::class.java)
        val clip = ClipData.newPlainText(TITLE, text)
        clipboard?.setPrimaryClip(clip)
    }
}