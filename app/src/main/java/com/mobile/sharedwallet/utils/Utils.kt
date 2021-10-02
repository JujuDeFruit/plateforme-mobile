package com.mobile.sharedwallet.utils

import com.google.firebase.auth.FirebaseUser
import com.mobile.sharedwallet.constants.FirebaseConstants
import com.mobile.sharedwallet.models.User

class Utils {

    companion object {
        fun getDisplayNameFromFirstnameAndLastName(fN: String?, lN: String?): String {
            return fN.plus(FirebaseConstants.String.DisplayNameSeparator).plus(lN)
        }

        fun getFirstnameAndLastnameFromDisplayName(displayName: String?) : HashMap<String, String> {
            return if (!displayName.isNullOrEmpty()) {
                val fNAndLN : List<String> = displayName.split(FirebaseConstants.String.DisplayNameSeparator)
                hashMapOf<String, String>(
                    "firstName" to fNAndLN.first(),
                    "lastName" to fNAndLN.last()
                )
            } else hashMapOf<String, String>(
                "firstName" to "",
                "lastName" to ""
            )
        }

        fun createUserFromFirebaseUser(firebaseUser : FirebaseUser?) : User {
            val names : HashMap<String, String> = getFirstnameAndLastnameFromDisplayName(firebaseUser?.displayName)
            return if(firebaseUser != null)
                User(firebaseUser.uid, names["firstName"], names["lastName"]!!, firebaseUser.email, firebaseUser.photoUrl)
            else
                User()
        }
    }
}