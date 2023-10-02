package com.hectoruiz.softonic

import android.app.Application
import com.hectoruiz.softonic.servicelocator.appModule
import com.hectoruiz.softonic.servicelocator.networkModule
import com.hectoruiz.softonic.servicelocator.viewModelModule
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(appModule, viewModelModule, networkModule)
        }
    }
}