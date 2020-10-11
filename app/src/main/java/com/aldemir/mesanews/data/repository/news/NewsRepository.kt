package com.aldemir.mesanews.data.repository.news

import androidx.lifecycle.LiveData
import com.aldemir.mesanews.data.model.ResponseNew
import com.aldemir.mesanews.data.model.ResponseNewHighlights
import com.aldemir.mesanews.ui.feed.domain.New

interface NewsRepository {
    suspend fun getAllNews(lastPage: Int, perPage: Int): ResponseNew
    suspend fun getAllNewsHighlights(): ResponseNewHighlights
    fun insertNew(new: New): Long
    fun insertNews(list: List<New>)
    fun updateNew(new: New)
    fun getNewsDatabase(): LiveData<List<New>>
    fun getFavorites(isFavorite: Boolean): LiveData<List<New>>
}