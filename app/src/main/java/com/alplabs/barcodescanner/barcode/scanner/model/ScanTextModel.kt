package com.alplabs.barcodescanner.barcode.scanner.model

import android.graphics.Rect

/**
 * Created by Alfredo Lima Porfirio on 13/03/23.
 */
data class ScanTextModel(
    val values: List<String>,
    val boundingBoxes: List<Rect>
)