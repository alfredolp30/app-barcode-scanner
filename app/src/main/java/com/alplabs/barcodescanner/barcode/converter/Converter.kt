package com.alplabs.barcodescanner.barcode.converter

import android.net.Uri

/**
 * Created by Alfredo Lima Porfirio on 13/03/23.
 */
interface Converter {
    suspend fun converter(): List<Uri>
}