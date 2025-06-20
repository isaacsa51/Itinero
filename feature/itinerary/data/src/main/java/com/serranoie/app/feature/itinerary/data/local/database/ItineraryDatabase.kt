/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ItineraryDatabase.kt
 - Project: Itinero
 - Module: Itinero.feature.itinerary.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 18 junio 2025
 */

package com.serranoie.app.feature.itinerary.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.serranoie.app.feature.itinerary.data.local.dao.ItineraryDao
import com.serranoie.app.feature.itinerary.data.local.entity.ItineraryItemEntity

@Database(
    entities = [ItineraryItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ItineraryDatabase : RoomDatabase() {
    abstract fun itineraryDao(): ItineraryDao

    companion object {
        @Volatile
        private var INSTANCE: ItineraryDatabase? = null

        fun getDatabase(context: Context): ItineraryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItineraryDatabase::class.java,
                    "itinerary_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}