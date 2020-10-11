package com.aldemir.mesanews.ui.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.aldemir.mesanews.R
import com.aldemir.mesanews.Resource
import com.aldemir.mesanews.data.api.SessionManager
import com.aldemir.mesanews.data.database.NewDataBase
import com.aldemir.mesanews.data.model.ResponseNew
import com.aldemir.mesanews.data.model.ResponseNewHighlights
import com.aldemir.mesanews.data.repository.news.NewsRepository
import com.aldemir.mesanews.ui.feed.domain.New
import kotlinx.coroutines.launch

class FeedViewModel(
    private val newsRepository: NewsRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _lastPage = MutableLiveData<Int>()
    val lastPage: LiveData<Int> = _lastPage

    private val _totalPages = MutableLiveData<Int>()
    val totalPages: LiveData<Int> = _totalPages

    private val _newsDatabase = MutableLiveData<List<New>>()
    var newsDatabase: LiveData<List<New>> = _newsDatabase

    private val _news = MutableLiveData<Resource<List<New>>>((Resource.loading(null)))
    var news: LiveData<Resource<List<New>>> = _news

    private val _newsHighlights = MutableLiveData<Resource<List<New>>>((Resource.loading(null)))
    var newsHighlights: LiveData<Resource<List<New>>> = _newsHighlights

    fun getNewsDatabase() {
            try {
               newsDatabase = newsRepository.getNewsDatabase()
            }catch (error: Exception) {
                Log.e("facebookLogin: ", "ERROR ROOM => : ${error}")
            }
    }

    fun insertNew() {
        val new = New(
            0,
            "Noticias teste  1",
            "essa é uma noticia para testar o room com o live date",
            "The logistical and technical debacle",
            "Aldemir gomes",
            "24/09/1983 : 13:00:09",
            false,
            "https://www.engadget.com/2020/02/07/podcast-tech-failed-iowa-caucus/",
            "https://o.aolcdn.com/images/dims?thumbnail=1200%2C630&quality=80&image_uri=https%3A%2F%2Fo.aolcdn.com%2Fimages%2Fdims%3Fcrop%3D1600%252C719%252C0%252C0%26quality%3D85%26format%3Djpg%26resize%3D1600%252C719%26image_uri%3Dhttps%253A%252F%252Fs.yimg.com%252Fos%252Fcreatr-uploaded-images%252F2019-10%252F9251a820-f130-11e9-9df5-19d3f012f517%26client%3Da1acac3e1b3290917d92%26signature%3D569bb91dab0b6654f0fc0287924bc32aa29d22ba&client=amp-blogside-v2&signature=29c7441da69c3b0234a720e23f0815964826b843"
        )
        try {
            val id = newsRepository.insertNew(new)
            Log.d("facebookLogin: ", "New insert success => id: $id")
        }catch (error: Exception) {
            Log.e("facebookLogin: ", "ERROR INSERT NEW => : ${error}")
        }
    }

    fun getAllNews(lastPage: Int, perPage: Int) {
        Log.d("facebookLogin: ", "getAllNews => lastPage: $lastPage")
        viewModelScope.launch {
            val mNews: ArrayList<New> = arrayListOf()
            try {
                val result: ResponseNew? = newsRepository.getAllNews(lastPage, perPage)

                if (result != null) {
                    for (new in result.data) {
                        val mNew = New()
                        mNew.author = new.author
                        mNew.content = new.content
                        mNew.description = new.description
                        mNew.highlight = new.highlight
                        mNew.image_url = new.image_url
                        mNew.published_at = new.published_at
                        mNew.title = new.title
                        mNew.url = new.url

                        mNews.add(mNew)
                    }
                    saveLastPage(result.pagination.current_page)
                    saveTotalPages(result.pagination.total_pages)
                    _news.value = (Resource.success(null))
                }

            }catch (error: Exception){
                Log.e("facebookLogin: ", "ERROR getAllNews  => : ${error}")
                _news.value = (Resource.error("Verifique sua conexão e tente novamente", null))
            }

            try {
                newsRepository.insertNews(mNews)
            }catch (error: Exception) {
                Log.e("facebookLogin: ", "ERROR ROOM => : ${error}")
            }
        }
    }
    fun getFavorites(isFavorite: Boolean) {
        viewModelScope.launch {
            try {
                newsDatabase  = newsRepository.getFavorites(isFavorite)

            }catch (error: Exception){
                Log.e("facebookLogin: ", "ERROR => : ${error}")
                _news.value = (Resource.error("Verifique sua conexão e tente novamente", null))
            }
        }
    }

    fun getAllNewsHighlights() {
        Log.d("workManager: ", "getAllNews =>")
        viewModelScope.launch {
            try {
                val result: ResponseNewHighlights? = newsRepository.getAllNewsHighlights()
                val mNews: ArrayList<New> = arrayListOf()
                if (result != null) {
                    for (new in result.data) {
                        val mNew = New()
                        mNew.author = new.author
                        mNew.content = new.content
                        mNew.description = new.description
                        mNew.highlight = new.highlight
                        mNew.image_url = new.image_url
                        mNew.published_at = new.published_at
                        mNew.title = new.title
                        mNew.url = new.url

                        mNews.add(mNew)
                    }
                    _newsHighlights.value = (Resource.success(mNews))
                }

            }catch (error: Exception){
                Log.e("facebookLogin: ", "ERROR => : ${error}")
                _news.value = (Resource.error("Verifique sua conexão e tente novamente", null))
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
        sessionManager.saveLastPage(lastPage+1)
    }

    private fun saveTotalPages(totalPages: Int) {
        sessionManager.saveTotalPages(totalPages)
    }

    fun getLastPage() {
        _lastPage.value = sessionManager.getLastPage()
    }

    fun getTotalPages() {
        Log.d("facebookLogin: ", "getTotalPages: ${sessionManager.getTotalPages()}")
        _totalPages.value = sessionManager.getTotalPages()
    }
}