package com.sporttracker.presentation

import com.sporttracker.domain.model.ActivityType
import com.sporttracker.domain.model.SportActivity
import com.sporttracker.domain.model.StorageType
import com.sporttracker.domain.repository.SportActivityRepository
import com.sporttracker.domain.usecase.DeleteActivityUseCase
import com.sporttracker.domain.usecase.GetActivitiesUseCase
import com.sporttracker.presentation.model.ActivityListEvent
import com.sporttracker.presentation.model.FilterType
import com.sporttracker.presentation.viewmodel.ActivityListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@OptIn(ExperimentalCoroutinesApi::class)
class ActivityListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val mockActivities = listOf(
        SportActivity(
            id = "1",
            name = "Morning Run",
            location = "Park",
            durationMinutes = 30,
            activityType = ActivityType.RUNNING,
            storageType = StorageType.LOCAL
        ),
        SportActivity(
            id = "2",
            name = "Evening Bike",
            location = "City",
            durationMinutes = 45,
            activityType = ActivityType.CYCLING,
            storageType = StorageType.REMOTE
        ),
        SportActivity(
            id = "3",
            name = "Swimming",
            location = "Pool",
            durationMinutes = 60,
            activityType = ActivityType.SWIMMING,
            storageType = StorageType.LOCAL
        )
    )

    private lateinit var mockRepository: TestSportActivityRepository
    private lateinit var mockDeleteRepository: MockDeleteRepository
    private lateinit var mockGetActivitiesUseCase: GetActivitiesUseCase
    private lateinit var mockDeleteActivityUseCase: DeleteActivityUseCase
    private lateinit var viewModel: ActivityListViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockRepository = object : TestSportActivityRepository {
            override var shouldSyncFail = false
            override var syncCallCount = 0

            override fun observeActivities() = flowOf(mockActivities)
            override suspend fun getActivityById(id: String) = Result.success(mockActivities.find { it.id == id })
            override suspend fun getActivities() = Result.success(mockActivities)
            override suspend fun saveActivity(activity: SportActivity) = Result.success(Unit)
            override suspend fun updateActivity(activity: SportActivity) = Result.success(Unit)
            override suspend fun deleteActivity(id: String) = Result.success(Unit)

            override suspend fun syncActivities(): Result<Unit> {
                syncCallCount++
                return if (shouldSyncFail) {
                    Result.failure(Exception("Sync failed"))
                } else {
                    Result.success(Unit)
                }
            }
        }

        mockDeleteRepository = MockDeleteRepository()
        mockGetActivitiesUseCase = GetActivitiesUseCase(mockRepository)
        mockDeleteActivityUseCase = DeleteActivityUseCase(mockDeleteRepository)

        viewModel = ActivityListViewModel(
            mockGetActivitiesUseCase,
            mockDeleteActivityUseCase,
            mockRepository
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should load all activities`() = runTest(testDispatcher) {
        // Given - viewModel is initialized in setUp
        testDispatcher.scheduler.advanceUntilIdle() // Let the init block complete

        // When
        val state = viewModel.uiState.first()

        // Then
        assertEquals(3, state.activities.size)
        assertEquals(3, state.totalActivitiesCount)
        assertEquals(FilterType.ALL, state.filter)
        assertFalse(state.isLoading)
        assertFalse(state.isRefreshing)
        assertEquals(null, state.error)
    }

    @Test
    fun `setFilter LOCAL should show only local activities`() = runTest(testDispatcher) {
        // Given - wait for initial loading to complete
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.setFilter(FilterType.LOCAL)
        testDispatcher.scheduler.advanceUntilIdle() // Let the filter take effect

        // Then
        val state = viewModel.uiState.first()
        assertEquals(2, state.activities.size) // 2 local activities
        assertEquals(3, state.totalActivitiesCount) // total remains same
        assertEquals(FilterType.LOCAL, state.filter)
        assertTrue(state.activities.all { it.storageType == StorageType.LOCAL })
    }

    @Test
    fun `setFilter REMOTE should show only remote activities`() = runTest(testDispatcher) {
        // Given - wait for initial loading to complete
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.setFilter(FilterType.REMOTE)
        testDispatcher.scheduler.advanceUntilIdle() // Let the filter take effect

        // Then
        val state = viewModel.uiState.first()
        assertEquals(1, state.activities.size) // 1 remote activity
        assertEquals(3, state.totalActivitiesCount) // total remains same
        assertEquals(FilterType.REMOTE, state.filter)
        assertTrue(state.activities.all { it.storageType == StorageType.REMOTE })
    }

    @Test
    fun `setFilter ALL should show all activities`() = runTest(testDispatcher) {
        // Given - wait for initial loading then set LOCAL filter
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.setFilter(FilterType.LOCAL)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.setFilter(FilterType.ALL)
        testDispatcher.scheduler.advanceUntilIdle() // Let the filter take effect

        // Then
        val state = viewModel.uiState.first()
        assertEquals(3, state.activities.size)
        assertEquals(3, state.totalActivitiesCount)
        assertEquals(FilterType.ALL, state.filter)
    }

    @Test
    fun `deleteActivity should emit success event when successful`() = runTest(testDispatcher) {
        // Given
        val activity = mockActivities[0]

        // When
        viewModel.events.test {
            viewModel.deleteActivity(activity)

            // Then
            val event = awaitItem()
            assertTrue(event is ActivityListEvent.ShowSuccess)
            assertEquals("Aktivita smazána", event.message)
        }
    }

    @Test
    fun `deleteActivity should emit error event when fails`() = runTest(testDispatcher) {
        // Given
        val activity = mockActivities[0]
        mockDeleteRepository.shouldFail = true

        // When
        viewModel.events.test {
            viewModel.deleteActivity(activity)

            // Then
            val event = awaitItem()
            assertTrue(event is ActivityListEvent.ShowError)
            assertEquals("Delete failed", event.message)
        }
    }

    @Test
    fun `refresh should sync activities successfully`() = runTest(testDispatcher) {
        // Given

        // When
        viewModel.events.test {
            viewModel.refresh()

            // Then
            val event = awaitItem()
            assertTrue(event is ActivityListEvent.ShowSuccess)
            assertEquals("Synchronizováno", event.message)
            assertEquals(1, mockRepository.syncCallCount)
        }
    }

    @Test
    fun `refresh should emit error event when sync fails`() = runTest(testDispatcher) {
        // Given
        mockRepository.shouldSyncFail = true

        // When
        viewModel.events.test {
            viewModel.refresh()

            // Then
            val event = awaitItem()
            assertTrue(event is ActivityListEvent.ShowError)
            assertEquals("Sync failed", event.message)
        }
    }

    @Test
    fun `refresh should set and clear refreshing state`() = runTest(testDispatcher) {
        // Given - wait for initial loading to complete
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(1) // Skip current state

            // When
            viewModel.refresh()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val refreshingState = awaitItem()
            assertTrue(refreshingState.isRefreshing)

            val completedState = awaitItem()
            assertFalse(completedState.isRefreshing)
        }
    }

    @Test
    fun `onAddClick should emit NavigateToAdd event`() = runTest(testDispatcher) {
        // When
        viewModel.events.test {
            viewModel.onAddClick()

            // Then
            val event = awaitItem()
            assertTrue(event is ActivityListEvent.NavigateToAdd)
        }
    }

    @Test
    fun `onEditClick should emit NavigateToEdit event with correct id`() = runTest(testDispatcher) {
        // Given
        val activityId = "test-id"

        // When
        viewModel.events.test {
            viewModel.onEditClick(activityId)

            // Then
            val event = awaitItem()
            assertTrue(event is ActivityListEvent.NavigateToEdit)
            assertEquals(activityId, event.activityId)
        }
    }

    // Mock classes for testing
    private class MockDeleteRepository : SportActivityRepository {
        var shouldFail = false
        var deletedId: String? = null

        override fun observeActivities() = flowOf(emptyList<SportActivity>())
        override suspend fun getActivityById(id: String) = Result.success(null)
        override suspend fun getActivities() = Result.success(emptyList<SportActivity>())
        override suspend fun saveActivity(activity: SportActivity) = Result.success(Unit)
        override suspend fun updateActivity(activity: SportActivity) = Result.success(Unit)
        override suspend fun syncActivities() = Result.success(Unit)

        override suspend fun deleteActivity(id: String): Result<Unit> {
            deletedId = id
            return if (shouldFail) {
                Result.failure(Exception("Delete failed"))
            } else {
                Result.success(Unit)
            }
        }
    }

    private interface TestSportActivityRepository : SportActivityRepository {
        var shouldSyncFail: Boolean
        var syncCallCount: Int
    }
}