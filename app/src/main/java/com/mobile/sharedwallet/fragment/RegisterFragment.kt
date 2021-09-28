package com.mobile.sharedwallet.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mobile.sharedwallet.R

class RegisterFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View? = inflater.inflate(R.layout.register_fragment, container, false)

        view?.findViewById<Button>(R.id.createAccount)?.setOnClickListener {
            if (validate()) {
                createAccount()
            }
        }

        return view
    }

    // fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    fun validate() : Boolean {
        Toast.makeText(activity, "Validation running", Toast.LENGTH_SHORT).show()
        return false
    }

    fun createAccount() {

    }
}