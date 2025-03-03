package com.alplabs.barcodescanner.barcode.scanner

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.alplabs.barcodescanner.barcode.converter.Converter
import com.alplabs.barcodescanner.barcode.converter.ImageConverter
import com.alplabs.barcodescanner.barcode.converter.PdfConverter

/**
 * Created by Alfredo Lima Porfirio on 13/03/23.
 */
enum class ScannerType {
    PDF,
    PNG,
    JPG,
    JPEG,
    BMP;

    companion object {
        fun uriToScannerType(context: Context, uri: Uri): ScannerType? {
            val mimeType = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(context.contentResolver.getType(uri))

            return values().firstOrNull { it.name.lowercase() == mimeType }
        }
    }

    fun getConverter(context: Context, uri: Uri, password: String?): Converter {
        return when(this) {
            PDF -> PdfConverter(context, uri, password)
            else -> ImageConverter(uri)
        }
    }
}