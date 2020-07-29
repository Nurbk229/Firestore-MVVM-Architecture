package com.example.nurbk.ps.firestoremvvmarchitecture.model

import com.google.firebase.firestore.DocumentId

class QuestionsModel(
    @DocumentId val questionId: String,
    val question: String,
    val answer: String,
    val option_a: String,
    val option_b: String,
    val option_c: String,
    val timer: Long
) {

    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        0
    )
}