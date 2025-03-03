package com.alplabs.barcodescanner.ui.main

import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alplabs.barcodescanner.ui.read.BarcodeReadFragment
import com.alplabs.barcodescanner.ui.history.HistoryFragment

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> BarcodeReadFragment.newInstance()
            else -> HistoryFragment.newInstance(isLastBarcodeOnly = false)
        }
    }
}