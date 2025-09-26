package com.sporttracker.data.datasource

import com.sporttracker.domain.model.SportActivity
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun observeAll(): Flow<List<SportActivity>>
    suspend fun getAll(): List<SportActivity>
    suspend fun getById(id: String): SportActivity?
    suspend fun insert(activity: SportActivity)
    suspend fun update(activity: SportActivity)
    suspend fun delete(id: String)
    suspend fun deleteAll()
}