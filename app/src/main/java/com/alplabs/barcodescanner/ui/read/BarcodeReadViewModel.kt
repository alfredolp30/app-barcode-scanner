package com.alplabs.barcodescanner.ui.read

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alplabs.barcodescanner.barcode.scanner.InvoiceDetector
import com.alplabs.barcodescanner.data.model.BarcodeModel
import com.alplabs.barcodescanner.data.repository.MainRepository
import com.alplabs.barcodescanner.data.repository.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class BarcodeReadViewModel : ViewModel(), InvoiceDetector.Listener {

    val foundedBarcodeModel: LiveData<BarcodeModel?> get() = _foundedBarcodeModel
    private val _foundedBarcodeModel = MutableLiveData<BarcodeModel?>()

    val addedBarcodeModel: LiveData<Result<Unit>> get() = _addedBarcodeModel
    private val _addedBarcodeModel = MutableLiveData<Result<Unit>>()

    val uriNeedsPassword: LiveData<Uri> get() = _uriNeedsPassword
    private val _uriNeedsPassword = MutableLiveData<Uri>()

    private val repository by lazy { MainRepository() }
    private var invoiceDetector: InvoiceDetector? = null

    suspend fun startScanner(context: Context, uri: Uri, password: String?) {
        invoiceDetector = InvoiceDetector(
            context,
            this
        )

        invoiceDetector?.start(uri, password)
    }

    fun stopScanner() {
        invoiceDetector?.stop()
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
        _uriNeedsPassword.postValue(uri)
    }

}