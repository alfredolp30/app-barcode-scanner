package com.alplabs.barcodescanner.barcode.scanner.model

import android.graphics.Rect

/**
 * Created by Alfredo Lima Porfirio on 13/03/23.
 */
data class ScanBarcodeModel(
    val value: String,
    val boundingBox: Rect
)