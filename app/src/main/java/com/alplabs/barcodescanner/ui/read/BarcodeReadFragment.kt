package com.alplabs.barcodescanner.ui.read

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.alplabs.barcodescanner.R
import com.alplabs.barcodescanner.data.model.BarcodeModel
import com.alplabs.barcodescanner.databinding.FragmentBarcodeReadBinding
import com.alplabs.barcodescanner.ui.preview.PreviewBarcodeActivity
import com.google.android.material.snackbar.Snackbar
import androidx.lifecycle.lifecycleScope
import com.alplabs.barcodescanner.extension.getFilename
import com.alplabs.barcodescanner.ui.camera.CameraActivity
import com.alplabs.barcodescanner.ui.main.SendMainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async


class BarcodeReadFragment : Fragment() {

    companion object {
        fun newInstance() : BarcodeReadFragment {
            return BarcodeReadFragment()
        }
    }

    private lateinit var viewModel: BarcodeReadViewModel
    private lateinit var binding: FragmentBarcodeReadBinding

    private var alert: AlertDialog? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            onSelectedFile(it, null)
        }
    }

    private val getActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            openBarcodePreview()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBarcodeReadBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.scannerCameraBtn.setOnClickListener {
            openCamera()
        }

        binding.scannerFileBtn.setOnClickListener {
            pickFile()
        }


        viewModel = ViewModelProvider(this).get(BarcodeReadViewModel::class.java)

        viewModel.foundedBarcodeModel.observe(viewLifecycleOwner) { barcodeModel ->
            dismissAlert()

            if (barcodeModel != null) {
                addBarcodeModel(barcodeModel)
            } else {
                showError()
            }
        }

        viewModel.addedBarcodeModel.observe(viewLifecycleOwner) {
            if (it.isSuccess) {
                openBarcodePreview()
            } else {
                showError()
            }
        }

        viewModel.uriNeedsPassword.observe(viewLifecycleOwner) {
            dismissAlert()

            showPasswordAlert(it)
        }

        val uriToOpen = getUriToOpen()
        uriToOpen?.let {
            onSelectedFile(it, null)
        }
    }

    private fun getUriToOpen(): Uri? {
        return (activity as? SendMainActivity)?.getSendedUri()
    }

    private fun pickFile() {
        getContent.launch("*/*")
    }

    private fun addBarcodeModel(barcodeModel: BarcodeModel) {
        lifecycleScope.async(context = Dispatchers.IO) {
            viewModel.addBarcodeModel(barcodeModel)
        }
    }

    private fun onSelectedFile(uri: Uri, password: String?) {
        showProgressAlert()

        lifecycleScope.async(context = Dispatchers.IO) {
            viewModel.startScanner(requireContext(), uri, password)
        }
    }

    private fun showError() {
        Snackbar
            .make(binding.root, getString(R.string.not_found_barcode), Snackbar.LENGTH_LONG)
            .show()
    }

    private fun openBarcodePreview() {
        startActivity(Intent(context, PreviewBarcodeActivity::class.java))
    }

    private fun openCamera() {
        val intent = Intent(context, CameraActivity::class.java)
        getActivity.launch(intent)
    }

    private fun showProgressAlert() {
        val alertBuilder = AlertDialog.Builder(requireContext())
        alertBuilder.setCancelable(false)
        alertBuilder.setTitle(R.string.alert_title)
        alertBuilder.setView(layoutInflater.inflate(R.layout.alert_progress, null))
        alertBuilder.setNegativeButton(R.string.alert_cancel) { _, _ ->
            viewModel.stopScanner()
        }
        alertBuilder.setOnCancelListener {
            viewModel.stopScanner()
        }

        alert = alertBuilder.create()
        alert?.show()
    }

    private fun dismissAlert() {
        alert?.dismiss()
    }

    private fun showPasswordAlert(uri: Uri) {
        val context = requireContext()

        val alertBuilder = AlertDialog.Builder(context)
        alertBuilder.setTitle(context.getString(R.string.alert_password_title, uri.getFilename(context)))

        val view = layoutInflater.inflate(R.layout.alert_password, null, false)

        val edtTextPass = view.findViewById<EditText>(R.id.edtTextPass)

        alertBuilder.setView(view)

        alertBuilder.setPositiveButton(R.string.alert_password_confirm_button) { _, _ ->
            edtTextPass.clearComposingText()

            val password = edtTextPass.text.toString().trim()

            onSelectedFile(uri, password)
        }

        alertBuilder.setNegativeButton(context.getString(R.string.alert_password_cancel_button)) { _, _ ->

        }

        alert = alertBuilder.create()
        alert?.show()
    }
}