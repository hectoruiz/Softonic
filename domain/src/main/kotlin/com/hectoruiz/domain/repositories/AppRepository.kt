package com.hectoruiz.domain.repositories

import com.hectoruiz.domain.models.AppDetailModel
import com.hectoruiz.domain.models.AppModel

interface AppRepository {

    suspend fun fetchApps(name: String): Result<List<AppModel>>

    suspend fun getApp(packageName: String): Result<AppDetailModel>
}