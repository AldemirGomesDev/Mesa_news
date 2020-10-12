package com.aldemir.mesanews.data.repository.news

import androidx.lifecycle.LiveData
import com.aldemir.mesanews.data.api.login.ApiService
import com.aldemir.mesanews.data.database.NewDao
import com.aldemir.mesanews.ui.feed.domain.New
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class NewsRepositoryImpl(private val newApiService: ApiService, private val newDao: NewDao):
    NewsRepository {

    override suspend fun getAllNews(lastPage: Int, perPage: Int) = newApiService.getAllNews(lastPage.toString(), perPage.toString(), "")

    override suspend fun getAllNewsHighlights() = newApiService.getAllNewsHighlights()

    override fun insertNew(new: New): Long {
        return newDao.insert(new)
    }

    override fun insertNews(list: List<New>){
        newDao.insertAll(list)
    }

    override fun updateNew(new: New) {
        newDao.update(new)
    }

    override fun getNewsDatabase(): LiveData<List<New>> {
        return newDao.getAll(false)
    }

    override fun getFavorites(isFavorite: Boolean): LiveData<List<New>> {
        return newDao.getFavorites(isFavorite)
    }

    override fun getHighLight(highlight: Boolean): List<New> {
        return newDao.getHighLight(highlight)
    }

    override fun getNewsFilter(search: String, isFavorite: Boolean): List<New> {
        return newDao.getNewsFilter(
            "%" + search.toUpperCase(Locale.getDefault()) + "%", isFavorite
        )
    }

}