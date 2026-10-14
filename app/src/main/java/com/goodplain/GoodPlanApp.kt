package com.goodplan.app

import android.app.Application
import androidx.room.Room
import com.goodplan.app.data.AppDatabase
import com.goodplan.app.repository.AppRepository

class GoodPlanApp : Application() {
    lateinit var database: AppDatabase
    lateinit var repository: AppRepository

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "goodplan.db"
        ).fallbackToDestructiveMigration().build()
        repository = AppRepository(database)
    }
}