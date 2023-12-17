package com.example.myapplication

// CreditCardAdapter.kt

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class CreditCardAdapter(private val creditCardList: List<CreditCard>, val c: Context) :
    RecyclerView.Adapter<CreditCardAdapter.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context: Context = parent.context
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val creditCardView: View = inflater.inflate(R.layout.item_credit_card, parent, false)
        return ViewHolder(creditCardView)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val creditCard: CreditCard = creditCardList[position]


        // Set the last 4 digits dynamically
        val last4Digits = creditCard.cardNumber.takeLast(4)
        val formattedCreditCardNumber = "Card Number: XXXX-XXXX-XXXX-$last4Digits"

        holder.textViewCreditCardNumber.text = formattedCreditCardNumber
        holder.radioButtonChooseCard.isChecked = position == selectedPosition

        holder.itemView.setOnClickListener {
            handleItemClick(position)
        }

        holder.radioButtonChooseCard.setOnClickListener {
            handleItemClick(position)
        }

        // Bind other data to the ViewHolder as needed
    }
    private fun handleItemClick(position: Int) {
        // Update your data model or perform any actions based on the selection
        val selectedCard = creditCardList[position]
        // Handle the selected card...

        // Update the selected position and notify data set changed
        if (selectedPosition != position) {
            selectedPosition = position
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return creditCardList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var radioButtonChooseCard: RadioButton = itemView.findViewById(R.id.radioButtonChooseCard)
        var textViewCreditCardNumber: TextView = itemView.findViewById(R.id.textViewCreditCardNumber)
        // Other views as needed
    }
    private fun showMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    fun getSelectedCreditCard(): CreditCard? {
        return if (selectedPosition != RecyclerView.NO_POSITION) {
            creditCardList[selectedPosition]
        } else {
            null
        }
    }
}


