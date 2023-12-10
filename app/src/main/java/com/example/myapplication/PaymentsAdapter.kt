package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.model.PaymentDetailsModel
import com.example.myapplication.R

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
        holder.amountTextView.text = currentPayment.amount.toString()
        holder.dateTextView.text = currentPayment.date.toString() // Assuming PaymentDetailsModel includes a 'date' field

        // Set a long click listener for refund request
        holder.itemView.setOnLongClickListener {
            onRefundRequested(currentPayment)
            true // Indicates the click was handled
        }
    }

    override fun getItemCount(): Int = payments.size

    class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView) // Make sure this ID exists in item_payment.xml
    }
}
