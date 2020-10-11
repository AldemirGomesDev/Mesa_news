package com.aldemir.mesanews.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.aldemir.mesanews.ui.feed.domain.New

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

    @Query("SELECT * FROM New")
    fun getAll(): LiveData<List<New>>

    @Query("SELECT * FROM New WHERE is_favorite = :isFavorite")
    fun getFavorites(isFavorite: Boolean): LiveData<List<New>>

}