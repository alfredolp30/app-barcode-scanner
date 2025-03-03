package com.alplabs.barcodescanner.ui.preview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.alplabs.barcodescanner.R
import com.alplabs.barcodescanner.databinding.ActivityBarcodePreviewBinding
import com.alplabs.barcodescanner.ui.history.HistoryFragment

class PreviewBarcodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBarcodePreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBarcodePreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragment = HistoryFragment.newInstance(true)

        supportFragmentManager.commit {
            add(binding.frame.id, fragment)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // close this activity as oppose to navigating up
        return false
    }
}