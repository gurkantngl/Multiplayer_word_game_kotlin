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
import com.gurkantngl.wordgame.databinding.ActivityTimeOverBinding

class TimeOverActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimeOverBinding
    private val db = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeOverBinding.inflate(layoutInflater)
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
        binding.btnTurnLoby.setOnClickListener{
            val username = intent.getStringExtra("username")
            val intent = Intent(this, ModSelectActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
            finish()
        }
    }
}