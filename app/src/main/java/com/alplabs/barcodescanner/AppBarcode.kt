package com.alplabs.barcodescanner

import android.app.Application
import com.alplabs.barcodescanner.data.database.DatabaseService

/**
 * Created by Alfredo Lima Porfirio on 13/03/23.
 */
class AppBarcode: Application() {

    override fun onCreate() {
        super.onCreate()
        DatabaseService.default.initialize(context = applicationContext)
    }

}