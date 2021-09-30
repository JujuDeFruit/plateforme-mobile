package com.mobile.sharedwallet.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.constants.FirebaseConstants
import com.mobile.sharedwallet.models.User
import java.util.regex.Pattern

class RegisterFragment: Fragment() {

    private var firstName : String? = null
    private var lastName : String? = null
    private var email : String? = null
    private var password1 : String? = null
    private var password2 : String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View? = inflater.inflate(R.layout.register_fragment, container, false)

        view?.findViewById<Button>(R.id.createAccount)?.setOnClickListener {
            if (validate(view)) {
                createAccount()
            }
        }

        return view
    }

    fun validate(view : View) : Boolean {

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

    fun createAccount() {
        /*
            Create authentication account
         */
        Firebase
            .auth
            .createUserWithEmailAndPassword(email!!, password1!!)
            .addOnSuccessListener {
                /*
                    Create specific user in Store with informations
                 */
                val user : User = User(
                    it.user!!.uid,
                    firstName!!,
                    lastName!!,
                    email!!,
                    false
                )
                Firebase
                    .firestore
                    .collection(FirebaseConstants.Users)
                    .add(user.toFirebase())
                    .addOnSuccessListener {
                        LoginFragment.currentUser = user
                        val dialog = createDialog()
                        dialog.show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            activity,
                            "An error occured while creating your acccount. Please retry !",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun createDialog() : AlertDialog.Builder {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("A confirmation email has been sent. Please confirm your email.")
            .setPositiveButton(
                "OK",
                DialogInterface.OnClickListener { _, _ ->
                    findNavController().navigate(R.id.homeFragment)
                })
        // Create the AlertDialog object and return it
        builder.create()
        return builder
    }
}