package com.hectoruiz.domain.commons

sealed class ErrorState {
    data object Unknown : ErrorState()
    data class NetworkError(val message: String = "") : ErrorState()
    data object NoError : ErrorState()
}
