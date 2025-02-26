package com.example.attendease

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController


class ManagmentFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_managment, container, false)


        val dr=view.findViewById<CardView>(R.id.dr)
        val std=view.findViewById<CardView>(R.id.std)
        val material=view.findViewById<CardView>(R.id.material)
        val update=view.findViewById<CardView>(R.id.update)
        val edit=view.findViewById<CardView>(R.id.edit_dr)
        val edit2=view.findViewById<CardView>(R.id.edit_std)


        dr.setOnClickListener {
            view.findNavController().navigate(R.id.action_managmentFragment_to_addDrFragment)
        }
        std.setOnClickListener {
            view.findNavController().navigate(R.id.action_managmentFragment_to_addStudentFragment)
        }
        material.setOnClickListener {
            view.findNavController().navigate(R.id.action_managmentFragment_to_addMaterielsFragment)
        }
        update.setOnClickListener {
            view.findNavController().navigate(R.id.action_managmentFragment_to_adminUpdateCurriculumFragment)
        }
        edit.setOnClickListener {
            view.findNavController().navigate(R.id.action_managmentFragment_to_editDrMaterialsFragment)
        }
        edit2.setOnClickListener {
            view.findNavController().navigate(R.id.action_managmentFragment_to_adminSelectStdToUpdateMaterialFragment)
        }



        return view
    }


}