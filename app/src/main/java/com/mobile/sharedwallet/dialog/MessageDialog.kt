package com.mobile.sharedwallet.dialog

import android.app.AlertDialog
import android.content.Context
import android.provider.Settings.Global.getString
import android.view.View
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mobile.sharedwallet.R

class MessageDialog (
    private val context: Context,
    private val view: View,
    private val callback: (() -> Any)? = null) {

    private var navigateToId : Int? = null

    fun navigateTo(id: Int) {
        navigateToId = id
    }

    fun create(message : String) : AlertDialog.Builder {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
            .setPositiveButton(context.getString(R.string.ok)) { _, _ ->
                if (navigateToId != null) findNavController(view).navigate(navigateToId!!)
                callback?.invoke()
            }
        // Create the AlertDialog object and return it
        builder.create()
        return builder
    }

    fun verifyAccountDialog(id : Int): AlertDialog.Builder {
        navigateTo(id)
        val builder : AlertDialog.Builder = create(
            context.getString(R.string.message_please_verify_account_before)
        )

        builder.setNeutralButton(context.getString(R.string.re_send_email)) { _, _ ->
            Firebase
                .auth
                .currentUser!!
                .sendEmailVerification()
                .addOnSuccessListener {
                    Toast.makeText(context, context.getString(R.string.email_sent), Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, context.getString(R.string.message_email_not_send), Toast.LENGTH_SHORT).show()
                }
        }

        builder.setOnDismissListener {
            if (navigateToId != null) findNavController(view).navigate(navigateToId!!)
        }

        return builder
    }
}
