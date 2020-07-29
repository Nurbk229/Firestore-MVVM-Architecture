package com.example.nurbk.ps.firestoremvvmarchitecture.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.nurbk.ps.firestoremvvmarchitecture.R
import com.example.nurbk.ps.firestoremvvmarchitecture.model.QuizListModel
import com.example.nurbk.ps.firestoremvvmarchitecture.viewModel.QuizListViewModel
import kotlinx.android.synthetic.main.fragment_details.*

/**
 * A simple [Fragment] subclass.
 */
class DetailsFragment : Fragment(R.layout.fragment_details) {

    val TAG = "DetailsFragment"
    private var position: Int = 0

    private var quizId: String? = null
    private var totalQuestions: Long = 0
    private var quizName: String? = null
    private var quiz: QuizListModel? = null
    private var quizListViewModel: QuizListViewModel? = null

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        position = DetailsFragmentArgs.fromBundle(requireArguments()).position
        Log.e(TAG, "Position: $position")

        details_start_btn.setOnClickListener {
            val action =
                DetailsFragmentDirections.actionDetailsFragmentToQuizFragment()
            action.quizId = quizId!!
            action.quizName = quizName!!
            action.totalQuestions = totalQuestions
            navController.navigate(action)
        }

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        quizListViewModel = ViewModelProvider(requireActivity()).get(
            QuizListViewModel::class.java
        )
        quizListViewModel!!.quizListModelData.observe(
            viewLifecycleOwner,
            Observer { quizListModels ->
                quiz = quizListModels[position]
                details_title.text = quiz!!.name
                details_desc.text = quiz!!.desc
                details_difficulty_text.text = quiz!!.level
                details_questions_text.text = quiz!!.questions.toString()
                details_score_text.text = 45.toString()

                Glide.with(requireActivity()).load(quiz!!.image)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_image)
                    .into(details_image)

                quizId = quiz!!.quiz_id
                totalQuestions = quiz!!.questions
                quizName = quiz!!.name
            })
    }


}
