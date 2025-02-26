package com.example.attendease

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController


class AdminAttendanceFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_admin_attendance, container, false)

        val archive=view.findViewById<CardView>(R.id.checkArchive)
        val attend=view.findViewById<CardView>(R.id.checkAttend)

        attend.setOnClickListener {
            view.findNavController().navigate(R.id.action_adminAttendanceFragment_to_adminAttendSelectMaterialFragment)
        }

        archive.setOnClickListener {
            view.findNavController().navigate(R.id.action_adminAttendanceFragment_to_adminCheckArchiveSelectMaterialFragment)
        }


        return view
    }


}