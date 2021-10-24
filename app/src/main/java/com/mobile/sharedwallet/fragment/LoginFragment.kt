package com.mobile.sharedwallet.fragment

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mobile.sharedwallet.R

class LoginFragment: Fragment() {

    private var email : String = String()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.login_fragment, container, false)
        view.findViewById<Button>(R.id.login).setOnClickListener{
            if (validate()) login()
        }
        view.findViewById<Button>(R.id.register).setOnClickListener{
            findNavController().navigate(R.id.registerFragment)
        }

        return view;
    }

    /**
     * Email validation
     */
    private fun validate() : Boolean {
        email = view?.findViewById<EditText>(R.id.email)?.text.toString()
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(activity, getString(R.string.message_email_not_good_format), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * Login function => firebase async methods
     */
    private fun login() {
        val password = view?.findViewById<EditText>(R.id.password)?.text.toString()
        Firebase
            .auth
            .signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                findNavController().navigate(R.id.homeFragment)
            }
            .addOnFailureListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
    }
}