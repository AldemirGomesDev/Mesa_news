package com.aldemir.mesanews.ui.filter

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.aldemir.mesanews.R
import kotlinx.android.synthetic.main.fragment_gallery.*
import java.text.SimpleDateFormat
import java.util.*


class FilterFragment : Fragment() {

    private lateinit var filterViewModel: FilterViewModel
    private lateinit var mContext: Context
    private var dateInitial: String = ""
    private var dateFinal: String = ""
    private var isFavorite = false
    var cal = Calendar.getInstance()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        filterViewModel =
                ViewModelProviders.of(this).get(FilterViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val checkBox = check_box_favorite
        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            isFavorite = isChecked
        }
        button_apply.setOnClickListener {
            applyFilters()
        }
        pickerDate()
    }

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    private fun applyFilters() {
        val bundle = bundleOf(
            "date_initial" to dateInitial,
            "date_final" to dateFinal,
            "favorite" to isFavorite
        )
        findNavController().navigate(R.id.nav_home, bundle)
    }

    private fun pickerDate() {
        date_initial.text = "---/---/------"
        date_final.text = "---/---/------"

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

}