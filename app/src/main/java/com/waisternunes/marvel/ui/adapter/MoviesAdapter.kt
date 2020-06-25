package com.waisternunes.marvel.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.hawk.Hawk
import com.squareup.picasso.Picasso
import com.waisternunes.marvel.R
import com.waisternunes.marvel.api.ApiMovie
import com.waisternunes.marvel.ui.activity.DetailsActivity
import com.waisternunes.marvel.utils.HAWK_MOVIES
import com.waisternunes.marvel.utils.ItemMoveCallback
import com.waisternunes.marvel.utils.PARAM_MOVIE
import com.waisternunes.marvel.utils.StartDragListener
import java.util.*
import kotlin.collections.ArrayList

class MoviesAdapter(
    private val activity: Activity,
    private val mStartDragListener: StartDragListener
) :
    RecyclerView.Adapter<MoviesAdapter.ViewHolder>(), ItemMoveCallback.ItemTouchHelperContract {

    private var dataArray: ArrayList<ApiMovie> = ArrayList()

    fun setData(data: ArrayList<ApiMovie>) {
        this.dataArray = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(activity)
            .inflate(R.layout.item_movie_card, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (dataArray.size > position) {
            holder.setData(dataArray[position])

            holder.ivDrag.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    mStartDragListener.requestDrag(holder)
                }
                false
            }
        }
    }

    override fun getItemCount(): Int {
        return dataArray.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivPoster: ImageView = itemView.findViewById(R.id.iv_poster)
        var tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        var tvReleased: TextView = itemView.findViewById(R.id.tv_released)
        var ivDrag: ImageView = itemView.findViewById(R.id.iv_drag)

        fun setData(apiMovie: ApiMovie) {
            tvTitle.text = apiMovie.title
            tvReleased.text = apiMovie.released

            if (apiMovie.poster.isNotEmpty())
                Picasso.get().load(apiMovie.poster).into(ivPoster)

            itemView.setOnClickListener {
                val intent = Intent(activity, DetailsActivity::class.java)
                intent.putExtra(PARAM_MOVIE, apiMovie)
                activity.startActivity(intent)
            }
        }
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(dataArray, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(dataArray, i, i - 1)
            }
        }

        Hawk.put(HAWK_MOVIES, dataArray)

        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onRowSelected(viewHolder: ViewHolder) {
        viewHolder.itemView.setBackgroundColor(Color.LTGRAY)
    }

    override fun onRowClear(myViewHolder: ViewHolder) {
        myViewHolder.itemView.setBackgroundColor(Color.WHITE)
    }

}