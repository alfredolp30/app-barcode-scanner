package com.alplabs.barcodescanner.barcode.scanner.text

import android.graphics.Rect
import android.util.Log
import com.alplabs.barcodescanner.barcode.scanner.model.ScanTextModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

/**
 * Created by Alfredo Lima Porfirio on 13/03/23.
 */
class TextScanner (
    private val textPattern: TextPattern
) {
    private val textDetector = TextRecognition.getClient(
        TextRecognizerOptions.DEFAULT_OPTIONS
    )

    fun scanner(inputImage: InputImage, callback: (itemModel: ScanTextModel?) -> Unit) {
        val task = textDetector.process(inputImage)

        task.addOnSuccessListener { results ->
            Log.d(TextScanner::class.simpleName, "Success: $results")

            val values = mutableListOf<String>()
            val boundingBoxes = mutableListOf<Rect>()

            results.textBlocks.forEach { result ->
                val value = textPattern.sanitizer(result.text)
                val boundingBox = result.boundingBox

                if (textPattern.validate(value) && boundingBox != null) {
                    values.add(value)
                    boundingBoxes.add(boundingBox)
                }
            }

            if (values.isNotEmpty() && boundingBoxes.isNotEmpty()) {
                callback.invoke(ScanTextModel(values, boundingBoxes))
            } else {
                callback.invoke(null)
            }
        }.addOnFailureListener { e ->
            Log.d(TextScanner::class.simpleName, "Error: $e")
            callback.invoke(null)
        }
    }
}