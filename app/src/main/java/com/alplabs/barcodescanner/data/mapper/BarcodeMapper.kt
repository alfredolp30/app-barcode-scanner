package com.alplabs.barcodescanner.data.mapper

import com.alplabs.barcodescanner.data.database.entity.Barcode
import com.alplabs.barcodescanner.data.model.BarcodeModel

/**
 * Created by Alfredo Lima Porfirio on 13/03/23.
 */
class BarcodeMapper {

    fun barcodeEntityToModel(barcode: Barcode) : BarcodeModel {
        return BarcodeModel(
            id = barcode.id,
            barcodeEncoded = barcode.barcodeEncoded,
            barcodeDecoded = barcode.barcodeDecoded,
            value = barcode.value,
            title = barcode.title,
            dueDate = barcode.dueDate,
            createdDatetime = barcode.createdDatetime,
            filePath = barcode.filePath
        )
    }

    fun barcodeModelToEntity(barcodeModel: BarcodeModel) : Barcode {
        return Barcode(
            id = barcodeModel.id,
            barcodeEncoded = barcodeModel.barcodeEncoded,
            barcodeDecoded = barcodeModel.barcodeDecoded,
            value = barcodeModel.value,
            title = barcodeModel.title,
            dueDate = barcodeModel.dueDate,
            createdDatetime = barcodeModel.createdDatetime,
            filePath = barcodeModel.filePath
        )
    }

}