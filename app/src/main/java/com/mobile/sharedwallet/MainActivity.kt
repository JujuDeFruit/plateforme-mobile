package com.mobile.sharedwallet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //val fragmentID : Int = if (LoginFragment.uID != null) R.id.homeFragment else R.id.loginFragment;
        findNavController(R.id.navigation).navigate(R.id.loginFragment)
    }

    private var globalCagnottetoLoad : String = ""

    fun getCagnotteToLoad() : String{
        return globalCagnottetoLoad
    }

    fun setCagnotteToLoad(cName : String){
        globalCagnottetoLoad = cName
    }


}