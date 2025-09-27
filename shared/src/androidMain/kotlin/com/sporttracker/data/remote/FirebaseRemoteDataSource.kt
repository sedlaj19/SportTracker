package com.sporttracker.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.sporttracker.data.datasource.RemoteDataSource
import com.sporttracker.domain.model.SportActivity
import kotlinx.coroutines.tasks.await

class FirebaseRemoteDataSource : RemoteDataSource {
    private val firestore = FirebaseFirestore.getInstance()
    private val activitiesCollection = firestore.collection("activities")

    override suspend fun getActivities(userId: String): Result<List<SportActivity>> {
        return try {
            val querySnapshot = activitiesCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val activities = querySnapshot.documents.mapNotNull { document ->
                document.toObject<SportActivity>()?.copy(id = document.id)
            }

            Result.success(activities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveActivity(userId: String, activity: SportActivity): Result<Unit> {
        return try {
            val activityData = activity.copy(userId = userId)

            if (activity.id.isNotEmpty()) {
                // Update existing activity
                activitiesCollection
                    .document(activity.id)
                    .set(activityData)
                    .await()
            } else {
                // Create new activity
                activitiesCollection
                    .add(activityData)
                    .await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateActivity(userId: String, activity: SportActivity): Result<Unit> {
        return try {
            val activityData = activity.copy(userId = userId)

            activitiesCollection
                .document(activity.id)
                .set(activityData)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteActivity(userId: String, activityId: String): Result<Unit> {
        return try {
            activitiesCollection
                .document(activityId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncActivities(userId: String, activities: List<SportActivity>): Result<Unit> {
        return try {
            val batch = firestore.batch()

            activities.forEach { activity ->
                val activityData = activity.copy(userId = userId)
                val docRef = if (activity.id.isNotEmpty()) {
                    activitiesCollection.document(activity.id)
                } else {
                    activitiesCollection.document()
                }
                batch.set(docRef, activityData)
            }

            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}