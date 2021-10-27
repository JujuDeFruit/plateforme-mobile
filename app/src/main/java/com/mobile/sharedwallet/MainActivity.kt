package com.mobile.sharedwallet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mobile.sharedwallet.fragment.HomeFragment
import com.mobile.sharedwallet.fragment.LoginFragment
import com.mobile.sharedwallet.utils.Utils
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val layout : Int = R.id.activityGlobalLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()


        val transaction : FragmentTransaction = supportFragmentManager
            .beginTransaction()
            .disallowAddToBackStack()

        val user : FirebaseUser? = FirebaseAuth.getInstance().currentUser


        if(user == null) {
            transaction.add(layout, LoginFragment())
            transaction.commit()
        } else {
            transaction.add(layout, HomeFragment())
            GlobalScope.launch {
                LoginFragment.user = Utils.createUserFromFirebaseUser(user, true)
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
            replace.setReorderingAllowed(true)
        }
        else replace.disallowAddToBackStack()

        replace.commit()
    }

    private var globalCagnottetoLoad : String = ""

    fun getCagnotteToLoad() : String{
        return globalCagnottetoLoad
    }

    fun setCagnotteToLoad(cName : String){
        globalCagnottetoLoad = cName
    }
}