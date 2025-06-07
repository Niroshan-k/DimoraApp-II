package com.example.dimoraapp.mappers

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dimoraapp.entities.AdvertisementEntity

@Database(entities = [AdvertisementEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun advertisementDao(): AdvertisementDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}