package com.alplabs.barcodescanner.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Alfredo Lima Porfirio on 13/03/23.
 */
@Entity
data class Barcode(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "barcode_encoded") val barcodeEncoded: String,
    @ColumnInfo(name = "barcode_decoded") val barcodeDecoded: String,
    @ColumnInfo(name = "value") val value: Double?,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "due_date") val dueDate: Long?,
    @ColumnInfo(name = "created_datetime") val createdDatetime: Long,
    @ColumnInfo(name = "file_path") val filePath: String
)