package org.example.project

import AnswerDatabase
import Api
import Repository
import UserRepository
import android.app.Application
import android.content.Context
import androidx.room.Room
import database.UserDatabase
import ml.bert.BertHelper
import ml.bert.BertQaHelper
import org.koin.dsl.bind
import org.koin.dsl.module
import repositories.RepositoryImpl
import repositories.UserInterface
import retrofit2.Retrofit
import util.initKoin

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin(
            module {
                single<Context> { this@AndroidApp }
                single<Application> { this@AndroidApp }

                single {
                    val retrofit: Retrofit = get()
                    retrofit.create(Api::class.java)
                }
                single<AnswerDatabase> {
                    Room.databaseBuilder(
                        this@AndroidApp,
                        AnswerDatabase::class.java,
                        "db_gpt2"
                    ).fallbackToDestructiveMigration(false).build()
                }
                single<Repository> {
                    val api: Api = get()
                    val database: AnswerDatabase = get()

                    RepositoryImpl(api = api, dao = database.answerDao())

                }

                single<BertHelper> {
                    BertQaHelper()
                }

                single { AndroidActivityViewModel() }

                single {
                    UserRepository(get())
                } bind UserInterface::class

                single<UserDatabase> {
                    Room.databaseBuilder(
                        this@AndroidApp,
                        UserDatabase::class.java,
                        "db_user"
                    ).fallbackToDestructiveMigration(false).build()
                }
            }
        )
    }
}