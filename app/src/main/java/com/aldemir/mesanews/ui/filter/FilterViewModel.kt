package com.aldemir.mesanews.ui.filter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aldemir.mesanews.data.api.SessionManager
import com.aldemir.mesanews.data.repository.news.NewsRepository
import com.aldemir.mesanews.ui.feed.domain.New
import java.sql.SQLException
import java.util.*

class FilterViewModel(
    private val newsRepository: NewsRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _newsDatabase = MutableLiveData<List<New>>()
    var newsDatabase: LiveData<List<New>> = _newsDatabase

    fun getNewsFilter(search: String, isFavorite: Boolean, inputDate: Date, outputDate: Date) {
        try {
            _newsDatabase.value = newsRepository.getNewsFilter(search, isFavorite, inputDate, outputDate)
            Log.d("dateHora: ", "search => : ${search}")
        }catch (error: SQLException) {
            Log.e("dateHora: ", "ERROR ROOM => : ${error}")
        }
    }

    fun addNewsFavorite(new: New) {
        newsRepository.updateNew(new)
    }

    fun removeNewsFavorite(new: New) {
        newsRepository.updateNew(new)
    }

}