package com.aldemir.mesanews.ui.feed.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "New")
data class New(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "title")
    var title: String = "",
    @ColumnInfo(name = "description")
    var description: String? = "",
    @ColumnInfo(name = "content")
    var content: String? = "",
    @ColumnInfo(name = "author")
    var author: String? = "",
    @ColumnInfo(name = "published_at")
    var published_at: String? = "",
    @ColumnInfo(name = "highlight")
    var highlight: Boolean? = false,
    @ColumnInfo(name = "url")
    var url: String? = "",
    @ColumnInfo(name = "image_url")
    var image_url: String? = "",
    @ColumnInfo(name = "is_favorite")
    var is_favorite: Boolean = false
)