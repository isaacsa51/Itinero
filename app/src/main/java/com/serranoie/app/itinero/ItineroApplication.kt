package com.serranoie.app.itinero

import android.app.Application
import com.serranoie.app.feature.auth.di.authViewModelModule
import com.serranoie.app.feature.di.homeViewModelModule
import com.serranoie.app.feature.di.welcomeViewModelModule
import com.serranoie.app.feature.itinerary.di.itineraryViewModelModule
import com.serranoie.itinero.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ItineroApplication : Application() {
    /**
     * Initializes the application and sets up dependency injection with Koin.
     *
     * This method configures Koin with Android logging, the application context, and all required dependency modules when the application starts.
     */
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@ItineroApplication)
            modules(appModule, authViewModelModule, welcomeViewModelModule, homeViewModelModule, itineraryViewModelModule)
        }
    }
}
