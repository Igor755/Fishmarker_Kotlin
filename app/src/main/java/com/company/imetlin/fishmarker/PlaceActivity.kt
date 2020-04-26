package com.company.imetlin.fishmarker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.company.imetlin.fishmarker.fragments_place.AddPlaceFragment

class PlaceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_place, AddPlaceFragment())
                .commit()
        }
    }

}