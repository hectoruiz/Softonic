package com.hectoruiz.data.repositories

import com.hectoruiz.data.models.ResponseApiModel
import com.hectoruiz.data.models.ResponseDetailApiModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppRepositoryImplTest {

    init {
        MockKAnnotations.init(this)
    }

    @MockK
    private lateinit var remoteDataSource: AppRemoteDataSource

    private val appRepository by lazy { AppRepositoryImpl(remoteDataSource) }

    @Test
    fun `error fetch apps`() {
        val errorMessage = "Error getting apps"
        val errorResponse = Result.failure<ResponseApiModel>(Throwable(errorMessage))
        coEvery { remoteDataSource.getApps("name") } returns errorResponse

        val result = runBlocking { appRepository.fetchApps("name") }

        assertTrue(result.isFailure)
        result.onFailure {
            assertEquals(errorMessage, it.message)
        }
    }

    @Test
    fun `success fetch apps`() {
        val successResponse = Result.success<ResponseApiModel>(mockk(relaxed = true))
        coEvery { remoteDataSource.getApps("name") } returns successResponse

        val result = runBlocking { appRepository.fetchApps("name") }

        assertTrue(result.isSuccess)
    }

    @Test
    fun `error get app`() {
        val errorMessage = "Error getting apps"
        val errorResponse = Result.failure<ResponseDetailApiModel>(Throwable(errorMessage))
        coEvery { remoteDataSource.getApp(any()) } returns errorResponse

        val result = runBlocking { appRepository.getApp("name") }

        assertTrue(result.isFailure)
        result.onFailure {
            assertEquals(errorMessage, it.message)
        }
    }

    @Test
    fun `success get app`() {
        val successResponse = Result.success<ResponseDetailApiModel>(mockk(relaxed = true))
        coEvery { remoteDataSource.getApp(any()) } returns successResponse

        val result = runBlocking { appRepository.getApp("name") }
        assertTrue(result.isSuccess)
    }
}