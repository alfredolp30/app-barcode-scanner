package com.alplabs.barcodescanner.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable


class SendMainActivity : MainActivity() {

    private var firstGetSendedUri = true

    fun getSendedUri() : Uri? {
        var uri: Uri? = null

        if (!firstGetSendedUri) return uri

        when (intent?.action) {
            Intent.ACTION_VIEW -> {
                intent?.data?.let {
                    uri = it
                }
            }

            Intent.ACTION_SEND -> {
                (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
                    uri = it
                }
            }
        }

        return uri
    }

}