package com.gurkantngl.wordgame.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.gurkantngl.wordgame.R
import com.gurkantngl.wordgame.databinding.ActivityWinner1Binding

class Winner1Activity : AppCompatActivity() {
    private lateinit var binding: ActivityWinner1Binding
    private val db = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWinner1Binding.inflate(layoutInflater)
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
        val winner = intent.getStringExtra("winner")
        val username = intent.getStringExtra("username")
        binding.txtWinner.text = "Kazanan Oyuncu: $winner"
        binding.btnWinner.setOnClickListener{
            val intent = Intent(this, ModSelectActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }
    }
}