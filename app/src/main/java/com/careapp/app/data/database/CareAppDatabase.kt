package com.careapp.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.careapp.app.data.CaregiverNote
import com.careapp.app.data.MemoryCard
import com.careapp.app.data.Medication
import com.careapp.app.data.MedicationLog
import com.careapp.app.data.RoutineTask
import com.careapp.app.data.SavedPlace
import com.careapp.app.data.dao.MedicationDao
import com.careapp.app.data.dao.RoutineDao
import com.careapp.app.data.dao.MemoryCardDao
import com.careapp.app.data.dao.CaregiverNoteDao
import com.careapp.app.data.dao.SavedPlaceDao

@Database(
    entities = [
        Medication::class,
        MedicationLog::class,
        RoutineTask::class,
        MemoryCard::class,
        SavedPlace::class,
        CaregiverNote::class
    ],
    version = 2,
    exportSchema = false
)
abstract class CareAppDatabase : RoomDatabase() {
    abstract fun medicationDao(): MedicationDao
    abstract fun routineDao(): RoutineDao
    abstract fun memoryCardDao(): MemoryCardDao
    abstract fun caregiverNoteDao(): CaregiverNoteDao
    abstract fun savedPlaceDao(): SavedPlaceDao

    companion object {
        private val INSTANCES = mutableMapOf<String, CareAppDatabase>()

        fun getDatabase(context: Context, databaseName: String = "careanchor_database"): CareAppDatabase {
            return synchronized(this) {
                INSTANCES[databaseName]?.let { return@synchronized it }
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CareAppDatabase::class.java,
                    databaseName
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCES[databaseName] = instance
                instance
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE medications ADD COLUMN frequencyMinutes INTEGER NOT NULL DEFAULT 1440")
            }
        }
    }
}
