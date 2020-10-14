package com.aldemir.mesanews.ui.filter

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.aldemir.mesanews.R
import com.aldemir.mesanews.ui.feed.domain.New
import com.aldemir.mesanews.ui.web_detail_new.DetailNewActivity
import kotlinx.android.synthetic.main.fragment_filter.*
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class FilterFragment : Fragment(), FilterAdapter.ClickListener{

    private val filterViewModel: FilterViewModel by viewModel()
    private lateinit var mContext: Context
    private lateinit var adapter: FilterAdapter
    private var mNews: List<New> = arrayListOf()
    private var mSearch: String = ""
    private var dateInitial: Date? = null
    private var dateFinal: Date? = Date()
    private var isFavorite = false
    private var cal = Calendar.getInstance()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_filter, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dateInitial = dateInitial()
        setupUI()
        observers()
        setupRecyclerView()
        pickerDate()

    }

    private fun setupUI() {
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
                    searchJob?.cancel()
                    searchJob = coroutineScope.launch {
                        delay(debouncePeriod)
                        filterViewModel.getNewsFilter(search.toString(), isFavorite, dateInitial!!, dateFinal!!)
                    }
                }
            }

        })

        check_box_favorite.setOnCheckedChangeListener { buttonView, isChecked ->
            isFavorite = isChecked
        }
        button_apply.setOnClickListener {
            applyFilters()
        }
        clear_filters.setOnClickListener {
            removeFilters()
        }
    }

    private fun observers() {
        filterViewModel.newsDatabase.observe(viewLifecycleOwner, androidx.lifecycle.Observer { news ->
            if (news.isNotEmpty()){
                renderList(news)
                recyclerView_news_filters.visibility = View.VISIBLE
            }
            else {
                recyclerView_news_filters.visibility = View.GONE
            }
        })
    }

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    private fun applyFilters() {
        filterViewModel.getNewsFilter(mSearch, isFavorite, dateInitial!!, dateFinal!!)
    }

    private fun removeFilters() {
        mSearch = ""
        isFavorite = false
        autoComplete_news.setText("")
        check_box_favorite.isChecked = false
        date_initial.text = resources.getString(R.string.text_data_inicial)
        date_final.text = resources.getString(R.string.text_data_final)
        dateInitial = dateInitial()
        dateFinal = Date()
        renderList(arrayListOf())
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

        adapter.setOnItemClickListener(this)
        adapter.setOnItemClickListenerFavorite(this)
        adapter.setOnItemClickListenerShared(this)
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
                dateInitial = if (formatDateDataBase(cal.time) == null) {
                    Date()
                }else {
                    formatDateDataBase(cal.time)
                }
                Log.d("DatePicker", "dateInitial: $dateInitial")
            }
        val dateSetListenerFinal =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                date_final.text = updateDateInView()
                dateFinal = if (formatDateDataBase(cal.time) == null) {
                    Date()
                }else {
                    formatDateDataBase(cal.time)
                }
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

    private fun formatDateDataBase(date: Date): Date? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val outputFormat = inputFormat.format(date)
        val date = inputFormat.parse(outputFormat)
        return date
    }

    private fun dateInitial(): Date? {
        var date: Date? = null
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        try {
            date = format.parse("1500-01-01T17:15:05.000Z")
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.d("formatDateDataBase", "error parse => : $e ")

        }

        return date
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

    override fun onClick(position: Int, aView: View) {
        val intent = DetailNewActivity.newIntent(mContext, mNews[position].url!!)
        startActivity(intent)
    }

    override fun onClickFavorite(position: Int, aView: View) {
        mNews[position].is_favorite = !mNews[position].is_favorite
        if (mNews[position].is_favorite) {
            filterViewModel.addNewsFavorite(mNews[position])
        } else {
            filterViewModel.removeNewsFavorite(mNews[position])
        }
        adapter.addData(mNews)
        adapter.notifyDataSetChanged()
    }

    override fun onClickShared(position: Int, aView: View) {
        val shareTask = mNews[position].url!!
        val dialog = AlertDialog.Builder(mContext).setTitle("Info").setMessage("Você deseja compartilhar?")
            .setPositiveButton("Sim") { dialog, _ ->
                setShareIntent(shareTask(shareTask))
                dialog.dismiss()
            }
            .setNegativeButton("Não") { dialog, _ ->
                dialog.dismiss()
            }
        dialog.show()
    }

    private fun setShareIntent(shareBody: String){
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
        startActivity(Intent.createChooser(sharingIntent, "Visualizar notícia"))
    }
    private fun shareTask(str: String): String {
        val resp = "Visualizar essa notícia:\n"+str+""
        return resp
    }

}