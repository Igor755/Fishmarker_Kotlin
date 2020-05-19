package com.company.imetlin.fishmarker.fragments_marker

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.company.imetlin.fishmarker.R
import com.company.imetlin.fishmarker.modelclass.MarkerDetail
import com.company.imetlin.fishmarker.modelclass.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.alert_big_detail_marker.*
import kotlinx.android.synthetic.main.alert_big_detail_marker.btnOk
import kotlinx.android.synthetic.main.alert_big_detail_marker.edit_dept
import kotlinx.android.synthetic.main.alert_big_detail_marker.edit_latitude
import kotlinx.android.synthetic.main.alert_big_detail_marker.edit_longitude
import kotlinx.android.synthetic.main.alert_big_detail_marker.edit_note
import kotlinx.android.synthetic.main.alert_big_detail_marker.edit_number_of_fish
import kotlinx.android.synthetic.main.alert_big_detail_marker.edit_title_marker

class BigDetailMarkerFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return  inflater.inflate(R.layout.alert_big_detail_marker, container, false)

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_big_detail_marker, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (when(item.itemId) {
            R.id.back_marker_map -> {

                activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.fragment_container_marker, MapMarkerFragment())
                    ?.commit()
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        val markerDetail : MarkerDetail
        markerDetail = bundle?.getSerializable("serializedObject") as MarkerDetail

        val query = FirebaseDatabase.getInstance().reference.child("Users").orderByChild("user_id").equalTo(markerDetail.uid)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataSnapshot1: DataSnapshot in dataSnapshot.children) {
                    val user: User = dataSnapshot1.getValue(User::class.java)!!
                    Picasso.get().load(user.url_photo).into(photo_user)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Maybe not internet (Failed to read value) :(" , Toast.LENGTH_SHORT).show()
            }
        })

        edit_latitude.setText(markerDetail.latitude.toString())
        edit_longitude.setText(markerDetail.longitude.toString())
        edit_title_marker.setText(markerDetail.title)
        edit_date.setText(markerDetail.date)
        edit_dept.setText(markerDetail.depth.toString())
        edit_number_of_fish.setText(markerDetail.amount.toString())
        edit_note.setText(markerDetail.note)


        btnOk.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_container_marker, MapMarkerFragment())
                ?.commit()

        }

    }
}
