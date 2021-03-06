package com.company.imetlin.fishmarker.model

data class User(var user_id: String = "",
           var user_name: String = "",
           var last_name: String = "" ,
           var email: String= "",
           var location: String= "",
           var telephone: String = "",
           var type_of_fishing: String= "",
           var trophies: String = "",
           var url_photo : String = "") {

    constructor(user_id : String, user_name: String, email: String, location: String) :
            this(user_id,user_name, "",email, location, "", "", "", "")


    constructor(user_id : String, email: String) :
            this(user_id,"", "",email, "", "", "", "", "")
}