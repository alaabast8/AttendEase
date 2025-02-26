package com.example.attendease

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController


class AdminFunctionsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_admin_functions, container, false)


        val managment=view.findViewById<CardView>(R.id.managment)
        val archive=view.findViewById<CardView>(R.id.archive)
        val check=view.findViewById<CardView>(R.id.cheking)

        managment.setOnClickListener {
            view.findNavController().navigate(R.id.action_adminFunctionsFragment_to_managmentFragment)
        }
        archive.setOnClickListener {
            view.findNavController().navigate(R.id.action_adminFunctionsFragment_to_adminDoArchiveFragment)
        }
        check.setOnClickListener {
            view.findNavController().navigate(R.id.action_adminFunctionsFragment_to_adminAttendanceFragment)
        }

        return view
    }


}