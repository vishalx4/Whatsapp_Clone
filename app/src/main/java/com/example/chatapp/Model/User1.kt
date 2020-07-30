package com.example.chatapp.Model

class User1 {

    var uid:String? = null
    var userEmail:String? = null
    var userName:String? = null
    var status:String? = null
    var profile:String? = null
    var instagram:String? = null
    var facebook:String? = null


    constructor(){}


    constructor(
        uid: String?,
        userEmail: String?,
        userName: String?,
        status: String?,
        profile: String?,
        instagram: String?,
        facebook: String?
    ) {
        this.uid = uid
        this.userEmail = userEmail
        this.userName = userName
        this.status = status
        this.profile = profile
        this.instagram = instagram
        this.facebook = facebook
    }


}