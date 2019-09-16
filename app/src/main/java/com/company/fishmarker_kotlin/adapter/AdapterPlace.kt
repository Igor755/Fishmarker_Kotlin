package com.company.fishmarker_kotlin.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.company.fishmarker_kotlin.R
import com.company.fishmarker_kotlin.modelclass.BigWater


class AdapterPlace(context: Context) : ArrayAdapter<BigWater>(context, 0, BigWater.values()) {

    var layoutInflater : LayoutInflater? = null



    init {
        this.layoutInflater = LayoutInflater.from(context)
    }

    @Suppress("NAME_SHADOWING")
    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {


             val convertView = layoutInflater?.inflate(R.layout.one_item_place, null)


            val imagePhoto = convertView?.findViewById(R.id.imageViewWater) as ImageView
            val nameWaterName = convertView.findViewById(R.id.textViewWaterName) as TextView

            nameWaterName.text = BigWater.values()[position].nameWater
            //imagePhoto.setImageResource(BigWater.values()[position].photoWater)
        Glide.with(context).load(BigWater.values()[position].photoWater).into(imagePhoto)

        return convertView
    }



}


