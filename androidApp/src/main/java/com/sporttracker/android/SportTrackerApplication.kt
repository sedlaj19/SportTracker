package com.sporttracker.android

import android.app.Application
import com.sporttracker.android.di.androidModule
import com.sporttracker.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SportTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@SportTrackerApplication)
            modules(appModule, androidModule)
        }
    }
}