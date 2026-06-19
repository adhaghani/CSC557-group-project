package com.example.groupproject.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Friend::class], version = 1, exportSchema = false)
abstract class FriendDatabase : RoomDatabase() {

    abstract fun friendDao(): FriendDao

    companion object {
        @Volatile
        private var INSTANCE: FriendDatabase? = null

        fun getInstance(context: Context): FriendDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FriendDatabase::class.java,
                    "buddy_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
