package com.mobile.sharedwallet.dialog

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.slider.Slider
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.fragment.CagnotteFragment
import com.mobile.sharedwallet.models.Cagnotte

class ColorPicker(private val parentFragment: CagnotteSettingsDialog) : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.color_picker, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.findViewById<MaterialButton>(R.id.cancelColor).setOnClickListener {
            dismiss()
        }

        view.findViewById<MaterialButton>(R.id.submitColor).setOnClickListener {
            pickColor()
            dismiss()
        }

        val red = view.findViewById<Slider>(R.id.red)
        val green = view.findViewById<Slider>(R.id.green)
        val blue = view.findViewById<Slider>(R.id.blue)

        val color : Int = Color.parseColor(CagnotteFragment.pot.color)

        red.value = ((color and 0xFF0000) shr 16).toFloat()
        green.value = ((color and 0xFF00) shr 8).toFloat()
        blue.value = (color and 0xFF).toFloat()

        red.addOnChangeListener { _, _, _ -> updateColor() }
        green.addOnChangeListener { _, _, _ -> updateColor() }
        blue.addOnChangeListener { _, _, _ -> updateColor() }

        // view.findViewById<TextView>(R.id.colorPreview).background = ColorDrawable(color)
        view.findViewById<Chip>(R.id.colorPreview).chipBackgroundColor = ColorStateList(arrayOf(intArrayOf(android.R.attr.state_enabled)), intArrayOf(Color.parseColor(CagnotteFragment.pot.color)))
    }


    private fun pickColor() {
        view?.let { v : View ->
            val red : Int = v.findViewById<Slider>(R.id.red).value.toInt()
            val green : Int = v.findViewById<Slider>(R.id.green).value.toInt()
            val blue : Int = v.findViewById<Slider>(R.id.blue).value.toInt()

            parentFragment.actualizeColor(String.format("#%02x%02x%02x", red, green, blue))
        }
    }

    private fun updateColor() {
        view?.let { v: View ->
            val red: Int = v.findViewById<Slider>(R.id.red).value.toInt()
            val green: Int = v.findViewById<Slider>(R.id.green).value.toInt()
            val blue: Int = v.findViewById<Slider>(R.id.blue).value.toInt()

            v.findViewById<Chip>(R.id.colorPreview).chipBackgroundColor = ColorStateList(arrayOf(intArrayOf(android.R.attr.state_enabled)), intArrayOf(Color.parseColor(String.format("#%02x%02x%02x", red, green, blue))))
        //v.findViewById<TextView>(R.id.colorPreview).background = ColorDrawable(Color.parseColor(String.format("#%02x%02x%02x", red, green, blue)))
        }
    }
}