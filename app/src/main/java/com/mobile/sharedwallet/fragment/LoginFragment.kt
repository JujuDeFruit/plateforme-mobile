package com.mobile.sharedwallet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mobile.sharedwallet.R

class LoginFragment: Fragment() {

    private lateinit var mAuth: FirebaseAuth

    companion object {
        var uID : String
            get() = this.uID
            set(value) { this.uID = value }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.login_fragment, container, false)

        view.findViewById<Button>(R.id.login).setOnClickListener{
            login()
        }

        return view;
    }

    fun login() {
        val email = view?.findViewById<EditText>(R.id.email)?.text.toString()
        val password = view?.findViewById<EditText>(R.id.password)?.text.toString()

        mAuth = Firebase.auth;
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                findNavController().navigate(R.id.homeFragment)
                uID = it.user?.uid.toString()
                Toast.makeText(activity, "OK", Toast.LENGTH_SHORT).show();
            }
            .addOnFailureListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
    }
}