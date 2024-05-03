package com.example.entrega1

class Question( private val question: String,
                private val options: ArrayList<String>,
                private val correctAnswerIndex: Int) {

    fun getQuestion(): String{
        return this.question
    }

    fun getOptions(): ArrayList<String>{
        return this.options
    }

    fun getCorrectAnswerIndex(): Int{
        return this.correctAnswerIndex
    }


}
