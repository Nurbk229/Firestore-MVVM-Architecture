package com.example.nurbk.ps.firestoremvvmarchitecture.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nurbk.ps.firestoremvvmarchitecture.R
import com.example.nurbk.ps.firestoremvvmarchitecture.model.QuizListModel
import kotlinx.android.synthetic.main.single_list_item.view.*


class QuizListAdapter(val onQuizListItemClicked: OnQuizListItemClicked) :
    RecyclerView.Adapter<QuizListAdapter.QuizViewHolder>() {

    private var quizListModels: List<QuizListModel>? = null

    fun setQuizListModels(quizListModels: List<QuizListModel>?) {
        this.quizListModels = quizListModels
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QuizViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_list_item, parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: QuizViewHolder,
        position: Int
    ) {
        holder.itemView.apply {

            list_title.text = quizListModels!![position].name

            Glide.with(holder.itemView.context)
                .load(quizListModels!![position].image)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .into(list_image)
            var listDescription = quizListModels!![position].desc
            if (listDescription.length > 150) {
                listDescription = listDescription.substring(0, 150);
            }
            list_desc.text = listDescription
            list_difficulty.text = quizListModels!![position].visibility
            list_btn.setOnClickListener {
                onQuizListItemClicked.onItemClicked(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (quizListModels == null) {
            0
        } else {
            quizListModels!!.size
        }
    }

    inner class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    interface OnQuizListItemClicked {
        fun onItemClicked(position: Int)
    }

}
