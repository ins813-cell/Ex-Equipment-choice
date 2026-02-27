package com.example.exchoice.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [EquipmentEntity::class, ProjectEntity::class, CheckLogEntity::class, RegistryCacheEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): ExDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context, AppDatabase::class.java, "ex_choice.db")
                .fallbackToDestructiveMigration()
                .build()
                .also { instance = it }
        }
    }
}
