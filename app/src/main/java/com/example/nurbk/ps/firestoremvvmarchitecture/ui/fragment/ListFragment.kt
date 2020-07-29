package com.example.nurbk.ps.firestoremvvmarchitecture.ui.fragment

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nurbk.ps.firestoremvvmarchitecture.R
import com.example.nurbk.ps.firestoremvvmarchitecture.adapter.QuizListAdapter
import com.example.nurbk.ps.firestoremvvmarchitecture.model.QuizListModel
import com.example.nurbk.ps.firestoremvvmarchitecture.viewModel.QuizListViewModel
import kotlinx.android.synthetic.main.fragment_list.*


class ListFragment : Fragment(R.layout.fragment_list), QuizListAdapter.OnQuizListItemClicked {


    private var quizListViewModel: QuizListViewModel? = null

    private var adapter: QuizListAdapter? = null
    private lateinit var navController: NavController

    private lateinit var fadeAnim: Animation
    private lateinit var fadeOutAnim: Animation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        list_view.layoutManager = LinearLayoutManager(activity)
        adapter = QuizListAdapter(this)
        list_view.setHasFixedSize(true)
        list_view.adapter = adapter

        fadeAnim = AnimationUtils.loadAnimation(activity, R.anim.fade_in)
        fadeOutAnim = AnimationUtils.loadAnimation(activity, R.anim.fade_out)

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        quizListViewModel = ViewModelProvider(requireActivity()).get(
            QuizListViewModel::class.java
        )
        quizListViewModel!!.quizListModelData.observe(
            viewLifecycleOwner,
            Observer { quizListModels ->
                //load RecyclerView
                list_view.animation = fadeAnim
                list_progress.startAnimation(fadeOutAnim)

                adapter!!.setQuizListModels(quizListModels)
                adapter!!.notifyDataSetChanged()
            })
    }

    override fun onItemClicked(position: Int) {
        val action = ListFragmentDirections.actionListFragmentToDetailsFragment()
        action.position = position
        navController.navigate(action)
    }
}
