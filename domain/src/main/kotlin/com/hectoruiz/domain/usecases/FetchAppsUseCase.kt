package com.hectoruiz.domain.usecases

import com.hectoruiz.domain.models.AppModel
import com.hectoruiz.domain.repositories.AppRepository

class FetchAppsUseCase(private val appRepository: AppRepository) {

    suspend fun fetchApps(name: String): Result<List<AppModel>> = appRepository.fetchApps(name)
}