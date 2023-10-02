package com.hectoruiz.data.models

import com.hectoruiz.domain.commons.Constants.CONVERSION_MB
import com.hectoruiz.domain.commons.Constants.TRUSTED
import com.hectoruiz.domain.commons.Constants.UNKNOWN
import com.hectoruiz.domain.models.AppDetailModel
import com.hectoruiz.domain.models.AppModel
import com.hectoruiz.domain.models.Reliability
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseApiModel(
    @SerialName("datalist")
    val result: ResultApiModel? = null,
)

@Serializable
data class ResultApiModel(
    @SerialName("list")
    val apps: List<AppApiModel?>? = null,
)

@Serializable
data class AppApiModel(
    val name: String? = null,
    @SerialName("package")
    val packageName: String? = null,
    val icon: String? = null,
    val stats: AppStatsApiModel? = null,
)

@Serializable
data class AppStatsApiModel(
    val downloads: Int? = null,
    val rating: AppRatingApiModel? = null,
)

@Serializable
data class AppRatingApiModel(
    val avg: Float? = null,
    val total: Int? = null,
)

fun ResponseApiModel?.toModel() = this?.result?.apps?.map { it.toModel() } ?: emptyList()

fun AppApiModel?.toModel() =
    AppModel(
        name = this?.name.orEmpty(),
        packageName = this?.packageName.orEmpty(),
        icon = this?.icon.orEmpty(),
        numDownloads = this?.stats?.downloads ?: 0,
        averageStats = this?.stats?.rating?.avg ?: 0F,
        totalStats = this?.stats?.rating?.total ?: 0,
    )


@Serializable
data class ResponseDetailApiModel(
    @SerialName("nodes")
    val node: NodeApiModel? = null,
)

@Serializable
data class NodeApiModel(
    @SerialName("meta")
    val meta: MetaApiModel? = null,
)

@Serializable
data class MetaApiModel(
    @SerialName("data")
    val appDetail: AppDetailApiModel? = null,
)

@Serializable
data class AppDetailApiModel(
    val name: String? = null,
    val size: Int? = null,
    val icon: String? = null,
    val updated: String? = null,
    val age: AppDetailAgeApiModel? = null,
    val developer: AppDetailDeveloperApiModel? = null,
    val file: AppDetailFileApiModel? = null,
    val media: AppDetailMediaApiModel? = null,
)

@Serializable
data class AppDetailAgeApiModel(
    @SerialName("pegi")
    val qualification: String? = null,
)

@Serializable
data class AppDetailDeveloperApiModel(
    val name: String? = null,
    val email: String? = null,
)

@Serializable
data class AppDetailFileApiModel(
    @SerialName("vername")
    val version: String? = null,
    val malware: AppDetailMalwareApiModel? = null,
    val path: String? = null,
)

@Serializable
data class AppDetailMalwareApiModel(
    @SerialName("rank")
    val reliability: String? = null,
)

@Serializable
data class AppDetailMediaApiModel(
    val keywords: List<String?>? = null,
    val description: String? = null,
)

fun ResponseDetailApiModel?.toModel() = this?.node?.meta?.appDetail?.toModel()

fun AppDetailApiModel?.toModel() =
    AppDetailModel(
        name = this?.name.orEmpty(),
        icon = this?.icon.orEmpty(),
        updated = this?.updated.orEmpty(),
        version = this?.file?.version.orEmpty(),
        pegi = this?.age?.qualification.orEmpty(),
        size = (this?.size ?: 0) / CONVERSION_MB,
        description = this?.media?.description.orEmpty(),
        reliability = when (this?.file?.malware?.reliability) {
            UNKNOWN -> Reliability.UNKNOWN
            TRUSTED -> Reliability.TRUSTED
            else -> Reliability.NO_DATA
        },
        developerName = this?.developer?.name.orEmpty(),
        developerEmail = this?.developer?.email.orEmpty(),
        keywords = this?.media?.keywords?.joinToString(" ") { "#$it" } ?: "",
        path = this?.file?.path.orEmpty()
    )