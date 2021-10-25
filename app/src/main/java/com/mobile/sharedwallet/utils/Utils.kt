package com.mobile.sharedwallet.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.constants.FirebaseConstants
import com.mobile.sharedwallet.fragment.LoginFragment
import com.mobile.sharedwallet.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class Utils {

    companion object {

        /**
         * Redirect to login page if not logged in
         */
        fun checkLoggedIn(navController: NavController) {
            val firebaseUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser
            val user : User? = LoginFragment.user
            if (firebaseUser != null && user != null) {
                if (!user.isNullOrEmpty() && firebaseUser.uid == user.uid) {
                    return
                }
            }
            navController.navigate(R.id.loginFragment)
        }

        fun getDisplayNameFromFirstnameAndLastName(fN: String?, lN: String?): String {
            return fN.plus(FirebaseConstants.String.DisplayNameSeparator).plus(lN)
        }

        private fun getFirstnameAndLastnameFromDisplayName(displayName: String?) : HashMap<String, String> {
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

        suspend fun createUserFromFirebaseUser(firebaseUser : FirebaseUser?, loadPhoto : Boolean = false) : User {
            return firebaseUser?.let {
                val names : HashMap<String, String> = getFirstnameAndLastnameFromDisplayName(firebaseUser.displayName)
                var photo : Bitmap? = null
                if(loadPhoto) {
                    photo = withContext(Dispatchers.IO) {
                        try {
                            val byteArray : ByteArray = FirebaseStorage
                                .getInstance()
                                .reference
                                .child(buildPicturePathRef(firebaseUser.uid))
                                .getBytes(FirebaseConstants.MAX_PROFILE_PHOTO_SIZE)
                                .await()

                            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                            return@withContext Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, false)

                        } catch (e : Exception) {
                            return@withContext null
                        }
                    } as Bitmap?

                }
                return@let User(
                    firebaseUser.uid,
                    names[User.Attributes.FIRST_NAME.string],
                    names[User.Attributes.LAST_NAME.string],
                    firebaseUser.email,
                    photo)
            } ?: User()
        }

        fun buildPicturePathRef(uid : String) : String {
            return FirebaseConstants.StorageRef.Pictures
                .plus("/")
                .plus(uid)
                .plus(".png")
        }

        fun checkPasswordConditions(context : Context, password1 : String?, password2 : String?) : Boolean? {
            val passwordPattern : String = "^(?=.*\\d)(?=.*[A-Z])[0-9a-zA-Z]{4,}$"

            return if(password1.isNullOrEmpty() || password2.isNullOrEmpty()) {
                Toast.makeText(context, context.getString(R.string.message_password_not_empty), Toast.LENGTH_SHORT).show()
                false
            } else if (password1 != password2) {
                Toast.makeText(context, context.getString(R.string.message_passwords_must_match), Toast.LENGTH_SHORT).show()
                false
            } else if(!Pattern.compile(passwordPattern).matcher(password1).matches()
                || !Pattern.compile(passwordPattern).matcher(password2).matches()) {
                Toast.makeText(
                    context,
                    context.getString(R.string.message_password_conditions),
                    Toast.LENGTH_SHORT
                ).show()
                false
            } else null
        }
    }
}