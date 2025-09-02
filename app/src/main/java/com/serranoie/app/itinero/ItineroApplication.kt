package com.serranoie.app.itinero

import android.app.Application
import com.serranoie.app.feature.auth.di.authViewModelModule
import com.serranoie.app.feature.chat.di.chatViewModelModule
import com.serranoie.app.feature.di.googlePlacesModule
import com.serranoie.app.feature.di.homeViewModelModule
import com.serranoie.app.feature.di.welcomeViewModelModule
import com.serranoie.app.feature.expenses.di.expensesDetailsViewModel
import com.serranoie.app.feature.expenses.di.expensesViewModelModule
import com.serranoie.app.feature.itinerary.di.itineraryViewModelModule
import com.serranoie.app.feature.settings.di.settingsModule
import com.serranoie.itinero.di.appModule
import com.serranoie.itinero.di.persistenceModule
import com.serranoie.itinero.di.repositoryModule
import com.serranoie.itinero.di.useCaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ItineroApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ItineroApplication)

            modules(
                appModule,
                persistenceModule,
                repositoryModule,
                useCaseModule,
                googlePlacesModule,
                authViewModelModule,
                welcomeViewModelModule,
                homeViewModelModule,
                itineraryViewModelModule,
                settingsModule,
                expensesViewModelModule,
                expensesDetailsViewModel,
                chatViewModelModule
            )
        }

        // Note: Language checking is now handled on every app entry in MainActivity
        // This ensures we always have the most up-to-date device language
        android.util.Log.d(
            "ItineroApp",
            " App initialized - language will be checked on MainActivity entry"
        )
    }
}
