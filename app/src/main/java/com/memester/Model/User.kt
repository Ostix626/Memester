package com.memester.Model

class User {
    private var email: String = ""
    private var image: String = ""
    private var uid: String = ""
    private var username: String = ""

    constructor()

    constructor(email: String, image: String, uid: String, username: String,)
    {
        this.email = email
        this.image = image
        this.uid = uid
        this.username = username
    }

    fun setImage(image: String) {
        this.image = image
    }
    fun setEmail(email: String) {
        this.email = email
    }
    fun setUid(uid: String) {
        this.uid = uid
    }
    fun setUsername(username: String) {
        this.username = username
    }


    fun getImage():String {
        return image
    }
    fun getEmail():String {
        return email
    }
    fun getUid():String {
        return uid
    }
    fun getUsername():String {
        return username
    }

}