import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AnswerEntity::class], version = 2)
abstract class AnswerDatabase : RoomDatabase() {
    abstract fun answerDao(): AnswerDao
}