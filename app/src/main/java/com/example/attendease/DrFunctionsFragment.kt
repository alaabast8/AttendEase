package com.example.attendease

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController


class DrFunctionsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_dr_functions, container, false)


        val drId=DrFunctionsFragmentArgs.fromBundle(requireArguments()).drId
        val materialId=DrFunctionsFragmentArgs.fromBundle(requireArguments()).materialId

        Toast.makeText(requireContext()," $drId $materialId",Toast.LENGTH_SHORT).show()

        val show=view.findViewById<CardView>(R.id.drshow)
        val take=view.findViewById<CardView>(R.id.take)

        show.setOnClickListener {
            val action=DrFunctionsFragmentDirections.actionDrFunctionsFragmentToDrShowAttendanceFragment(materialId)
            view.findNavController().navigate(action)
        }
        take.setOnClickListener {
            val action=DrFunctionsFragmentDirections.actionDrFunctionsFragmentToDrCreateSessionFragment(drId,materialId)
            view.findNavController().navigate(action)
        }


        return view
    }

}