package com.mobile.sharedwallet.fragment

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.mobile.sharedwallet.dialog.MessageDialog
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.utils.Utils
import java.util.regex.Pattern

class RegisterFragment: Fragment() {

    private lateinit var auth : FirebaseAuth
    private lateinit var user : FirebaseUser

    private var firstName : String? = null
    private var lastName : String? = null
    private var email : String? = null
    private var password1 : String? = null
    private var password2 : String? = null

    /**
     * Affect firebase
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        user = auth.currentUser!!
    }

    /**
     * Bind buttons
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View? = inflater.inflate(R.layout.register_fragment, container, false)

        view?.findViewById<FloatingActionButton>(R.id.createAccount)?.setOnClickListener {
            if (validate(view)) {
                createAccount()
            }
        }

        return view
    }

    /**
     * Validate form
     *
     * @param view : View of the form
     * @return true if everything is OK, false else
     */
    private fun validate(view : View) : Boolean {

        firstName = view.findViewById<EditText>(R.id.firstNameEditText).text.toString();
        lastName = view.findViewById<EditText>(R.id.lastNameEditText).text.toString();
        email = view.findViewById<EditText>(R.id.emailEditText).text.toString();
        password1 = view.findViewById<EditText>(R.id.passwordEditText).text.toString();
        password2 = view.findViewById<EditText>(R.id.password2EditText).text!!.toString();

        val passwordPattern : String = "^(?=.*\\d)(?=.*[A-Z])[0-9a-zA-Z]{4,}$"

        /*
            Check form conditions before to create an account
         */
        if (firstName.isNullOrEmpty()) {
            Toast.makeText(activity, getString(R.string.message_first_name_must_not_empty), Toast.LENGTH_SHORT).show()
            return false
        }
        else if (lastName.isNullOrEmpty()) {
            Toast.makeText(activity, getString(R.string.message_last_name_must_not_empty), Toast.LENGTH_SHORT).show()
            return false
        }
        else if (email.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(activity, getString(R.string.message_enter_valid_email), Toast.LENGTH_SHORT).show()
            return false
        }
        else if (password1 != password2) {
            Toast.makeText(activity, getString(R.string.message_passwords_must_match), Toast.LENGTH_SHORT).show()
            return false
        }
        else if(!Pattern.compile(passwordPattern).matcher(password1).matches()
            || !Pattern.compile(passwordPattern).matcher(password2).matches()) {
            Toast.makeText(
                activity,
                getString(R.string.message_password_conditions),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }

    /**
     * Create authentication account
     */
    private fun createAccount() {
        auth
            .createUserWithEmailAndPassword(email!!, password1!!)
            .addOnSuccessListener {
                sendVerificationEmail()
            }
            .addOnFailureListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Send verification e-mail
     */
    private fun sendVerificationEmail() {
        user
            .sendEmailVerification()
            .addOnSuccessListener {
                submitInfos()
            }
            .addOnFailureListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Update user data in Firebase
     */
    private fun submitInfos() {
        user
            .updateProfile(userProfileChangeRequest {
                displayName = Utils.getDisplayNameFromFirstnameAndLastName(firstName, lastName)
            })
            .addOnSuccessListener {
                val dialog : MessageDialog = MessageDialog(requireContext(), requireView())
                dialog.navigateTo(R.id.homeFragment)
                dialog
                    .create(getString(R.string.message_confirmation_email_sent))
                    .show()
            }
            .addOnFailureListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
    }
}