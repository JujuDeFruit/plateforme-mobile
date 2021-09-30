package com.mobile.sharedwallet.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.models.User

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view : View = inflater.inflate(R.layout.profile_fragment, container, false)!!

        val user : User = LoginFragment.currentUser

        view.findViewById<TextView>(R.id.firstNameProfile).text = user.firstName
        view.findViewById<TextView>(R.id.lastNameProfile).text = user.lastName
        view.findViewById<TextView>(R.id.emailProfile).text = user.email
        view.findViewById<TextView>(R.id.validEmailProfile).text = if(user.validEmail) resources.getString(R.string.yes) else resources.getString(R.string.no)

        view.findViewById<FloatingActionButton>(R.id.logoutProfile).setOnClickListener {
            LoginFragment.currentUser = User()
            findNavController().navigate(R.id.loginFragment)
        }

        return view
    }
}