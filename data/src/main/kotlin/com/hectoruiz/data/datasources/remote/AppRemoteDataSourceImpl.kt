package com.hectoruiz.data.datasources.remote

import com.hectoruiz.data.models.ResponseApiModel
import com.hectoruiz.data.models.ResponseDetailApiModel
import com.hectoruiz.data.repositories.AppRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.request.get

class AppRemoteDataSourceImpl(private val httpClient: HttpClient) : AppRemoteDataSource {

    override suspend fun getApps(name: String): Result<ResponseApiModel> {
        return try {
            val response = httpClient.get("apps/search") {
                url {
                    parameters.append(QUERY, name)
                }
            }
            Result.success(response.body())
        } catch (e: RedirectResponseException) {
            Result.failure(Throwable(e.message))
        }
    }

    override suspend fun getApp(packageName: String): Result<ResponseDetailApiModel> {
        return try {
            val response = httpClient.get("getApp") {
                url {
                    parameters.append(PACKAGE_NAME, packageName)
                }
            }
            Result.success(response.body())
        } catch (e: RedirectResponseException) {
            Result.failure(Throwable(e.message))
        }
    }

    companion object {
        const val QUERY = "query"
        const val PACKAGE_NAME = "package_name"
    }
}