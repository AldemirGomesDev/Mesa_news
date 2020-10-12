package com.aldemir.mesanews.ui.feed

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aldemir.mesanews.R
import com.aldemir.mesanews.Status
import com.aldemir.mesanews.ui.feed.domain.New
import com.aldemir.mesanews.ui.web_detail_new.DetailNewActivity
import com.aldemir.mesanews.workbackground.WorkFetchData
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.fixedRateTimer


class FeedFragment : Fragment(), FeedAdapter.ClickListener, FeedAdapterHorizontal.ClickListener {

    private var mLastPage: Int = 1
    private var mPerPage: Int = 10
    private var mTotalPages: Int = 1
    private var mTotalNews: Int = 0
    private var mTotalNewsHighlights: Int = 0
    private lateinit var myFixedRateTimer: Timer
    private lateinit var adapter: FeedAdapter
    private lateinit var adapterHorizontal: FeedAdapterHorizontal
    private lateinit var mContext: Context
    private val mNews: ArrayList<New> = arrayListOf()
    private var mNewsCarousel: List<New> = arrayListOf()
    private val feedViewModel: FeedViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_feed, container, false)

        feedViewModel.getTotalNews("news")
        feedViewModel.getTotalNews("newsHighLights")
        feedViewModel.getLastPage()
        feedViewModel.getTotalPages()
        feedViewModel.getNewsDatabase()
        feedViewModel.getNewsHighLight(true)

        WorkFetchData(mContext, feedViewModel).fetchData()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments!=null) {
            Log.d("DatePicker", "${arguments?.getString("date_initial")}")
            Log.d("DatePicker", "${arguments?.getString("date_final")}")
            Log.d("DatePicker", "${arguments?.getBoolean("favorite")}")
            val isFavorite = arguments?.getBoolean("favorite")
            feedViewModel.getFavorites(isFavorite!!)
        }

        setupUI()
        setupObservers()
        showLoading()

        val fab = fab_feed
        fab.setOnClickListener { view ->
            feedViewModel.insertNew()

            Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()
        }

    }

    private fun updateNews() {
        myFixedRateTimer = fixedRateTimer("timer",false,30000,30000){
            requireActivity().runOnUiThread {
                Log.d("getAllNews: ", "Timer Rate in fragment")
                feedViewModel.getLastPage()
                feedViewModel.getTotalPages()
                feedViewModel.getNewsDatabase()
                feedViewModel.getNewsHighLight(true)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

    }

    override fun onResume() {
        super.onResume()
        updateNews()
        Log.d("DatePicker: ", "Timer Rate onResume")
    }

    override fun onStop() {
        super.onStop()
        myFixedRateTimer.cancel()
        Log.d("DatePicker: ", "Timer Rate onStop")
    }

    private fun setupObservers() {
        feedViewModel.newsHighlights.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data!!.size >= mTotalNewsHighlights) {
                        feedViewModel.getNewsHighLight(true)
                    }
                }
                Status.LOADING -> {

                }
                Status.ERROR -> {
                    //Handle Error
                    Toast.makeText(activity, "${it.message}", Toast.LENGTH_LONG).show()


                }
            }
        })

        feedViewModel.news.observe(viewLifecycleOwner, Observer {
            Log.d("workManager: ", "Result => : ${it.data}")
            Log.d("workManager: ", "status => : ${it.status}")
            when (it.status) {
                Status.SUCCESS -> {
                    showNews()
                    feedViewModel.getLastPage()
                    feedViewModel.getTotalPages()
                }
                Status.LOADING -> {
                    showLoading()
                }
                Status.ERROR -> {
                    Toast.makeText(activity, "${it.message}", Toast.LENGTH_LONG).show()
                    showNews()

                }
            }
        })

        feedViewModel.totalNewsHighLight.observe(viewLifecycleOwner, Observer { totalNews ->
            mTotalNewsHighlights = totalNews
        })

        feedViewModel.totalNews.observe(viewLifecycleOwner, Observer { totalNews ->
            mTotalNews = totalNews
        })

        feedViewModel.lastPage.observe(viewLifecycleOwner, Observer { lastPage ->
            mLastPage = lastPage
        })

        feedViewModel.totalPages.observe(viewLifecycleOwner, Observer { totalPages ->
            mTotalPages = totalPages
            Log.d("getAllNews: ", "getNewsDatabase ROOM => mLastPage: $mLastPage | mTotalPages: $mTotalPages")
            if (mLastPage <= mTotalPages) {
                feedViewModel.getAllNews(mLastPage, mPerPage, mTotalNews)
            }
        })

        feedViewModel.newsDatabase.observe(viewLifecycleOwner, Observer { news ->
            Log.d("DatePicker: ", "getNewsDatabase ROOM => news.size: ${news.size}")
            Log.d("DatePicker: ", "getNewsDatabase Page: $mLastPage/$mTotalPages")
            feedViewModel.getTotalNews("news")
            showNews()
            renderList(news)
        })

        feedViewModel.newsHighLight.observe(viewLifecycleOwner, Observer { news ->
            Log.d("caroussel_news: ", "list => : ${news.size}")
            feedViewModel.getTotalNews("newsHighLights")
            showNews()
            renderListHorizontal(news)
            feedViewModel.getAllNewsHighlights(mTotalNewsHighlights)
        })
    }

    private fun setupUI() {
        val layoutManager = LinearLayoutManager(activity)
        recyclerView_news.layoutManager = layoutManager
        adapter =
            FeedAdapter(
                arrayListOf(),
                mContext
            )
        recyclerView_news.addItemDecoration(
            DividerItemDecoration(
                recyclerView_news.context,
                (recyclerView_news.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView_news.adapter = adapter
        endlessScrollingSearch(layoutManager)

        adapter.setOnItemClickListener(this)
        adapter.setOnItemClickListenerFavorite(this)

        //==========recyclerview horizontal=================

        val horizontalLayoutManagaer =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView_news_horizontal.layoutManager = horizontalLayoutManagaer
        adapterHorizontal =
            FeedAdapterHorizontal(
                arrayListOf()
            )
        recyclerView_news_horizontal.addItemDecoration(
            DividerItemDecoration(
                recyclerView_news_horizontal.context,
                (recyclerView_news_horizontal.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView_news_horizontal.adapter = adapterHorizontal
        adapterHorizontal.setOnItemClickListener(this)
    }

    private fun renderListHorizontal(list: List<New>) {
        mNewsCarousel = list
        adapterHorizontal.addData(list)
        adapterHorizontal.notifyDataSetChanged()
    }

    private fun renderList(list: List<New>) {
        list.sortedByDescending { it.published_at }
        mNews.addAll(list)
        adapter.addData(list)
        adapter.notifyDataSetChanged()
    }

    private fun showNews() {
        loading_news.visibility = View.GONE
        recyclerView_news.visibility = View.VISIBLE
        recyclerView_news_horizontal.visibility = View.VISIBLE
    }

    private fun showLoading() {
        loading_news.visibility = View.VISIBLE
        recyclerView_news.visibility = View.GONE
        recyclerView_news_horizontal.visibility = View.GONE
    }

    private fun endlessScrollingSearch(layoutManager: LinearLayoutManager) {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        val debouncePeriod: Long = 800
        var searchJob: Job? = null

        recyclerView_news.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                val currentItems = layoutManager.childCount
                val scrollOutItems = layoutManager.findFirstVisibleItemPosition()
                val totalItems = layoutManager.itemCount

                if (currentItems + scrollOutItems >= totalItems) {
                    Log.d("facebookLogin", "Chegou no ultimo 1.")
                    searchJob?.cancel()
                    searchJob = coroutineScope.launch {
                        delay(debouncePeriod)
                        Log.w("facebookLogin", "Chegou no ultimo lastPage: $mLastPage")
                    }
                }//fim do if
            }
        })
    }

    override fun onClick(position: Int, aView: View) {
        Log.d("facebookLogin", "Item $position clicado => url: ${mNews[position].title}")
        val intent = DetailNewActivity.newIntent(mContext, mNews[position].url!!)
        startActivity(intent)
    }

    override fun onClickFavorite(position: Int, aView: View) {
        mNews[position].is_favorite = !mNews[position].is_favorite
        if (mNews[position].is_favorite) {
            feedViewModel.addNewsFavorite(mNews[position])
        } else {
            feedViewModel.removeNewsFavorite(mNews[position])
        }
        adapter.addData(mNews)
        adapter.notifyDataSetChanged()
        Log.d("DatePicker: ", "is_favorite => : ${mNews[position].is_favorite}")

    }

    override fun onClickCarousel(position: Int, aView: View) {
        Log.d("facebookLogin", "Item $position clicado => url: ${mNews[position].title}")
        val intent = DetailNewActivity.newIntent(mContext, mNews[position].url!!)
        startActivity(intent)
    }
}