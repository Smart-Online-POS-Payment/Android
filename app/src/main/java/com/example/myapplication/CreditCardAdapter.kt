package com.example.myapplication

// CreditCardAdapter.kt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CreditCardAdapter(private val creditCardList: List<CreditCard>) :
    RecyclerView.Adapter<CreditCardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context: Context = parent.context
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val creditCardView: View = inflater.inflate(R.layout.item_credit_card, parent, false)
        return ViewHolder(creditCardView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val creditCard: CreditCard = creditCardList[position]

        // Bind data to the ViewHolder
        holder.textViewCreditCardNumber.text = "Card Number: ${creditCard.cardNumber}"
        // Bind other credit card details as needed
    }

    override fun getItemCount(): Int {
        return creditCardList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewCreditCardNumber: TextView = itemView.findViewById(R.id.textViewCreditCardNumber)
        // Initialize other TextViews as needed
    }
}
