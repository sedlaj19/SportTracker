package com.sporttracker.data.auth

import kotlinx.coroutines.flow.Flow

interface AuthService {
    val currentUserId: Flow<String?>
    suspend fun signInAnonymously(): Result<String>
    suspend fun signOut()
    suspend fun getCurrentUserId(): String?
}