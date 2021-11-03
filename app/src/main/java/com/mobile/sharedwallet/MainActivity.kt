package com.mobile.sharedwallet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.mobile.sharedwallet.constants.FirebaseConstants
import com.mobile.sharedwallet.dialog.InvitationDialog
import com.mobile.sharedwallet.fragment.HomeFragment
import com.mobile.sharedwallet.fragment.LoginFragment
import com.mobile.sharedwallet.models.Cagnotte
import com.mobile.sharedwallet.models.User
import com.mobile.sharedwallet.models.WaitingPot
import com.mobile.sharedwallet.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val layout : Int = R.id.activityGlobalLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialisation()
    }

    private fun initialisation() {
        val transaction : FragmentTransaction = supportFragmentManager
            .beginTransaction()
            .disallowAddToBackStack()

        val user : FirebaseUser? = FirebaseAuth.getInstance().currentUser

        if(user == null) {
            transaction.add(layout, LoginFragment())
            transaction.commit()
        } else {
            GlobalScope.launch {
                LoginFragment.user = Utils.createUserFromFirebaseUser(user, true)
                checkIfInvitation(LoginFragment.user!!)  // TODO Cagnottes not loaded
                transaction.add(layout, HomeFragment(LoginFragment.loadCagnotteList()))
                transaction.commit()
            }
        }
    }

    suspend fun checkIfInvitation(user : User) {
        val store : FirebaseFirestore = FirebaseFirestore.getInstance()
        user.uid?.let {
            withContext(Dispatchers.Main) {
                try {
                    val snapShot : QuerySnapshot = store
                        .collection(FirebaseConstants.CollectionNames.WaitingPot)
                        .whereArrayContains(WaitingPot.Attributes.WAITING_UID.string, it)
                        .get()
                        .await()

                    for(doc in snapShot) {
                        InvitationDialog(user, (doc.get(WaitingPot.Attributes.POT_REF.string) as DocumentReference).path, doc.id).show(supportFragmentManager, "InvitationDialog")
                    }
                }
                catch (_ : Exception) {}
            }
        }
    }

    fun replaceFragment(fragment: Fragment, possibleReturn : Boolean = true) {
        val replace : FragmentTransaction = supportFragmentManager
            .beginTransaction()
            .replace(layout, fragment)

        if(possibleReturn) {
            replace.addToBackStack(null)
        }

        replace.commit()

        if (!possibleReturn) clearFragmentManager()
    }

    private fun clearFragmentManager() {
        for (i in 0 until supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStack()
        }
    }
}