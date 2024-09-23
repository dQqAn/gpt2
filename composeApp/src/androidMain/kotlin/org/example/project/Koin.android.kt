package org.example.project

import AnswerDatabase
import Api
import FirebaseMessageRepositoryImp
import MessageRepository
import SpeechRepository
import UserRepository
import android.app.Application
import android.content.Context
import androidx.room.Room
import database.UserDatabase
import ml.bert.BertHelper
import ml.bert.BertQaHelper
import ml.image_classification.ImageClassifierHelper
import ml.image_classification.ImageClassifierHelperInterface
import org.koin.dsl.bind
import org.koin.dsl.module
import presentation.components.SpeechInterface
import presentation.components.UserInterface
import repositories.FirebaseMessageRepository
import repositories.MessageRepositoryImpl
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

                single<MessageRepository> {
                    val database: AnswerDatabase = get()

                    MessageRepositoryImpl(dao = database.answerDao())
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

                single<FirebaseMessageRepository> {
                    FirebaseMessageRepositoryImp()
                }

                single<SpeechInterface> {
                    SpeechRepository(get())
                }

                single<ImageClassifierHelperInterface> {
                    ImageClassifierHelper(context = get(), imageClassifierListener = get())
                }
            }
        )
    }
}