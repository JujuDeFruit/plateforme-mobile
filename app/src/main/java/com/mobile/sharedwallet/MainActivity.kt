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
import com.mobile.sharedwallet.models.User
import com.mobile.sharedwallet.models.WaitingPot
import com.mobile.sharedwallet.utils.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
                // checkIfInvitation(LoginFragment.user!!)  // TODO Cagnottes not loaded
                transaction.add(layout, HomeFragment(HomeFragment.loadCagnotteList()))
                transaction.commit()
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
        else clearFragmentManager()

        replace.commit()


    }

    private fun clearFragmentManager() {
        for (i in 0 until supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStack()
        }
    }
}