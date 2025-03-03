package com.alplabs.barcodescanner.ui.history

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.alplabs.barcodescanner.databinding.CellBarcodeBinding

/**
 * Created by Alfredo L. Porfirio on 01/03/19.
 * Copyright Universo Online 2019. All rights reserved.
 */
class HistoryBarcodeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val barcodeCellBinding = CellBarcodeBinding.bind(view)
}