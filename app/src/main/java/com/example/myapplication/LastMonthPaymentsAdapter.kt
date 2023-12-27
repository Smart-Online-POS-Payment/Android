package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemLastMonthPaymentBinding
import com.example.myapplication.model.PaymentDetailsModel
import java.text.SimpleDateFormat
import java.util.*

class LastMonthPaymentsAdapter(private val lastMonthPayments: List<PaymentDetailsModel>)
    : RecyclerView.Adapter<LastMonthPaymentsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLastMonthPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val payment = lastMonthPayments[position]
        holder.bind(payment)
    }

    override fun getItemCount(): Int = lastMonthPayments.size
    fun updateData(lastMonthPayments: List<PaymentDetailsModel>) {

    }


    class ViewHolder(private val binding: ItemLastMonthPaymentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(payment: PaymentDetailsModel) {
            binding.textViewLastMonthDescription.text = payment.description
            binding.textViewLastMonthAmount.text = "Amount: $${payment.amount}"
            binding.textViewLastMonthDate.text = formatDate(payment.date)
        }

        private fun formatDate(date: Date): String {
            val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
            return dateFormat.format(date)
        }
    }
}
