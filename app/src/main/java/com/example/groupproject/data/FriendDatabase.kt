package com.example.groupproject.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Friend::class, User::class], version = 2, exportSchema = false)
abstract class FriendDatabase : RoomDatabase() {

    abstract fun friendDao(): FriendDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: FriendDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        username TEXT NOT NULL,
                        email TEXT NOT NULL,
                        passwordHash TEXT NOT NULL,
                        salt TEXT NOT NULL
                    )
                """)
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_users_username ON users(username)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_users_email ON users(email)")
            }
        }

        fun getInstance(context: Context): FriendDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FriendDatabase::class.java,
                    "buddy_database"
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
