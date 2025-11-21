package com.example.booksaver.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmark_table")
data class BookmarkEntity(

    @PrimaryKey(autoGenerate = true)
    val bookmarkId: Int = 0,

    val bookmarkTitle: String,
    val bookmarkUrl: String,
    val bookmarkCategory: String
)
