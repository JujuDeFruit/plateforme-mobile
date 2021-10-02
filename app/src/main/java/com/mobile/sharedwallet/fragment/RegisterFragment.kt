package com.mobile.sharedwallet.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.os.Message
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.mobile.sharedwallet.dialog.MessageDialog
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.utils.Utils
import java.util.regex.Pattern

class RegisterFragment: Fragment() {

    private lateinit var auth : FirebaseAuth

    private var firstName : String? = null
    private var lastName : String? = null
    private var email : String? = null
    private var password1 : String? = null
    private var password2 : String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View? = inflater.inflate(R.layout.register_fragment, container, false)

        auth = Firebase.auth

        view?.findViewById<FloatingActionButton>(R.id.createAccount)?.setOnClickListener {
            if (validate(view)) {
                createAccount()
            }
        }

        return view
    }

    private fun validate(view : View) : Boolean {

        firstName = view.findViewById<EditText>(R.id.firstNameEditText).text.toString();
        lastName = view.findViewById<EditText>(R.id.lastNameEditText).text.toString();
        email = view.findViewById<EditText>(R.id.emailEditText).text.toString();
        password1 = view.findViewById<EditText>(R.id.passwordEditText).text.toString();
        password2 = view.findViewById<EditText>(R.id.password2EditText).text.toString();

        val passwordPattern : String = "^(?=.*\\d)(?=.*[A-Z])[0-9a-zA-Z]{4,}$"

        /*
            Check form conditions before to create an account
         */
        if (firstName.isNullOrEmpty()) {
            Toast.makeText(activity, "First name must not be empty !", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (lastName.isNullOrEmpty()) {
            Toast.makeText(activity, "Last name must not be empty !", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (email.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(activity, "Please enter a valid email !", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (password1 != password2) {
            Toast.makeText(activity, "Passwords must match !", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(!Pattern.compile(passwordPattern).matcher(password1).matches()
            || !Pattern.compile(passwordPattern).matcher(password2).matches()) {
            Toast.makeText(
                activity,
                "Password must contain at least one digit, one upper case and has to be 8 char long minimum",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }

    private fun createAccount() {
        /*
            Create authentication account
         */
        auth
            .createUserWithEmailAndPassword(email!!, password1!!)
            .addOnSuccessListener {
                sendVerificationEmail()
            }
            .addOnFailureListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendVerificationEmail() {
        auth
            .currentUser
            ?.sendEmailVerification()
            ?.addOnSuccessListener {
                submitInfos()
            }
            ?.addOnFailureListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun submitInfos() {
        auth
            .currentUser!!
            .updateProfile(userProfileChangeRequest {
                displayName = Utils.getDisplayNameFromFirstnameAndLastName(firstName, lastName)
            })
            .addOnSuccessListener {
                val dialog : MessageDialog = MessageDialog(requireContext(), requireView())
                dialog.navigateTo(R.id.homeFragment)
                dialog
                    .create("A confirmation email has been sent. Please confirm your email.")
                    .show()
            }
            .addOnFailureListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT)
            }

    }
}