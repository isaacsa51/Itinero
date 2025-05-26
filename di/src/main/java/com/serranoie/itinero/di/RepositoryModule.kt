package com.serranoie.itinero.di

import com.serranoie.itinero.core.data.remote.ItineroApi
import com.serranoie.itinero.core.data.remote.ItineroApiImpl
import com.serranoie.itinero.core.data.remote.repository.AuthRepositoryImpl
import com.serranoie.itinero.core.domain.repository.AuthRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<ItineroApi> { ItineroApiImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
}
