package com.company.fishmarker_kotlin.registration_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.company.fishmarker_kotlin.R

class SignInFragment : Fragment() {




    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)
        val btn_registration = view.findViewById<TextView>(R.id.btn_registration)
        val btn_forgot_password = view.findViewById<TextView>(R.id.btn_forgot_password)




        btn_registration.setOnClickListener{


            fragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_container_auth, RegistrationFragment())
                ?.addToBackStack(null)
                ?.commit()

        }

        btn_forgot_password.setOnClickListener {

            fragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_container_auth, ResetPasswordFragment())
                ?.addToBackStack(null)
                ?.commit()


        }


        return view
    }
}