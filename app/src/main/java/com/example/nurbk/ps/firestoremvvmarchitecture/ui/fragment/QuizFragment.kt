package com.example.nurbk.ps.firestoremvvmarchitecture.ui.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.nurbk.ps.firestoremvvmarchitecture.R
import com.example.nurbk.ps.firestoremvvmarchitecture.Repository.FirebaseRepository
import com.example.nurbk.ps.firestoremvvmarchitecture.model.QuestionsModel
import com.example.nurbk.ps.firestoremvvmarchitecture.viewModel.QuizListViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.fragment_quiz.*


/**
 * A simple [Fragment] subclass.
 */
class QuizFragment : Fragment(R.layout.fragment_quiz), View.OnClickListener {

    private val TAG = "QUIZ_FRAGMENT_LOG"

    private var navController: NavController? = null
    private var quizListViewModel: QuizListViewModel? = null

    private var firebaseFirestore: FirebaseFirestore? = null
    private var firebaseAuth: FirebaseAuth? = null

    private var currentUserId: String? = null

    private var quizName: String? = null
    private var quizId: String? = null


    private var allQuestionsList = ArrayList<QuestionsModel>()
    private var questionsToAnswer = ArrayList<QuestionsModel>()

    private var totalQuestionsToAnswer = 0
    private var countDownTimer: CountDownTimer? = null

    private var canAnswer = false
    private var currentQuestion = 0

    private var correctAnswers = 0
    private var wrongAnswers = 0
    private var notAnswered = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance()
        //Get User ID
        if (firebaseAuth!!.currentUser != null) {
            currentUserId = firebaseAuth!!.currentUser!!.getUid();
        } else {
            //Go Back to Home Page
        }

        quizId = QuizFragmentArgs.fromBundle(requireArguments()).quizId
        quizName = QuizFragmentArgs.fromBundle(requireArguments()).quizName
        totalQuestionsToAnswer =
            QuizFragmentArgs.fromBundle(requireArguments()).totalQuestions.toInt()


        //Query Firestore Data

