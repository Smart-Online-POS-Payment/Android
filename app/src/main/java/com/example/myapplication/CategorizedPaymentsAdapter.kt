package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemCategoryBinding
import com.example.myapplication.model.PaymentDetailsModel

class CategorizedPaymentsAdapter(private var categorizedPayments: Map<String, List<PaymentDetailsModel>>)
    : RecyclerView.Adapter<CategorizedPaymentsAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categorizedPayments.keys.elementAt(position)
        holder.bind(category, categorizedPayments[category] ?: emptyList())
    }

    override fun getItemCount(): Int = categorizedPayments.size

    fun updateData(newCategorizedPayments: Map<String, List<PaymentDetailsModel>>) {
        categorizedPayments = newCategorizedPayments
        notifyDataSetChanged()
    }

    class CategoryViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: String, payments: List<PaymentDetailsModel>) {
            binding.textViewCategoryTitle.text = category
            binding.textViewCategoryDetails.text = "Payments: ${payments.size}"
        }
    }
}
