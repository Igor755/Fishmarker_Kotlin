package com.company.imetlin.fishmarker.fragments_registration

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.company.imetlin.fishmarker.ProfileActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.company.imetlin.fishmarker.AuthActivity
import com.company.imetlin.fishmarker.R
import com.company.imetlin.fishmarker.extension.addKeyboardListener
import com.company.imetlin.fishmarker.model.User
import com.facebook.*
import com.facebook.login.LoginManager
import kotlinx.android.synthetic.main.fragment_sign_in.*
import com.facebook.login.LoginResult
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.database.FirebaseDatabase
import java.util.*


class SignInFragment : Fragment(R.layout.fragment_sign_in), TextWatcher {

    private var mAuth: FirebaseAuth? = null
    //private var callbackManager: CallbackManager? = null

    private fun signIn(email: String, password: String) {
        mAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                checkIfEmailVerified()
            } else {
                btnLoginMainLogin.isLoading = false
                Toast.makeText(context,R.string.autorithation, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkIfEmailVerified() {
        val user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        if (user.isEmailVerified) {
            btnLoginMainLogin.isLoading = false
            val intent = Intent(activity, ProfileActivity::class.java)
            startActivity(intent)
            activity?.supportFragmentManager?.beginTransaction()?.remove(SignInFragment())?.commit()
        } else {
            btnLoginMainLogin.isLoading = false
            Toast.makeText(context, R.string.verify, Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let { ctx ->
            addKeyboardListener(ctx as AuthActivity, tiEtLoginEmail)
            addKeyboardListener(ctx, tiEtLoginPassword)
        }
        mAuth = FirebaseAuth.getInstance()
        //callbackManager = CallbackManager.Factory.create()
        /*withFacebook.setOnClickListener {
            facebooklogin()
        }*/
        tiEtLoginEmail.addTextChangedListener(this)
        tiEtLoginPassword.addTextChangedListener(this)

        btnLoginMainLogin.setOnClickListener {
            val email : String = tiEtLoginEmail.text.toString().trim()
            val password = tiEtLoginPassword.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, R.string.empty_email_or_password, Toast.LENGTH_SHORT).show()
            } else {
                btnLoginMainLogin.isLoading = true
                signIn(email, password)
            }
        }
        btnLoginMainRegistration.setOnClickListener {
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

    private fun validateFields() {
        btnLoginMainLogin.isEnabled = listOf(
            (tiEtLoginEmail.text!!.isNotEmpty()
                    && Patterns.EMAIL_ADDRESS.matcher(
                tiEtLoginEmail.text.toString()
            ).matches()),
            tiEtLoginPassword.text!!.isNotEmpty(),
            tiEtLoginPassword.length() >= 8,
        ).all { it }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        validateFields()
    }

    override fun afterTextChanged(s: Editable?) {
    }
}