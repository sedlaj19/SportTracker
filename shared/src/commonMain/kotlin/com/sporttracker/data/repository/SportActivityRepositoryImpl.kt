package com.sporttracker.data.repository

import com.sporttracker.data.datasource.LocalDataSource
import com.sporttracker.data.datasource.RemoteDataSource
import com.sporttracker.domain.model.SportActivity
import com.sporttracker.domain.model.StorageType
import com.sporttracker.domain.model.SyncStatus
import com.sporttracker.domain.repository.SportActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlin.time.ExperimentalTime
import kotlin.time.Clock

class SportActivityRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val getUserId: suspend () -> String?
) : SportActivityRepository {

    override fun observeActivities(): Flow<List<SportActivity>> {
        return localDataSource.observeAll()
            .catch { emit(emptyList()) }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun saveActivity(activity: SportActivity): Result<Unit> {
        return try {
            val activityWithTimestamp = activity.copy(
                lastModified = Clock.System.now().toEpochMilliseconds()
            )

            when (activity.storageType) {
                StorageType.LOCAL -> {
                    localDataSource.insert(activityWithTimestamp)
                    Result.success(Unit)
                }
                StorageType.REMOTE -> {
                    // Save locally first with PENDING status
                    localDataSource.insert(
                        activityWithTimestamp.copy(syncStatus = SyncStatus.PENDING)
                    )

                    // Try to save remotely
                    val userId = getUserId()
                    if (userId != null) {
                        remoteDataSource.saveActivity(userId, activityWithTimestamp)
                            .fold(
                                onSuccess = {
                                    // Update local with SYNCED status
                                    localDataSource.update(
                                        activityWithTimestamp.copy(syncStatus = SyncStatus.SYNCED)
                                    )
                                    Result.success(Unit)
                                },
                                onFailure = { error ->
                                    // Keep local with PENDING status for later sync
                                    localDataSource.update(
                                        activityWithTimestamp.copy(syncStatus = SyncStatus.ERROR)
                                    )
                                    Result.failure(error)
                                }
                            )
                    } else {
                        Result.failure(Exception("User not authenticated"))
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun updateActivity(activity: SportActivity): Result<Unit> {
        return try {
            val activityWithTimestamp = activity.copy(
                lastModified = Clock.System.now().toEpochMilliseconds()
            )

            when (activity.storageType) {
                StorageType.LOCAL -> {
                    localDataSource.update(activityWithTimestamp)
                    Result.success(Unit)
                }
                StorageType.REMOTE -> {
                    // Update locally first with PENDING status
                    localDataSource.update(
                        activityWithTimestamp.copy(syncStatus = SyncStatus.PENDING)
                    )

                    // Try to update remotely
                    val userId = getUserId()
                    if (userId != null) {
                        remoteDataSource.updateActivity(userId, activityWithTimestamp)
                            .fold(
                                onSuccess = {
                                    // Update local with SYNCED status
                                    localDataSource.update(
                                        activityWithTimestamp.copy(syncStatus = SyncStatus.SYNCED)
                                    )
                                    Result.success(Unit)
                                },
                                onFailure = { error ->
                                    // Keep local with ERROR status for later sync
                                    localDataSource.update(
                                        activityWithTimestamp.copy(syncStatus = SyncStatus.ERROR)
                                    )
                                    Result.failure(error)
                                }
                            )
                    } else {
                        Result.failure(Exception("User not authenticated"))
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getActivities(): Result<List<SportActivity>> {
        return try {
            val activities = localDataSource.getAll()
            Result.success(activities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getActivityById(id: String): Result<SportActivity?> {
        return try {
            val activity = localDataSource.getById(id)
            Result.success(activity)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun deleteActivity(id: String): Result<Unit> {
        return try {
            val activity = localDataSource.getById(id)
            if (activity != null && activity.storageType == StorageType.REMOTE) {
                val userId = getUserId()
                if (userId != null) {
                    // Mark as deleted locally for sync
                    localDataSource.update(
                        activity.copy(
                            isDeleted = true,
                            syncStatus = SyncStatus.PENDING,
                            lastModified = Clock.System.now().toEpochMilliseconds()
                        )
                    )

                    // Try to delete remotely
                    remoteDataSource.deleteActivity(userId, id)
                        .fold(
                            onSuccess = {
                                // Remove from local after successful remote delete
                                localDataSource.delete(id)
                            },
                            onFailure = {
                                // Keep marked as deleted for later sync
                            }
                        )
                } else {
                    localDataSource.delete(id)
                }
            } else {
                localDataSource.delete(id)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncActivities(): Result<Unit> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not authenticated"))

            // Get all local activities that need sync
            val localActivities = localDataSource.getAll()
            val pendingActivities = localActivities.filter {
                it.syncStatus == SyncStatus.PENDING || it.syncStatus == SyncStatus.ERROR
            }

            // Sync pending activities
            pendingActivities.forEach { activity ->
                if (activity.isDeleted) {
                    remoteDataSource.deleteActivity(userId, activity.id)
                        .onSuccess { localDataSource.delete(activity.id) }
                } else {
                    remoteDataSource.saveActivity(userId, activity)
                        .onSuccess {
                            localDataSource.update(activity.copy(syncStatus = SyncStatus.SYNCED))
                        }
                }
            }

            // Fetch remote activities
            remoteDataSource.getActivities(userId)
                .onSuccess { remoteActivities ->
                    // Merge with local using last-write-wins
                    remoteActivities.forEach { remoteActivity ->
                        val localActivity = localDataSource.getById(remoteActivity.id)
                        if (localActivity == null ||
                            remoteActivity.lastModified > localActivity.lastModified) {
                            localDataSource.insert(remoteActivity.copy(syncStatus = SyncStatus.SYNCED))
                        }
                    }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}