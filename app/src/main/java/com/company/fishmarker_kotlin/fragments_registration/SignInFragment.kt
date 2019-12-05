package com.company.fishmarker_kotlin.fragments_registration

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.company.fishmarker_kotlin.ProfileActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.company.fishmarker_kotlin.AuthActivity
import com.company.fishmarker_kotlin.R
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.android.synthetic.main.fragment_sign_in.*
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


class SignInFragment : Fragment() {

    private var mAuth: FirebaseAuth? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {



        return  inflater.inflate(R.layout.fragment_sign_in, container, false)

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
            Toast.makeText(context, R.string.verify, Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        withFacebook.setOnClickListener {

        }

        mAuth = FirebaseAuth.getInstance()


        btn_sign_in.setOnClickListener {


            val email = edit_text_email.text.toString().trim()
            val password = edit_text_password.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()){

                Toast.makeText(context,R.string.empty_email_or_password, Toast.LENGTH_SHORT).show()

            }else{
                signIn(email, password)

            }

        }

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
    }
}