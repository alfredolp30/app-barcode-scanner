package com.alplabs.barcodescanner.barcode.scanner

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.media.Image
import android.net.Uri
import android.view.Surface
import androidx.core.graphics.applyCanvas
import com.alplabs.barcodescanner.barcode.scanner.barcode.BarcodeScanner
import com.alplabs.barcodescanner.barcode.scanner.model.ScanBarcodeModel
import com.alplabs.barcodescanner.barcode.scanner.model.ScanTextModel
import com.alplabs.barcodescanner.barcode.scanner.text.BarcodeTextPattern
import com.alplabs.barcodescanner.barcode.scanner.text.TextScanner
import com.alplabs.barcodescanner.data.model.BarcodeModel
import com.alplabs.barcodescanner.barcode.scanner.invoice.InvoiceChecker
import com.alplabs.barcodescanner.barcode.scanner.invoice.InvoiceCollection
import com.alplabs.filebarcodescanner.invoice.InvoiceBank
import com.google.mlkit.vision.common.InputImage
import com.shockwave.pdfium.PdfPasswordException
import java.io.File
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Alfredo Lima Porfirio on 13/03/23.
 */
class InvoiceDetector (
    private val context: Context,
    listener: Listener
) {

    interface Listener {
        fun onFinish(barcodeModel: BarcodeModel?)
        fun onNeedsPassword(uri: Uri)
    }

    private val currentUris = mutableListOf<Uri>()
    private var position = 0
    private val weakListener = WeakReference(listener)

    suspend fun start(sourceUri: Uri, password: String?) {
        val scannerType = ScannerType.uriToScannerType(context, sourceUri)

        val uris = try {
            scannerType?.getConverter(context, sourceUri, password)?.converter()
        } catch(e: PdfPasswordException) {
            weakListener.get()?.onNeedsPassword(sourceUri)
            return
        }


        if (uris != null && uris.isNotEmpty()) {
            currentUris.addAll(uris)
            position = 0
        }

        next()
    }

    fun start(image: Image) {
        val inputImage = InputImage.fromMediaImage(
            image,
            Surface.ROTATION_270 * 90
        )
        process(inputImage)
    }

    fun stop() {
        weakListener.clear()
    }

    fun next() {
        val uri = currentUris.getOrNull(position++)

        if (uri != null) {
            val inputImage = InputImage.fromFilePath(context, uri)
            process(inputImage)
        } else {
            weakListener.get()?.onFinish(null)
        }
    }

    private fun process(inputImage: InputImage) {
        val barcodeScanner = BarcodeScanner()

        barcodeScanner.scanner(inputImage) { itemModel1 ->
            if (itemModel1 != null) {
                foundScanBarcode(itemModel1, inputImage)
            } else {
                processText(inputImage)
            }
        }
    }

    private fun processText(inputImage: InputImage) {
        val textScanner = TextScanner(BarcodeTextPattern())

        textScanner.scanner(inputImage) { itemModel2 ->
            if (itemModel2 != null) {
                foundScanText(itemModel2, inputImage)
            } else {
                next()
            }
        }
    }

    private fun foundScanBarcode(scanBarcodeModel: ScanBarcodeModel, inputImage: InputImage) {
        val barcodeEncoded = scanBarcodeModel.value
        val checker = InvoiceChecker(barcodeEncoded)

        val invoiceBase = when {
            checker.isBarcodeCollection -> InvoiceCollection(barcodeEncoded, GregorianCalendar())
            else -> InvoiceBank(barcodeEncoded)
        }

        val filePath = saveImageToFile(inputImage, listOf(scanBarcodeModel.boundingBox))

        val barcodeModel = BarcodeModel(
            barcodeEncoded = barcodeEncoded,
            barcodeDecoded = invoiceBase.barcodeWithDigits,
            value = invoiceBase.value,
            title = "",
            dueDate = invoiceBase.calendar?.time?.time,
            createdDatetime = GregorianCalendar().timeInMillis,
            filePath = filePath
        )

        weakListener.get()?.onFinish(barcodeModel)
    }

    private fun foundScanText(scanTextModel: ScanTextModel, inputImage: InputImage) {
        val barcodeDecoded = scanTextModel.values.joinToString("")
        val filePath = saveImageToFile(inputImage, scanTextModel.boundingBoxes)

        val barcodeModel = BarcodeModel(
            barcodeEncoded = "",
            barcodeDecoded = barcodeDecoded,
            value = null,
            title = "",
            dueDate = null,
            createdDatetime = GregorianCalendar().timeInMillis,
            filePath = filePath
        )

        weakListener.get()?.onFinish(barcodeModel)
    }


    private fun saveImageToFile(
        image: InputImage,
        boundingBoxes: List<Rect>,
    ) : String {

        val bitmap = image.bitmapInternal ?: return ""

        val mutableBitmap: Bitmap = bitmap.copy(Bitmap.Config.RGB_565, true)

        mutableBitmap.applyCanvas {
            val paint = Paint().apply {
                color = Color.RED
                style = Paint.Style.STROKE
                strokeWidth = 10f
            }

            boundingBoxes.forEach {
                drawRect(
                    it.left.toFloat(),
                    it.top.toFloat(),
                    it.right.toFloat(),
                    it.bottom.toFloat(),
                    paint
                )
            }

        }

        val sdf = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-S", Locale("pt", "BR"))
        val dateStr = sdf.format(GregorianCalendar().time)

        val directory = File(context.filesDir, "barcode")
        if (!directory.exists()) directory.mkdirs()

        val file = File(directory, "$dateStr.png")

        mutableBitmap.compress(Bitmap.CompressFormat.PNG, 50, file.outputStream())

        return file.absolutePath
    }

}