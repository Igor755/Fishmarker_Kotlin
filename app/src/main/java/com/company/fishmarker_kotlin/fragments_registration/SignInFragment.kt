package com.company.fishmarker_kotlin.fragments_registration

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.company.fishmarker_kotlin.ProfileActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import android.widget.EditText
import android.widget.Toast
import com.company.fishmarker_kotlin.AuthActivity
import com.company.fishmarker_kotlin.R
import kotlinx.android.synthetic.main.activity_auth.*
import kotlin.system.exitProcess


class SignInFragment : Fragment() {

    private var mAuth: FirebaseAuth? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)

        val btnReg = view.findViewById<TextView>(R.id.btn_registration)
        val btnForgotPas = view.findViewById<TextView>(com.company.fishmarker_kotlin.R.id.btn_forgot_password)
        val btnSignIn = view.findViewById<Button>(com.company.fishmarker_kotlin.R.id.btn_sign_in)

        val edit_text_email = view.findViewById<EditText>(com.company.fishmarker_kotlin.R.id.et_email)
        val edit_text_password = view.findViewById<EditText>(com.company.fishmarker_kotlin.R.id.et_password)

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth!!.currentUser




        btnSignIn.setOnClickListener {


            val email = edit_text_email.text.toString().trim()
            val password = edit_text_password.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()){

                Toast.makeText(context, com.company.fishmarker_kotlin.R.string.empty_email_or_password, Toast.LENGTH_SHORT).show()

            }else{
                signIn(email, password)

            }

        }

        btnReg.setOnClickListener{
            fragmentManager
                ?.beginTransaction()
                ?.replace(com.company.fishmarker_kotlin.R.id.fragment_container_auth, RegistrationFragment())
                ?.addToBackStack(null)
                ?.commit()
        }

        btnForgotPas.setOnClickListener {
            fragmentManager
                ?.beginTransaction()
                ?.replace(com.company.fishmarker_kotlin.R.id.fragment_container_auth, ResetPasswordFragment())
                ?.addToBackStack(null)
                ?.commit()
        }


        return view
    }
    private fun signIn(email : String, password : String){
        mAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener { task ->
            if (task.isSuccessful){
                checkIfEmailVerified()


            }else{
                Toast.makeText(context, com.company.fishmarker_kotlin.R.string.autorithation, Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun checkIfEmailVerified(){

        val user : FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        if (user.isEmailVerified){
            val intent = Intent(activity, ProfileActivity::class.java)
            startActivity(intent)


            AuthActivity().finish()
            fragmentManager?.beginTransaction()?.remove(SignInFragment())?.commit()


        }else{
            Toast.makeText(context, com.company.fishmarker_kotlin.R.string.verify, Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()
        }

    }
}