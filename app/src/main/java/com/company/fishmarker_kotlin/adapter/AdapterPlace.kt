package com.company.fishmarker_kotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.company.fishmarker_kotlin.R
import com.company.fishmarker_kotlin.modelclass.Place

class AdapterPlace(private val list: List<Place>) : RecyclerView.Adapter<AdapterPlace.RecyclerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterPlace.RecyclerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RecyclerViewHolder(inflater, parent)
    }


    override fun onBindViewHolder(holder: AdapterPlace.RecyclerViewHolder, position: Int) {
        val place: Place = list[position]
        holder.bind(place)
    }

    override fun getItemCount(): Int = list.size




    class RecyclerViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.one_item_place, parent, false)){

        private var txtnameplace: TextView? = null
        private var txtlatitude: TextView? = null
        private var txtlongitude: TextView? = null
        private var txtzoom: TextView? = null

        init {
            txtnameplace = itemView.findViewById(R.id.txtnameplace)
            txtlatitude = itemView.findViewById(R.id.txtlatitude)
            txtlongitude = itemView.findViewById(R.id.txtlongitude)
            txtzoom = itemView.findViewById(R.id.txtzoom)

        }

        fun bind(place: Place) {

            txtnameplace?.text = place.name_place
            txtlatitude?.text = place.toString()
            txtlongitude?.text = place.toString()
            txtzoom?.text = place.zoom.toString()
        }



    }
}
