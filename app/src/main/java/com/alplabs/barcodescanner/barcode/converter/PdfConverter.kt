package com.alplabs.barcodescanner.barcode.converter

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.alplabs.barcodescanner.extension.getFilename
import com.shockwave.pdfium.PdfPasswordException
import com.shockwave.pdfium.PdfiumCore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLEncoder


/**
 * Created by Alfredo Lima Porfirio on 13/03/23.
 */
class PdfConverter(
    private val context: Context,
    private val uri: Uri,
    private val password: String?
): Converter {

    @Throws(PdfPasswordException::class)
    override suspend fun converter(): List<Uri> {
        val ctx = context
        val uris = mutableListOf<Uri>()

        val fd = ctx.contentResolver.openFileDescriptor(uri, "r")
        val filename = uri.getFilename(ctx)

        Log.i("FILENAME", filename)

        val filenameEncoded = withContext(Dispatchers.IO) {
            URLEncoder.encode(filename, Charsets.UTF_8.name())
        }

        val core = PdfiumCore(ctx)

        val pdfDocument = core.newDocument(fd, password)

        val pageCount = core.getPageCount(pdfDocument)

        Log.i(PdfConverter::class.simpleName, "page count $pageCount")

        for (page in 0 until pageCount) {

            val file = File(ctx.cacheDir, "${filenameEncoded}-$page.png")
            if (file.exists()) {
                file.delete()
            }

            Log.d(PdfConverter::class.simpleName, "Init page $page")

            core.openPage(pdfDocument, page)

            val width = core.getPageWidth(pdfDocument, page)
            val height = core.getPageHeight(pdfDocument, page)

            val bitmap = Bitmap.createBitmap(
                width, height,
                Bitmap.Config.RGB_565
            )

            core.renderPageBitmap(
                pdfDocument, bitmap, page, 0, 0,
                width, height
            )

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, file.outputStream())

            uris.add(Uri.fromFile(file))
        }

        core.closeDocument(pdfDocument)

        return uris
    }


}