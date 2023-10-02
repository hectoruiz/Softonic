package com.hector.domain

import com.hectoruiz.domain.models.AppModel
import com.hectoruiz.domain.repositories.AppRepository
import com.hectoruiz.domain.usecases.FetchAppsUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FetchAppsUseCaseTest {

    init {
        MockKAnnotations.init(this)
    }

    @RelaxedMockK
    private lateinit var appRepository: AppRepository

    private val fetchAppsUseCase by lazy { FetchAppsUseCase(appRepository) }

    @Test
    fun `error fetching apps from remote`() {
        val errorMessage = "Error!!"
        val errorResponse = Result.failure<List<AppModel>>(Throwable(errorMessage))
        coEvery { appRepository.fetchApps("name") } returns errorResponse

        val result = runBlocking { fetchAppsUseCase.fetchApps("name") }
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }

    @Test
    fun `success fetching apps from remote`() {
        val appList = listOf<AppModel>(mockk(), mockk())
        val successResponse = Result.success(appList)
        coEvery { appRepository.fetchApps("name") } returns successResponse

        val result = runBlocking { fetchAppsUseCase.fetchApps("name") }
        assertTrue(result.isSuccess)
        assertEquals(successResponse, result)
        result.onSuccess {
            assertEquals(appList.size, it.size)
            assertEquals(appList, it)
        }
    }
}