/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: AppDatabase.kt
 - Project: Itinero
 - Module: Itinero.core.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 09 June 2025
 */

package com.serranoie.itinero.core.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.serranoie.itinero.core.data.local.dao.ItineraryDao
import com.serranoie.itinero.core.data.local.dao.TripDao
import com.serranoie.itinero.core.data.local.entity.ItineraryItemEntity
import com.serranoie.itinero.core.data.local.entity.TripEntity

@Database(
    entities = [
        TripEntity::class,
        ItineraryItemEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun itineraryDao(): ItineraryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "itinero_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
