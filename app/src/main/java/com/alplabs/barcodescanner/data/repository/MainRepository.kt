package com.alplabs.barcodescanner.data.repository

import com.alplabs.barcodescanner.data.database.DatabaseService
import com.alplabs.barcodescanner.data.database.MainDatabase
import com.alplabs.barcodescanner.data.mapper.BarcodeMapper
import com.alplabs.barcodescanner.data.model.BarcodeModel

/**
 * Created by Alfredo Lima Porfirio on 13/03/23.
 */
class MainRepository(
    private val mainDatabase: MainDatabase = DatabaseService.default.mainDatabase,
    private val barcodeMapper: BarcodeMapper = BarcodeMapper()
) {

    suspend fun addBarcodeModel(barcodeModel: BarcodeModel): Result<Unit> {
        val value = mainDatabase.barcodeDao().add(
            barcodeMapper.barcodeModelToEntity(barcodeModel)
        )

        return if (value != null && value >= 0) {
            Result.success(Unit)
        } else {
            Result.failure(Throwable("Not saved ${BarcodeModel::class.java.simpleName} with success"))
        }
    }

    suspend fun getBarcodeModels(): Result<List<BarcodeModel>> {
        val values = mainDatabase.barcodeDao().list().map {
            barcodeMapper.barcodeEntityToModel(it)
        }

        return Result.success(values)
    }

    suspend fun getLastBarcodeModel(): Result<List<BarcodeModel>> {
        val barcodeModel = barcodeMapper.barcodeEntityToModel(mainDatabase.barcodeDao().last())
        return Result.success(listOf(barcodeModel))
    }

    suspend fun deleteBarcodeModel(barcodeModel: BarcodeModel): Result<BarcodeModel> {
        val barcode = barcodeMapper.barcodeModelToEntity(barcodeModel)
        mainDatabase.barcodeDao().remove(barcode)
        return Result.success(barcodeModel)
    }

}