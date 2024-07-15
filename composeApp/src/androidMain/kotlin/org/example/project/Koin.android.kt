package org.example.project

import Api
import AppDatabase
import Repository
import android.app.Application
import androidx.room.Room
import org.koin.dsl.bind
import org.koin.dsl.module
import repositories.RepositoryImpl
import retrofit2.Retrofit
import util.initKoin

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin(
            module {
                single {
                    val retrofit: Retrofit = get()
                    retrofit.create(Api::class.java)
                }
                single {
                    Room.databaseBuilder(
                        this@AndroidApp,
                        AppDatabase::class.java,
                        "db_gpt2"
                    ).fallbackToDestructiveMigration(false).build()
                }
                single {
                    val api: Api = get()
                    val database: AppDatabase = get()

                    RepositoryImpl(api = api, dao = database.answerDao())

                } bind Repository::class
            }
        )
    }
}