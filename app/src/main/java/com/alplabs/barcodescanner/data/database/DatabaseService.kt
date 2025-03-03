package com.alplabs.barcodescanner.data.database

import android.content.Context
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.room.Room

/**
 * Created by Alfredo Lima Porfirio on 13/03/23.
 */
class DatabaseService private constructor() {

    private var _mainDatabase: MainDatabase? = null

    val mainDatabase: MainDatabase get() {
        return checkNotNull(_mainDatabase) {
            Log.e("Error", "Database wasn't initialized. Please, call ${DatabaseService::initialize.name} after.")
        }
    }

    companion object {
        private const val databaseName = "main-database.db"

        val default by lazy { DatabaseService() }
    }

    fun initialize(context: Context) {
        _mainDatabase = Room.databaseBuilder(
            context,
            MainDatabase::class.java,
            databaseName
        ).build()
    }



}