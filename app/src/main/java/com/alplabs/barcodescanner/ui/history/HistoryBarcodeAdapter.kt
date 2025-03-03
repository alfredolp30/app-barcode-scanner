package com.alplabs.barcodescanner.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.alplabs.barcodescanner.R
import com.alplabs.barcodescanner.data.model.BarcodeModel
import java.lang.ref.WeakReference


/**
 * Created by Alfredo Lima Porfirio on 27/03/23.
 */
class HistoryBarcodeAdapter(
    val barcodeModels: MutableList<BarcodeModel>,
    private val isLastBarcodeOnly: Boolean,
    listener: Listener
): RecyclerView.Adapter<HistoryBarcodeViewHolder>() {

    private val weakListener = WeakReference(listener)

    interface Listener {
        fun onCopyBarcode(barcodeModel: BarcodeModel)
        fun onDeleteBarcode(barcodeModel: BarcodeModel)
        fun onOpenPreview(barcodeModel: BarcodeModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryBarcodeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_barcode, parent, false)
        return HistoryBarcodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryBarcodeViewHolder, position: Int) {
        val barcodeModel = barcodeModels[position]

        holder.barcodeCellBinding.apply {
            txtBarcode.text = barcodeModel.barcodeDecoded
            txtDue.text = barcodeModel.dueDateFormatted
            txtValue.text = barcodeModel.valueFormatted
            txtCreated.text = barcodeModel.createDatetimeFormatted
            imgPreview.load(barcodeModel.filePath) {
                crossfade(true)
            }

            imgPreview.setOnClickListener {
                weakListener.get()?.onOpenPreview(barcodeModel)
            }

            btnCopy.setOnClickListener {
                weakListener.get()?.onCopyBarcode(barcodeModel)
            }

            btnDelete.isEnabled = !isLastBarcodeOnly
            btnDelete.setOnClickListener {
                weakListener.get()?.onDeleteBarcode(barcodeModel)
            }
        }
    }

    override fun getItemCount(): Int = barcodeModels.size

}