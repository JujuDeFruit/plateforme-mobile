package com.mobile.sharedwallet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.mobile.sharedwallet.fragment.LoginFragment


class MainActivity : AppCompatActivity() {

    private val layout : Int = R.id.activityGlobalLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .add(layout, LoginFragment())
            .disallowAddToBackStack()
            .commit()
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