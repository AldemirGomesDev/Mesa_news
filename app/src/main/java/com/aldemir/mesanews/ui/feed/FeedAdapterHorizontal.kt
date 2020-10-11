package com.aldemir.mesanews.ui.feed

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.aldemir.mesanews.R
import com.aldemir.mesanews.ui.feed.domain.New
import com.aldemir.mesanews.ui.web_detail_new.DetailNewActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_horizontal.view.*
import kotlinx.android.synthetic.main.item_news.view.*

class FeedAdapterHorizontal(private val users: ArrayList<New>)
    : RecyclerView.Adapter<FeedAdapterHorizontal.DataViewHolder>() {

    lateinit var mClickListener: ClickListener

    fun setOnItemClickListener(aClickListener: ClickListener) {
        mClickListener = aClickListener
    }

    interface ClickListener {
        fun onClickCarousel(position: Int, aView: View)
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        fun bind(new: New) {
            itemView.text_view_title_horizontal.text = new.title.substring(0, 40)
            Glide.with(itemView.imageViewNewHorizontal.context)
                .load(new.image_url)
                .into(itemView.imageViewNewHorizontal)
        }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            mClickListener.onClickCarousel(adapterPosition, v)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_horizontal, parent,
                false
            )
        )

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) =
        holder.bind(users[position])

    fun addData(list: List<New>) {
        users.addAll(list)
        Log.d("workManager: ", "list => : ${list}")
    }

}