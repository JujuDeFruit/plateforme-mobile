import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.mobile.sharedwallet.models.Participants


class ParticipantsAdapter(private val dataSet: ArrayList<Participants>) :
    RecyclerView.Adapter<ParticipantsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkbox:CheckBox
        val textView:TextView

        init {
            // Define click listener for the ViewHolder's View.
            checkbox = view.findViewById(com.mobile.sharedwallet.R.id.check_box)
            textView = view.findViewById(com.mobile.sharedwallet.R.id.textView)

        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(com.mobile.sharedwallet.R.layout.row_item, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = dataSet[position].name
        viewHolder.checkbox.isSelected = dataSet[position].selected
        viewHolder.checkbox.setOnCheckedChangeListener { _, b ->
            dataSet[position].selected = b
            notifyDataSetChanged()
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}