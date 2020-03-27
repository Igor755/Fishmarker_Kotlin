package com.company.fishmarker_kotlin.modelclass

import com.company.fishmarker_kotlin.R

enum class BigWater(val nameWater: String, val photoWater: Int) {

    Type1("SEA", R.drawable.big_water_sea),
    Type2("OCEAN", R.drawable.big_water_ocean),
    Type3("RIVER", R.drawable.big_water_river),
    Type4("LAKE", R.drawable.big_water_lake),
    Type5("GULF", R.drawable.big_water_gulf),
    Type6("RESERVOIR", R.drawable.big_water_reservoir),
    Type7("LADE", R.drawable.big_water_lade),
    Type8("POND", R.drawable.big_water_pond),
    Type9("ANOTHER", R.drawable.big_water_another),
}