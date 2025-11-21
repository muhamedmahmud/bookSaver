package com.example.booksaver.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookmarkDao {

    @Insert
    suspend fun addBookmark(item: BookmarkEntity)

    @Query("SELECT * FROM bookmark_table ORDER BY bookmarkId DESC")
    suspend fun fetchAll(): List<BookmarkEntity>

    @Query("SELECT DISTINCT bookmarkCategory FROM bookmark_table ORDER BY bookmarkCategory ASC")
    suspend fun fetchCategories(): List<String>

    @Query("SELECT * FROM bookmark_table WHERE bookmarkCategory = :selected")
    suspend fun fetchByCategory(selected: String): List<BookmarkEntity>
}
