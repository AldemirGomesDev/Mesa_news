package com.aldemir.mesanews.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.aldemir.mesanews.ui.feed.domain.New
import java.util.*

@Dao
interface NewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(new: New): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(news: List<New>): List<Long>

    @Update
    fun update(new: New): Int

    @Delete
    fun delete(new: New)

    @Query("SELECT * FROM New WHERE id = :id")
    fun get(id: Int): New

    @Query("SELECT * FROM New WHERE highlight = :highlight")
    fun getAll(highlight: Boolean): LiveData<List<New>>

    @Query("SELECT * FROM New WHERE is_favorite = :isFavorite")
    fun getFavorites(isFavorite: Boolean): LiveData<List<New>>

    @Query("SELECT * FROM New WHERE highlight = :highlight")
    fun getHighLight(highlight: Boolean): List<New>

    @Query("SELECT * FROM New WHERE title LIKE :search AND is_favorite =:isFavorite")
    fun getNewsFilter(search: String, isFavorite: Boolean): List<New>

    @Query("SELECT * FROM New WHERE title LIKE :search AND is_favorite =:isFavorite AND published_at BETWEEN :from AND :to")
    fun getNewsFilterDates(search: String, isFavorite: Boolean, from: Date, to: Date): List<New>

}