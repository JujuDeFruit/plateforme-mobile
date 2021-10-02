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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mobile.sharedwallet.dialog.MessageDialog
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.utils.Utils
import android.app.AlertDialog
import android.app.Dialog
import android.text.Editable
import android.widget.EditText
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.EmailAuthProvider.getCredential


class ProfileFragment : Fragment() {

    private lateinit var container : ViewGroup
    private lateinit var alertDialog : AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        this.container = container!!

        val view : View = inflater.inflate(R.layout.profile_fragment, container, false)

        val auth : FirebaseAuth = Firebase.auth
        val user : FirebaseUser = auth.currentUser!!

        val names : HashMap<String, String> = Utils.getFirstnameAndLastnameFromDisplayName(user.displayName)

        view.findViewById<TextView>(R.id.firstNameProfile).text = names["firstName"]
        view.findViewById<TextView>(R.id.lastNameProfile).text = names["lastName"]
        view.findViewById<TextView>(R.id.emailProfile).text = user.email
        view.findViewById<TextView>(R.id.validEmailProfile).text = if(user.isEmailVerified) resources.getString(R.string.yes) else resources.getString(R.string.no)

        view.findViewById<FloatingActionButton>(R.id.logoutProfile).setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.loginFragment)
        }
        view.findViewById<FloatingActionButton>(R.id.editProfile).setOnClickListener {
            if (Firebase.auth.currentUser?.isEmailVerified == true) showEditProfileDialog()
            else {
                val dialog = MessageDialog(requireContext(), requireView())
                    .verifyAccountDialog(R.id.profileFragment)
                dialog.show()
            }
        }
        view.findViewById<FloatingActionButton>(R.id.changePassword).setOnClickListener {
            if (Firebase.auth.currentUser?.isEmailVerified == true) showChangePasswordDialog()
            else {
                val dialog = MessageDialog(requireContext(), requireView())
                    .verifyAccountDialog(R.id.profileFragment)
                dialog.show()
            }
        }

        return view
    }

    private fun showEditProfileDialog() {
        /*val editDialog: Dialog = Dialog(requireContext())

        val copyView : View = view?.findViewById(R.id.editProfileCoreInfos)!!

        editDialog.setContentView(copyView)

        /*editView
            .findViewById<FloatingActionButton>(R.id.cancel)
            .setOnClickListener { editDialog.dismiss() }
        editView
            .findViewById<FloatingActionButton>(R.id.submit)
            .setOnClickListener {  }*/

        editDialog.show()*/
    }

    private fun showChangePasswordDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        val dialogView: View =
            LayoutInflater.from(context).inflate(R.layout.change_password_dialog, container, false)
        builder.setView(dialogView)

        alertDialog = builder.create()

        dialogView
            .findViewById<FloatingActionButton>(R.id.cancel)
            .setOnClickListener { alertDialog.dismiss() }
        dialogView
            .findViewById<FloatingActionButton>(R.id.submit)
            .setOnClickListener { changePassword(dialogView) }

        alertDialog.show()
    }

    private fun changePassword(dialogView : View) {
        val password : Editable? = dialogView.findViewById<EditText>(R.id.passwordOnChangePassword)?.text

        val currentUser : FirebaseUser = Firebase.auth.currentUser!!

        if (!password.isNullOrEmpty()) {
            currentUser
                .reauthenticate(getCredential(currentUser.email!!, password.toString()))
                .addOnSuccessListener { updatePassword(dialogView) }
                .addOnFailureListener {
                    Toast.makeText(context, getString(R.string.message_wrong_password), Toast.LENGTH_SHORT).show()
                }
        }
    }

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

        Firebase
            .auth
            .currentUser!!
            .updatePassword(newPassword.toString())
            .addOnSuccessListener {
                val dialog : MessageDialog = MessageDialog(
                    requireContext(),
                    requireView()
                ) {
                    Firebase.auth.signOut()
                    alertDialog.dismiss()
                }
                dialog.navigateTo(R.id.loginFragment)
                dialog
                    .create(getString(R.string.message_successfully_updated_password))
                    .show()
            }
    }
}