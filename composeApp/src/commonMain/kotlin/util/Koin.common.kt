package util

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun initKoin(appModule: Module): KoinApplication = startKoin { modules(coreModule, appModule) }

private val coreModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/chat/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}