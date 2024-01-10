import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.model.PaymentDetailsModel
import com.example.myapplication.R
import java.text.SimpleDateFormat
import java.util.*

class PaymentsAdapter(
    private val payments: List<PaymentDetailsModel>,
    private val onRefundRequested: (PaymentDetailsModel) -> Unit
) : RecyclerView.Adapter<PaymentsAdapter.PaymentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_payment, parent, false)
        return PaymentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val currentPayment = payments[position]
        holder.descriptionTextView.text = currentPayment.description
        holder.categoryTextView.text = currentPayment.category.toString()
        holder.amountTextView.text = String.format("$%.2f", currentPayment.amount)
        holder.dateTextView.text = formatDateTime(currentPayment.date)

        // Set a long click listener for refund request
        holder.itemView.setOnLongClickListener {
            showRefundPopup(holder.itemView, currentPayment)
            true
        }
    }

    private fun showRefundPopup(view: View, payment: PaymentDetailsModel) {
        val popup = PopupMenu(view.context, view)
        popup.inflate(R.menu.refund_request_menu) // Assume you have a menu XML for this
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.request_refund -> {
                    onRefundRequested(payment)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
    private fun formatDateTime(date: Date): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH.mm", Locale.getDefault())
        return dateFormat.format(date)
    }

    override fun getItemCount(): Int = payments.size

    class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val categoryTextView: TextView = itemView.findViewById(R.id.categoryTextView)
    }
}
