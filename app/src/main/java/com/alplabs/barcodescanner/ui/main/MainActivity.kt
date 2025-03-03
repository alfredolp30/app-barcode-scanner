package com.alplabs.barcodescanner.ui.main

import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.alplabs.barcodescanner.R
import com.alplabs.barcodescanner.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

open class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val sectionsPagerAdapter by lazy {
        SectionsPagerAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = sectionsPagerAdapter

        val tabTitles = resources.getStringArray(R.array.tab_titles)

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

}