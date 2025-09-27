package com.sporttracker.android

import android.app.Application
import com.google.firebase.FirebaseApp
import com.sporttracker.android.di.androidModule
import com.sporttracker.di.appModule
import com.sporttracker.data.auth.AuthService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SportTrackerApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val authService: AuthService by inject()

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        startKoin {
            androidLogger()
            androidContext(this@SportTrackerApplication)
            modules(appModule, androidModule)
        }

        // Sign in anonymously when app starts
        applicationScope.launch {
            try {
                authService.signInAnonymously()
            } catch (e: Exception) {
                // Log error but don't crash the app
                android.util.Log.w("SportTracker", "Failed to sign in anonymously", e)
            }
        }
    }
}