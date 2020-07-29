package com.example.nurbk.ps.firestoremvvmarchitecture.Repository

import android.os.CountDownTimer
import android.util.Log
import com.example.nurbk.ps.firestoremvvmarchitecture.R
import com.example.nurbk.ps.firestoremvvmarchitecture.model.QuestionsModel
import com.example.nurbk.ps.firestoremvvmarchitecture.model.QuizListModel
import com.example.nurbk.ps.firestoremvvmarchitecture.model.ResultsModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_quiz.*

class FirebaseRepository(val onFirestoreTaskComplete: OnFirestoreTaskComplete) {

    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private val quizRef = firebaseFirestore.collection("QuizList")
        .whereEqualTo("visiblity", "public")


    fun getQuizData() {
        quizRef.get().addOnCompleteListener {
            if (it.isSuccessful) {
                onFirestoreTaskComplete
                    .quizListDataAdded(it.result!!.toObjects(QuizListModel::class.java))
            } else {
                onFirestoreTaskComplete.onError(it.exception)
            }
        }
    }


    fun getQuestion(quizId: String) {
        firebaseFirestore.collection("QuizList")
            .document(quizId).collection("Questions")
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onFirestoreTaskComplete.questionListDataAdded(
                        task.result!!.toObjects(QuestionsModel::class.java)
                    )

                } else {
                    onFirestoreTaskComplete.onError(task.exception)
                }
            }
    }

    fun getResult(quizId: String, uid: String) {
        firebaseFirestore.collection("QuizList")
            .document(quizId)
            .collection("Results")
            .document(uid)
            .get().addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    val documentSnapshot = it.result


                    val correct = documentSnapshot!!.getLong("correct")
                    val wrong = documentSnapshot.getLong("wrong")
                    val missed = documentSnapshot.getLong("unanswered")
                    onFirestoreTaskComplete.resultListDataAdded(
                        ResultsModel(correct!!, missed!!, wrong!!)
                    )


                } else {
                    onFirestoreTaskComplete.onError(it.exception)
                }
            }
    }


    fun getDownTimer(timeToAnswer: Long) {

        object : CountDownTimer(
            timeToAnswer * 1000,
            10
        ) {
            override fun onTick(millisUntilFinished: Long) {

                onFirestoreTaskComplete.getDownTimer(millisUntilFinished / (timeToAnswer * 10))

            }

            override fun onFinish() {
                //Time Up, Cannot Answer Question Anymore
                onFirestoreTaskComplete.finishtimer("Time Up! No answer was submitted.")
            }
        }.start()

    }


//    fun getRandomInt(min: Int, max: Int): Int {
//        return (Math.random() * (max - min)).toInt() + min
//    }
//
//     fun pickQuestions(
//        totalQuestionsToAnswer: Int,
//        allQuestionsList: ArrayList<QuestionsModel>,
//        questionsToAnswer: ArrayList<QuestionsModel>
//    ) {
//        for (i in 0 until totalQuestionsToAnswer) {
//            val randomNumber = getRandomInt(0, allQuestionsList.size - 1)
//            questionsToAnswer.add(allQuestionsList[randomNumber])
//            allQuestionsList.removeAt(randomNumber)
//            Log.d(
//                "QUESTIONS LOG",
//                "Question " + i + " : " + questionsToAnswer[i.toInt()].question
//            )
//        }
//    }
//

    interface OnFirestoreTaskComplete {
        fun quizListDataAdded(quizs: List<QuizListModel>)
        fun questionListDataAdded(question: List<QuestionsModel>)
        fun resultListDataAdded(result: ResultsModel)
        fun getDownTimer(percent: Long)
        fun finishtimer(text: String)
        fun onError(e: Exception?)
    }


}