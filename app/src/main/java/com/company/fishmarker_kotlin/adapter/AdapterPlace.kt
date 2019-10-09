package com.company.fishmarker_kotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.company.fishmarker_kotlin.R
import com.company.fishmarker_kotlin.modelclass.Place

class AdapterPlace(private val list: List<Place>) : RecyclerView.Adapter<AdapterPlace.RecyclerViewHolder>() {


    lateinit var mClickListener: ClickListener

    fun setOnItemClickListener(aClickListener: ClickListener) {
        mClickListener = aClickListener
    }

    interface ClickListener {
        fun onClick(pos: Int, aView: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RecyclerViewHolder(inflater, parent)
    }


    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val place: Place = list[position]
        holder.bind(place)
    }

    override fun getItemCount(): Int = list.size




    inner class RecyclerViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.one_item_place, parent, false)), View.OnClickListener{

        override fun onClick(v: View) {
            mClickListener.onClick(adapterPosition, v)
        }

        private var txtnameplace: TextView? = null
        private var txtlatitude: TextView? = null
        private var txtlongitude: TextView? = null
        private var txtzoom: TextView? = null

        init {
            txtnameplace = itemView.findViewById(R.id.txtnameplace)
            txtlatitude = itemView.findViewById(R.id.txtlatitude)
            txtlongitude = itemView.findViewById(R.id.txtlongitude)
            txtzoom = itemView.findViewById(R.id.txtzoom)

            itemView.setOnClickListener(this)

        }

        fun bind(place: Place) {

            txtnameplace?.text = place.name
            txtlatitude?.text = place.latitude.toString()
            txtlongitude?.text = place.longitude.toString()
            txtzoom?.text = place.zoom.toString()
        }



    }
}
