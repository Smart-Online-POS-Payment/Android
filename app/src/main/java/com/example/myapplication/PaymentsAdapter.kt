import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.PaymentHistoryActivity
import com.example.myapplication.R

class PaymentsAdapter(private val payments: List<PaymentHistoryActivity.Payment>) : RecyclerView.Adapter<PaymentsAdapter.PaymentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        // Inflate the custom layout
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_payment, parent, false)
        return PaymentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        // Bind data to the ViewHolder
        val currentPayment = payments[position]
        holder.explanationTextView.text = currentPayment.explanation
        holder.amountTextView.text = currentPayment.amount
    }

    override fun getItemCount() = payments.size

    class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val explanationTextView: TextView = itemView.findViewById(R.id.explanationTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
    }
}
