package com.hectoruiz.ui.appdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hectoruiz.domain.commons.Constants.PARAM_NAME
import com.hectoruiz.domain.commons.ErrorState
import com.hectoruiz.domain.models.AppDetailModel
import com.hectoruiz.domain.models.Reliability
import com.hectoruiz.domain.usecases.GetAppUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val getAppUseCase: GetAppUseCase,
) : ViewModel() {

    private val packageName: String = savedStateHandle[PARAM_NAME] ?: ""
    private val coroutineHandler = CoroutineExceptionHandler { _, throwable ->
        notifyError(ErrorState.NetworkError(throwable.message ?: ""))
        _loading.update { false }
    }
    private val _appDetail = MutableStateFlow(initAppDetailModel())
    val appDetail = _appDetail.asStateFlow()
    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()
    private val _error = MutableSharedFlow<ErrorState>()
    val error = _error.asSharedFlow()

    init {
        viewModelScope.launch(coroutineHandler) {
            getAppUseCase.getApp(packageName).fold(
                onSuccess = { app ->
                    _appDetail.update { app }
                },
                onFailure = {
                    notifyError(ErrorState.NetworkError(it.message ?: ""))
                }
            )
            _loading.update { false }
        }
    }

    private fun initAppDetailModel() =
        AppDetailModel(
            name = "",
            icon = "",
            updated = "",
            version = "",
            pegi = "",
            size = 0,
            description = "",
            reliability = Reliability.NO_DATA,
            developerName = "",
            developerEmail = "",
            keywords = "",
            path = ""
        )

    private fun notifyError(errorState: ErrorState) {
        viewModelScope.launch {
            _error.emit(errorState)
        }
    }
}
