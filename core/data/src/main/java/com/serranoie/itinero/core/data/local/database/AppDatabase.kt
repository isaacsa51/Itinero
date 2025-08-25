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

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.serranoie.itinero.core.data.local.dao.ExpenseDao
import com.serranoie.itinero.core.data.local.dao.ItineraryDao
import com.serranoie.itinero.core.data.local.dao.TripDao
import com.serranoie.itinero.core.data.local.entity.ExpenseDebtorEntity
import com.serranoie.itinero.core.data.local.entity.ExpenseEntity
import com.serranoie.itinero.core.data.local.entity.ItineraryItemEntity
import com.serranoie.itinero.core.data.local.entity.TripEntity
import com.serranoie.itinero.core.data.local.entity.UserBalanceEntity
import com.serranoie.itinero.core.data.local.entity.UserExpenseSummaryEntity

@Database(
    entities = [
        TripEntity::class,
        ItineraryItemEntity::class,
        ExpenseEntity::class,
        ExpenseDebtorEntity::class,
        UserExpenseSummaryEntity::class,
        UserBalanceEntity::class,
    ], version = 6, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun itineraryDao(): ItineraryDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE expense_debtors ADD COLUMN hasPaid INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Remove duplicate accommodation fields that were moved to trip level
                // Create new table without the duplicate fields
                database.execSQL(
                    """
                    CREATE TABLE trips_new (
                        id TEXT NOT NULL PRIMARY KEY,
                        destination TEXT NOT NULL,
                        groupName TEXT NOT NULL,
                        startDate TEXT NOT NULL,
                        endDate TEXT NOT NULL,
                        summary TEXT NOT NULL,
                        totalMembers INTEGER NOT NULL,
                        accommodation_name TEXT NOT NULL,
                        accommodation_phone TEXT NOT NULL,
                        accommodation_checkIn TEXT NOT NULL,
                        accommodation_checkOut TEXT NOT NULL,
                        accommodation_location TEXT NOT NULL,
                        accommodation_mapUri TEXT,
                        accommodation_latitude REAL,
                        accommodation_longitude REAL,
                        reservationCode TEXT NOT NULL,
                        extraInfo TEXT NOT NULL,
                        additionalInfo TEXT NOT NULL,
                        groupCode TEXT NOT NULL,
                        ownerId TEXT NOT NULL
                    )
                """
                )

                // Copy data from old table to new table (excluding the duplicate fields)
                database.execSQL(
                    """
                    INSERT INTO trips_new (
                        id, destination, groupName, startDate, endDate, summary, totalMembers,
                        accommodation_name, accommodation_phone, accommodation_checkIn, 
                        accommodation_checkOut, accommodation_location, accommodation_mapUri, 
                        accommodation_latitude, accommodation_longitude, reservationCode, 
                        extraInfo, additionalInfo, groupCode, ownerId
                    )
                    SELECT 
                        id, destination, groupName, startDate, endDate, summary, totalMembers,
                        accommodation_name, accommodation_phone, accommodation_checkIn, 
                        accommodation_checkOut, accommodation_location, accommodation_mapUri, 
                        accommodation_latitude, accommodation_longitude, reservationCode, 
                        extraInfo, additionalInfo, groupCode, ownerId
                    FROM trips
                """
                )

                // Drop old table and rename new table
                database.execSQL("DROP TABLE trips")
                database.execSQL("ALTER TABLE trips_new RENAME TO trips")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "itinero_database"
                ).addMigrations(MIGRATION_4_5, MIGRATION_5_6).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
