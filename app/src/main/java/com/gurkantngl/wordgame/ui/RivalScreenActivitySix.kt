package com.gurkantngl.wordgame.ui

import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.gurkantngl.wordgame.R
import com.gurkantngl.wordgame.databinding.ActivityRivalScreenFiveBinding
import com.gurkantngl.wordgame.databinding.ActivityRivalScreenSixBinding

class RivalScreenActivitySix : AppCompatActivity() {
    private lateinit var binding: ActivityRivalScreenSixBinding
    private val db = Firebase.database.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRivalScreenSixBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_rival_screen_six)
        initUI()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun initUI() {
        val username = intent.getStringExtra("username")
        val textList = listOf<EditText>(
            binding.et611, binding.et612, binding.et613, binding.et614, binding.et615, binding.et616,
                    binding.et621, binding.et622, binding.et623, binding.et624, binding.et625, binding.et626,
                    binding.et631, binding.et632, binding.et633, binding.et634, binding.et635, binding.et636,
                    binding.et641, binding.et642, binding.et643, binding.et644, binding.et645, binding.et646,
                    binding.et651, binding.et652, binding.et653, binding.et654, binding.et655, binding.et656,
                    binding.et661, binding.et662, binding.et663, binding.et664, binding.et665, binding.et666
        )

        for (editText in textList){
            editText.setEnabled(false)
        }
        db.child("games").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val user_1 = snapshot.child("user_1").value.toString()
                    val user_2 = snapshot.child("user_2").value.toString()

                    if (username == user_1 || username == user_2) {
                        val gameId = snapshot.key
                        val word = if (username == user_1) {
                            db.child("games").child(gameId!!).child("user_1_word").toString()
                        } else {
                            db.child("games").child(gameId!!).child("user_2_word").toString()
                        }
                        val trialsRef = if (username == user_1) {
                            db.child("games").child(gameId!!).child("user_2_trials")
                        } else {
                            db.child("games").child(gameId!!).child("user_1_trials")
                        }

                        trialsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val trialsList = ArrayList<String>()
                                for (childSnapshot in dataSnapshot.children) {
                                    val trial = childSnapshot.getValue(String::class.java)
                                    if (trial != null) {
                                        trialsList.add(trial)
                                    }
                                }
                                var counter = 0
                                for (i in 0 until trialsList.size){
                                    for (j in 0 until trialsList[i].length) {
                                        textList[counter].setText(trialsList[i][j].toString())
                                        counter += 1
                                    }
                                }
                            }
                            override fun onCancelled(databaseError: DatabaseError) {
                                println("loadPost:onCancelled ${databaseError.toException()}")
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}