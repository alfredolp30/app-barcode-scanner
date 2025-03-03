package com.alplabs.barcodescanner.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alplabs.barcodescanner.data.model.BarcodeModel
import com.alplabs.barcodescanner.data.repository.MainRepository
import com.alplabs.barcodescanner.data.repository.Result

class HistoryViewModel : ViewModel() {

    val barcodeModels: LiveData<Result<List<BarcodeModel>>> get() = _barcodeModels
    private val _barcodeModels = MutableLiveData<Result<List<BarcodeModel>>>()

    val deletedBarcodeModel: LiveData<Result<BarcodeModel>> get() = _deletedBarcodeModel
    private val _deletedBarcodeModel = MutableLiveData<Result<BarcodeModel>>()

    private val repository by lazy { MainRepository() }

    suspend fun loadBarcodeModels() {
        _barcodeModels.postValue(repository.getBarcodeModels())
    }

    suspend fun loadLastBarcodeModel() {
        _barcodeModels.postValue(repository.getLastBarcodeModel())
    }

    suspend fun deleteBarcodeModel(barcodeModel: BarcodeModel) {
        _deletedBarcodeModel.postValue(repository.deleteBarcodeModel(barcodeModel))
    }
}