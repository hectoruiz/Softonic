package com.hectoruiz.data.repositories

import com.hectoruiz.data.models.toModel
import com.hectoruiz.domain.models.AppDetailModel
import com.hectoruiz.domain.models.AppModel
import com.hectoruiz.domain.repositories.AppRepository

class AppRepositoryImpl(private val appRemoteDataSource: AppRemoteDataSource) : AppRepository {

    override suspend fun fetchApps(name: String): Result<List<AppModel>> {
        return appRemoteDataSource.getApps(name).fold(
            onSuccess = {
                Result.success(it.toModel())
            },
            onFailure = {
                Result.failure(it)
            })
    }

    override suspend fun getApp(packageName: String): Result<AppDetailModel> {
        return appRemoteDataSource.getApp(packageName).fold(
            onSuccess = {
                val appDetail = it.toModel()
                if (appDetail != null) {
                    Result.success(appDetail)
                } else {
                    Result.failure(Throwable())
                }
            },
            onFailure = {
                Result.failure(it)
            })
    }
}
