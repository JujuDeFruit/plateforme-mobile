package com.mobile.sharedwallet.fragment

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.mobile.sharedwallet.MainActivity
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.constants.FirebaseConstants
import com.mobile.sharedwallet.dialog.ResetPasswordDialog
import com.mobile.sharedwallet.models.Cagnotte
import com.mobile.sharedwallet.models.User
import com.mobile.sharedwallet.utils.Overlay
import com.mobile.sharedwallet.utils.Utils
import com.mobile.sharedwallet.utils.Validate
import kotlinx.coroutines.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class LoginFragment(): Fragment() {

    private var overlay : Overlay? = null

    companion object {
        var user : User? = null
            get() {
                if (field == null || field!!.isNullOrEmpty())
                    return User()
                return field
            }

        suspend fun loadCagnotteList() : HashMap<String, Cagnotte> {
            // Load all pots current user is involved in
            return user?.let { user: User ->
                return@let withContext(Dispatchers.Main) {
                    try {
                        val result: QuerySnapshot = FirebaseFirestore
                            .getInstance()
                            .collection(FirebaseConstants.CollectionNames.Pot)
                            .whereArrayContains(
                                Cagnotte.Attributes.PARTICIPANTS.string,
                                Utils.castUserToParticipant(user).toFirebase()
                            )
                            .get()
                            .await()

                        val cagnottes: HashMap<String, Cagnotte> = HashMap()
                        for (document in result) {
                            val cagnotte : Cagnotte = document.toObject()
                            cagnottes[document.id] = cagnotte
                        }
                        return@withContext cagnottes
                    } catch (e: Exception) {
                        return@withContext null
                    }
                }
            } ?: HashMap()
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
                        user?.let { u -> (requireActivity() as MainActivity).checkIfInvitation(u) }
                        val cagnottes = loadCagnotteList()
                        (requireActivity() as MainActivity).replaceFragment(HomeFragment(cagnottes), false)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
                    overlay?.hide()
                }
        }
    }

}