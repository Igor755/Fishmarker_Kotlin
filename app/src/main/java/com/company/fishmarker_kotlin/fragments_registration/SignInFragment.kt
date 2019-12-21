package com.company.fishmarker_kotlin.fragments_registration

import android.content.ContentValues.TAG
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
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject
import java.util.*


class SignInFragment : Fragment() {

    private var mAuth: FirebaseAuth? = null
    private var callbackManager: CallbackManager? = CallbackManager.Factory.create()
    val EMAIL = "email"
    val PUBLIC_PROFILE = "public_profile"

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
            withFacebook.setReadPermissions(Arrays.asList(EMAIL, PUBLIC_PROFILE))
        }


        withFacebook.registerCallback(callbackManager,  object : FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult) {

                handleFacebookAccessToken(result.accessToken)            }

            override fun onCancel() {
                Toast.makeText(context, "cancel", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException?) {
                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
            }

        })

       /* withFacebook.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(activity, listOf("public_profile", "email"))
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {

                        Log.d("SignInFragment", "Facebook_token: " + loginResult)

                        handleFacebookAccessToken(loginResult.accessToken)
*//*
                        val accessToken : AccessToken = loginResult.accessToken

                        var request : GraphRequest  = GraphRequest.newMeRequest(accessToken, object : GraphRequest.GraphJSONObjectCallback {
                            override fun onCompleted(`object` : JSONObject?,response : GraphResponse) {
                                Log.v("LoginActivity", response.toString())
                                try {
                                    val email : String  = response.jsonObject.getString("email")
                                    val userID : String = FirebaseAuth.getInstance().currentUser!!.uid
                                    val user  =  User(userID,email)
                                    FirebaseDatabase.getInstance().getReference("Users").child(userID).setValue(user)
                                }catch(e : Exception){
                                    e.printStackTrace()
                                }
                            }
                        })*//*
                    }

                    override fun onCancel() {
                        Log.d("SignInFragment", "Facebook onCancel.")
                    }

                    override fun onError(error: FacebookException) {
                        Log.d("SignInFragment", "Facebook onError.")

                    }
                })

        }
*/

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

        mAuth = FirebaseAuth.getInstance()

        val credential : AuthCredential = FacebookAuthProvider.getCredential(token.token)
        mAuth?.signInWithCredential(credential)?.addOnCompleteListener{ task ->
            if (task.isComplete){

                if (!task.isSuccessful){
                    Toast.makeText(context, "FAIL" + task.exception, Toast.LENGTH_SHORT).show()
                }

                else{
                    val userID : String = FirebaseAuth.getInstance().currentUser!!.uid
                    val email : String? = task.result?.user?.email
                    val user  = email?.let { User(userID, it) }
                    FirebaseDatabase.getInstance().getReference("Users").child(userID).setValue(user)
                    updateUI()
                }

            } else{

                Toast.makeText(context, "exception" + task.exception, Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun updateUI(){

        val intent = Intent(activity, ProfileActivity::class.java)
        startActivity(intent)
        AuthActivity().finish()

    }
}