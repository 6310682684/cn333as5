package com.example.phoneapp.database

import android.content.Context
import androidx.room.*

@Database(entities = [PhoneDbModel::class, ColorDbModel::class, TagDbModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun phoneDao(): PhoneDao
    abstract fun colorDao(): ColorDao
    abstract fun tagDao(): TagDao

    companion object {
        private const val DATABASE_NAME = "phone-maker-database"
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            var instance = INSTANCE
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()

                INSTANCE = instance
            }

            return instance
        }
    }
}