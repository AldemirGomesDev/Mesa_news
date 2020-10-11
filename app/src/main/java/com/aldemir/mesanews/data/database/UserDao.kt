package com.aldemir.mesanews.data.database

import androidx.room.*
import com.aldemir.mesanews.ui.register.domain.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User): Long

    @Update
    fun update(user: User): Int

    @Delete
    fun delete(user: User)

    @Query("SELECT * FROM User WHERE email = :email")
    fun getUserEmail(email: String): User

    @Query("SELECT * FROM User WHERE email = :email AND isLogged = :isLogged")
    fun getUserLogged(email: String, isLogged: Boolean): User
}