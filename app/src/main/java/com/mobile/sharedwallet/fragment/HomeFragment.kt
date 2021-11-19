package com.mobile.sharedwallet.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.mobile.sharedwallet.MainActivity
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.adapter.CagnotteAdapter
import com.mobile.sharedwallet.constants.FirebaseConstants
import com.mobile.sharedwallet.dialog.CagnotteSettingsDialog
import com.mobile.sharedwallet.dialog.MessageDialog
import com.mobile.sharedwallet.models.Cagnotte
import com.mobile.sharedwallet.models.User
import com.mobile.sharedwallet.models.WaitingPot
import com.mobile.sharedwallet.utils.Colors
import com.mobile.sharedwallet.utils.Overlay
import com.mobile.sharedwallet.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class HomeFragment : Fragment() {

    private lateinit var store : FirebaseFirestore
    private var user : User? = null
    private var overlay : Overlay? = null
    private lateinit var adapter : CagnotteAdapter

    companion object {
        var cagnottes : HashMap<String, Cagnotte> = HashMap()

        suspend fun loadCagnotteList() {
            // Load all pots current user is involved in
            LoginFragment.user?.let { user : User ->
                withContext(Dispatchers.Main) {
                    try {
                        val result: QuerySnapshot = FirebaseFirestore
                            .getInstance()
                            .collection(FirebaseConstants.CollectionNames.Pot)
                            .whereArrayContains(Cagnotte.Attributes.UIDS.string, user.uid!!)
                            .get()
                            .await()

                        val lCagnottes: HashMap<String, Cagnotte> = HashMap()
                        for (document in result) {
                            val cagnotte : Cagnotte = document.toObject()
                            lCagnottes[document.id] = cagnotte
                        }
                        cagnottes = lCagnottes.toList().sortedByDescending { (_, v) -> v.creationDate }.toMap() as HashMap<String, Cagnotte>
                    } catch (e: Exception) {
                        cagnottes = HashMap()
                    }
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = FirebaseFirestore.getInstance()
        user = LoginFragment.user

        adapter = CagnotteAdapter(cagnottes, ::loadCagnotteView)
    }


    override fun onStart() {
        super.onStart()
        Utils.checkLoggedIn(requireActivity())
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

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

        overlay = Overlay(view)

        // Getting SwipeContainerLayout
        val swipe = view.findViewById<SwipeRefreshLayout>(R.id.swipeContainer)
        // Adding Listener
        swipe.setOnRefreshListener {
            requireActivity().recreate()

            // To keep animation for 4 seconds
            Handler(Looper.getMainLooper()).postDelayed({ // Stop animation (This will be after 3 seconds)
                swipe.isRefreshing = false
            }, 1000) // Delay in millis
        }

        view.findViewById<ListView>(R.id.listCagnotte).adapter = adapter
    }


    override fun onResume() {
        super.onResume()
        // cagnottes.forEach { addCardToView(it.key) }
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


    /*private fun addCardToView(cagnotteRef : String) {
        val cagnotte = cagnottes[cagnotteRef]!!

        val liste = view?.findViewById<LinearLayout>(R.id.listCagnotte)

        val container : ViewGroup = layoutInflater.inflate(R.layout.activity_main, null) as ViewGroup

        val cardView : View = LayoutInflater.from(requireContext()).inflate(R.layout.cagnotte_row, container,false)

        cardView.findViewById<MaterialCardView>(R.id.cagnottePreview).setCardBackgroundColor(Color.parseColor(cagnotte.color))
        cardView.findViewById<TextView>(R.id.cardTextPreview).text = cagnotte.name

        val totalSpentAmountPreview = cardView.findViewById<TextView>(R.id.totalSpentAmountPreview)

        var sum = 0f
        cagnotte.totalSpent.forEach { sum += it.amountPaid }

        totalSpentAmountPreview?.text = sum.toString().plus(getString(R.string.space)).plus(getString(R.string.euro_symbol))

        cardView.setOnClickListener{
            loadCagnotteView(cagnotteRef)
        }

        liste?.addView(cardView)
    }*/


    private fun loadCagnotteView(cagnotteRef : String) {
        overlay?.show()
        CagnotteFragment.pot = cagnottes[cagnotteRef]!!
        CagnotteFragment.pot.totalSpent.sortByDescending { it.creationDate }
        CagnotteFragment.potRef = cagnotteRef

        MainScope().launch {
            CagnotteFragment.pot.participants.forEach {
                it.photo = Utils.fetchPhoto(it.uid)
            }
            overlay?.hide()
            (requireActivity() as MainActivity).replaceFragment(CagnotteFragment())
        }
    }


    private fun addCagnotte(name: String) {
        user?.let { user : User ->
            // Create a new cagnotte with a first and last name
            val newCagnotte = Cagnotte(arrayListOf(user.uid!!), name, Colors.randomColor(), Timestamp.now(), user.uid, ArrayList(), arrayListOf(Utils.castUserToParticipant(user)))

            // Add a new document with a generated ID
            store
                .collection(FirebaseConstants.CollectionNames.Pot)
                .add(newCagnotte.toFirebase())
                .addOnSuccessListener { docRef : DocumentReference ->
                    store
                        .collection(FirebaseConstants.CollectionNames.WaitingPot)
                        .add(WaitingPot(docRef, ArrayList()).toFirebase())
                        .addOnSuccessListener { _ ->
                            val id = docRef.id
                            cagnottes[id] = newCagnotte
                            // addCardToView(id)
                            adapter.add(Pair(id, newCagnotte))
                        }
                }
                .addOnFailureListener { _ ->
                    Toast.makeText(requireActivity(), getString(R.string.message_error_creating_new_pot), Toast.LENGTH_SHORT).show()
                }
        }
    }
}