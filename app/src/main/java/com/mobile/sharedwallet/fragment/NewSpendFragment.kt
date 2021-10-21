package com.mobile.sharedwallet.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.models.Category
import com.mobile.sharedwallet.models.CategoryAdapter


class NewSpendFragment: Fragment() {


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(com.mobile.sharedwallet.R.layout.newspend_fragment, container, false)

        val recyclerlistview = view.findViewById<RecyclerView>(com.mobile.sharedwallet.R.id.recyclerListView)

        //Adding LayoutManager to recycler
        recyclerlistview.layoutManager = LinearLayoutManager(activity)

        //Now Adding Adapter to our recycler with checkbox
        val testslist = getCategoryList()
        recyclerlistview.adapter = CategoryAdapter(requireContext(), R.layout.row_item, testslist)
        return view
    }


    //getting data for checkbox list you can use server API to get the list data
    private fun getCategoryList(): ArrayList<Category> {
        val cat1 = Category("Android", false)
        val cat2 = Category("Apple", false)

        val cat3 = Category( "iOS", false)

        val cat4 = Category( "Kotlin", false)

        val cat5 = Category( "Checkbox", false)

        val catList: ArrayList<Category> = ArrayList() //Init our list
        catList.add(cat1)
        catList.add(cat2)
        catList.add(cat3)
        catList.add(cat4)
        catList.add(cat5)
        return catList
    }

}