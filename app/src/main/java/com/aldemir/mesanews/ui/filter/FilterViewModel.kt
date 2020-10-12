package com.aldemir.mesanews.ui.filter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aldemir.mesanews.data.api.SessionManager
import com.aldemir.mesanews.data.repository.news.NewsRepository
import com.aldemir.mesanews.ui.feed.domain.New

class FilterViewModel(
    private val newsRepository: NewsRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _newsDatabase = MutableLiveData<List<New>>()
    var newsDatabase: LiveData<List<New>> = _newsDatabase

    fun getNewsFilter(search: String, isFavorite: Boolean) {
        try {
            _newsDatabase.value = newsRepository.getNewsFilter(search, isFavorite)
            Log.d("search_filters: ", "search => : ${search}")
        }catch (error: Exception) {
            Log.e("search_filters: ", "ERROR ROOM => : ${error}")
        }
    }

}