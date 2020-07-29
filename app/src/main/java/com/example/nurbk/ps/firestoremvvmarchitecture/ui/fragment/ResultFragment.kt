package com.example.nurbk.ps.firestoremvvmarchitecture.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.nurbk.ps.firestoremvvmarchitecture.R
import com.example.nurbk.ps.firestoremvvmarchitecture.viewModel.QuizListViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.fragment_result.*


class ResultFragment : Fragment(R.layout.fragment_result) {

    private var navController: NavController? = null
    private var quizListViewModel: QuizListViewModel? = null

    private var firebaseFirestore: FirebaseFirestore? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var currentUserId: String? = null

    private var quizId: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        firebaseAuth = FirebaseAuth.getInstance()

        //Get User ID
        if (firebaseAuth!!.currentUser != null) {
            currentUserId = firebaseAuth!!.currentUser!!.uid
        } else {
            //Go Back to Home Page
        }

        firebaseFirestore = FirebaseFirestore.getInstance()
        quizId = ResultFragmentArgs.fromBundle(requireArguments()).quizId

        results_home_btn.setOnClickListener {
            navController!!.navigate(R.id.action_resultFragment_to_listFragment)
        }

//        firebaseFirestore!!.collection("QuizList")
//            .document(quizId!!)
//            .collection("Results")
//            .document(currentUserId!!)
//            .get().addOnCompleteListener {
//                if (it.isSuccessful) {
//                    val documentSnapshot = it.result
//
//                    val correct = documentSnapshot!!.getLong("correct")
//                    val wrong = documentSnapshot.getLong("wrong")
//                    val missed = documentSnapshot.getLong("unanswered")
//
//                    results_correct_text.text = correct.toString()
//                    results_wrong_text.text = wrong.toString()
//                    results_missed_text.text = missed.toString()
//
//                    val total = correct!! + wrong!! + missed!!
//                    val percent = (correct * 100) / total
//
//                    results_percent.text = "${percent}%"
//                    results_progress.progress = percent.toInt()
//
//                }
//            }

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        quizListViewModel = ViewModelProvider(requireActivity()).get(
            QuizListViewModel::class.java
        )
        quizListViewModel!!.firebaseRepository.getResult(quizId!!, currentUserId!!)

        quizListViewModel!!.resultModelData.observe(
            viewLifecycleOwner,
            Observer { resultModelData ->

                val correct = resultModelData.correct
                val wrong = resultModelData.wrong
                val unanswered = resultModelData.unanswered

                results_correct_text.text = correct.toString()
                results_wrong_text.text = wrong.toString()
                results_missed_text.text = unanswered.toString()

                val total = correct + wrong + unanswered
                val percent = (correct * 100) / total

                results_percent.text = "${percent}%"
                results_progress.progress = percent.toInt()
            })
    }
}
