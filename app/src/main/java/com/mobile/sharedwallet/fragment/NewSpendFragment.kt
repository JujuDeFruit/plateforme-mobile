package com.mobile.sharedwallet.fragment


import ParticipantsAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.sharedwallet.models.Participants
import java.util.*


class NewSpendFragment: Fragment() {


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(com.mobile.sharedwallet.R.layout.newspend_fragment, container, false)

        val recyclerlistview = view.findViewById<RecyclerView>(com.mobile.sharedwallet.R.id.recyclerListView)

        //Adding LayoutManager to recycler
        recyclerlistview.layoutManager = LinearLayoutManager(activity)

        //Now Adding Adapter to our recycler with checkbox
        recyclerlistview.adapter = ParticipantsAdapter(getCategoryList())

        return view
    }


    //getting data for checkbox list you can use server API to get the list data
    private fun getCategoryList(): ArrayList<Participants> {
        val cat1 = Participants("Android", false)
        val cat2 = Participants("Apple", false)

        val cat3 = Participants( "iOS", false)

        val cat4 = Participants( "Kotlin", false)

        val cat5 = Participants( "Checkbox", false)

        val catList: ArrayList<Participants> = ArrayList() //Init our list
        catList.add(cat1)
        catList.add(cat2)
        catList.add(cat3)
        catList.add(cat4)
        catList.add(cat5)
        return catList
    }

}