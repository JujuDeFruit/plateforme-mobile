package com.mobile.sharedwallet.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.constants.FirebaseConstants
import com.mobile.sharedwallet.fragment.CagnotteFragment
import com.mobile.sharedwallet.models.Cagnotte
import org.w3c.dom.Text

class CagnotteSettingsDialog : DialogFragment() {

    private lateinit var store : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        store = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.cagnotte_settings_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val colorPreview = view.findViewById<TextView>(R.id.cagnotteSettingsColorPreview)

        colorPreview.background = ColorDrawable(
            Color.parseColor(CagnotteFragment.pot.color)
        )

        colorPreview.setOnClickListener {
            ColorPicker(this).show(parentFragmentManager, "ColorPicker")
        }
    }

    fun actualizeColor(color : String) {
        store
            .collection(FirebaseConstants.CollectionNames.Pot)
            .document(CagnotteFragment.potRef)
            .update(Cagnotte.Attributes.COLOR.string, color)
            .addOnSuccessListener {
                view?.findViewById<TextView>(R.id.cagnotteSettingsColorPreview)?.background = ColorDrawable(Color.parseColor(color))
            }
    }
}