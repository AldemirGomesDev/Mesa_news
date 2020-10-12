package com.aldemir.mesanews.ui.filter

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.aldemir.mesanews.R
import com.aldemir.mesanews.ui.feed.domain.New
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class FilterFragment : Fragment() {

    private val filterViewModel: FilterViewModel by viewModel()
    private lateinit var mContext: Context
    private lateinit var adapter: FilterAdapter
    private var mNews: List<New> = arrayListOf()
    private var mSearch: String = ""
    private var dateInitial: String = ""
    private var dateFinal: String = ""
    private var isFavorite = false
    var cal = Calendar.getInstance()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        container_filter.setOnFocusChangeListener{p0, p1 ->
            hideKeyBoard()
        }

        container_filter.setOnClickListener {
            hideKeyBoard()
        }

        autoComplete_news.addTextChangedListener(object : TextWatcher {
            val coroutineScope = CoroutineScope(Dispatchers.Main)
            val debouncePeriod: Long = 800
            var searchJob: Job? = null

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(search: CharSequence?, start: Int, before: Int, count: Int) {
                mSearch = search.toString()
                if (search!!.isNotEmpty()) {
                    Log.d("search_filters", "Chegou no ultimo 1. $search")
                    searchJob?.cancel()
                    searchJob = coroutineScope.launch {
                        delay(debouncePeriod)
                        Log.w("search_filters", "Chegou no ultimo last delay: $search isFavorite: $isFavorite")
                    filterViewModel.getNewsFilter(search.toString(), isFavorite)
                    }
                }
            }

        })

        val checkBox = check_box_favorite
        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            isFavorite = isChecked
        }
        button_apply.setOnClickListener {
            applyFilters()
        }
        clear_filters.setOnClickListener {
            removeFilters()
        }
        observers()
        setupRecyclerView()
        pickerDate()
    }

    private fun observers() {
        filterViewModel.newsDatabase.observe(viewLifecycleOwner, androidx.lifecycle.Observer { news ->
            if (mSearch.length < 2){
            Log.d("search_filters: ", "search ROOM => news.size: ${news.size} search: ${mSearch.length}")
                recyclerView_news_filters.visibility = View.GONE
            }
            else {
                renderList(news)
                recyclerView_news_filters.visibility = View.VISIBLE
            }
        })
    }

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    private fun applyFilters() {
        filterViewModel.getNewsFilter(mSearch, isFavorite)
    }

    private fun removeFilters() {
        mSearch = ""
        isFavorite = false
        autoComplete_news.setText("")
        check_box_favorite.isChecked = false
        filterViewModel.getNewsFilter(mSearch, isFavorite)
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(activity)
        recyclerView_news_filters.layoutManager = layoutManager
        adapter =
            FilterAdapter(
                arrayListOf(),
                mContext
            )
        recyclerView_news_filters.addItemDecoration(
            DividerItemDecoration(
                recyclerView_news_filters.context,
                (recyclerView_news_filters.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView_news_filters.adapter = adapter
    }

    private fun renderList(list: List<New>) {
        list.sortedByDescending { it.published_at }
        mNews = list
        adapter.addData(list)
        adapter.notifyDataSetChanged()
    }

    private fun pickerDate() {

        val dateSetListenerInitial =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                date_initial.text = updateDateInView()
                dateInitial = updateDateInView()
                Log.d("DatePicker", "dateInitial: $dateInitial")
            }
        val dateSetListenerFinal =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                date_final.text = updateDateInView()
                dateFinal = updateDateInView()
                Log.d("DatePicker", "dateFinal: $dateFinal")
            }

        image_date_initial.setOnClickListener {
            DatePickerDialog(
                mContext,
                dateSetListenerInitial,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        image_date_final.setOnClickListener {
            DatePickerDialog(
                mContext,
                dateSetListenerFinal,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateDateInView(): String {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        return sdf.format(cal.time)
    }

    private fun showKeyBoard() {
        try {
            val mImm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            mImm.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.RESULT_UNCHANGED_SHOWN
            )

        }catch (err: Exception) {
            Log.e("keyboard_hide", "hideKeyboard Error: $err")
        }

    }

    private fun hideKeyBoard() {
        try {
            val mImm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            mImm.hideSoftInputFromWindow(view?.windowToken, 0)
            mImm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)

        }catch (err: Exception) {
            Log.e("keyboard_hide", "hideKeyboard Error: $err")
        }
    }

}