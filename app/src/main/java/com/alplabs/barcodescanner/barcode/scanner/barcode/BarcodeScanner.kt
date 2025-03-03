package com.alplabs.barcodescanner.barcode.scanner.barcode

import android.util.Log
import com.alplabs.barcodescanner.barcode.scanner.model.ScanBarcodeModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

/**
 * Created by Alfredo Lima Porfirio on 13/03/23.
 */
class BarcodeScanner {

    private val barcodeDetector = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder().setBarcodeFormats(
            Barcode.FORMAT_ITF
        ).build()
    )

    fun scanner(inputImage: InputImage, callback: (itemModel: ScanBarcodeModel?) -> Unit) {
        val task = barcodeDetector.process(inputImage)

        task.addOnSuccessListener { results ->
            Log.d(BarcodeScanner::class.simpleName, "Success: $results")

            val foundedItem = results.mapNotNull {
                val rawValue = it.rawValue
                val boundingBox = it.boundingBox

                if (rawValue != null && boundingBox != null) {
                    val value = sanitizer(rawValue)
                    ScanBarcodeModel(value, boundingBox)
                } else {
                    null
                }
            }.firstOrNull {
                validate(it.value)
            }

            if (foundedItem != null) {
                callback.invoke(foundedItem)
            } else {
                callback.invoke(null)
            }
        }.addOnFailureListener { e ->
            Log.d(BarcodeScanner::class.simpleName, "Error: $e")
            callback.invoke(null)
        }
    }

    private fun sanitizer(displayedValue: String): String {
        return displayedValue.filter { it.isDigit() }
    }

    private fun validate(value: String): Boolean {
        return value.length == 44
    }

}