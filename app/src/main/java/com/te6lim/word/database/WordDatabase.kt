package com.te6lim.word.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "word")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo
    val data: String? = null
)

@Dao
interface WordDatabaseDao {

    @Insert
    fun insertWords(wordList: List<String>): List<Long>

    @Query("SELECT * FROM word WHERE id = :wordId")
    suspend fun getWord(wordId: Long): Word

    @Query("SELECT * FROM word")
    fun getAll(): LiveData<List<Word>?>

    @Query("DELETE FROM word WHERE id = :wordId LIMIT 1")
    suspend fun deleteWord(wordId: Long)

    @Query("DELETE FROM word")
    suspend fun clear()
}

@Database(entities = [Word::class], version = 1, exportSchema = false)
abstract class WordDatabase : RoomDatabase() {

    abstract val wordDao: WordDatabaseDao

    companion object {

        @Volatile
        var INSTANCE: WordDatabase? = null

        fun getInstance(context: Context): WordDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context, WordDatabase::class.java, "wordDatabase"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}