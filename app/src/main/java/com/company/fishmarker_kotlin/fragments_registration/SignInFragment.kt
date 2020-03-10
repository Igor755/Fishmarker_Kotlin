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
import com.company.fishmarker_kotlin.R
import com.company.fishmarker_kotlin.modelclass.User
import com.facebook.*
import com.facebook.login.LoginManager
import kotlinx.android.synthetic.main.fragment_sign_in.*
import com.facebook.login.LoginResult
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
                    context,R.string.autorithation,
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

            activity?.supportFragmentManager?.beginTransaction()?.remove(SignInFragment())?.commit()

        } else {

            Toast.makeText(context, R.string.verify, Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()

        withFacebook.setOnClickListener {
            facebooklogin()
        }

        btn_sign_in.setOnClickListener {

            val email : String = et_email.text.toString().trim()
            val password = et_password.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, R.string.empty_email_or_password, Toast.LENGTH_SHORT).show()
            } else {
                signIn(email, password)
            }
        }
        btn_registration.setOnClickListener {
            activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragment_container_auth, RegistrationFragment())
            ?.addToBackStack(null)
            ?.commit()
        }
        btn_forgot_password.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_container_auth, ResetPasswordFragment())
                ?.addToBackStack(null)
                ?.commit()
        }
    }

    private fun facebooklogin() {

        LoginManager.getInstance()
            .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    handleFacebookAccessToken(result.accessToken)
                }

                override fun onCancel() {
                    Toast.makeText(context, "cancel", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException?) {
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
                }
            })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)

    }

    private fun handleFacebookAccessToken(token: AccessToken) {

        val credential: AuthCredential = FacebookAuthProvider.getCredential(token.token)
        mAuth?.signInWithCredential(credential)?.addOnCompleteListener { task ->

            if (!task.isSuccessful) {
                Toast.makeText(context, "FAIL" + task.exception, Toast.LENGTH_SHORT).show()
            } else {
                val userID: String = FirebaseAuth.getInstance().currentUser!!.uid
                val email: String? = task.result?.user?.email
                val user = email?.let { User(userID, it) }
                FirebaseDatabase.getInstance().getReference("Users").child(userID).setValue(user)
                updateUI()
            }


        }
    }

    private fun updateUI() {

        val intent = Intent(activity, ProfileActivity::class.java)
        startActivity(intent)

    }
}