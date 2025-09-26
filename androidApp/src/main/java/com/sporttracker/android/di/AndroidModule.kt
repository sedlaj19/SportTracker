package com.sporttracker.android.di

import android.content.Context
import androidx.room.Room
import com.sporttracker.android.util.AndroidNetworkMonitor
import com.sporttracker.data.datasource.LocalDataSource
import com.sporttracker.data.datasource.RemoteDataSource
import com.sporttracker.data.local.LocalDataSourceImpl
import com.sporttracker.data.local.database.SportTrackerDatabase
import com.sporttracker.data.remote.MockRemoteDataSource
import com.sporttracker.data.auth.AuthService
import com.sporttracker.data.auth.MockAuthService
import com.sporttracker.domain.util.NetworkMonitor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            SportTrackerDatabase::class.java,
            "sporttracker.db"
        ).build()
    }

    single { get<SportTrackerDatabase>().activityDao() }

    single<LocalDataSource> {
        LocalDataSourceImpl(get())
    }

    single<AuthService> {
        MockAuthService()
    }

    single<RemoteDataSource> {
        MockRemoteDataSource()
    }

    single<NetworkMonitor> {
        AndroidNetworkMonitor(androidContext())
    }
}