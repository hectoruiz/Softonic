package com.hectoruiz.domain.usecases

import com.hectoruiz.domain.commons.Constants.PARSING_ERROR
import com.hectoruiz.domain.models.AppDetailModel
import com.hectoruiz.domain.repositories.AppRepository
import java.text.SimpleDateFormat
import java.util.Locale

class GetAppUseCase(private val appRepository: AppRepository) {

    suspend fun getApp(packageName: String): Result<AppDetailModel> {
        return appRepository.getApp(packageName).fold(
            onSuccess = {
                try {
                    val app = it.copy(updated = it.updated.transformDate())
                    Result.success(app)
                } catch (e: Exception) {
                    Result.failure(Throwable(PARSING_ERROR))
                }
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }
}

fun String.transformDate(): String {
    val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return formatter.format(parser.parse(this))
}
