package com.example.myapplication.model

import java.math.BigDecimal
import java.util.Date

data class PaymentDetailsModel(
    val amount: BigDecimal,
    val description: String,
    val date: Date
)
