package com.gurkantngl.wordgame.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.gurkantngl.wordgame.R
import com.gurkantngl.wordgame.databinding.ActivityMainBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var  binding : ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var sharedProcess: SharedPreferences
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        var sharedPreferences = this.getSharedPreferences("com.gurkantngl.wordgame", Context.MODE_PRIVATE)
        sharedProcess = sharedPreferences

        firebaseAuth = FirebaseAuth.getInstance()

        var username = sharedPreferences.getString("username", "")
        if(username == "") {
            //Initialize the UI
            initUI()
        }else {
            val intent = Intent(this, ModSelectActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
            finish()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private  fun initUI() {
        binding.fabBack.setOnClickListener{

        }
        binding.btnSignIn.setOnClickListener{
            val username = binding.etUsernameSignin.text.toString()
            val password = binding.etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Kullanıcı adı ve şifre gereklidir", Toast.LENGTH_SHORT).show()
            }else{
                db.collection("users")
                    .whereEqualTo("username", username)
                    .whereEqualTo("password", password)
                    .get()
                    .addOnCompleteListener{task ->
                        if(task.isSuccessful) {
                            if(!task.result.isEmpty) {
                                Toast.makeText(this, "Successfully signed in", Toast.LENGTH_SHORT).show()
                                sharedProcess.edit().putString("username", username).apply()
                                val intent = Intent(this, ModSelectActivity::class.java)
                                intent.putExtra("username", username)
                                startActivity(intent)
                                finish()
                            }else {
                                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }

                    }
            }


        }
        binding.tvForgotPassword.setOnClickListener{

        }
        binding.tvSignUp.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}