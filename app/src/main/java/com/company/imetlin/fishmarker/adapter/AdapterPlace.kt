package com.company.imetlin.fishmarker.adapter

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.company.imetlin.fishmarker.R
import androidx.recyclerview.widget.RecyclerView
import com.company.imetlin.fishmarker.helper_class.StaticHelper
import com.company.imetlin.fishmarker.modelclass.Place
import com.google.firebase.database.*
import java.lang.String

class AdapterPlace(var list: MutableList<Place>) : RecyclerView.Adapter<AdapterPlace.RecyclerViewHolder>() {


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
    fun refreshPlaceList(list: MutableList<Place>) {
        this.list = list
        notifyDataSetChanged()
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
        private var btnDelete: ImageButton? = null

        init {
            txtnameplace = itemView.findViewById(R.id.txtnameplace)
            txtlatitude = itemView.findViewById(R.id.txtlatitude)
            txtlongitude = itemView.findViewById(R.id.txtlongitude)
            txtzoom = itemView.findViewById(R.id.txtzoom)
            btnDelete = itemView.findViewById(R.id.btnDelete)
            itemView.setOnClickListener(this)
        }

        fun bind(place: Place) {

            txtnameplace?.text = itemView.context.resources.getString(R.string.name_place) + " "  + place.name
            txtlatitude?.text = itemView.context.resources.getString(R.string.lat_c) + " "  + place.latitude.toString()
            txtlongitude?.text = itemView.context.resources.getString(R.string.lon_c) + " "  + place.longitude.toString()
            txtzoom?.text = itemView.context.resources.getString(R.string.zoom) + " "  +  place.zoom.toString()

            btnDelete?.setOnClickListener {
                val builder = AlertDialog.Builder(itemView.context)
                builder.setTitle("Delete place")
                builder.setMessage("Are you seriosly want add place?")

                builder.setPositiveButton("YES"){ _, _ ->

                    val ref = FirebaseDatabase.getInstance().reference
                    val delmark: Query = ref.child("Place").orderByChild("id").equalTo(place.id)

                    val iterator: MutableListIterator<Place> = StaticHelper.allplace.listIterator()

                    while (iterator.hasNext()) {
                        val next: Place = iterator.next()
                        if (next.id == place.id) {
                            iterator.remove()
                            break
                        }
                    }


                    delmark.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (appleSnapshot in dataSnapshot.children) {
                                appleSnapshot.ref.removeValue()
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.e(TAG, String.valueOf(R.string.cancel), databaseError.toException())
                        }
                    })


                    list.removeAt(position)
                    refreshPlaceList(list)
                    Toast.makeText(itemView.context, R.string.delete, Toast.LENGTH_LONG).show()


                }
                builder.setNegativeButton("No"){ _, _ ->
                    Toast.makeText(itemView.context,"You are not agree.", Toast.LENGTH_SHORT).show()
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }


        }



    }
}
