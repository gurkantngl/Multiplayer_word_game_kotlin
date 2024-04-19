package com.gurkantngl.wordgame.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.gurkantngl.wordgame.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySignUpBinding
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initUI()
    }

    private fun initUI(){
        binding.btnSignup.setOnClickListener{
            val username = binding.etUsernameSignup.text.toString().trim()
            val email = binding.etEmailSignup.text.toString().trim()
            val password = binding.etPasswordSignup.text.toString().trim()
            val confirmpass = binding.etConfirmPassSignup.text.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmpass.isNotEmpty()) {
                if (password == confirmpass){
                    val userMap = hashMapOf(
                        "username" to username,
                        "email" to email,
                        "password" to password
                    )

                    db.collection("users").document().set(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(this, "User Added Successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, SignInActivity::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error Adding User", Toast.LENGTH_SHORT).show()
                        }
                }else{
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            }else {
                    Toast.makeText(this,"Empty Fields Are not Allowed !!!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvSignIn.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}