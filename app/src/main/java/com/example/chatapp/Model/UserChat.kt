package com.example.chatapp.Model

class UserChat {
    var senderId:String? = null
    var receiverId:String? = null
    var id:String? = null
    var message:String? = null

    constructor(){}


    constructor(senderId: String?, receiverId: String?, id: String?, message: String?) {
        this.senderId = senderId
        this.receiverId = receiverId
        this.id = id
        this.message = message
    }


}