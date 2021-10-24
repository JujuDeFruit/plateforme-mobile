package com.mobile.sharedwallet.utils

import com.google.firebase.auth.FirebaseUser
import com.mobile.sharedwallet.constants.FirebaseConstants
import com.mobile.sharedwallet.models.User
import java.io.File

class Utils {

    companion object {
        fun getDisplayNameFromFirstnameAndLastName(fN: String?, lN: String?): String {
            return fN.plus(FirebaseConstants.String.DisplayNameSeparator).plus(lN)
        }

        fun getFirstnameAndLastnameFromDisplayName(displayName: String?) : HashMap<String, String> {
            return if (!displayName.isNullOrEmpty()) {
                val fNAndLN : List<String> = displayName.split(FirebaseConstants.String.DisplayNameSeparator)
                hashMapOf<String, String>(
                    User.Attributes.FIRST_NAME.string to fNAndLN.first(),
                    User.Attributes.LAST_NAME.string to fNAndLN.last()
                )
            } else hashMapOf<String, String>(
                User.Attributes.FIRST_NAME.string to "",
                User.Attributes.LAST_NAME.string to ""
            )
        }

        fun createUserFromFirebaseUser(firebaseUser : FirebaseUser?) : User {
            return firebaseUser?.let {
                val names : HashMap<String, String> = getFirstnameAndLastnameFromDisplayName(firebaseUser.displayName)
                // val file : File? = if (loadPhoto) File("") else null // TODO
                return@let User(firebaseUser.uid, names[User.Attributes.FIRST_NAME.string], names[User.Attributes.LAST_NAME.string], firebaseUser.email)
            } ?: User()
        }

        fun buildPicturePathRef(fbUser : FirebaseUser) : String {
            return FirebaseConstants.StorageRef.Pictures
                .plus("/")
                .plus(fbUser.uid)
                .plus(".png")
        }
    }
}