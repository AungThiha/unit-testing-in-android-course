package com.techyourchance.unittesting.questions

data class QuestionDetails(val id: String, val title: String, val body: String)

class QuestionDetailsCache(val savedWhen: Long, val questionDetails: QuestionDetails)