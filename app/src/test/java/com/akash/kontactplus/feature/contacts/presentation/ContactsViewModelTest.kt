package com.akash.kontactplus.feature.contacts.presentation

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ContactsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: ContactsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ContactsViewModel(SavedStateHandle())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Checking`() {
        assertEquals(ContactsPermissionState.Checking, viewModel.uiState.value.permissionState)
    }

    @Test
    fun `onPermissionStatusChecked with granted=true sets state to Granted`() {
        viewModel.onPermissionStatusChecked(isGranted = true, shouldShowRationale = false)
        assertEquals(ContactsPermissionState.Granted, viewModel.uiState.value.permissionState)
    }

    @Test
    fun `onPermissionStatusChecked with granted=false and never requested sets state to NotRequested`() {
        viewModel.onPermissionStatusChecked(isGranted = false, shouldShowRationale = false)
        assertEquals(ContactsPermissionState.NotRequested, viewModel.uiState.value.permissionState)
    }

    @Test
    fun `onPermissionResultReceived with granted=false and rationale sets state to Denied`() {
        viewModel.onPermissionResultReceived(isGranted = false, shouldShowRationale = true)
        assertEquals(ContactsPermissionState.Denied, viewModel.uiState.value.permissionState)
    }

    @Test
    fun `onPermissionResultReceived with granted=false and no rationale sets state to PermanentlyDenied`() {
        viewModel.onPermissionResultReceived(isGranted = false, shouldShowRationale = false)
        assertEquals(ContactsPermissionState.PermanentlyDenied, viewModel.uiState.value.permissionState)
    }

    @Test
    fun `onPermissionStatusChecked after denial sets state to Denied`() {
        viewModel.onPermissionRequestStarted()
        viewModel.onPermissionStatusChecked(isGranted = false, shouldShowRationale = true)
        assertEquals(ContactsPermissionState.Denied, viewModel.uiState.value.permissionState)
    }
}
