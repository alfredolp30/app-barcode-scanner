package com.alplabs.barcodescanner.data.model

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Alfredo Lima Porfirio on 13/03/23.
 */
data class BarcodeModel(
    val id: Int? = null,
    val barcodeEncoded: String,
    val barcodeDecoded: String,
    val value: Double?,
    val title: String,
    val dueDate: Long?,
    val createdDatetime: Long,
    val filePath: String
) {

    val dueDateFormatted: String? get() {
        val dueDateSdf = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

        return if (dueDate != null) {
            val calendar = GregorianCalendar().apply {
                time.time = dueDate
            }

            dueDateSdf.format(calendar.time)
        } else {
            null
        }
    }

    val createDatetimeFormatted: String get() {
        val createDatetimeSdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("pt", "BR"))

        val calendar = GregorianCalendar().apply {
            timeInMillis = createdDatetime
        }

        return createDatetimeSdf.format(calendar.time)
    }

    val valueFormatted: String? get() {
        val valueSdf = DecimalFormat("#.00")

        return if (value != null) {
            valueSdf.format(value)
        } else {
            null
        }
    }

    fun pathProviderUri(context: Context) : Uri? {
        return if (filePath.isNotBlank()) {
            FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".provider",
                File(filePath)
            )
        } else {
            null
        }
    }

}