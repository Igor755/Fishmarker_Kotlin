package com.company.imetlin.fishmarker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.company.imetlin.fishmarker.R
import com.company.imetlin.fishmarker.customview.spinner.DataSpinner
import kotlinx.android.synthetic.main.one_item_bait_for_detail.view.*

class AdapterBait (var list: MutableList<DataSpinner>) : RecyclerView.Adapter<AdapterBait.RecyclerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RecyclerViewHolder(inflater, parent)
    }
    fun refreshBaitList(list: MutableList<DataSpinner>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val dataSpinner: DataSpinner = list[position]
        holder.bind(dataSpinner)
    }

    override fun getItemCount(): Int = list.size

    inner class RecyclerViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.one_item_bait_for_detail, parent, false)){

        private var nameBait: TextView? = null
        private var imageBait: ImageView? = null

        init {
            nameBait = itemView.findViewById(R.id.nameBait)
            imageBait = itemView.findViewById(R.id.imageBait)
        }
        fun bind(dataSpinner: DataSpinner) {
            nameBait?.text = dataSpinner.name
            Glide.with(itemView.context).load(dataSpinner.image).dontTransform().into(itemView.imageBait)
        }
    }
}

