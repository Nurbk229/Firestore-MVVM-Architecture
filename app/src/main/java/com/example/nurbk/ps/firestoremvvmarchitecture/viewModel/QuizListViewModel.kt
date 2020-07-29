package com.example.nurbk.ps.firestoremvvmarchitecture.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nurbk.ps.firestoremvvmarchitecture.Repository.FirebaseRepository
import com.example.nurbk.ps.firestoremvvmarchitecture.model.QuestionsModel
import com.example.nurbk.ps.firestoremvvmarchitecture.model.QuizListModel
import com.example.nurbk.ps.firestoremvvmarchitecture.model.ResultsModel

class QuizListViewModel : ViewModel(),
    FirebaseRepository.OnFirestoreTaskComplete {

    val firebaseRepository = FirebaseRepository(this)
    val quizListModelData = MutableLiveData<List<QuizListModel>>()
    val questionModelData = MutableLiveData<List<QuestionsModel>>()
    val resultModelData = MutableLiveData<ResultsModel>()

    val fiedback = MutableLiveData<String>()
    val timer = MutableLiveData<Long>()

    init {
        firebaseRepository.getQuizData()

    }

    override fun quizListDataAdded(quizs: List<QuizListModel>) {
        quizListModelData.postValue(quizs)
    }

    override fun onError(e: Exception?) {
    }

    override fun questionListDataAdded(question: List<QuestionsModel>) {
        questionModelData.postValue(question)
    }


    override fun resultListDataAdded(result: ResultsModel) {
        resultModelData.postValue(result)
    }

    override fun getDownTimer(percent: Long) {
        timer.postValue(percent)
    }

    override fun finishtimer(text: String) {
        fiedback.postValue(text)
    }
}