package com.gurkantngl.wordgame.ui

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.database
import com.gurkantngl.wordgame.R
import com.gurkantngl.wordgame.databinding.ActivityWinner2Binding

class Winner2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityWinner2Binding
    private val db = Firebase.database.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWinner2Binding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        initUI()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun initUI() {
        val username = intent.getStringExtra("username")
        binding.btnDuello.setOnClickListener{
            db.child("games").addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    val duello = dataSnapshot.child("duello").getValue(String::class.java)
                    val duelloId = dataSnapshot.child("duelloId").getValue(String::class.java)
                    if (duello?.length!! > 0 && duello != username){
                        lateinit var alertDialog: AlertDialog
                        val countDownTimerobject = object : CountDownTimer(10000, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).text = "Kabul Et (${millisUntilFinished / 1000})"
                            }
                            override fun onFinish() {
                                alertDialog.dismiss()
                            }
                        }
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
}