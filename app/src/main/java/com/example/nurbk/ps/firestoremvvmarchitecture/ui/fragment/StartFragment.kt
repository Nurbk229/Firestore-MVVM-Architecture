package com.example.nurbk.ps.firestoremvvmarchitecture.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.nurbk.ps.firestoremvvmarchitecture.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_start.*

/**
 * A simple [Fragment] subclass.
 */
class StartFragment : Fragment(R.layout.fragment_start) {

    val TAG = "StartFragment"

    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        start_feedback.text="Checking User Account..."
    }

    override fun onStart() {
        super.onStart()

        val currentUser = mAuth.currentUser
        if (currentUser == null) {
            //create a new user
            start_feedback.text="Creating Account..."
            mAuth.signInAnonymously().addOnCompleteListener {
                if (it.isSuccessful) {
                    start_feedback.text="Account Created..."
                    navController.navigate(R.id.action_startFragment_to_listFragment)

                } else {
                    Log.e(TAG, "Start Log: ${it.exception!!.message.toString()}")
                }
            }
        } else {
            //Navigation to HomePage
            start_feedback.text="Logged in..."
            navController.navigate(R.id.action_startFragment_to_listFragment)

        }

    }

}
