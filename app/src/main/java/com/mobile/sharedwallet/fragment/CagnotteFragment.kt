package com.mobile.sharedwallet.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mobile.sharedwallet.MainActivity
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.models.Cagnotte
import com.mobile.sharedwallet.utils.Utils

class CagnotteFragment : Fragment() {

    companion object {
        var pot : Cagnotte = Cagnotte()
            get() {
                if (field.isEmpty()) {
                    field = Cagnotte()
                }
                return field
            }
        var potRef : String = ""
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.cagnotte_fragment, container, false)
    }


    override fun onStart() {
        super.onStart()
        Utils.checkLoggedIn(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.cagnotteName).text = pot.name

        // Set up navigation bar & navigation controller
        val navController = (childFragmentManager.findFragmentById(R.id.fragmentPlace) as NavHostFragment).navController
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigationHome, R.id.navigationBalance))
        setupActionBarWithNavController(requireActivity() as MainActivity, navController, appBarConfiguration)
        setupWithNavController(view.findViewById<BottomNavigationView>(R.id.navView), navController)
    }
}
