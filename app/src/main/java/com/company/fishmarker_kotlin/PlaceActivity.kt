package com.company.fishmarker_kotlin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.company.fishmarker_kotlin.fragments_place.AddPlaceFragment

class PlaceActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)

        val intent : Intent = this.intent

        val fff : String = intent.getStringExtra("nameBigWater")


        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_place, AddPlaceFragment())
                .commit()
        }
    }
}