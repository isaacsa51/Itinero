package com.serranoie.app.itinero

import android.app.Application
import com.serranoie.app.feature.auth.di.authViewModelModule
import com.serranoie.app.feature.chat.di.chatViewModelModule
import com.serranoie.app.feature.di.homeViewModelModule
import com.serranoie.app.feature.di.welcomeViewModelModule
import com.serranoie.app.feature.expenses.di.expensesDetailsViewModel
import com.serranoie.app.feature.expenses.di.expensesViewModelModule
import com.serranoie.app.feature.itinerary.di.itineraryViewModelModule
import com.serranoie.app.feature.settings.di.tripSettingsViewModelModule
import com.serranoie.itinero.di.appModule
import com.serranoie.itinero.di.persistenceModule
import com.serranoie.itinero.di.repositoryModule
import com.serranoie.itinero.di.useCaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ItineroApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@ItineroApplication)

            modules(
                appModule,
                persistenceModule,
                repositoryModule,
                useCaseModule,
                authViewModelModule,
                welcomeViewModelModule,
                homeViewModelModule,
                itineraryViewModelModule,
                tripSettingsViewModelModule,
                expensesViewModelModule,
                expensesDetailsViewModel,
                chatViewModelModule
            )
        }
    }
}
