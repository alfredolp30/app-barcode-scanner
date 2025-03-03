package com.alplabs.barcodescanner.ui.camera

import android.content.Context
import android.media.Image
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alplabs.barcodescanner.barcode.scanner.InvoiceDetector
import com.alplabs.barcodescanner.data.model.BarcodeModel
import com.alplabs.barcodescanner.data.repository.MainRepository
import com.alplabs.barcodescanner.data.repository.Result

/**
 * Created by Alfredo Lima Porfirio on 30/03/23.
 */
class CameraViewModel: ViewModel(), InvoiceDetector.Listener {

    val foundedBarcodeModel: LiveData<BarcodeModel?> get() = _foundedBarcodeModel
    private val _foundedBarcodeModel = MutableLiveData<BarcodeModel?>()

    val addedBarcodeModel: LiveData<Result<Unit>> get() = _addedBarcodeModel
    private val _addedBarcodeModel = MutableLiveData<Result<Unit>>()

    private val repository by lazy { MainRepository() }

    private var invoiceDetector: InvoiceDetector? = null

    fun startScanner(context: Context, image: Image) {
        invoiceDetector = InvoiceDetector(context, this)
        invoiceDetector?.start(image)
    }

    suspend fun addBarcodeModel(barcodeModel: BarcodeModel) {
        _addedBarcodeModel.postValue(
            repository.addBarcodeModel(barcodeModel)
        )
    }

    override fun onFinish(barcodeModel: BarcodeModel?) {
        _foundedBarcodeModel.postValue(barcodeModel)
    }

    override fun onNeedsPassword(uri: Uri) {

    }
}