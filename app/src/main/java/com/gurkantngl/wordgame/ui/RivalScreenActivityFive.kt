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
import com.gurkantngl.wordgame.databinding.ActivityRivalScreenBinding
import com.gurkantngl.wordgame.databinding.ActivityRivalScreenFiveBinding

class RivalScreenActivityFive : AppCompatActivity() {

    private lateinit var binding: ActivityRivalScreenFiveBinding
    private val db = Firebase.database.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRivalScreenFiveBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_rival_screen_five)
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
            binding.et511, binding.et512, binding.et513, binding.et514, binding.et515,
                    binding.et521, binding.et522, binding.et523, binding.et524, binding.et525,
            binding.et541, binding.et542, binding.et543, binding.et544, binding.et545,
                    binding.et551, binding.et552, binding.et553, binding.et554, binding.et555

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