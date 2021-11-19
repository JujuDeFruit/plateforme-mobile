package com.mobile.sharedwallet.dialog

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.sharedwallet.MainActivity
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.constants.FirebaseConstants
import com.mobile.sharedwallet.fragment.CagnotteFragment
import com.mobile.sharedwallet.fragment.HomeFragment
import com.mobile.sharedwallet.models.Cagnotte
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CagnotteSettingsDialog : DialogFragment() {

    private lateinit var store : FirebaseFirestore
    private lateinit var color : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        store = FirebaseFirestore.getInstance()
        color = CagnotteFragment.pot.color
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.cagnotte_settings_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<MaterialButton>(R.id.submitSettings).setOnClickListener {
            updateCagnotte()
            dismiss()
        }

        view.findViewById<MaterialButton>(R.id.cancelSettings).setOnClickListener { dismiss() }

        view.findViewById<TextView>(R.id.cagnotteSettingsCagnotteName).text = CagnotteFragment.pot.name

        val colorPreview = view.findViewById<Chip>(R.id.cagnotteSettingsColorPreview)

        colorPreview.chipBackgroundColor = ColorStateList(arrayOf(intArrayOf(android.R.attr.state_enabled)), intArrayOf(Color.parseColor(CagnotteFragment.pot.color)))

        colorPreview.setOnClickListener {
            ColorPicker(this).show(parentFragmentManager, "ColorPicker")
        }

        view.findViewById<CardView>(R.id.cagnotteSettingsDelete).setOnClickListener {
            MessageDialog(requireActivity()) {
                try {
                    store
                        .collection(FirebaseConstants.CollectionNames.Pot)
                        .document(CagnotteFragment.potRef)
                        .delete()
                        .await()

                    HomeFragment.cagnottes.remove(CagnotteFragment.potRef) as Any
                } catch (e : Exception) {}
            }
                .navigateTo(HomeFragment(), false)
                .create(getString(R.string.message_warning_delete_cagnotte))
                .setNeutralButton(getString(R.string.cancel), DialogInterface.OnClickListener { _, _ ->
                    dismiss()
                })
                .show()
        }
    }

    fun actualizeColor(color_ : String) {
        color = color_
        view?.findViewById<Chip>(R.id.cagnotteSettingsColorPreview)?.chipBackgroundColor = ColorStateList(arrayOf(intArrayOf(android.R.attr.state_enabled)), intArrayOf(Color.parseColor(color_)))
    }

    private fun updateCagnotte() {
        MainScope().launch {
            val uName : Boolean = updateName()
            val uColor : Boolean = updateColor()

            CagnotteFragment.cagnotteFragment?.update(
                if(uColor) CagnotteFragment.pot.color else null,
                if(uName) CagnotteFragment.pot.name else null
            )
        }
    }

    private suspend fun updateName() : Boolean {
        return view?.let { v : View ->
            val newName = v.findViewById<TextView>(R.id.cagnotteSettingsCagnotteName).text.toString()
            return@let if (CagnotteFragment.pot.name != newName && newName.isNotEmpty()) {
                try {
                    store
                        .collection(FirebaseConstants.CollectionNames.Pot)
                        .document(CagnotteFragment.potRef)
                        .update(Cagnotte.Attributes.NAME.string, newName)
                        .await()

                    CagnotteFragment.pot.name = newName
                    true
                } catch (e : Exception) { false }
            } else false
        } ?: false
    }

    private suspend fun updateColor() : Boolean {
        return if (CagnotteFragment.pot.color != color && color.isNotEmpty()) {
            try {
                store
                    .collection(FirebaseConstants.CollectionNames.Pot)
                    .document(CagnotteFragment.potRef)
                    .update(Cagnotte.Attributes.COLOR.string, color)
                    .await()

                CagnotteFragment.pot.color = color
                true
            } catch (e : Exception) { false }
        } else false
    }
}