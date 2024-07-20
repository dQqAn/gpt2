package org.example.project

import Api
import AppDatabase
import MyClass
import MyInterface
import Repository
import android.app.Application
import android.content.Context
import androidx.room.Room
import ml.bert.BertHelper
import ml.bert.BertQaHelper
import org.koin.dsl.module
import repositories.RepositoryImpl
import retrofit2.Retrofit
import util.initKoin

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin(
            module {
                single<Context> { this@AndroidApp }

                single {
                    val retrofit: Retrofit = get()
                    retrofit.create(Api::class.java)
                }
                single<AppDatabase> {
                    Room.databaseBuilder(
                        this@AndroidApp,
                        AppDatabase::class.java,
                        "db_gpt2"
                    ).fallbackToDestructiveMigration(false).build()
                }
                single<Repository> {
                    val api: Api = get()
                    val database: AppDatabase = get()

                    RepositoryImpl(api = api, dao = database.answerDao())

                } /*bind Repository::class*/

                single<MyInterface> {
                    MyClass()
                }

                single<BertHelper> {
//                    BertQaHelper(context = get())
                    BertQaHelper()
                }
                /*single {
                    BertQaHelper(context = get())
                } bind BertHelper::class*/
//                viewModel { ChatViewModel(bertHelper = get(), database = get(), repository = get()) }
//                viewModelOf(::ChatViewModel)//{ChatViewModel()}
            }
        )
    }
}