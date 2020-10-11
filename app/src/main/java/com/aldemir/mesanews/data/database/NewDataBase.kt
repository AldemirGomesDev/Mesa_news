package com.aldemir.mesanews.data.database

import com.aldemir.mesanews.ui.feed.domain.New

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aldemir.mesanews.ui.register.domain.User

@Database(entities = arrayOf(New::class, User::class), version = 1)
abstract class NewDataBase : RoomDatabase() {

    abstract fun newDao(): NewDao
    abstract fun userDao(): UserDao

    companion object {
        private lateinit var INSTANCE: NewDataBase

        fun getDataBase(context: Context): NewDataBase {
            if (!::INSTANCE.isInitialized) {
                synchronized(NewDataBase::class.java) {
                    INSTANCE = Room.databaseBuilder(context, NewDataBase::class.java, "newDataBase")
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }
    }
}