package com.company.fishmarker_kotlin.registration_fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.company.fishmarker_kotlin.ProfileActivity
import com.company.fishmarker_kotlin.R

class SignInFragment : Fragment() {




    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)

        val btnReg = view.findViewById<TextView>(R.id.btn_registration)
        val btnForgotPas = view.findViewById<TextView>(R.id.btn_forgot_password)
        val btnSignIn = view.findViewById<Button>(R.id.btn_sign_in)



        btnSignIn.setOnClickListener {

            val intent = Intent (activity, ProfileActivity::class.java)
            startActivity(intent)

        }




        btnReg.setOnClickListener{


            fragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_container_auth, RegistrationFragment())
                ?.addToBackStack(null)
                ?.commit()

        }

        btnForgotPas.setOnClickListener {

            fragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_container_auth, ResetPasswordFragment())
                ?.addToBackStack(null)
                ?.commit()


        }


        return view
    }
}