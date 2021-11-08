package com.mobile.sharedwallet.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.dialog.AddUserToPotDialog
import com.mobile.sharedwallet.dialog.NewSpendDialog
import com.mobile.sharedwallet.models.Participant
import com.mobile.sharedwallet.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.min

class BalanceFragment: Fragment() {

    private var participants : ArrayList<Participant>? = null
    private var partifForGraph : MutableList<Participant>? = (CagnotteFragment.pot.participants)?.toMutableList()

    class ChartXAxisFormatter() : ValueFormatter(), Parcelable {


        constructor(a:ArrayList<String>) :this(){
            names = a.toTypedArray()
        }
        private var names = arrayOf("Julien", "Robin", "Remi")

        constructor(parcel: Parcel) : this() {
            names = parcel.createStringArray() as Array<String>
        }

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return names.getOrNull(value.toInt()) ?: value.toString()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeStringArray(names)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<ChartXAxisFormatter> {
            override fun createFromParcel(parcel: Parcel): ChartXAxisFormatter {
                return ChartXAxisFormatter(parcel)
            }

            override fun newArray(size: Int): Array<ChartXAxisFormatter?> {
                return arrayOfNulls(size)
            }
        }
    }

    class ChartYValueFormatter : ValueFormatter() {

        private val format = DecimalFormat("###.#")

        // override this for BarChart
        override fun getBarLabel(barEntry: BarEntry?): String {
            return format.format(barEntry?.y)
        }

        // override this for custom formatting of XAxis or YAxis labels
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return format.format(value)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.balance_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        Utils.checkLoggedIn(requireActivity())
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        participants = CagnotteFragment.pot.participants
        quiDoitQuoi()
        DisplayGraph()
    }

    private fun quiDoitQuoi(){
        //on trie la liste des participants par ordre croissant de cout
        val sortedpaticipants = participants!!.sortedByDescending{it.solde}

        // on retire a toute la liste des participants le montant moyen
        var i = 0;
        var j = sortedpaticipants.size - 1;
        var debt : Float

        while (i < j) {
            debt = min(
                abs(sortedpaticipants[i].solde),
                abs(sortedpaticipants[j].solde)
            )

            sortedpaticipants[i].solde = sortedpaticipants[i].solde - debt;
            sortedpaticipants[j].solde = sortedpaticipants[j].solde + debt;

            createTextView(sortedpaticipants[i].name.toString(), sortedpaticipants[j].name.toString(), debt.toString())
            if (sortedpaticipants[i].solde == 0f) {
                i++;
            }
            if (sortedpaticipants[j].solde == 0f) {
                j--;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun createTextView(p1: String, p2: String, debt: String) {
        val liste = view?.findViewById<LinearLayout>(R.id.remboursement)
        val newTextView = TextView(requireContext())
        newTextView.setPadding(90, 50, 80, 50)
        newTextView.setTextColor(Color.BLACK)
        newTextView.textSize = 15f
        newTextView.text = "$p2 owes $debt to $p1"
        liste?.addView(newTextView)
    }

    private fun GatherInfo() : ArrayList<BarEntry>{
        val entries: ArrayList<BarEntry> = ArrayList()
        var i = 0f
        partifForGraph?.forEach{
            entries.add(BarEntry(i, it.solde))
            //axeAbs.names?.add(it.name)
            println(it.solde)
            println(it.name)
            i += 1.0f
        }
        entries.clear()
        entries.add(BarEntry(0f, 85f))
        entries.add(BarEntry(1f, 10.5f))
        entries.add(BarEntry(2f, -24.6f))

        return entries
    }

    private fun DisplayGraph(){
        val entries = GatherInfo()
        var names: ArrayList<String> = ArrayList()
        partifForGraph?.forEach {
            names.add(it.name)
        }

        var axeAbs = ChartXAxisFormatter(names)
        var axeOrd = ChartYValueFormatter()

        val barDataSet = BarDataSet(entries, "Graph of the costs")
        barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

        var barChart = view?.findViewById<BarChart>(R.id.barChart)
        val data = BarData(barDataSet)
        if (barChart != null) {
            barChart.data = data
        }
        else {
            println("barchart null")
        }

        if (barChart != null) {
            barChart.axisLeft.setDrawGridLines(false)
            barChart.xAxis.setDrawGridLines(false)
            barChart.xAxis.setDrawAxisLine(false)
            barChart.xAxis.granularity = 1.0f
            barChart.xAxis.valueFormatter = axeAbs
            barChart.axisLeft.valueFormatter = axeOrd
            //remove right y-axis
            barChart.axisRight.isEnabled = false
            //remove legend
            barChart.legend.isEnabled = false
            //remove description label
            barChart.description.isEnabled = false
            //add animation
            barChart.animateY(1000)
            barChart.invalidate()
        }
    }
}