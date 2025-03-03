package com.alplabs.barcodescanner.barcode.converter

import android.net.Uri

/**
 * Created by Alfredo Lima Porfirio on 13/03/23.
 */
class ImageConverter(val uri: Uri): Converter {

    override suspend fun converter(): List<Uri> {
        return listOf(uri)
    }

}