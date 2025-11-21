package com.example.booksaver.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [BookmarkEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BookmarkDatabase : RoomDatabase() {

    abstract fun dao(): BookmarkDao

    companion object {

        @Volatile
        private var instance: BookmarkDatabase? = null

        fun getInstance(context: Context): BookmarkDatabase {
            return instance ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context,
                    BookmarkDatabase::class.java,
                    "bookmark_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                instance = db
                db
            }
        }
    }
}
