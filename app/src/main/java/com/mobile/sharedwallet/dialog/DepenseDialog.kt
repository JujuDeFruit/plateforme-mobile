package com.mobile.sharedwallet.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.models.Depense
import com.mobile.sharedwallet.utils.Utils

class DepenseDialog(private val depense : Depense) : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.depense_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.depenseDialogTitle).text = depense.title
        view.findViewById<TextView>(R.id.depenseDialogCreationDate).text = Utils.dateFormatter(depense.creationDate)
        view.findViewById<TextView>(R.id.depenseDialogCategory).text = "Category"
        view.findViewById<TextView>(R.id.depenseDialogPayedBy).text = depense.whoPaid.name
        view.findViewById<TextView>(R.id.depenseDialogAmountPaid).text = depense.amountPaid.toString().plus(getString(R.string.euro_symbol))
        view.findViewById<ListView>(R.id.depenseDialogForWho).adapter = ArrayAdapter(requireContext(), R.layout.for_who_textview, depense.forWho)
    }

    override fun onStart() {
        super.onStart()
        Utils.checkLoggedIn(requireActivity())
    }
}