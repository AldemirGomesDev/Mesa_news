package com.aldemir.mesanews.ui.feed

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aldemir.mesanews.R
import com.aldemir.mesanews.ui.feed.domain.New
import com.aldemir.mesanews.ui.filter.FilterAdapter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_news.view.*

class FeedAdapter(private var users: List<New>, private var context: Context)
    : RecyclerView.Adapter<FeedAdapter.DataViewHolder>() {

    lateinit var mClickListener: ClickListener
    lateinit var mClickListenerFavorite: ClickListener
    lateinit var mClickListenerShared: ClickListener

    fun setOnItemClickListener(aClickListener: ClickListener) {
        mClickListener = aClickListener
    }

    fun setOnItemClickListenerFavorite(aClickListener: ClickListener) {
        mClickListenerFavorite = aClickListener
    }

    fun setOnItemClickListenerShared(aClickListener: ClickListener) {
        mClickListenerShared = aClickListener
    }

    interface ClickListener {
        fun onClick(position: Int, aView: View)
        fun onClickFavorite(position: Int, aView: View)
        fun onClickShared(position: Int, aView: View)
    }
   inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener{
        fun bind(new: New) {
            itemView.text_view_title.text = new.title
            itemView.text_view_published_at.text = new.description
            Glide.with(itemView.imageViewNew.context)
                .load(new.image_url)
                .into(itemView.imageViewNew)

            if (new.is_favorite){
                itemView.image_favorite.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary))
                itemView.text_favorite.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                itemView.text_favorite.text = "Desfavoritar"
            }else {
                itemView.image_favorite.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent))
                itemView.text_favorite.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                itemView.text_favorite.text = "Favoritar"
            }
        }

        override fun onClick(v: View) {
            mClickListener.onClick(adapterPosition, v)
        }

        init {
            itemView.setOnClickListener(this)
            itemView.button_favorite.setOnClickListener {
                mClickListenerFavorite.onClickFavorite(adapterPosition, it)
            }
            itemView.button_shared.setOnClickListener {
                mClickListenerShared.onClickShared(adapterPosition, it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_news, parent,
                false
            )
        )

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) =
        holder.bind(users[position])

    fun addData(list: List<New>) {
        users = list
        notifyDataSetChanged()
        Log.d("DatePicker: ", "list => : ${list.size}")
    }

}