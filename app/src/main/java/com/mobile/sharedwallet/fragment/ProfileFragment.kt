package com.mobile.sharedwallet.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mobile.sharedwallet.dialog.MessageDialog
import com.mobile.sharedwallet.utils.Utils
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.text.Editable
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import com.google.firebase.auth.EmailAuthProvider.getCredential
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.constants.FirebaseConstants
import com.mobile.sharedwallet.models.User
import java.io.ByteArrayOutputStream


class ProfileFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private var user : FirebaseUser? = null

    private var storageRef : StorageReference? = null

    private lateinit var currentDialog: AlertDialog

    private lateinit var selectPictureActivity : ActivityResultLauncher<String>
    private var pictureUri : Uri? = null

    private var bitmap : Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storageRef = FirebaseStorage.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        user = mAuth.currentUser

        selectPictureActivity =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                pictureUri = uri
                currentDialog.findViewById<ImageView>(R.id.changeProfilePicture).setImageURI(uri)
            }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view : View = inflater.inflate(R.layout.profile_fragment, container, false)

        user?.let { user : FirebaseUser ->
            setValues(view)

            view.findViewById<FloatingActionButton>(R.id.logoutProfile).setOnClickListener {
                mAuth.signOut()
                findNavController().navigate(R.id.loginFragment)
            }

            view.findViewById<FloatingActionButton>(R.id.editProfile).setOnClickListener {
                if (user.isEmailVerified) showEditProfileDialog(container)
                else {
                    val dialog = MessageDialog(requireContext(), requireView())
                        .verifyAccountDialog(R.id.profileFragment)
                    dialog.show()
                }
            }

            view.findViewById<FloatingActionButton>(R.id.changePassword).setOnClickListener {
                if (user.isEmailVerified) showChangePasswordDialog(container)
                else {
                    val dialog = MessageDialog(requireContext(), requireView())
                        .verifyAccountDialog(R.id.profileFragment)
                    dialog.show()
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

        user?.let { user : FirebaseUser ->
            val names : HashMap<String, String> = Utils.getFirstnameAndLastnameFromDisplayName(user.displayName)

            view.findViewById<TextView>(R.id.firstNameProfile).text = names[User.Attributes.FIRST_NAME.string]
            view.findViewById<TextView>(R.id.lastNameProfile).text = names[User.Attributes.LAST_NAME.string]
            view.findViewById<TextView>(R.id.emailProfile).text = user.email
            view.findViewById<TextView>(R.id.validEmailProfile).text = if(user.isEmailVerified) resources.getString(R.string.yes) else resources.getString(R.string.no)
            fetchPhoto()

        }
    }


    /**
     * Show edit profile dialog
     */
    private fun showEditProfileDialog(container: ViewGroup?) {

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())

        val dialogView: View = LayoutInflater.from(context).inflate(R.layout.edit_profile_dialog, container, false)

        val reSendEmailButton : FloatingActionButton = dialogView.findViewById<FloatingActionButton>(R.id.reSendEmailEditProfile)
        val isEmailVerifiedText : TextView = dialogView.findViewById<TextView>(R.id.isEmailVerified)

        user?.let { user : FirebaseUser ->
            val mySelf : HashMap<String, String> = Utils.getFirstnameAndLastnameFromDisplayName(user.displayName)

            dialogView.findViewById<EditText>(R.id.firstNameEditProfile).setText(mySelf[User.Attributes.FIRST_NAME.string])
            dialogView.findViewById<EditText>(R.id.lastNameEditProfile).setText(mySelf[User.Attributes.LAST_NAME.string])

            if (user.isEmailVerified) {
                isEmailVerifiedText.text = getString(R.string.yes)
                reSendEmailButton.visibility = View.INVISIBLE
            } else {
                isEmailVerifiedText.text = getString(R.string.no)
                reSendEmailButton.visibility = View.VISIBLE
                reSendEmailButton.setOnClickListener {
                    user
                        .sendEmailVerification()
                        .addOnSuccessListener {
                            Toast.makeText(context, getString(R.string.email_sent), Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, getString(R.string.message_email_not_send), Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }

        builder.setView(dialogView)

        currentDialog = builder.create()

        val changeProfilePicture : ImageView = dialogView.findViewById<ImageView>(R.id.changeProfilePicture)

        bitmap?.let {
            changeProfilePicture.setImageBitmap(it)
        }

        changeProfilePicture.setOnClickListener {
            selectPictureActivity.launch("image/*")
        }

        dialogView.findViewById<FloatingActionButton>(R.id.cancelEditProfile).setOnClickListener { currentDialog.dismiss() }

        dialogView.findViewById<FloatingActionButton>(R.id.confirmEditProfile).setOnClickListener {
            if(validateUpdateProfile(dialogView)) updateProfile(dialogView)
            if(validateUpdatePhoto(dialogView)) updatePhoto(dialogView)
        }

        currentDialog.show()
    }


    /**
     * Check if a modification occurred in the first name or in the last name
     *
     * @param dialogView : current edit view
     */
    private fun validateUpdateProfile(dialogView: View) : Boolean {
        return user?.let { user: FirebaseUser ->
            val firstName: String =
                dialogView.findViewById<EditText>(R.id.firstNameEditProfile).text.toString()
            val lastName: String =
                dialogView.findViewById<EditText>(R.id.lastNameEditProfile).text.toString()

            val currentUser: HashMap<String, String> =
                Utils.getFirstnameAndLastnameFromDisplayName(user.displayName)

            return@let lastName != currentUser[User.Attributes.LAST_NAME.string] || firstName != currentUser[User.Attributes.FIRST_NAME.string]
        } ?: false
    }


    /**
     * Check if profile photo has been changed TODO
     */
    private fun validateUpdatePhoto(dialogView: View) : Boolean {
        return true;
        storageRef?.let {
            val photoPath : StorageReference = it.child(FirebaseConstants.StorageRef.Pictures)
        }
    }


    /**
     * Update user profile on Firebase
     */
    private fun updateProfile(dialogView: View) {
        user?.let { user : FirebaseUser ->
            val firstName : String = dialogView.findViewById<EditText>(R.id.firstNameEditProfile).text.toString()
            val lastName : String = dialogView.findViewById<EditText>(R.id.lastNameEditProfile).text.toString()

            user
                .updateProfile(userProfileChangeRequest {
                    displayName = Utils.getDisplayNameFromFirstnameAndLastName(firstName, lastName)
                })
                .addOnSuccessListener {
                    val dialog : MessageDialog = MessageDialog(requireContext(), requireView()) {
                        setValues()
                        currentDialog.dismiss()
                    }
                    dialog
                        .create(getString(R.string.message_profile_successfully_updated))
                        .show()
                }
                .addOnFailureListener {
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }
        }
    }


    /**
     * Update photo in storage
     */
    private fun updatePhoto(dialogView: View) {

        val imageView : ImageView = dialogView.findViewById<ImageView>(R.id.changeProfilePicture)
        val bitmap_ = (imageView.drawable as BitmapDrawable).bitmap

        val baos : ByteArrayOutputStream = ByteArrayOutputStream()

        bitmap_.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data : ByteArray = baos.toByteArray()

        storageRef?.let { storageRef : StorageReference ->
            user?.let { user : FirebaseUser ->
                storageRef
                    .child(Utils.buildPicturePathRef(user))
                    .putBytes(data)
                    .addOnSuccessListener {
                        currentDialog.dismiss()
                        requireView().findViewById<ImageView>(R.id.profilePicture).setImageBitmap(bitmap_)
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), getString(R.string.message_error_update_photo), Toast.LENGTH_SHORT).show()
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
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        val dialogView: View =
            LayoutInflater.from(context).inflate(R.layout.change_password_dialog, container, false)
        builder.setView(dialogView)

        currentDialog = builder.create()

        dialogView
            .findViewById<FloatingActionButton>(R.id.cancel)
            .setOnClickListener { currentDialog.dismiss() }
        dialogView
            .findViewById<FloatingActionButton>(R.id.submit)
            .setOnClickListener { changePassword(dialogView) }

        currentDialog.show()
    }


    /**
     * Test authentication, user need to enter his password before changing it
     *
     * @param dialogView dialog view of the changing password dialog
     */
    private fun changePassword(dialogView : View) {
        val password : Editable? = dialogView.findViewById<EditText>(R.id.passwordOnChangePassword)?.text

        if (!password.isNullOrEmpty()) {
            user?.let { user : FirebaseUser ->
                user
                    .reauthenticate(getCredential(user.email!!, password.toString()))
                    .addOnSuccessListener { updatePassword(dialogView) }
                    .addOnFailureListener {
                        Toast.makeText(context, getString(R.string.message_wrong_password), Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }


    /**
     * Update password on firebase
     *
     * @param dialogView dialog view of the changing password dialog
     */
    private fun updatePassword(dialogView : View) {
        val newPassword : Editable? = dialogView.findViewById<EditText>(R.id.newPassword)?.text
        val repeatedNewPassword : Editable? = dialogView.findViewById<EditText>(R.id.repeatedNewPassword)?.text

        if (newPassword.toString() != repeatedNewPassword.toString()) {
            Toast.makeText(context, getString(R.string.message_new_repeated_password_not_match), Toast.LENGTH_SHORT).show()
            return
        } else if (newPassword.isNullOrEmpty() || repeatedNewPassword.isNullOrEmpty()) {
            Toast.makeText(context, getString(R.string.message_new_password_not_empty), Toast.LENGTH_SHORT).show()
            return
        }

        user?.let { user : FirebaseUser ->
            user
                .updatePassword(newPassword.toString())
                .addOnSuccessListener { _ : Void ->
                    val dialog : MessageDialog = MessageDialog(
                        requireContext(),
                        requireView()
                    ) {
                        mAuth.signOut()
                        currentDialog.dismiss()
                    }
                    dialog.navigateTo(R.id.loginFragment)
                    dialog
                        .create(getString(R.string.message_successfully_updated_password))
                        .show()
                }
                .addOnFailureListener { _ : Exception ->
                    Toast.makeText(context, getString(R.string.message_error_updating_password), Toast.LENGTH_SHORT).show()
                }
        }
    }


    /**
     * Fetch user profile photo from firebase storage
     */
    private fun fetchPhoto() {
        storageRef?.let { storageRef : StorageReference ->
            user?.let { user : FirebaseUser ->
                storageRef
                    .child(Utils.buildPicturePathRef(user))
                    .getBytes(FirebaseConstants.MAX_PROFILE_PHOTO_SIZE)
                    .addOnSuccessListener { byteArray : ByteArray ->
                        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

                        val imageBitmap : Bitmap =
                            Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height,false)

                        this.bitmap = imageBitmap
                        requireView()
                            .findViewById<ImageView>(R.id.profilePicture)
                            .setImageBitmap(imageBitmap)

                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), getString(R.string.message_error_fetch_photo), Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}