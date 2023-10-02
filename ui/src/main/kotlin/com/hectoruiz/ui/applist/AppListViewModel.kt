package com.hectoruiz.ui.applist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hectoruiz.domain.commons.ErrorState
import com.hectoruiz.domain.models.AppModel
import com.hectoruiz.domain.usecases.FetchAppsUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppListViewModel(private val fetchAppsUseCase: FetchAppsUseCase) : ViewModel() {

    private val coroutineHandler = CoroutineExceptionHandler { _, throwable ->
        notifyError(ErrorState.NetworkError(throwable.message ?: ""))
        _loading.update { false }
    }
    private val _apps = MutableStateFlow<List<AppModel>>(emptyList())
    val apps = _apps.asStateFlow()
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()
    private val _error = MutableSharedFlow<ErrorState>()
    val error = _error.asSharedFlow()

    fun searchApps(name: String) {
        viewModelScope.launch(coroutineHandler) {
            _loading.update { true }
            fetchAppsUseCase.fetchApps(name).fold(
                onSuccess = { apps ->
                    _apps.update { apps }
                },
                onFailure = {
                    notifyError(ErrorState.NetworkError(it.message ?: ""))
                }
            )
            _loading.update { false }
        }
    }

    private fun notifyError(errorState: ErrorState) {
        viewModelScope.launch {
            _error.emit(errorState)
        }
    }
}
