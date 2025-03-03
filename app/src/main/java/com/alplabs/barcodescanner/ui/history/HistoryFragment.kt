package com.alplabs.barcodescanner.ui.history

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alplabs.barcodescanner.R
import com.alplabs.barcodescanner.data.model.BarcodeModel
import com.alplabs.barcodescanner.databinding.FragmentHistoryBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class HistoryFragment : Fragment(), HistoryBarcodeAdapter.Listener {

    private lateinit var binding: FragmentHistoryBinding

    companion object {
        private const val IS_LAST_BARCODE_ONLY = "isLastBarcodeOnly"

        fun newInstance(isLastBarcodeOnly: Boolean) : HistoryFragment {
            val bundle = Bundle().apply {
                putBoolean(IS_LAST_BARCODE_ONLY, isLastBarcodeOnly)
            }

            return HistoryFragment().apply {
                arguments = bundle
            }
        }
    }

    private lateinit var viewModel: HistoryViewModel
    private lateinit var adapter: HistoryBarcodeAdapter

    private val isLastBarcodeOnly by lazy {
        arguments?.getBoolean(IS_LAST_BARCODE_ONLY) ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)

        viewModel.barcodeModels.observe(viewLifecycleOwner) {
            if (it.isSuccess) {
                showBarcodeModels(it.get())
            }
        }

        viewModel.deletedBarcodeModel.observe(viewLifecycleOwner) {
            if (it.isSuccess) {
                didDeleteBarcode(it.get())
            }
        }


        binding.rcBarcodes.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.async(context = Dispatchers.IO) {
            if (isLastBarcodeOnly) {
                viewModel.loadLastBarcodeModel()
            } else {
                viewModel.loadBarcodeModels()
            }
        }
    }

    private fun showBarcodeModels(barcodeModels: List<BarcodeModel>) {
        adapter = HistoryBarcodeAdapter(barcodeModels.toMutableList(), isLastBarcodeOnly, this)
        binding.rcBarcodes.adapter = adapter
    }

    override fun onCopyBarcode(barcodeModel: BarcodeModel) {
        ClipboardManager().copy(requireContext(), barcodeModel.barcodeDecoded)
    }

    override fun onDeleteBarcode(barcodeModel: BarcodeModel) {
        lifecycleScope.async(context = Dispatchers.IO) {
            viewModel.deleteBarcodeModel(barcodeModel)
        }
    }

    private fun didDeleteBarcode(barcodeModel: BarcodeModel) {
        val index = adapter.barcodeModels.indexOf(barcodeModel)
        if (index >= 0) {
            adapter.barcodeModels.removeAt(index)
            adapter.notifyItemRemoved(index)
        }

        Snackbar
            .make(binding.root, getString(R.string.delete_barcode_success), Snackbar.LENGTH_LONG)
            .show()
    }

    override fun onOpenPreview(barcodeModel: BarcodeModel) {
        Intent().apply {
            action = Intent.ACTION_VIEW
            setDataAndType(barcodeModel.pathProviderUri(requireContext()), "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(this)
        }
    }

}