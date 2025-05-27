package com.serranoie.app.itinero

import android.app.Application
import com.serranoie.app.feature.auth.di.authViewModelModule
import com.serranoie.itinero.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ItineroApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@ItineroApplication)
            modules(appModule, authViewModelModule)
        }
    }
}