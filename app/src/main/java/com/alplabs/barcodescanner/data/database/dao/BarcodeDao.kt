package com.alplabs.barcodescanner.data.database.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.Companion.REPLACE
import com.alplabs.barcodescanner.data.database.entity.Barcode

/**
 * Created by Alfredo Lima Porfirio on 13/03/23.
 */
@Dao
interface BarcodeDao {

    @Query("SELECT * FROM Barcode ORDER BY created_datetime DESC")
    suspend fun list() : List<Barcode>

    @Query("SELECT * FROM Barcode ORDER BY created_datetime DESC LIMIT 1")
    suspend fun last() : Barcode

    @Insert(onConflict = REPLACE)
    suspend fun add(barcode: Barcode): Long?

    @Delete
    suspend fun remove(barcode: Barcode)

    @Update
    suspend fun replace(barcode: Barcode)

}