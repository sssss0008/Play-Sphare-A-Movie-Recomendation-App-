package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GameVaultEntity::class], version = 1, exportSchema = false)
abstract class PlaySphereDatabase : RoomDatabase() {

    abstract fun gameVaultDao(): GameVaultDao

    companion object {
        @Volatile
        private var INSTANCE: PlaySphereDatabase? = null

        fun getDatabase(context: Context): PlaySphereDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlaySphereDatabase::class.java,
                    "playsphere_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
