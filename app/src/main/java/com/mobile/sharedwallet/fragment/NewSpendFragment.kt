package com.mobile.sharedwallet.fragment

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.adapter.ParticipantsAdapter
import com.mobile.sharedwallet.constants.FirebaseConstants
import com.mobile.sharedwallet.models.*
import com.mobile.sharedwallet.utils.Utils


class NewSpendFragment : Fragment() {

    private lateinit var store : FirebaseFirestore;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.newspend_fragment, container, false)
        val recyclerlistview = view.findViewById<RecyclerView>(R.id.recyclerListView)
        recyclerlistview.layoutManager = LinearLayoutManager(activity)

        var arrayListAllParticipants=getPartcipantList()
        val particiantAdapter= ParticipantsAdapter(arrayListAllParticipants)
        recyclerlistview.adapter = particiantAdapter

        //Chargement des éléments dans le spinner
        //spiner(view,arrayListAllParticipants)
        val spinner = view.findViewById<Spinner>(R.id.spinner_payeur)
        //spinner.onItemSelectedListener = this

        view.findViewById<Button>(R.id.savebutton).setOnClickListener{
            val title : String =view.findViewById<EditText>(R.id.title).text.toString()
            val montant = view.findViewById<EditText>(R.id.montant).text.toString().toFloat()
            val participant_selected = particiantAdapter.saveNewSpend()
            val payeur=User("LWBvvOIMgNeEL9b20J4ETVXoX9M2", "Julien" ,"Raynal","julien.raynal@yahoo.fr",null,0.0f )

            for (k in participant_selected){
                k.cout = repartition( montant ,participant_selected )
            }

            val depense = Depense(title,payeur,montant,participant_selected)

            Firebase
                .firestore
                .collection(FirebaseConstants.CollectionNames.Pot)
                .document(CagnotteFragment.potRef)
                .update(Cagnotte.Attributes.TOTAL_SPENT.string, FieldValue.arrayUnion(depense.toFirebase()))
                .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e ->
                    Toast.makeText(activity, "An error occured while creating new pot. Please retry !", Toast.LENGTH_SHORT).show()
                    Log.w(ContentValues.TAG, "Error writing document", e)
                }
            updateAllSoldes(montant,participant_selected,payeur)
            findNavController().navigate(R.id.cagnotteFragment)

        }

        view.findViewById<Button>(R.id.backbutton).setOnClickListener{
            findNavController().navigate(R.id.cagnotteFragment)
        }

        return view

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
        val moy : Float = montant/(participantSelected.size.toFloat())
        var updatedSolde= CagnotteFragment.pot.participants
        for (soldes in updatedSolde){
            for ( participant in participantSelected){
                if ( soldes.uid == participant.id){
                    if (soldes.uid == payeur.uid){
                        soldes.solde += moy*( participantSelected.size -1.0f)
                    }else{
                        soldes.solde -= moy
                    }
                }
            }
        }
        store
            .collection(FirebaseConstants.CollectionNames.Pot)
            .document(CagnotteFragment.potRef)
            .update(Cagnotte.Attributes.PARTICIPANTS.string, updatedSolde.map { it.toFirebase() })
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e ->
                Toast.makeText(activity, "An error occured while creating new pot. Please retry !", Toast.LENGTH_SHORT).show()
                Log.w(ContentValues.TAG, "Error writing document", e)
            }
    }

    //getting data for checkbox list you can use server API to get the list data
    private fun getPartcipantList(): ArrayList<Participant> {
        return ArrayList(CagnotteFragment.pot.participants.map { Utils.castUserToParticipant(it) })
    }

    private fun repartition(float: Float, participant_selected: ArrayList<Participant>):Float{
        return float / (participant_selected.size).toFloat()
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
}