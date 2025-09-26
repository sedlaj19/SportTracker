package com.sporttracker.data.local

import com.sporttracker.data.datasource.LocalDataSource
import com.sporttracker.data.local.database.ActivityDao
import com.sporttracker.data.local.database.toActivityEntity
import com.sporttracker.data.local.database.toDomainModel
import com.sporttracker.domain.model.SportActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalDataSourceImpl(
    private val dao: ActivityDao
) : LocalDataSource {
    override suspend fun insert(activity: SportActivity) {
        dao.insertActivity(activity.toActivityEntity())
    }

    override suspend fun update(activity: SportActivity) {
        dao.updateActivity(activity.toActivityEntity())
    }

    override suspend fun delete(id: String) {
        dao.deleteActivity(id)
    }

    override suspend fun getById(id: String): SportActivity? {
        return dao.getActivityById(id)?.toDomainModel()
    }

    override suspend fun getAll(): List<SportActivity> {
        return dao.getAllActivities().map { it.toDomainModel() }
    }

    override fun observeAll(): Flow<List<SportActivity>> {
        return dao.observeAllActivities().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun deleteAll() {
        dao.deleteAllActivities()
    }
}