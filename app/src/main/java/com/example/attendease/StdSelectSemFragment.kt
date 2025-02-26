package com.example.attendease

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.google.firebase.firestore.FirebaseFirestore


class StdSelectSemFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_std_select_sem, container, false)
        val linearLayout = view.findViewById<LinearLayout>(R.id.linearLayout)
        val StudentId=StdSelectSemFragmentArgs.fromBundle(requireArguments()).studentId



        db.collection("Semesters")
            .orderBy("Semester Name")
            .get().addOnSuccessListener { result ->
            for (document in result) {
                val semesterId = document.id
                val semesterName = document.getString("Semester Name")

                val cardView = context?.let {
                    CardView(it).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            topMargin = 16
                            bottomMargin = 16
                        }
                        radius = 15F
                        setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
                        elevation = 10F

                        addView(TextView(context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                            )
                            text = semesterName
                            textSize = 20F
                            gravity = Gravity.CENTER
                            setTextColor(ContextCompat.getColor(context, R.color.red))
                        })

                        setOnClickListener {
                            val action = StdSelectSemFragmentDirections.actionStdSelectSemFragmentToStdShowAttendanceFragment(StudentId,semesterId)
                            view.findNavController().navigate(action)
                        }
                    }
                }

                linearLayout.addView(cardView)
            }
        }

        return view
    }
}
