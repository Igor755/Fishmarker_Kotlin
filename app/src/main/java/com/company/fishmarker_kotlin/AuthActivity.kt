package com.company.fishmarker_kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.company.fishmarker_kotlin.fragments_registration.SignInFragment
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth?.currentUser


        //если уже авторизован
        if (currentUser != null) {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()

        }

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_auth, SignInFragment())
                .commit()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }



}
