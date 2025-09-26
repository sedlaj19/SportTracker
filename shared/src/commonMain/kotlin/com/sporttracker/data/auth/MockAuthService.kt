package com.sporttracker.data.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class MockAuthService : AuthService {
    private val _currentUserId = MutableStateFlow<String?>("mock_user_123")

    override val currentUserId: Flow<String?> = _currentUserId

    override suspend fun signInAnonymously(): Result<String> {
        val userId = "mock_user_123"
        _currentUserId.value = userId
        return Result.success(userId)
    }

    override suspend fun signOut() {
        _currentUserId.value = null
    }

    override suspend fun getCurrentUserId(): String? {
        return _currentUserId.value ?: run {
            signInAnonymously().getOrNull()
        }
    }
}