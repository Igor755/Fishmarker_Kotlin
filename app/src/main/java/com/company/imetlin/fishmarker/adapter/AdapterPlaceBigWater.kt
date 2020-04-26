package com.company.imetlin.fishmarker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.company.imetlin.fishmarker.R
import com.company.imetlin.fishmarker.modelclass.BigWater
import com.facebook.FacebookSdk.getApplicationContext


class AdapterPlaceBigWater(context: Context) :
    ArrayAdapter<BigWater>(context, 0, BigWater.values()) {

    var layoutInflater: LayoutInflater? = null


    init {
        this.layoutInflater = LayoutInflater.from(context)
    }

    @Suppress("NAME_SHADOWING")
    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {


        val convertView = layoutInflater?.inflate(R.layout.one_item_big_water, null)
        val imagePhoto = convertView?.findViewById(R.id.imageViewWater) as ImageView
        val nameWaterName = convertView.findViewById(R.id.textViewWaterName) as TextView

        nameWaterName.text = getApplicationContext().getString(BigWater.values()[position].nameStringResource)
        Glide.with(context).load(BigWater.values()[position].photoWater).into(imagePhoto)

        return convertView
    }


}


