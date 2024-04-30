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
import com.gurkantngl.wordgame.databinding.ActivityRivalScreenSevenBinding

class RivalScreenActivitySeven : AppCompatActivity() {

    private lateinit var binding: ActivityRivalScreenSevenBinding
    private val db = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRivalScreenSevenBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_rival_screen_seven)
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
            binding.et711, binding.et712, binding.et713, binding.et714, binding.et715, binding.et716, binding.et717,
                    binding.et721, binding.et722, binding.et723, binding.et724, binding.et725, binding.et726, binding.et727,
            binding.et741, binding.et742, binding.et743, binding.et744, binding.et745, binding.et746, binding.et747,
                    binding.et751, binding.et752, binding.et753, binding.et754, binding.et755 , binding.et756, binding.et757,
            binding.et761, binding.et762, binding.et763, binding.et764, binding.et765, binding.et766, binding.et767,
                    binding.et771, binding.et772, binding.et773, binding.et774, binding.et775, binding.et776, binding.et777,
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