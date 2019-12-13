package com.company.fishmarker_kotlin.fragments_registration

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.company.fishmarker_kotlin.modelclass.User
import com.facebook.*
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.android.synthetic.main.fragment_sign_in.*
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.database.FirebaseDatabase
import java.util.*


class SignInFragment : Fragment() {

    private var mAuth: FirebaseAuth? = null

    private var callbackManager: CallbackManager? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    private fun signIn(email: String, password: String) {
        mAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                checkIfEmailVerified()
            } else {
                Toast.makeText(
                    context,
                    com.company.fishmarker_kotlin.R.string.autorithation,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun checkIfEmailVerified() {

        val user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        if (user.isEmailVerified) {

            val intent = Intent(activity, ProfileActivity::class.java)
            startActivity(intent)
            AuthActivity().finish()

            fragmentManager?.beginTransaction()?.remove(SignInFragment())?.commit()
        } else {

            Toast.makeText(context, R.string.verify, Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        withFacebook.setOnClickListener {

            callbackManager = CallbackManager.Factory.create()

            LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile", "email"))
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        Log.d("SignInFragment", "Facebook_token: " + loginResult)
                        handleFacebookAccessToken(loginResult.accessToken)

                        var profile : Profile  = Profile.getCurrentProfile()

                        //var email : String = profile.

                        val userID : String = FirebaseAuth.getInstance().currentUser!!.uid
                        
                            //val user : User =  User(userID,  )
                        //FirebaseDatabase.getInstance().getReference("Users").child(userID).setValue(user)


                    }

                    override fun onCancel() {
                        Log.d("SignInFragment", "Facebook onCancel.")
                    }

                    override fun onError(error: FacebookException) {
                        Log.d("SignInFragment", "Facebook onError.")

                    }
                })

        }


        mAuth = FirebaseAuth.getInstance()
        btn_sign_in.setOnClickListener {

            val email = edit_text_email.text.toString().trim()
            val password = edit_text_password.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, R.string.empty_email_or_password, Toast.LENGTH_SHORT).show()
            } else {
                signIn(email, password)
            }
        }
        btn_registration.setOnClickListener {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookAccessToken(token: AccessToken){

        val credential : AuthCredential = FacebookAuthProvider.getCredential(token.token)
        mAuth?.signInWithCredential(credential)?.addOnCompleteListener{ task ->
            if (task.isSuccessful){

                val intent = Intent(activity, ProfileActivity::class.java)
                startActivity(intent)
                AuthActivity().finish()

            } else{

                Toast.makeText(context, "exception" + task.exception, Toast.LENGTH_SHORT).show()


            }

        }

    }
}