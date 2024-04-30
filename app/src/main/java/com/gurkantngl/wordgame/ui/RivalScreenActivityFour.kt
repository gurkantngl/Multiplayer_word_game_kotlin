package com.gurkantngl.wordgame.ui

import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.gurkantngl.wordgame.R
import com.gurkantngl.wordgame.databinding.ActivityFourGameBinding
import com.gurkantngl.wordgame.databinding.ActivityRivalScreenBinding

class RivalScreenActivityFour : AppCompatActivity() {

    private lateinit var binding: ActivityRivalScreenBinding
    private val db = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRivalScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }
    private fun initUI() {
        val username = intent.getStringExtra("username")
        val textList = listOf<EditText>(
            binding.et411, binding.et412, binding.et413, binding.et414,
            binding.et421, binding.et422, binding.et423, binding.et424,
            binding.et431, binding.et432, binding.et433, binding.et434,
            binding.et441, binding.et442, binding.et443, binding.et444
        )
        val textList1 = textList.subList(0, 4)
        val textList2 = textList.subList(4, 8)
        val textList3 = textList.subList(8, 12)
        val textList4 = textList.subList(12, 16)
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