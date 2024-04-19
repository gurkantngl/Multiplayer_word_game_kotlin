package com.gurkantngl.wordgame.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gurkantngl.wordgame.R
import com.gurkantngl.wordgame.databinding.ActivityModSelectBinding

class ModSelectActivity : AppCompatActivity() {

    private lateinit var binding : ActivityModSelectBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModSelectBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        val intent = intent
        val username = intent.getStringExtra("username")
        binding.tvWelcome.text = "Hoş geldiniz $username"

        initUI()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initUI () {

        binding.btnMod1.setOnClickListener{
            val mod = 1
            val username = intent.getStringExtra("username")
            val intent = Intent(this, CharNumberActivity::class.java)
            intent.putExtra("mod", mod)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        binding.btnMod2.setOnClickListener{
            val mod = 2
            val username = intent.getStringExtra("username")
            val intent = Intent(this, CharNumberActivity::class.java)
            intent.putExtra("mod", mod)
            intent.putExtra("username", username)
            startActivity(intent)

        }

        binding.btnExit.setOnClickListener{
            val sharedPreferences = this.getSharedPreferences("com.gurkantngl.wordgame", Context.MODE_PRIVATE)
            sharedPreferences.edit().remove("username").apply()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
