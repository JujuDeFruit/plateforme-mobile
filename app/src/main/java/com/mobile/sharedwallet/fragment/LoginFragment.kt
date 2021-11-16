package com.mobile.sharedwallet.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.mobile.sharedwallet.MainActivity
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.dialog.ResetPasswordDialog
import com.mobile.sharedwallet.models.User
import com.mobile.sharedwallet.utils.Overlay
import com.mobile.sharedwallet.utils.Utils
import com.mobile.sharedwallet.utils.Validate
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoginFragment(): Fragment() {

    private var overlay : Overlay? = null

    companion object {
        var user : User? = null
            get() {
                if (field == null || field!!.isNullOrEmpty())
                    return User()
                return field
            }
    }

    private var email : String = String()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.login).setOnClickListener{
            if (validate()) login()
        }

        view.findViewById<Button>(R.id.register).setOnClickListener{
            (activity as MainActivity).replaceFragment(RegisterFragment(), true)
        }

        view.findViewById<Button>(R.id.resetPassword).setOnClickListener{
            ResetPasswordDialog().show(parentFragmentManager, "ResetPasswordDialog")
        }

        overlay = Overlay(requireView())
    }

    /**
     * Email validation
     */
    private fun validate() : Boolean {
        email = view?.findViewById<EditText>(R.id.email)?.text.toString()
        return Validate.checkEmailConditions(requireContext(), email)
    }

    /**
     * Login function => firebase async methods
     */
    private fun login() {
        view?.let { view : View ->
            overlay?.show()
            val password = view.findViewById<EditText>(R.id.password).text.toString()
            FirebaseAuth
                .getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    MainScope().launch {
                        user = Utils.createUserFromFirebaseUser(it.user, true)
                        overlay?.hide()
                        user?.let { u : User -> (requireActivity() as MainActivity).checkIfInvitation(u) }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
                    overlay?.hide()
                }
        }
    }

}