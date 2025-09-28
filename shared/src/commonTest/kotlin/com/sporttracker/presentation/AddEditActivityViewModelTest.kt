package com.sporttracker.presentation

import com.sporttracker.domain.model.ActivityType
import com.sporttracker.domain.model.SportActivity
import com.sporttracker.domain.model.StorageType
import com.sporttracker.domain.repository.SportActivityRepository
import com.sporttracker.domain.usecase.DeleteActivityUseCase
import com.sporttracker.domain.usecase.SaveActivityUseCase
import com.sporttracker.domain.usecase.UpdateActivityUseCase
import com.sporttracker.presentation.model.AddEditEvent
import com.sporttracker.presentation.viewmodel.AddEditActivityViewModel
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
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditActivityViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val sampleActivity = SportActivity(
        id = "test-id",
        name = "Morning Run",
        location = "Park",
        durationMinutes = 30,
        activityType = ActivityType.RUNNING,
        storageType = StorageType.LOCAL
    )

    private lateinit var mockRepository: TestRepository
    private lateinit var mockSaveRepository: MockSaveRepository
    private lateinit var mockUpdateRepository: MockUpdateRepository
    private lateinit var mockDeleteRepository: MockDeleteRepository
    private lateinit var mockSaveActivityUseCase: SaveActivityUseCase
    private lateinit var mockUpdateActivityUseCase: UpdateActivityUseCase
    private lateinit var mockDeleteActivityUseCase: DeleteActivityUseCase
    private lateinit var viewModel: AddEditActivityViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockRepository = object : TestRepository {
            override var shouldFail = false
            override var returnedActivity: SportActivity? = sampleActivity

            override fun observeActivities() = flowOf(emptyList<SportActivity>())
            override suspend fun getActivities() = Result.success(emptyList<SportActivity>())
            override suspend fun saveActivity(activity: SportActivity) = Result.success(Unit)
            override suspend fun updateActivity(activity: SportActivity) = Result.success(Unit)
            override suspend fun deleteActivity(id: String) = Result.success(Unit)
            override suspend fun syncActivities() = Result.success(Unit)

            override suspend fun getActivityById(id: String): Result<SportActivity?> {
                return if (shouldFail) {
                    Result.failure(Exception("Activity not found"))
                } else {
                    Result.success(returnedActivity?.takeIf { it.id == id })
                }
            }
        }

        mockSaveRepository = MockSaveRepository()
        mockUpdateRepository = MockUpdateRepository()
        mockDeleteRepository = MockDeleteRepository()

        mockSaveActivityUseCase = SaveActivityUseCase(mockSaveRepository)
        mockUpdateActivityUseCase = UpdateActivityUseCase(mockUpdateRepository)
        mockDeleteActivityUseCase = DeleteActivityUseCase(mockDeleteRepository)

        viewModel = AddEditActivityViewModel(
            mockSaveActivityUseCase,
            mockUpdateActivityUseCase,
            mockDeleteActivityUseCase,
            mockRepository
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be empty for new activity`() = runTest(testDispatcher) {
        // When
        val state = viewModel.uiState.first()

        // Then
        assertNull(state.id)
        assertEquals("", state.name)
        assertEquals("", state.location)
        assertEquals(0, state.durationMinutes)
        assertEquals(ActivityType.OTHER, state.selectedActivityType)
        assertEquals(StorageType.LOCAL, state.selectedStorage)
        assertFalse(state.isEditMode)
        assertFalse(state.isLoading)
        assertTrue(state.validationErrors.isEmpty())
    }

    @Test
    fun `setActivityId should load activity for editing`() = runTest(testDispatcher) {
        // When
        viewModel.setActivityId("test-id")
        testDispatcher.scheduler.advanceUntilIdle() // Wait for loading to complete

        // Then
        val state = viewModel.uiState.first()
        assertEquals("test-id", state.id)
        assertEquals("Morning Run", state.name)
        assertEquals("Park", state.location)
        assertEquals(30, state.durationMinutes)
        assertEquals(ActivityType.RUNNING, state.selectedActivityType)
        assertEquals(StorageType.LOCAL, state.selectedStorage)
        assertTrue(state.isEditMode)
        assertFalse(state.isLoading)
    }

    @Test
    fun `setActivityId with null should not load activity`() = runTest(testDispatcher) {
        // When
        viewModel.setActivityId(null)

        // Then
        val state = viewModel.uiState.first()
        assertFalse(state.isEditMode)
        assertNull(state.id)
    }

    @Test
    fun `setActivityId should handle loading errors`() = runTest(testDispatcher) {
        // Given
        mockRepository.shouldFail = true

        // When
        viewModel.events.test {
            viewModel.setActivityId("test-id")

            // Then
            val event = awaitItem()
            assertTrue(event is AddEditEvent.ShowError)
            assertEquals("Activity not found", event.message)
        }
    }

    @Test
    fun `updateName should update name and clear validation errors`() = runTest(testDispatcher) {
        // Given - set validation error first by trying to save invalid form
        viewModel.saveActivity() // This will create validation errors
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.updateName("New Name")

        // Then
        val state = viewModel.uiState.first()
        assertEquals("New Name", state.name)
        assertFalse(state.validationErrors.containsKey("name"))
    }

    @Test
    fun `updateLocation should update location and clear validation errors`() = runTest(testDispatcher) {
        // Given - set validation error first by trying to save invalid form
        viewModel.saveActivity()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.updateLocation("New Location")

        // Then
        val state = viewModel.uiState.first()
        assertEquals("New Location", state.location)
        assertFalse(state.validationErrors.containsKey("location"))
    }

    @Test
    fun `updateDuration should update duration and clear validation errors`() = runTest(testDispatcher) {
        // Given - set validation error first by trying to save invalid form
        viewModel.saveActivity()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.updateDuration(45)

        // Then
        val state = viewModel.uiState.first()
        assertEquals(45, state.durationMinutes)
        assertFalse(state.validationErrors.containsKey("duration"))
    }

    @Test
    fun `updateActivityType should update activity type`() = runTest(testDispatcher) {
        // When
        viewModel.uiState.test {
            skipItems(1) // Skip initial state

            viewModel.updateActivityType(ActivityType.CYCLING)

            // Then
            val state = awaitItem()
            assertEquals(ActivityType.CYCLING, state.selectedActivityType)
        }
    }

    @Test
    fun `updateStorageType should update storage type`() = runTest(testDispatcher) {
        // When
        viewModel.uiState.test {
            skipItems(1) // Skip initial state

            viewModel.updateStorageType(StorageType.REMOTE)

            // Then
            val state = awaitItem()
            assertEquals(StorageType.REMOTE, state.selectedStorage)
        }
    }

    @Test
    fun `saveActivity should validate empty name`() = runTest(testDispatcher) {
        // When
        viewModel.saveActivity()

        // Then
        val state = viewModel.uiState.first()
        assertTrue(state.validationErrors.containsKey("name"))
        assertEquals("Název je povinný", state.validationErrors["name"])
    }

    @Test
    fun `saveActivity should validate empty location`() = runTest(testDispatcher) {
        // Given
        viewModel.updateName("Valid Name")

        // When
        viewModel.saveActivity()

        // Then
        val state = viewModel.uiState.first()
        assertTrue(state.validationErrors.containsKey("location"))
        assertEquals("Místo je povinné", state.validationErrors["location"])
    }

    @Test
    fun `saveActivity should validate invalid duration`() = runTest(testDispatcher) {
        // Given
        viewModel.updateName("Valid Name")
        viewModel.updateLocation("Valid Location")

        // When
        viewModel.saveActivity()

        // Then
        val state = viewModel.uiState.first()
        assertTrue(state.validationErrors.containsKey("duration"))
        assertEquals("Délka musí být větší než 0", state.validationErrors["duration"])
    }

    @Test
    fun `saveActivity should create new activity successfully`() = runTest(testDispatcher) {
        // Given
        viewModel.updateName("New Activity")
        viewModel.updateLocation("New Location")
        viewModel.updateDuration(60)
        viewModel.updateActivityType(ActivityType.CYCLING)
        viewModel.updateStorageType(StorageType.REMOTE)

        // When
        viewModel.events.test {
            viewModel.saveActivity()

            // Then
            val successEvent = awaitItem()
            assertTrue(successEvent is AddEditEvent.ShowSuccess)
            assertEquals("Aktivita uložena", successEvent.message)

            val navigationEvent = awaitItem()
            assertTrue(navigationEvent is AddEditEvent.NavigateBack)

            // Verify the activity was saved
            val savedActivity = mockSaveRepository.savedActivity!!
            assertEquals("New Activity", savedActivity.name)
            assertEquals("New Location", savedActivity.location)
            assertEquals(60, savedActivity.durationMinutes)
            assertEquals(ActivityType.CYCLING, savedActivity.activityType)
            assertEquals(StorageType.REMOTE, savedActivity.storageType)
        }
    }

    @Test
    fun `saveActivity should update existing activity successfully`() = runTest(testDispatcher) {
        // Given - set up edit mode
        viewModel.setActivityId("test-id")
        testDispatcher.scheduler.advanceUntilIdle() // Wait for loading

        viewModel.updateName("Updated Activity")
        viewModel.updateLocation("Updated Location")
        viewModel.updateDuration(90)

        // When
        viewModel.events.test {
            viewModel.saveActivity()

            // Then
            val successEvent = awaitItem()
            assertTrue(successEvent is AddEditEvent.ShowSuccess)
            assertEquals("Aktivita upravena", successEvent.message)

            val navigationEvent = awaitItem()
            assertTrue(navigationEvent is AddEditEvent.NavigateBack)

            // Verify the activity was updated
            val updatedActivity = mockUpdateRepository.updatedActivity!!
            assertEquals("test-id", updatedActivity.id)
            assertEquals("Updated Activity", updatedActivity.name)
            assertEquals("Updated Location", updatedActivity.location)
            assertEquals(90, updatedActivity.durationMinutes)
        }
    }

    @Test
    fun `saveActivity should handle save errors`() = runTest(testDispatcher) {
        // Given
        viewModel.updateName("Valid Name")
        viewModel.updateLocation("Valid Location")
        viewModel.updateDuration(30)
        mockSaveRepository.shouldFail = true

        // When
        viewModel.events.test {
            viewModel.saveActivity()

            // Then
            val event = awaitItem()
            assertTrue(event is AddEditEvent.ShowError)
            assertEquals("Save failed", event.message)
        }
    }

    @Test
    fun `saveActivity should handle update errors`() = runTest(testDispatcher) {
        // Given - set up edit mode
        viewModel.setActivityId("test-id")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateName("Updated Name")
        viewModel.updateLocation("Updated Location")
        viewModel.updateDuration(30)
        mockUpdateRepository.shouldFail = true

        // When
        viewModel.events.test {
            viewModel.saveActivity()

            // Then
            val event = awaitItem()
            assertTrue(event is AddEditEvent.ShowError)
            assertEquals("Update failed", event.message)
        }
    }

    @Test
    fun `deleteActivity should fail when not in edit mode`() = runTest(testDispatcher) {
        // When - try to delete in create mode
        viewModel.deleteActivity()

        // Then - nothing should happen (no events or state changes)
        assertEquals(null, mockDeleteRepository.deletedId)
    }

    @Test
    fun `deleteActivity should delete activity successfully`() = runTest(testDispatcher) {
        // Given - set up edit mode
        viewModel.setActivityId("test-id")
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.events.test {
            viewModel.deleteActivity()

            // Then
            val successEvent = awaitItem()
            assertTrue(successEvent is AddEditEvent.ShowSuccess)
            assertEquals("Aktivita smazána", successEvent.message)

            val navigationEvent = awaitItem()
            assertTrue(navigationEvent is AddEditEvent.NavigateBack)

            // Verify the activity was deleted
            assertEquals("test-id", mockDeleteRepository.deletedId)
        }
    }

    @Test
    fun `deleteActivity should handle delete errors`() = runTest(testDispatcher) {
        // Given - set up edit mode and error condition
        viewModel.setActivityId("test-id")
        testDispatcher.scheduler.advanceUntilIdle()
        mockDeleteRepository.shouldFail = true

        // When
        viewModel.events.test {
            viewModel.deleteActivity()

            // Then
            val event = awaitItem()
            assertTrue(event is AddEditEvent.ShowError)
            assertEquals("Delete failed", event.message)
        }
    }

    // Mock classes for testing
    private interface TestRepository : SportActivityRepository {
        var shouldFail: Boolean
        var returnedActivity: SportActivity?
    }

    private class MockSaveRepository : SportActivityRepository {
        var shouldFail = false
        var savedActivity: SportActivity? = null

        override fun observeActivities() = flowOf(emptyList<SportActivity>())
        override suspend fun getActivityById(id: String) = Result.success(null)
        override suspend fun getActivities() = Result.success(emptyList<SportActivity>())
        override suspend fun updateActivity(activity: SportActivity) = Result.success(Unit)
        override suspend fun deleteActivity(id: String) = Result.success(Unit)
        override suspend fun syncActivities() = Result.success(Unit)

        override suspend fun saveActivity(activity: SportActivity): Result<Unit> {
            savedActivity = activity
            return if (shouldFail) {
                Result.failure(Exception("Save failed"))
            } else {
                Result.success(Unit)
            }
        }
    }

    private class MockUpdateRepository : SportActivityRepository {
        var shouldFail = false
        var updatedActivity: SportActivity? = null

        override fun observeActivities() = flowOf(emptyList<SportActivity>())
        override suspend fun getActivityById(id: String) = Result.success(null)
        override suspend fun getActivities() = Result.success(emptyList<SportActivity>())
        override suspend fun saveActivity(activity: SportActivity) = Result.success(Unit)
        override suspend fun deleteActivity(id: String) = Result.success(Unit)
        override suspend fun syncActivities() = Result.success(Unit)

        override suspend fun updateActivity(activity: SportActivity): Result<Unit> {
            updatedActivity = activity
            return if (shouldFail) {
                Result.failure(Exception("Update failed"))
            } else {
                Result.success(Unit)
            }
        }
    }

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
}