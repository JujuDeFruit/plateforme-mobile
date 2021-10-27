package com.mobile.sharedwallet.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.sharedwallet.MainActivity
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.adapter.ParticipantsAdapter
import com.mobile.sharedwallet.constants.FirebaseConstants
import com.mobile.sharedwallet.models.Cagnotte
import com.mobile.sharedwallet.models.Depense
import com.mobile.sharedwallet.models.Participant
import com.mobile.sharedwallet.models.User
import com.mobile.sharedwallet.utils.Utils


class NewSpendFragment : Fragment() {

    private lateinit var store : FirebaseFirestore;
    private var participants : ArrayList<Participant>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = FirebaseFirestore.getInstance()
        participants = getParticipantList()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.newspend_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerlistview = view.findViewById<RecyclerView>(R.id.recyclerListView)
        recyclerlistview.layoutManager = LinearLayoutManager(activity)

        val particiantAdapter = ParticipantsAdapter(participants!!)
        recyclerlistview.adapter = particiantAdapter

        //Chargement des éléments dans le spinner
        //spiner(view,arrayListAllParticipants)
        val spinner = view.findViewById<Spinner>(R.id.spinner_payeur)
        //spinner.onItemSelectedListener = this

        view.findViewById<Button>(R.id.saveButton).setOnClickListener {
            saveNewSpend(particiantAdapter)
        }

        /*view.findViewById<Button>(R.id.backbutton).setOnClickListener{
            findNavController().navigate(R.id.cagnotteFragment)
        }*/
    }

    /*private fun spiner(view: View, arrayListAllParticipants: ArrayList<Participant>){
        val spinner = view.findViewById<Spinner>(R.id.spinner_payeur)
        val spinnerArrayAdapter: ArrayAdapter<Participant> = ArrayAdapter<Participant>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayListAllParticipants
        )
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerArrayAdapter
    }*/

    private fun updateAllSoldes(montant: Float, participantSelected: ArrayList<Participant>, payeur: User) {
        val moy : Float = montant / participantSelected.size.toFloat()
        val updatedSolde = CagnotteFragment.pot.participants
        for (soldes in updatedSolde) {
            for (participant in participantSelected) {
                if (soldes.uid == participant.id) {
                    if (soldes.uid == payeur.uid) {
                        soldes.solde += moy * (participantSelected.size - 1.0f)
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

    //getting data for checkbox list you can use server API to get the list data
    private fun getParticipantList() : ArrayList<Participant> {
        return ArrayList(CagnotteFragment.pot.participants.map { Utils.castUserToParticipant(it) })
    }

    private fun repartition(float: Float, participantSelected: ArrayList<Participant>) : Float{
        return float / participantSelected.size.toFloat()
    }

    /*private fun equilibrage(){
        //on trie la liste des participants par ordre croissant dé dépense
        pot.participants.sortedByDescending{it.solde}

        //Dépense moyenne à atteindre par personne
        val moy = montant/participant.size

        // on retire a toute la liste des participants le montant moyen

        var i = 0;
        var j = sortedpaticipants.size - 1;
        var debt:Float;

        while (i < j) {
            debt = min(abs((sortedpaticipants[i]), sortedpaticipants[j])
            sortedpaticipants[i]= sortedpaticipants[i] + debt;
            sortedpaticipants[j]=sortedpaticipants[j]- debt;

            println("$(sortedPeople[i]) owes $(sortedPeople[j]) $debt");

            if (sortedValuesPaid[i] === 0) {
                i++;
            }

            if (sortedValuesPaid[j] === 0) {
                j--;
            }
        }
    }*/

    private fun saveNewSpend(adapter: ParticipantsAdapter) {
        view?.let {
            val title : String = it.findViewById<EditText>(R.id.title).text.toString()
            val montant = it.findViewById<EditText>(R.id.montant).text.toString().toFloat()
            val selectedParticipant = adapter.saveNewSpend()
            val payeur = User("LWBvvOIMgNeEL9b20J4ETVXoX9M2", "Julien" ,"Raynal","julien.raynal@yahoo.fr",null,0.0f )

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
                    (requireActivity() as MainActivity).replaceFragment(CagnotteFragment())
                }
                .addOnFailureListener {
                    Toast.makeText(requireActivity(), getString(R.string.message_error_add_new_spend), Toast.LENGTH_SHORT).show()
                }

        }
    }
}