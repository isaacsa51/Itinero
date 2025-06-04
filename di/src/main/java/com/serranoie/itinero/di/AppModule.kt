package com.serranoie.itinero.di

import org.koin.dsl.module

val appModule = module {
    includes(
        diModules,
        networkModule,
    )
}
