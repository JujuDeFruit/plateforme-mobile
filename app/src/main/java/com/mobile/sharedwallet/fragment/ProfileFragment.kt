package com.mobile.sharedwallet.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mobile.sharedwallet.dialog.MessageDialog
import com.mobile.sharedwallet.utils.Utils
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.text.Editable
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.EmailAuthProvider.getCredential
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.lang.Exception


class ProfileFragment : Fragment() {

    private var user : User? = null
    private var storageRef : StorageReference? = null
    private var dialog : AlertDialog? = null
    private lateinit var selectPictureActivity : ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storageRef = FirebaseStorage.getInstance().reference
        user = LoginFragment.user

        selectPictureActivity =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                dialog?.findViewById<ImageView>(R.id.changeProfilePicture)?.setImageURI(uri)
            }
    }


    override fun onStart() {
        super.onStart()
        Utils.checkLoggedIn(findNavController())
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view : View = inflater.inflate(R.layout.profile_fragment, container, false)

        user?.let { userIt : User ->
            setValues(view)

            view.findViewById<FloatingActionButton>(R.id.logoutProfile).setOnClickListener {
                logout()
                findNavController().navigate(R.id.loginFragment)
            }

            view.findViewById<FloatingActionButton>(R.id.editProfile).setOnClickListener {
                if (userIt.isEmailVerified) showEditProfileDialog(container)
                else {
                    MessageDialog(requireContext(), requireView())
                        .verifyAccountDialog(R.id.profileFragment).show()
                }
            }

            view.findViewById<FloatingActionButton>(R.id.changePassword).setOnClickListener { _ ->
                if (userIt.isEmailVerified) showChangePasswordDialog(container)
                else {
                    MessageDialog(requireContext(), requireView())
                        .verifyAccountDialog(R.id.profileFragment).show()
                }
            }
        }

        return view
    }


    /**
     * Set user current values in text fields
     *
     * @param view : Profile view
     */
    private fun setValues(v : View? = null) {
        val view : View = v ?: requireView()

        user?.let { user : User ->
            view.findViewById<TextView>(R.id.firstNameProfile).text = user.firstName
            view.findViewById<TextView>(R.id.lastNameProfile).text = user.lastName
            view.findViewById<TextView>(R.id.emailProfile).text = user.email
            view.findViewById<TextView>(R.id.validEmailProfile).text = if(user.isEmailVerified) resources.getString(R.string.yes) else resources.getString(R.string.no)
            view.findViewById<ImageView>(R.id.profilePicture).setImageBitmap(user.photo)
        }
    }


    /**
     * Show edit profile dialog
     */
    private fun showEditProfileDialog(container: ViewGroup?) {

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())

        val dialogView: View = LayoutInflater.from(requireContext()).inflate(R.layout.edit_profile_dialog, container, false)

        val reSendEmailButton : FloatingActionButton = dialogView.findViewById(R.id.reSendEmailEditProfile)
        val isEmailVerifiedText : TextView = dialogView.findViewById(R.id.isEmailVerified)

        user?.let { user : User ->

            dialogView.findViewById<EditText>(R.id.firstNameEditProfile).setText(user.firstName)
            dialogView.findViewById<EditText>(R.id.lastNameEditProfile).setText(user.lastName)

            if (user.isEmailVerified) {
                isEmailVerifiedText.text = getString(R.string.yes)
                reSendEmailButton.visibility = View.INVISIBLE
            } else {
                isEmailVerifiedText.text = getString(R.string.no)
                reSendEmailButton.visibility = View.VISIBLE
                reSendEmailButton.setOnClickListener {
                    FirebaseAuth
                        .getInstance()
                        .currentUser
                        ?.let { firebaseUser : FirebaseUser ->
                            firebaseUser.sendEmailVerification()
                                .addOnSuccessListener {
                                    Toast.makeText(context, getString(R.string.email_sent), Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, getString(R.string.message_email_not_send), Toast.LENGTH_SHORT).show()
                                }
                        }
                }
            }
        }

        builder.setView(dialogView)

        dialog = builder.create()

        val changeProfilePicture : ImageView = dialogView.findViewById<ImageView>(R.id.changeProfilePicture)

        user?.photo?.let {
            changeProfilePicture.setImageBitmap(it)
        }

        changeProfilePicture.setOnClickListener {
            selectPictureActivity.launch("image/*")
        }

        dialogView.findViewById<FloatingActionButton>(R.id.cancelEditProfile).setOnClickListener { dialog?.dismiss() }

        dialogView.findViewById<FloatingActionButton>(R.id.confirmEditProfile).setOnClickListener {
            val updateProfile : Boolean = validateUpdateProfile()
            val updatePhoto : Boolean = validateUpdatePhoto()

            if(updateProfile) {
                CoroutineScope(Dispatchers.IO).launch {
                    updateProfile()
                }
            }
            if (updatePhoto) {
                CoroutineScope(Dispatchers.IO).launch {
                    updatePhoto()
                }
            }

            if (updatePhoto || updateProfile) {
                val mDialog = MessageDialog(requireContext(), requireView()) {
                    setValues()
                    dialog!!.dismiss()
                }
                mDialog
                    .create(getString(R.string.message_profile_successfully_updated))
                    .show()
            } else {
                dialog!!.dismiss()
            }
        }

        dialog?.show()
    }


    /**
     * Check if a modification occurred in the first name or in the last name
     *
     * @param dialogView : current edit view
     */
    private fun validateUpdateProfile() : Boolean {
        return user?.let { user: User ->
            return@let dialog?.let { dialog ->
                val firstName: String =
                    dialog.findViewById<EditText>(R.id.firstNameEditProfile).text.toString()
                val lastName: String =
                    dialog.findViewById<EditText>(R.id.lastNameEditProfile).text.toString()

                return@let lastName != user.lastName || firstName != user.firstName
            }
        } ?: false
    }


    /**
     * Check if loaded bitmap is different from current bitmap
     */
    private fun validateUpdatePhoto() : Boolean {
        return user?.let { user : User ->
            dialog?.let { dialog ->
                val newPhotoImageView : ImageView = dialog.findViewById<ImageView>(R.id.changeProfilePicture)
                val newPhoto : BitmapDrawable = newPhotoImageView.drawable as BitmapDrawable
                return@let !user.photo!!.sameAs(newPhoto.bitmap)
            }
        } ?: false
    }


    /**
     * Update user profile on Firebase
     */
    private suspend fun updateProfile() {
        user?.let { user : User ->
            dialog?.let { dialog ->
                val firstName : String = dialog.findViewById<EditText>(R.id.firstNameEditProfile).text.toString()
                val lastName : String = dialog.findViewById<EditText>(R.id.lastNameEditProfile).text.toString()

                FirebaseAuth
                    .getInstance()
                    .currentUser
                    ?.let { firebaseUser : FirebaseUser ->
                        try {
                            firebaseUser.updateProfile(userProfileChangeRequest {
                                displayName = Utils.getDisplayNameFromFirstnameAndLastName(firstName, lastName)
                            }).await()

                            user.firstName = firstName
                            user.lastName = lastName
                        }
                        catch (e: Exception) {
                            Toast.makeText(requireContext(), getString(R.string.message_error_update_profile), Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }


    /**
     * Update photo in storage
     */
    private suspend fun updatePhoto() {
        dialog?.let { dialog ->
            val imageView : ImageView = dialog.findViewById(R.id.changeProfilePicture)
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap

            val baos = ByteArrayOutputStream()

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val data : ByteArray = baos.toByteArray()

            storageRef?.let { storageRef : StorageReference ->
                user?.let { user : User ->
                    try {
                        storageRef
                            .child(Utils.buildPicturePathRef(user.uid!!))
                            .putBytes(data)
                            .await()
                        user.photo = bitmap
                    }
                    catch (e: Exception) {
                        Toast.makeText(requireContext(), getString(R.string.message_error_update_photo), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    /**
     * Show alert dialog to change password
     *
     * @param container ViewGroup needed to build AlertDialog
     */
    private fun showChangePasswordDialog(container: ViewGroup?) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())

        val dialogView: View =
            LayoutInflater.from(requireContext()).inflate(R.layout.change_password_dialog, container, false)

        builder.setView(dialogView)

        val passwordDialog : AlertDialog = builder.create()

        dialogView
            .findViewById<FloatingActionButton>(R.id.cancel)
            .setOnClickListener { passwordDialog.dismiss() }

        dialogView
            .findViewById<FloatingActionButton>(R.id.submit)
            .setOnClickListener {
                changePassword(passwordDialog)
            }

        passwordDialog.show()
    }


    /**
     * Test authentication, user need to enter his password before changing it
     *
     * @param dialogView dialog view of the changing password dialog
     */
    private fun changePassword(passwordDialog: AlertDialog) {
        val password : Editable? = passwordDialog.findViewById<EditText>(R.id.passwordOnChangePassword)?.text

        if (!password.isNullOrEmpty()) {
            user?.let { user : User ->
                FirebaseAuth
                    .getInstance()
                    .currentUser
                    ?.let { fbUser : FirebaseUser ->
                        fbUser
                            .reauthenticate(getCredential(user.email!!, password.toString()))
                            .addOnSuccessListener { updatePassword(passwordDialog) }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), getString(R.string.message_wrong_password), Toast.LENGTH_SHORT).show()
                            }
                    }

            }
        }
    }


    /**
     * Update password on firebase
     *
     * @param dialogView dialog view of the changing password dialog
     */
    private fun updatePassword(passwordDialog: AlertDialog) {
        val newPassword : Editable? = passwordDialog.findViewById<EditText>(R.id.newPassword)?.text
        val repeatedNewPassword : Editable? = passwordDialog.findViewById<EditText>(R.id.repeatedNewPassword)?.text

        if (newPassword.toString() != repeatedNewPassword.toString()) {
            Toast.makeText(requireContext(), getString(R.string.message_new_repeated_password_not_match), Toast.LENGTH_SHORT).show()
            return
        } else if (newPassword.isNullOrEmpty() || repeatedNewPassword.isNullOrEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.message_new_password_not_empty), Toast.LENGTH_SHORT).show()
            return
        } else {
            val condition : Boolean = Utils.checkPasswordConditions(requireContext(), newPassword.toString(), repeatedNewPassword.toString()) ?: true
            if (!condition) return
        }

        FirebaseAuth
            .getInstance()
            .currentUser?.let {
                it.updatePassword(newPassword.toString())
                    .addOnSuccessListener {
                        val dialog : MessageDialog = MessageDialog(
                            requireContext(),
                            requireView()
                        ) {
                            logout()
                            passwordDialog.dismiss()
                        }
                        dialog.navigateTo(R.id.loginFragment)
                        dialog
                            .create(getString(R.string.message_successfully_updated_password))
                            .show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), getString(R.string.message_error_updating_password), Toast.LENGTH_SHORT).show()
                    }
            }
    }


    /**
     * Logout from account
     * Reset Fb user & local user
     */
    private fun logout() {
        user = null
        FirebaseAuth.getInstance().signOut()
    }
}