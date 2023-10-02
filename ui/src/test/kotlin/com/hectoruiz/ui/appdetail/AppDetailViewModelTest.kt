package com.hectoruiz.ui.appdetail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.hectoruiz.domain.commons.Constants.PARAM_NAME
import com.hectoruiz.domain.commons.ErrorState
import com.hectoruiz.domain.models.AppDetailModel
import com.hectoruiz.domain.models.Reliability
import com.hectoruiz.domain.usecases.GetAppUseCase
import com.hectoruiz.ui.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AppDetailViewModelTest {

    init {
        MockKAnnotations.init(this)
    }

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var savedStateHandle: SavedStateHandle

    @MockK
    private lateinit var getAppUseCase: GetAppUseCase

    private lateinit var appDetailViewModel: AppDetailViewModel

    @Before
    fun setUp() {
        every { savedStateHandle.get<String>(PARAM_NAME) } returns "packageName"
    }

    @Test
    fun `coroutine exception handled`() {
        runTest {
            coEvery { getAppUseCase.getApp("packageName") } returns Result.failure(Throwable())

            val errorJob = launch {
                appDetailViewModel.loading.test {
                    assertTrue(awaitItem())
                    cancelAndConsumeRemainingEvents()
                }
                appDetailViewModel.appDetail.test {
                    assertFalse(awaitItem().hasInitialInformation())
                    cancelAndConsumeRemainingEvents()
                }
                appDetailViewModel.error.test {
                    val errorState = awaitItem()
                    assertTrue(errorState is ErrorState.NetworkError)
                    cancelAndConsumeRemainingEvents()
                }
            }
            appDetailViewModel = AppDetailViewModel(savedStateHandle, getAppUseCase)

            errorJob.join()
            errorJob.cancel()

            assertFalse(appDetailViewModel.loading.value)
        }
    }

    @Test
    fun `error retrieving specific app`() {
        runTest {
            val errorMessage = "Network message"
            coEvery { getAppUseCase.getApp(any()) } returns Result.failure(
                Throwable(
                    errorMessage
                )
            )

            val errorJob = launch {
                appDetailViewModel.loading.test {
                    assertTrue(awaitItem())
                    cancelAndConsumeRemainingEvents()
                }
                appDetailViewModel.appDetail.test {
                    assertFalse(awaitItem().hasInitialInformation())
                    cancelAndConsumeRemainingEvents()
                }
                appDetailViewModel.error.test {
                    val errorState = awaitItem()
                    assertTrue(errorState is ErrorState.NetworkError)
                    assertEquals(
                        errorMessage,
                        (errorState as ErrorState.NetworkError).message
                    )
                    cancelAndConsumeRemainingEvents()
                }
            }
            appDetailViewModel = AppDetailViewModel(savedStateHandle, getAppUseCase)

            errorJob.join()
            errorJob.cancel()

            assertFalse(appDetailViewModel.loading.value)
        }
    }

    @Test
    fun `success retrieving specific app`() {
        runTest {
            coEvery { getAppUseCase.getApp(any()) } returns Result.success(
                AppDetailModel(
                    name = "CallApp: Caller ID, Call Blocker & Recording Calls",
                    icon = "icon",
                    updated = "2005-04-07",
                    version = "2.3.0",
                    pegi = "PEGI-3",
                    size = 1291,
                    description = "description",
                    reliability = Reliability.TRUSTED,
                    developerName = "Bryan Reynolds",
                    developerEmail = "bryan.reynolds@callapp.com",
                    keywords = "#telegram #messenger #communication",
                    path = "path"
                )
            )

            val appsJob = launch {
                appDetailViewModel.loading.test {
                    assertTrue(awaitItem())
                    cancelAndConsumeRemainingEvents()
                }
                appDetailViewModel.appDetail.test {
                    val initialAppDetailValue = awaitItem()
                    assertFalse(initialAppDetailValue.hasInitialInformation())

                    val appDetailValue = awaitItem()
                    assertTrue(appDetailValue.hasInitialInformation())
                    cancelAndConsumeRemainingEvents()
                }
            }
            appDetailViewModel = AppDetailViewModel(savedStateHandle, getAppUseCase)

            appsJob.join()
            appsJob.cancel()

            assertFalse(appDetailViewModel.loading.value)
        }
    }
}