        //Query Firestore Data
//        firebaseFirestore!!.collection("QuizList")
//            .document(quizId!!).collection("Questions")
//            .get().addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    allQuestionsList =
//                        task.result!!.toObjects(QuestionsModel::class.java) as ArrayList<QuestionsModel>
//                    pickQuestions()
//                    loadUI()
//                } else {
//                    quiz_title.text = "Error :  ${task.exception!!.message}"
//                }
//            }
        quiz_option_one.setOnClickListener(this)
        quiz_option_two.setOnClickListener(this)
        quiz_option_three.setOnClickListener(this)
        quiz_next_btn.setOnClickListener(this)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        quizListViewModel = ViewModelProvider(requireActivity()).get(
            QuizListViewModel::class.java
        )
        quizListViewModel!!.firebaseRepository.getQuestion(quizId!!)
        quizListViewModel!!.questionModelData.observe(
            viewLifecycleOwner,
            Observer { questionModelData ->
                //load RecyclerView
                allQuestionsList = questionModelData as ArrayList<QuestionsModel>
                pickQuestions()
                loadUI()
            })
    }

    private fun loadUI() {
        //Quiz Data Loaded, Load the UI
        quiz_title.text = quizName
        quiz_question.text = "Load First Question"

        //Enable Options
        enableOptions()

        //Load First Question
        loadQuestion(1)
    }

    private fun loadQuestion(questNum: Int) {
        //Set Question Number
        quiz_question_number.text = questNum.toString()

        //Load Question Text
        quiz_question.text = questionsToAnswer[questNum - 1].question

        //Load Options
        quiz_option_one.text = questionsToAnswer[questNum - 1].option_a
        quiz_option_two.text = questionsToAnswer[questNum - 1].option_b
        quiz_option_three.text = questionsToAnswer[questNum - 1].option_c

        //Question Loaded, Set Can Answer
        canAnswer = true
        currentQuestion = questNum

        //Start Question Timer
        startTimer(questNum)
    }

    private fun startTimer(questionNumber: Int) {

        //Set Timer Text
        val timeToAnswer = questionsToAnswer[questionNumber - 1].timer
        quiz_question_time.text = timeToAnswer.toString()

        //Show Timer ProgressBar
        quiz_question_progress.visibility = View.VISIBLE

        //Start CountDown

        quizListViewModel!!.firebaseRepository.getDownTimer(timeToAnswer)
        quizListViewModel!!.timer.observe(viewLifecycleOwner, {
            quiz_question_time.text = it.toString()
            quiz_question_progress.progress = it.toInt()
        })
        quizListViewModel!!.fiedback.observe(viewLifecycleOwner,{
            canAnswer = false
            quiz_question_feedback.text=it
            quiz_question_feedback.setTextColor(resources.getColor(R.color.colorPrimary, null))
            notAnswered++
            showNextBtn()
        })
//
//        countDownTimer = object : CountDownTimer(
//            timeToAnswer * 1000,
//            10
//        ) {
//            override fun onTick(millisUntilFinished: Long) {
//                //Update Time
//                quiz_question_time.text = (millisUntilFinished / 1000).toString()
//
//                //Progress in percent
//                val percent = millisUntilFinished / (timeToAnswer * 10)
//                quiz_question_progress.progress = percent.toInt()
//            }
//
//            override fun onFinish() {
//                //Time Up, Cannot Answer Question Anymore
//                canAnswer = false
//                quiz_question_feedback.text = "Time Up! No answer was submitted."
//                quiz_question_feedback.setTextColor(resources.getColor(R.color.colorPrimary, null))
//                notAnswered++
//                showNextBtn()
//            }
//        }
//        countDownTimer!!.start()
    }


    private fun enableOptions() {
        //Show All Option Buttons
        quiz_option_one.visibility = View.VISIBLE
        quiz_option_two.visibility = View.VISIBLE
        quiz_option_three.visibility = View.VISIBLE

        //Enable Option Buttons
        quiz_option_one.isEnabled = true
        quiz_option_two.isEnabled = true
        quiz_option_three.isEnabled = true

        //Hide Feedback and next Button
        quiz_question_feedback.visibility = View.INVISIBLE
        quiz_next_btn.visibility = View.INVISIBLE
        quiz_next_btn.isEnabled = false
    }

    private fun pickQuestions() {
        for (i in 0 until totalQuestionsToAnswer) {
            val randomNumber = getRandomInt(0, allQuestionsList.size - 1)
            questionsToAnswer.add(allQuestionsList[randomNumber])
            allQuestionsList.removeAt(randomNumber)
            Log.d(
                "QUESTIONS LOG",
                "Question " + i + " : " + questionsToAnswer[i.toInt()].question
            )
        }
    }

    private fun getRandomInt(min: Int, max: Int): Int {
        return (Math.random() * (max - min)).toInt() + min
    }


    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.quiz_option_one -> {
                verifyAnswer(quiz_option_one)
            }
            R.id.quiz_option_two -> {
                verifyAnswer(quiz_option_two)

            }
            R.id.quiz_option_three -> {
                verifyAnswer(quiz_option_three)

            }
            R.id.quiz_next_btn -> {
                if (currentQuestion == totalQuestionsToAnswer) {
                    quiz_next_btn.text = "Submit Results"
                    submitResults()
                } else {


                    currentQuestion++
                    loadQuestion(currentQuestion.toInt())
                    resetOptions()
                }
            }
        }

    }


    private fun submitResults() {
        val resultMap: HashMap<String, Any> = HashMap()
        resultMap["correct"] = correctAnswers
        resultMap["wrong"] = wrongAnswers
        resultMap["unanswered"] = notAnswered
        firebaseFirestore!!.collection("QuizList")
            .document(quizId!!).collection("Results")
            .document(currentUserId!!).set(resultMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Go To Results Page
                    val action =
                        QuizFragmentDirections.actionQuizFragmentToResultFragment()
                    action.quizId = quizId!!
                    navController!!.navigate(action)
                } else {
                    //Show Error
                    quiz_title.text = task.exception!!.message
                }
            }
    }

    private fun resetOptions() {
        quiz_option_one.background = resources.getDrawable(
            R.drawable.outline_light_btn_bg,
            null
        )
        quiz_option_two.background = resources.getDrawable(
            R.drawable.outline_light_btn_bg,
            null
        )
        quiz_option_three.background = resources.getDrawable(
            R.drawable.outline_light_btn_bg,
            null
        )
        quiz_option_one.setTextColor(resources.getColor(R.color.colorLightText, null))
        quiz_option_two.setTextColor(resources.getColor(R.color.colorLightText, null))
        quiz_option_three.setTextColor(resources.getColor(R.color.colorLightText, null))
        quiz_question_feedback.visibility = View.INVISIBLE
        quiz_next_btn.visibility = View.INVISIBLE
        quiz_next_btn.isEnabled = false
    }


    private fun verifyAnswer(selectedAnswerBtn: Button) {
        //Check Answer
        if (canAnswer) {
            //Set Answer Btn Text Color to Black
            selectedAnswerBtn.setTextColor(resources.getColor(R.color.colorDark, null))
            if (questionsToAnswer[currentQuestion - 1].answer == selectedAnswerBtn.text) {
                //Correct Answer
                correctAnswers++
                selectedAnswerBtn.background = resources.getDrawable(
                    R.drawable.correct_answer_btn_bg,
                    null
                )

                //Set Feedback Text
                quiz_question_feedback.text = "Correct Answer"
                quiz_question_feedback.setTextColor(resources.getColor(R.color.colorPrimary, null))
            } else {
                //Wrong Answer
                wrongAnswers++
                selectedAnswerBtn.background = resources.getDrawable(
                    R.drawable.wrong_answer_btn_bg,
                    null
                )

                //Set Feedback Text
                quiz_question_feedback.text =
                    "Wrong Answer \n Correct Answer : ${questionsToAnswer[currentQuestion - 1].answer}"
                quiz_question_feedback.setTextColor(resources.getColor(R.color.colorAccent, null))
            }
            //Set Can answer to false
            canAnswer = false

            //Stop The Timer
            countDownTimer!!.cancel()

            //Show Next Button
            showNextBtn()
        }
    }


    private fun showNextBtn() {
        if (currentQuestion == totalQuestionsToAnswer) {
            quiz_next_btn.text = "Submit Results"
        }
        quiz_question_feedback.visibility = View.VISIBLE
        quiz_next_btn.visibility = View.VISIBLE
        quiz_next_btn.isEnabled = true

    }


}
