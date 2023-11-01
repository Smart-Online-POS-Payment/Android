package com.example.myapplication

class Account(inputString: String) {
    private val innerNumber: Int = 100 // mock // account_total // An inner variable
    init {
        // This block is executed when an instance of the class is created.
        println("Kotlin class created with input string: $inputString")
        processString(inputString)
    }

    private fun processString(inputString: String) {
        // Add your custom logic to process the input string here.
        // For example, you can convert it to uppercase and print it.
    }
}