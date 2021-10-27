package com.mobile.sharedwallet.fragment

import android.app.ActionBar
import android.app.AlertDialog
import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.mobile.sharedwallet.MainActivity
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.constants.FirebaseConstants
import com.mobile.sharedwallet.dialog.MessageDialog
import com.mobile.sharedwallet.models.Cagnotte
import com.mobile.sharedwallet.models.User
import com.mobile.sharedwallet.utils.Utils


class HomeFragment : Fragment() {

    private lateinit var store : FirebaseFirestore

    private var user : User? = null

    private var cagnottes : HashMap<String, Cagnotte> = HashMap()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = FirebaseFirestore.getInstance()
        user = LoginFragment.user
        loadCagnotteList()
    }

    override fun onStart() {
        super.onStart()
        Utils.checkLoggedIn(requireActivity())
    }

    override fun onResume() {
        super.onResume()
        cagnottes.forEach { createButtonClick(it.key) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<FloatingActionButton>(R.id.createButton).setOnClickListener{
            if (FirebaseAuth.getInstance().currentUser?.isEmailVerified == true) openDialog()
            else {
                val dialog = MessageDialog(requireActivity())
                    .verifyAccountDialog()
                dialog.show()
            }
        }

        view.findViewById<FloatingActionButton>(R.id.profileButton).setOnClickListener{
            (requireActivity() as MainActivity).replaceFragment(ProfileFragment())
        }

        view.findViewById<TextView>(R.id.welcome).text =
            getString(R.string.welcome)
                .plus(getString(R.string.space))
                .plus(user?.firstName)
    }


    private fun loadCagnotteList() {
        // Load all pots current user is involved in
        user?.let { user : User ->
            store
                .collection(FirebaseConstants.CollectionNames.Pot)
                .whereArrayContains(Cagnotte.Attributes.PARTICIPANTS.string, user.toFirebase())
                .get()
                .addOnSuccessListener { result : QuerySnapshot ->
                    for (document in result) {
                        val cagnotte : Cagnotte = document.toObject()
                        cagnottes[document.id] = cagnotte
                        createButtonClick(document.id)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), getString(R.string.message_error_fetch_cagnottes), Toast.LENGTH_SHORT).show()
                }
        }

    }


    private fun openDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.pot_name))

        val layout = FrameLayout(builder.context)
        layout.setPadding(125,15,125,0)

        // Set up the input
        val input = EditText(layout.context)
        input.gravity = Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL
        input.inputType = 1

        layout.addView(input)
        builder.setView(layout)

        // Set up the buttons
        builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
            // Here you get get input text from the Edittext
            val mText = input.text.toString()
            if(mText != "") {
                if(!ArrayList(cagnottes.values.map { it.name }).contains(mText)) {
                    addCagnotte(mText)
                } else {
                    Toast.makeText(requireActivity(), getString(R.string.message_pot_name_already_exists), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireActivity(), getString(R.string.message_invalid_group_name), Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }

        builder.show()
    }


    private fun createButtonClick(cagnotteRef : String) {
        val cagnotte = cagnottes[cagnotteRef]!!

        val liste = view?.findViewById<LinearLayout>(R.id.listCagnotte)
        val newTextView = TextView(requireActivity())
        newTextView.setPadding(90,50,80,50)
        newTextView.layoutParams = ActionBar.LayoutParams(
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.MATCH_PARENT
        )
        newTextView.setTextColor(Color.BLACK)
        newTextView.textSize = 25f
        newTextView.text = cagnotte.name
        newTextView.id = cagnotte.name.hashCode()
        newTextView.isClickable = true
        newTextView.setOnClickListener{
            loadCagnotteView(cagnotteRef)
        }
        liste?.addView(newTextView)
    }


    private fun loadCagnotteView(cagnotteRef : String) {
        cagnottes[cagnotteRef]?.let { CagnotteFragment.pot = it }
        CagnotteFragment.potRef = cagnotteRef
        (requireActivity() as MainActivity).replaceFragment(CagnotteFragment())
    }


    private fun addCagnotte(name: String){
        user?.let { user : User ->
            // Create a new cagnotte with a first and last name
            val newCagnotte = Cagnotte(name, Timestamp.now(), ArrayList(), arrayListOf(user))

            // Add a new document with a generated ID
            store
                .collection(FirebaseConstants.CollectionNames.Pot)
                .add(newCagnotte.toFirebase())
                .addOnSuccessListener {
                    val id = it.id
                    cagnottes[id] = newCagnotte
                    createButtonClick(id)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireActivity(), getString(R.string.message_error_creating_new_pot), Toast.LENGTH_SHORT).show()
                    Log.w(ContentValues.TAG, "Error writing document", e)
                }
        }
    }
}