package com.mobile.sharedwallet.models

data class User(
    var uid : String,
    var firstName: String,
    var lastName: String,
    var email: String,
    var validEmail: Boolean
) {
    constructor() : this("", "", "", "", false)

    fun isEmpty() : Boolean {
        return uid == "" && firstName == "" && lastName == "" && email == "" && !validEmail
    }
}