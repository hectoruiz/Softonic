package com.hectoruiz.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class AppModel(
    val name: String,
    val packageName: String,
    val icon: String,
    val numDownloads: Int,
    val averageStats: Float,
    val totalStats: Int,
)

@Serializable
data class AppDetailModel(
    val name: String,
    val icon: String,
    val updated: String,
    val version: String,
    val pegi: String,
    val size: Int,
    val description: String,
    val reliability: Reliability,
    val developerName: String,
    val developerEmail: String,
    val keywords: String,
    val path: String,
) {
    fun hasInitialInformation() =
        updated.isNotBlank() && pegi.isNotBlank() && version.isNotBlank() && size != 0
}

enum class Reliability {
    TRUSTED,
    UNKNOWN,
    NO_DATA
}

