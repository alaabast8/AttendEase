package com.example.attendease

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController


class WelcomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_welcome, container, false)


        val admin=view.findViewById<CardView>(R.id.adminlog)
        val dr=view.findViewById<CardView>(R.id.drlog)
        val std=view.findViewById<CardView>(R.id.stdlog)

        admin.setOnClickListener {
            view.findNavController().navigate(R.id.action_welcomeFragment_to_adminLoginFragment)
        }
        dr.setOnClickListener {
            view.findNavController().navigate(R.id.action_welcomeFragment_to_drLoginFragment)
        }
        std.setOnClickListener {
            view.findNavController().navigate(R.id.action_welcomeFragment_to_stdLoginFragment)
        }

        return view
    }


}