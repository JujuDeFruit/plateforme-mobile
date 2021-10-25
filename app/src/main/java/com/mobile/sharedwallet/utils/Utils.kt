package com.mobile.sharedwallet.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.FirebaseStorageKtxRegistrar
import com.google.firebase.storage.ktx.storage
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

        fun createUserFromFirebaseUser(firebaseUser : FirebaseUser?, loadPhoto : Boolean = false) : User {
            return firebaseUser?.let {
                val names : HashMap<String, String> = getFirstnameAndLastnameFromDisplayName(firebaseUser.displayName)
                var photo : Bitmap? = null
                if(loadPhoto) {
                    FirebaseStorage
                        .getInstance()
                        .reference
                        .child(buildPicturePathRef(firebaseUser))
                        .getBytes(FirebaseConstants.MAX_PROFILE_PHOTO_SIZE)
                        .addOnSuccessListener { byteArray: ByteArray ->
                            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                            photo = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, false)
                        }
                }
                return@let User(
                    firebaseUser.uid,
                    names[User.Attributes.FIRST_NAME.string],
                    names[User.Attributes.LAST_NAME.string],
                    firebaseUser.email,
                    photo)
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