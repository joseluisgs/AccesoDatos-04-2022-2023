package di

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import repositories.UsersRepository
import repositories.UsersRepositoryImpl
import services.database.SqlDeLightClient
import services.remote.KtorFitClient
import viewmodels.UserViewModel

val mainModule = module {
    // Dos formas posibles, normal y con DSL
    // single { KtorFitClient() }
    singleOf(::KtorFitClient)

    // single<UsersRepository> { UsersRepositoryImpl(get(), get()) }
    singleOf(::UsersRepositoryImpl) { bind<UsersRepository>() }

    single { SqlDeLightClient(getProperty("database.url"), getProperty("database.init")) }

    //single{ UserViewModel(get()) }
    singleOf(::UserViewModel)
}