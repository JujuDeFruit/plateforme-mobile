package com.mobile.sharedwallet.fragment

import android.app.ActionBar
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.mobile.sharedwallet.R
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.toObject
import com.mobile.sharedwallet.MainActivity
import com.mobile.sharedwallet.constants.FirebaseConstants
import com.mobile.sharedwallet.models.Cagnotte
import com.mobile.sharedwallet.models.Depense
import android.view.Gravity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.sharedwallet.dialog.MessageDialog
import com.mobile.sharedwallet.models.User
import com.mobile.sharedwallet.utils.Utils
import java.util.*
import kotlin.collections.HashMap


class HomeFragment : Fragment() {

    private lateinit var store : FirebaseFirestore

    private var user : User? = LoginFragment.user

    private var cagnotteList : LinkedList<Cagnotte>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = FirebaseFirestore.getInstance()
        cagnotteList = LinkedList<Cagnotte>()
        loadCagnotteList()
    }


    override fun onStart() {
        super.onStart()
        Utils.checkLoggedIn(findNavController())
    }


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view: View = inflater.inflate(R.layout.home_fragment, container, false)

        cagnotteList?.forEach { cagnotte -> createButtonClick(cagnotte.name) }

        view.findViewById<FloatingActionButton>(R.id.createButton).setOnClickListener{
            if (FirebaseAuth.getInstance().currentUser?.isEmailVerified == true) openDialog()
            else {
                val dialog = MessageDialog(requireContext(), requireView())
                    .verifyAccountDialog()
                dialog.show()
            }
        }

        view.findViewById<FloatingActionButton>(R.id.profileButton).setOnClickListener{
            findNavController().navigate(R.id.profileFragment)
        }

        view.findViewById<TextView>(R.id.welcome).text =
            getString(R.string.welcome)
                .plus(getString(R.string.space))
                .plus(user?.firstName)

        return view
    }

    private fun loadCagnotteList() {

        // Load all pots current user is involved in
        user?.let { user : User ->
            store
                .collection(FirebaseConstants.CollectionNames.Pot)
                .whereArrayContains(Cagnotte.Attributes.PARTICIPANTS.string, user.toFirebase())
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val cagnotte = document.toObject<Cagnotte>()
                        cagnotteList?.add(cagnotte)
                        createButtonClick(cagnotte.name)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "Error getting documents: ", exception)
                }
        }

    }

    private fun openDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.pot_name))

        val layout = FrameLayout(builder.context)
        layout.setPadding(125,15,125,0)

        // Set up the input
        val input : EditText = EditText(layout.context)
        input.gravity = Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL

        layout.addView(input)
        builder.setView(layout)

        // Set up the buttons
        builder.setPositiveButton(getString(R.string.ok), DialogInterface.OnClickListener { _, _ ->
            // Here you get get input text from the Edittext
            val mText = input.text.toString()
            if(mText != ""){
                createButtonClick(mText)
                addCagnotte(mText)
            } else {
                Toast.makeText(activity, getString(R.string.message_invalid_group_name), Toast.LENGTH_SHORT).show()
            }
        })

        builder.setNegativeButton(getString(R.string.cancel), DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })

        builder.show()
    }

    private fun createButtonClick(inputText : String?) {
        val liste = view?.findViewById<LinearLayout>(R.id.listCagnotte)
        val newTextView = TextView(activity)
        newTextView.setPadding(90,50,80,50)
        newTextView.layoutParams = ActionBar.LayoutParams(
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.MATCH_PARENT
        )
        newTextView.setTextColor(Color.BLACK)
        newTextView.textSize = 25f
        newTextView.text = inputText
        newTextView.id = inputText.hashCode()
        newTextView.isClickable = true
        newTextView.setOnClickListener{
            loadCagnottePage(inputText)
        }
        liste?.addView(newTextView)
    }

    private fun loadCagnottePage(inputText : String?){
        if (inputText != null) {
            (activity as MainActivity).setCagnotteToLoad(inputText)
            findNavController().navigate(R.id.cagnotteFragment)
        }

        val liste = view?.findViewById<LinearLayout>(R.id.listCagnotte)
        val tvDynamic = TextView(activity)
        tvDynamic.setPadding(90,50,80,50)
        tvDynamic.layoutParams = ActionBar.LayoutParams(
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.MATCH_PARENT
        )
        tvDynamic.setTextColor(Color.BLACK)
        tvDynamic.textSize = 25f
        tvDynamic.text = inputText
        liste?.addView(tvDynamic)
    }

    private fun addCagnotte(name: String){

        user?.let { user : User ->
            // Create a new cagnotte with a first and last name
            val info : HashMap<String, Any?> =
                Cagnotte(
                    name,
                    Timestamp.now(),
                    ArrayList<Depense>(),
                    arrayListOf(user)
                ).toFirebase()

            // Add a new document with a generated ID
            store
                .collection(FirebaseConstants.CollectionNames.Pot)
                .add(info)
                .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e ->
                    Toast.makeText(activity, getString(R.string.message_error_creating_new_pot), Toast.LENGTH_SHORT).show()
                    Log.w(ContentValues.TAG, "Error writing document", e)
                }
        }
    }
}