package com.hector.domain

import com.hectoruiz.domain.commons.Constants.PARSING_ERROR
import com.hectoruiz.domain.models.AppDetailModel
import com.hectoruiz.domain.models.Reliability
import com.hectoruiz.domain.repositories.AppRepository
import com.hectoruiz.domain.usecases.GetAppUseCase
import com.hectoruiz.domain.usecases.transformDate
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetAppUseCaseTest {

    init {
        MockKAnnotations.init(this)
    }

    @MockK
    private lateinit var appRepository: AppRepository

    @MockK
    private lateinit var appDetailModel: AppDetailModel

    private val getAppUseCase by lazy { GetAppUseCase(appRepository) }

    @Test
    fun `error get app`() {
        val errorMessage = "App not found"
        val errorResponse = Result.failure<AppDetailModel>(Throwable(errorMessage))
        coEvery { appRepository.getApp(any()) } returns errorResponse


        val result = runBlocking { getAppUseCase.getApp("123") }
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }

    @Test
    fun `success get app`() {
        val registeredDate = "2023-10-01 06:00:00"
        val successResponse = Result.success(
            AppDetailModel(
                name = "CallApp: Caller ID, Call Blocker & Recording Calls",
                icon = "icon",
                updated =  registeredDate,
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
        coEvery { appRepository.getApp(any()) } returns successResponse

        val result = runBlocking { getAppUseCase.getApp("123") }

        assertTrue(result.isSuccess)
        result.onSuccess {
            assertEquals(registeredDate.transformDate(), it.updated)
        }
    }

    @Test
    fun `success get app with invalid date`() {
        val registeredDate = "05.54.2004"
        every { appDetailModel.updated } returns registeredDate
        val successResponse = Result.success(appDetailModel)
        coEvery { appRepository.getApp(any()) } returns successResponse

        val result = runBlocking { getAppUseCase.getApp("123") }

        assertTrue(result.isFailure)
        assertEquals(PARSING_ERROR, result.exceptionOrNull()?.message)
    }
}