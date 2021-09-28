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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.constants.FirebaseConstants
import com.mobile.sharedwallet.models.User

class LoginFragment: Fragment() {

    private lateinit var mAuth: FirebaseAuth

    private var email : String = String()

    companion object {
        var currentUser : User = User()
            get() {
                if (field.isEmpty()) {
                    field = User("ACGGNPVUBIPpaH7A480QN6V7npU2", "test", "test", "test@yahoo.fr", true)
                }
                return field
            }
            set(value: User) { field = value }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
        if (email.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(activity, "Email is not in the good format !", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * Login function => firebase async methods
     */
    private fun login() {
        val password = view?.findViewById<EditText>(R.id.password)?.text.toString()
        mAuth = Firebase.auth;
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Firebase
                    .firestore
                    .collection(FirebaseConstants.Users)
                    .whereEqualTo("uid", it.user?.uid)
                    .limit(1)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            // Cast firebase user to kotlin user model
                            currentUser = document.toObject<User>()
                        }
                        // Redirect to home fragment after authentication
                        findNavController().navigate(R.id.homeFragment)
                    }
                    .addOnFailureListener{ _ ->
                        Toast.makeText(activity, "An error occured. PLease retry.", Toast.LENGTH_SHORT).show();
                    }
            }
            .addOnFailureListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
    }
}