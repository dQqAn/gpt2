package database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Insert
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    @Update
    fun setLang(userEntity: UserEntity)

    //    @Query("SELECT * FROM `user` WHERE lang")
    @Query("SELECT * FROM `user`")
    suspend fun getLang(): List<UserEntity?>

    @Query("DELETE FROM user")
    suspend fun clear()
}