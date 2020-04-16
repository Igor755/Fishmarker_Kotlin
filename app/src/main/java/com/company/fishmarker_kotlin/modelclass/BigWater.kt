package com.company.fishmarker_kotlin.modelclass

import com.company.fishmarker_kotlin.R

enum class BigWater(val nameWater: String, val photoWater: Int, val nameStringResource: Int) {
    Type1("SEA", R.drawable.big_water_sea, R.string.sea),
    Type2("OCEAN", R.drawable.big_water_ocean, R.string.ocean),
    Type3("RIVER", R.drawable.big_water_river, R.string.river),
    Type4("LAKE", R.drawable.big_water_lake, R.string.lake),
    Type5("GULF", R.drawable.big_water_gulf, R.string.gulf),
    Type6("RESERVOIR", R.drawable.big_water_reservoir, R.string.reservoir),
    Type7("LADE", R.drawable.big_water_lade, R.string.lade),
    Type8("POND", R.drawable.big_water_pond, R.string.pond),
    Type9("ANOTHER", R.drawable.big_water_another, R.string.another),
}