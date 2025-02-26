package com.example.attendease

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

class StdShowAttendanceFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_std_show_attendance, container, false)

        val materialsList= mutableListOf<String>()
        val materialsSemList= mutableListOf<String>()
        val nbrSessionsList= mutableMapOf<String, Int>()

        val studentId = StdShowAttendanceFragmentArgs.fromBundle(requireArguments()).studentId
        val selectedSem = StdShowAttendanceFragmentArgs.fromBundle(requireArguments()).selectedSem

        val db = FirebaseFirestore.getInstance()

        val linearr=view.findViewById<LinearLayout>(R.id.linear)

        //get all the materials of the student
        db.collection("Students Materials")
            .whereEqualTo("Student Id",studentId)
            .get()
            .addOnSuccessListener { documents->
                for(document in documents){
                    val m=document.getString("Material Id").toString()
                    materialsList.add(m)
                }

            }
        var message=""

        //get the materials of the student in the selected semester
        db.collection("Materials")
            .orderBy("Material Name")
            .whereEqualTo("Semester",selectedSem)
            .get()
            .addOnSuccessListener { documents->
                linearr.removeAllViews()
                for(document in documents){
                    val m=document.id
                    val materialName =document.getString("Material Name")
                    val materialCode =document.getString("Material Code")


                    if(materialsList.contains(m)){
                        materialsSemList.add(m)
                        //get the number of lecture of the material
                        db.collection("Sessions")
                            .whereEqualTo("Material Id",m)
                            .get()
                            .addOnSuccessListener { sessions ->
                                val nbr=sessions.size()
                                nbrSessionsList[m]=nbr
                                val sessionIds = sessions.map { it.id }
                                message=" $message material= $materialName -$materialCode  nb of sessions = $nbr  sessionid= $sessionIds \n"

                                // get the number of attendance of the student in the material
                                if(sessionIds.isNotEmpty()) {
                                    db.collection("Attendances")
                                        .whereIn("Session Id", sessionIds)
                                        .whereEqualTo("Student Id", studentId)
                                        .get()
                                        .addOnSuccessListener { attend ->
                                            val nbr2 = attend.size()
                                            message = "$materialName - $materialCode - nb of attendance= $nbr2 / $nbr\n"
                                            val textView = TextView(requireContext())
                                            textView.text =message
                                            textView.textSize=20f
                                            val prob = nbr2.toFloat()/nbr.toFloat()
                                            if(prob >= 0.5)
                                                textView.setTextColor(Color.parseColor("#0c5e38"))
                                            else
                                                textView.setTextColor(Color.parseColor("#da251c"))
                                            linearr.addView(textView)
                                        }
                                }
                                else {
                                    message = "$materialName - $materialCode - no sessions\n"
                                    val textView = TextView(requireContext())
                                    textView.text =message
                                    textView.textSize=20f
                                    textView.setTextColor(Color.parseColor("#000000"))
                                    linearr.addView(textView)
                                }


                            }
                    }
                }

            }



        return view
    }

}