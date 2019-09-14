package com.company.fishmarker_kotlin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.company.fishmarker_kotlin.modelclass.Place
import com.company.fishmarker_kotlin.modelclass.WaterObject


class AdapterPlace(var context: Context?, val listplace: List<Place>?) : BaseAdapter() {

    val layoutInflater : LayoutInflater? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val holder : ViewHolder

        if (convertView == null){
            val convertView = layoutInflater?.inflate(com.company.fishmarker_kotlin.R.layout.one_item_place, null)
            holder = ViewHolder()
            holder.imagePhoto = convertView?.findViewById(com.company.fishmarker_kotlin.R.id.imageViewWater) as ImageView
            holder.namePhoto = convertView.findViewById(com.company.fishmarker_kotlin.R.id.textViewWaterName) as TextView
            convertView.setTag(holder)

        }else{

            holder = convertView.getTag() as ViewHolder

        }

        val WaterObject = listplace?.get(position)

       /* holder.namePhoto.setText(WaterObject.getName())


        val imageId = this.getMipmapResIdByName(WaterObject.getPhoto())



        holder.imagePhoto.setImageResource(imageId)
*/




        return convertView!!
    }


    override fun getItem(position: Int): Any {
        return listplace?.get(position)!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return listplace?.size!!
    }
    class ViewHolder{

        var imagePhoto : ImageView? = null
        var namePhoto : TextView? = null
    }
}