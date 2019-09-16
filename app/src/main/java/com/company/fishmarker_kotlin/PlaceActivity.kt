package com.company.fishmarker_kotlin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PlaceActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)


        var namewater: String = intent.getStringExtra("nameWater")
        Toast.makeText(this, namewater, Toast.LENGTH_SHORT).show()
    }
}