package com.alplabs.barcodescanner.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alplabs.barcodescanner.data.database.dao.BarcodeDao
import com.alplabs.barcodescanner.data.database.entity.Barcode

/**
 * Created by Alfredo Lima Porfirio on 13/03/23.
 */
@Database(entities = [Barcode::class], version = 1)
abstract class MainDatabase: RoomDatabase() {
    abstract fun barcodeDao(): BarcodeDao
}