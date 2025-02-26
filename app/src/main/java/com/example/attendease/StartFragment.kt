package com.example.attendease

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController

class StartFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_start, container, false)

        val buttonGo  = view.findViewById<CardView>(R.id.go)
        buttonGo.setOnClickListener{
            view.findNavController().navigate(R.id.action_startFragment_to_welcomeFragment)
        }

        return view
    }

}