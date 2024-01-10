package com.example.myapplication.model

import java.math.BigDecimal
import java.util.Date

data class PaymentDetailsModel(
    val orderId: String,  // Add this field
    val amount: BigDecimal,
    val description: String,
    val category: String,
    val date: Date
)