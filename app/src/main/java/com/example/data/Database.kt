package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface SensorDao {
    @Query("SELECT * FROM sensor_progress")
    fun getAllSensorProgress(): Flow<List<SensorProgress>>

    @Query("SELECT * FROM sensor_progress WHERE sensorId = :sensorId")
    suspend fun getSensorProgress(sensorId: String): SensorProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSensorProgress(progress: SensorProgress)

    @Query("SELECT * FROM quiz_result")
    fun getAllQuizResults(): Flow<List<QuizResult>>

    @Query("SELECT * FROM quiz_result WHERE sensorId = :sensorId")
    suspend fun getQuizResult(sensorId: String): QuizResult?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizResult(result: QuizResult)

    @Query("DELETE FROM sensor_progress")
    suspend fun clearSensorProgress()

    @Query("DELETE FROM quiz_result")
    suspend fun clearQuizResults()
}

@Database(entities = [SensorProgress::class, QuizResult::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sensorDao(): SensorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mechatronics_sensor_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
