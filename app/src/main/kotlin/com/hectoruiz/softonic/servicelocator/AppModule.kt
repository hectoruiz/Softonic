package com.hectoruiz.softonic.servicelocator

import com.hectoruiz.data.api.remote.ApiClient
import com.hectoruiz.data.datasources.remote.AppRemoteDataSourceImpl
import com.hectoruiz.data.repositories.AppRemoteDataSource
import com.hectoruiz.data.repositories.AppRepositoryImpl
import com.hectoruiz.domain.repositories.AppRepository
import com.hectoruiz.domain.usecases.FetchAppsUseCase
import com.hectoruiz.domain.usecases.GetAppUseCase
import com.hectoruiz.ui.appdetail.AppDetailViewModel
import com.hectoruiz.ui.applist.AppListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<AppRepository> { AppRepositoryImpl(get()) }
    factory { FetchAppsUseCase(get()) }
    factory { GetAppUseCase(get()) }
}

val viewModelModule = module {
    viewModel { AppListViewModel(get()) }
    viewModel { AppDetailViewModel(get(), get()) }
}

val networkModule = module {
    single { ApiClient().httpClient }
    single<AppRemoteDataSource> { AppRemoteDataSourceImpl(get()) }
}