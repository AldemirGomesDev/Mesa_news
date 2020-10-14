package com.aldemir.mesanews.ui.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldemir.mesanews.Resource
import com.aldemir.mesanews.data.api.SessionManager
import com.aldemir.mesanews.data.api.model.ResponseNew
import com.aldemir.mesanews.data.api.model.ResponseNewHighlights
import com.aldemir.mesanews.data.repository.news.NewsRepository
import com.aldemir.mesanews.ui.feed.domain.New
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FeedViewModel(
    private val newsRepository: NewsRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _lastPage = MutableLiveData<Int>()
    val lastPage: LiveData<Int> = _lastPage

    private val _totalPages = MutableLiveData<Int>()
    val totalPages: LiveData<Int> = _totalPages

    private val _totalNews = MutableLiveData<Int>()
    val totalNews: LiveData<Int> = _totalNews

    private val _totalNewsHighLight = MutableLiveData<Int>()
    val totalNewsHighLight: LiveData<Int> = _totalNewsHighLight

    private val _newsDatabase = MutableLiveData<List<New>>()
    var newsDatabase: LiveData<List<New>> = _newsDatabase

    private val _newsHighLight = MutableLiveData<List<New>>()
    var newsHighLight: LiveData<List<New>> = _newsHighLight


    private val _news = MutableLiveData<Resource<List<New>>>((Resource.loading(null)))
    var news: LiveData<Resource<List<New>>> = _news

    private val _newsHighlights = MutableLiveData<Resource<List<New>>>((Resource.loading(null)))
    var newsHighlights: LiveData<Resource<List<New>>> = _newsHighlights

    fun getNewsDatabase() {
        try {
            newsDatabase = newsRepository.getNewsDatabase()
        } catch (error: Exception) {
            Log.e("facebookLogin: ", "ERROR ROOM => : ${error}")
        }
    }

    fun getNewsHighLight(highlight: Boolean) {
        try {
            _newsHighLight.value = newsRepository.getHighLight(highlight)
            Log.d("DatePicker: ", "HIGHLIGHTS => : ${highlight}")
        } catch (error: Exception) {
            Log.e("DatePicker: ", "ERROR ROOM => : ${error}")
        }
    }


    fun getAllNews(lastPage: Int, perPage: Int, countNews: Int) {
        Log.e("getAllNews: ", "getAllNews =============================> lastPage: $lastPage")
        viewModelScope.launch {
            val mNews: ArrayList<New> = arrayListOf()
            try {
                val result: ResponseNew? = newsRepository.getAllNews(lastPage, perPage)

                if (result != null) {
                    Log.d(
                        "getAllNews: ",
                        "total_items: ${result.pagination.total_items} => countNews: $countNews"
                    )
                    if (result.pagination.total_items > countNews) {

                        for (new in result.data) {
                            val mNew = New()
                            mNew.author = new.author
                            mNew.content = new.content
                            mNew.description = new.description
                            mNew.highlight = new.highlight
                            mNew.image_url = new.image_url
                            mNew.published_at = getDateFormatted(new.published_at)
                            mNew.title = new.title
                            mNew.url = new.url

                            mNews.add(mNew)
                        }
                        saveTotalPages(result.pagination.total_pages)
                        _news.value = (Resource.success(null))
                        if (lastPage == result.pagination.total_pages) {
                            saveTotalNews(result.pagination.total_items, "news")
                        } else {
                            saveLastPage(result.pagination.current_page)
                        }
                        try {
                            newsRepository.insertNews(mNews)
                        } catch (error: Exception) {
                            Log.e("getAllNews: ", "ERROR ROOM => : ${error}")
                        }
                    }
                }

            } catch (error: Exception) {
                Log.e("getAllNews: ", "ERROR getAllNews  => : ${error}")
                _news.value = (Resource.error("Verifique sua conexão e tente novamente", null))
            }

        }
    }

    fun getAllNewsHighlights(count: Int) {
        Log.d("getAllNewsHighlights: ", "getAllNewsHighlights =>")
        val mNews: ArrayList<New> = arrayListOf()
        viewModelScope.launch {
            try {
                val result: ResponseNewHighlights? = newsRepository.getAllNewsHighlights()
                if (result != null) {
                    if (result.data.size > count) {
                        for (new in result.data) {
                            val mNew = New()
                            mNew.author = new.author
                            mNew.content = new.content
                            mNew.description = new.description
                            mNew.highlight = new.highlight
                            mNew.image_url = new.image_url
                            mNew.published_at = getDateFormatted(new.published_at)
                            mNew.title = new.title
                            mNew.url = new.url

                            mNews.add(mNew)
                        }
                        _newsHighlights.value = (Resource.success(mNews))
                    }
                    saveTotalNews(result.data.size, "newsHighLights")
                    Log.e("getAllNewsHighlights", "News size => : ${result.data.size} / count => $count")
                }


            } catch (error: Exception) {
                Log.e("getAllNewsHighlights", "ERROR => : ${error}")
                _news.value = (Resource.error("Verifique sua conexão e tente novamente", null))
            }
            if (mNews.size > count) {
                try {
                    newsRepository.insertNews(mNews)
                } catch (error: Exception) {
                    Log.e("getAllNewsHighlights", "ERROR ROOM => : ${error}")
                }
            }
        }
    }

    fun addNewsFavorite(new: New) {
        newsRepository.updateNew(new)
    }

    fun removeNewsFavorite(new: New) {
        newsRepository.updateNew(new)
    }

    private fun saveLastPage(lastPage: Int) {
        sessionManager.saveLastPage(lastPage + 1)
    }

    private fun saveTotalPages(totalPages: Int) {
        sessionManager.saveTotalPages(totalPages)
    }

    private fun saveTotalNews(total: Int, tag: String) {
        sessionManager.saveTotalNews(total, tag)
    }

    fun getLastPage() {
        _lastPage.value = sessionManager.getLastPage()
    }

    fun getTotalPages() {
        Log.d("facebookLogin: ", "getTotalPages: ${sessionManager.getTotalPages()}")
        _totalPages.value = sessionManager.getTotalPages()
    }

    fun getTotalNews(tag: String) {
        val totalNews = sessionManager.getTotalNews(tag)
        if (tag == "news") {
            _totalNews.value = totalNews
        } else {
            _totalNewsHighLight.value = totalNews
        }
    }

    private fun getDateFormatted(dateOriginal: String): Date? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        return inputFormat.parse(dateOriginal)

    }
}