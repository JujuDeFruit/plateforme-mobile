package com.mobile.sharedwallet.dialog

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.sharedwallet.MainActivity
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.adapter.ParticipantsAdapter
import com.mobile.sharedwallet.constants.FirebaseConstants
import com.mobile.sharedwallet.fragment.CagnotteFragment
import com.mobile.sharedwallet.models.Cagnotte
import com.mobile.sharedwallet.models.Depense
import com.mobile.sharedwallet.models.Participant
import com.mobile.sharedwallet.utils.Utils
import com.mobile.sharedwallet.adapter.SpinnerAdapter
import com.mobile.sharedwallet.fragment.SpendFragment
import com.mobile.sharedwallet.models.Tributaire
import java.lang.Float.min
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs

class NewSpendDialog(private val parentFrag : SpendFragment) : DialogFragment() {

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

        view.findViewById<MaterialButton>(R.id.saveButton).setOnClickListener {
            saveNewSpend(particiantAdapter)
        }

        // Spinner Payeur
        var spinnerAdapter = SpinnerAdapter(participants!!)
        spinnerAdapter.generateSpinner(requireContext(),view)

    }

    override fun onStart() {
        super.onStart()
        Utils.checkLoggedIn(requireActivity())

    }

    private fun updateAllSoldes(montant: Float, tributaireSelected: ArrayList<Tributaire>) {
        val payeur : Tributaire = SpinnerAdapter.payeur ?: Tributaire()
        val moy : Float = montant / tributaireSelected.size.toFloat()
        val updatedSolde = participants ?: ArrayList()
        for (soldes in updatedSolde) {
            if (soldes.uid == payeur.uid) {
                if (tributaireSelected.map { it.uid }.contains(payeur.uid)){
                    soldes.solde += BigDecimal((moy * (tributaireSelected.size - 1).toFloat()).toDouble()).setScale(2, RoundingMode.HALF_UP).toFloat()
                }else{
                    soldes.solde += BigDecimal((moy * tributaireSelected.size.toFloat()).toDouble()).setScale(2, RoundingMode.HALF_UP).toFloat()
                }
            }
            for (tributaire in tributaireSelected) {
                if (soldes.uid == tributaire.uid && tributaire.uid!=payeur.uid) {
                    soldes.solde -= BigDecimal(moy.toDouble()).setScale(2, RoundingMode.HALF_UP).toFloat()
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

    private fun repartition(float: Float, participantSelected: ArrayList<Tributaire>) : Float{
        return float / participantSelected.size.toFloat()
    }

    private fun saveNewSpend(adapter: ParticipantsAdapter) {
        view?.let {
            val title : String = it.findViewById<EditText>(R.id.title).text.toString()
            val montant = it.findViewById<EditText>(R.id.montant).text.toString().toFloat()
            val selectedParticipant = adapter.peopleSelected()

            for (k in selectedParticipant){
                k.cout = BigDecimal(repartition(montant, selectedParticipant).toDouble()).setScale(2, RoundingMode.HALF_UP).toFloat()
            }

            val depense = Depense(title, SpinnerAdapter.payeur ?: Tributaire(), montant, selectedParticipant)

            store
                .collection(FirebaseConstants.CollectionNames.Pot)
                .document(CagnotteFragment.potRef)
                .update(Cagnotte.Attributes.TOTAL_SPENT.string, FieldValue.arrayUnion(depense.toFirebase()))
                .addOnSuccessListener {
                    updateAllSoldes(montant, selectedParticipant)
                    parentFrag.actualizeListDepenses(depense)
                    dismiss()
                }
                .addOnFailureListener {
                    dismiss()
                    Toast.makeText(requireActivity(), getString(R.string.message_error_add_new_spend), Toast.LENGTH_SHORT).show()
                }
        }
    }

}