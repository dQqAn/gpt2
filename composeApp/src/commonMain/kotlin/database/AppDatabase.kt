import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AnswerEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun answerDao(): AnswerDao
}