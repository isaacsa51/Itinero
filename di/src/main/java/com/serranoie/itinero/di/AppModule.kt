package com.serranoie.itinero.di

import org.koin.dsl.module

val appModule = module {
    includes(
        ktorModule,
        repositoryModule,
        useCaseModule,
        networkModule,
        authModule,
    )
}
