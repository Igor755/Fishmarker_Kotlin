package com.company.fishmarker_kotlin.fragments_profile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.company.fishmarker_kotlin.R
import com.company.fishmarker_kotlin.modelclass.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {

    val mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    val firebaseDatabase : FirebaseDatabase = FirebaseDatabase.getInstance()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)



/*
        view?.edit_name?.setText("kukuk")

        edit_last_name
        edit_location
        edit_email
        edit_telephone
        edit_preferred_type_of_fishing
        edit_trophies
        edit_about_me
        */

        var user = User()
        val myRef : Query = firebaseDatabase.getReference("Users").orderByKey().equalTo(mAuth.currentUser?.uid)

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataSnapshot1 : DataSnapshot in dataSnapshot.children) {

                    user = dataSnapshot1.getValue(User::class.java)!!
                    println(user)

                }
                view?.edit_name?.setText(user.user_name)
                edit_last_name
                view?.edit_location?.setText(user.location)
                view?.edit_email?.setText(user.email)
                view?.edit_telephone?.setText(user.telephone)
                view?.edit_telephone?.setText(user.telephone)

                }





            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })




        return view
    }


}