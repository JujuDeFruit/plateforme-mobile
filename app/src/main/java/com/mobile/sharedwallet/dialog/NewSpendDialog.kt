package com.mobile.sharedwallet.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.adapter.ParticipantsAdapter
import com.mobile.sharedwallet.constants.FirebaseConstants
import com.mobile.sharedwallet.fragment.CagnotteFragment
import com.mobile.sharedwallet.models.Cagnotte
import com.mobile.sharedwallet.models.Depense
import com.mobile.sharedwallet.models.Participant
import com.mobile.sharedwallet.utils.Utils
import com.mobile.sharedwallet.adapter.SpinnerAdapter

class NewSpendDialog : DialogFragment(){

    private lateinit var store : FirebaseFirestore;
    private var participants : ArrayList<Participant>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = FirebaseFirestore.getInstance()
        participants = CagnotteFragment.pot.participants
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.newspend_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerlistview = view.findViewById<RecyclerView>(R.id.recyclerListView)
        recyclerlistview.layoutManager = LinearLayoutManager(activity)

        val particiantAdapter = ParticipantsAdapter(participants!!)
        recyclerlistview.adapter = particiantAdapter

        view.findViewById<FloatingActionButton>(R.id.saveButton).setOnClickListener {
            saveNewSpend(particiantAdapter)
        }

        //Spinner Payeur
        SpinnerAdapter(participants!!).generateSpinner(requireContext(),view)

    }

    override fun onStart() {
        super.onStart()
        Utils.checkLoggedIn(requireActivity())

    }



    private fun updateAllSoldes(montant: Float, participantSelected: ArrayList<Participant>, payeur: Participant) {
        val moy : Float = montant / participantSelected.size.toFloat()
        val updatedSolde = participants ?: ArrayList()
        for (soldes in updatedSolde) {
            for (participant in participantSelected) {
                if (soldes.uid == participant.uid) {
                    if (soldes.uid == payeur.uid) {
                        soldes.solde += moy * (participantSelected.size - 1).toFloat()
                    } else {
                        soldes.solde -= moy
                    }
                }
            }
        }

        store
            .collection(FirebaseConstants.CollectionNames.Pot)
            .document(CagnotteFragment.potRef)
            .update(Cagnotte.Attributes.PARTICIPANTS.string, updatedSolde.map { it.toFirebase() })
            .addOnFailureListener {
                Toast.makeText(requireActivity(), getString(R.string.message_error_update_balances), Toast.LENGTH_SHORT).show()
            }
    }

    private fun repartition(float: Float, participantSelected: ArrayList<Participant>) : Float{
        return float / participantSelected.size.toFloat()
    }

    private fun saveNewSpend(adapter: ParticipantsAdapter) {
        view?.let {
            val title : String = it.findViewById<EditText>(R.id.title).text.toString()
            val montant = it.findViewById<EditText>(R.id.montant).text.toString().toFloat()
            val selectedParticipant = adapter.saveNewSpend()
            val payeur = Participant( "Julien" ,"LWBvvOIMgNeEL9b20J4ETVXoX9M2", 10f,0f, true)

            for (k in selectedParticipant){
                k.cout = repartition(montant, selectedParticipant)
            }

            val depense = Depense(title, payeur, montant, selectedParticipant)

            store
                .collection(FirebaseConstants.CollectionNames.Pot)
                .document(CagnotteFragment.potRef)
                .update(Cagnotte.Attributes.TOTAL_SPENT.string, FieldValue.arrayUnion(depense.toFirebase()))
                .addOnSuccessListener {
                    updateAllSoldes(montant, selectedParticipant, payeur)
                    dismiss()
                }
                .addOnFailureListener {
                    dismiss()
                    Toast.makeText(requireActivity(), getString(R.string.message_error_add_new_spend), Toast.LENGTH_SHORT).show()
                }

        }
    }
}