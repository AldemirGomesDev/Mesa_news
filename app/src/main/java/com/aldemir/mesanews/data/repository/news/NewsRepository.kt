package com.aldemir.mesanews.data.repository.news

import androidx.lifecycle.LiveData
import com.aldemir.mesanews.data.api.model.ResponseNew
import com.aldemir.mesanews.data.api.model.ResponseNewHighlights
import com.aldemir.mesanews.ui.feed.domain.New
import java.util.*

interface NewsRepository {
    suspend fun getAllNews(lastPage: Int, perPage: Int): ResponseNew
    suspend fun getAllNewsHighlights(): ResponseNewHighlights
    fun insertNew(new: New): Long
    fun insertNews(list: List<New>)
    fun updateNew(new: New)
    fun getNewsDatabase(): LiveData<List<New>>
    fun getFavorites(isFavorite: Boolean): LiveData<List<New>>
    fun getHighLight(highlight: Boolean): List<New>
    fun getNewsFilter(search: String, isFavorite: Boolean, inputDate: Date, outputDate: Date): List<New>
}