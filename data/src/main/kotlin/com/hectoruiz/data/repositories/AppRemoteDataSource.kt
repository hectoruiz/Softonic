package com.hectoruiz.data.repositories

import com.hectoruiz.data.models.ResponseApiModel
import com.hectoruiz.data.models.ResponseDetailApiModel

interface AppRemoteDataSource {

    suspend fun getApps(name: String): Result<ResponseApiModel>

    suspend fun getApp(packageName: String): Result<ResponseDetailApiModel>
}