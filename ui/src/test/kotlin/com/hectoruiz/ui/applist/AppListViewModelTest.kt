package com.hectoruiz.ui.applist

import app.cash.turbine.test
import com.hectoruiz.domain.commons.ErrorState
import com.hectoruiz.domain.usecases.FetchAppsUseCase
import com.hectoruiz.ui.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AppListViewModelTest {

    init {
        MockKAnnotations.init(this)
    }

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var fetchAppsUseCase: FetchAppsUseCase

    private lateinit var appListViewModel: AppListViewModel

    @Before
    fun setUp() {
        appListViewModel = AppListViewModel(fetchAppsUseCase)
    }

    @Test
    fun `coroutine exception handled`() {
        runTest {
            coEvery { fetchAppsUseCase.fetchApps("name") } returns Result.failure(Throwable())

            assertFalse(appListViewModel.loading.value)

            val errorJob = launch {
                appListViewModel.error.test {
                    val errorState = awaitItem()
                    assertTrue(errorState is ErrorState.NetworkError)
                    cancelAndConsumeRemainingEvents()
                }
            }
            appListViewModel.searchApps("name")

            errorJob.join()
            errorJob.cancel()

            assertFalse(appListViewModel.loading.value)
            assertTrue(appListViewModel.apps.value.isEmpty())
        }
    }

    @Test
    fun `error retrieving apps`() {
        runTest {
            val errorMessage = "Network message"
            coEvery { fetchAppsUseCase.fetchApps(any()) } returns Result.failure(
                Throwable(
                    errorMessage
                )
            )

            assertFalse(appListViewModel.loading.value)

            val errorJob = launch {
                appListViewModel.error.test {
                    val errorState = awaitItem()
                    assertTrue(errorState is ErrorState.NetworkError)
                    assertEquals(errorMessage, (errorState as ErrorState.NetworkError).message)
                    cancelAndConsumeRemainingEvents()
                }
            }
            appListViewModel.searchApps("name")

            errorJob.join()
            errorJob.cancel()

            assertFalse(appListViewModel.loading.value)
        }
    }

    @Test
    fun `success retrieving apps`() {
        runTest {
            coEvery { fetchAppsUseCase.fetchApps(any()) } returns Result.success(
                listOf(
                    mockk(),
                    mockk(),
                    mockk()
                )
            )

            assertFalse(appListViewModel.loading.value)

            val appsJob = launch {
                appListViewModel.apps.test {
                    val emptyApps = awaitItem()
                    assertTrue(emptyApps.isEmpty())

                    val apps = awaitItem()
                    assertTrue(apps.isNotEmpty())
                    assertTrue(apps.size == 3)
                    cancelAndConsumeRemainingEvents()
                }
            }
            appListViewModel.searchApps("name")

            appsJob.join()
            appsJob.cancel()

            assertFalse(appListViewModel.loading.value)
        }
    }
}