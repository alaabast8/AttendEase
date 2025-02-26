package com.example.attendease

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController


class AdminUpdateCurriculumFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_admin_update_curriculum, container, false)
        val dept=view.findViewById<CardView>(R.id.dept)
        val yearr=view.findViewById<CardView>(R.id.year)
        val sem = view.findViewById<CardView>(R.id.sem)
        val lang = view.findViewById<CardView>(R.id.lang)


        dept.setOnClickListener {
            view.findNavController().navigate(R.id.action_adminUpdateCurriculumFragment_to_addDeptFragment)
        }
        yearr.setOnClickListener {
            view.findNavController().navigate(R.id.action_adminUpdateCurriculumFragment_to_addYearFragment)
        }
        sem.setOnClickListener {
            view.findNavController().navigate(R.id.action_adminUpdateCurriculumFragment_to_addSemFragment)
        }
        lang.setOnClickListener {
            view.findNavController().navigate(R.id.action_adminUpdateCurriculumFragment_to_addLangFragment)
        }
        return view
    }


}