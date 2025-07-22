package com.serranoie.itinero.di

import org.koin.dsl.module

val diModules = module {
    includes(
        persistenceModule,
        repositoryModule,
        useCaseModule,
        utilitiesModule
    )
}
