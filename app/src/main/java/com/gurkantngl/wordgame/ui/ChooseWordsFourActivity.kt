package com.gurkantngl.wordgame.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gurkantngl.wordgame.R
import com.gurkantngl.wordgame.databinding.ActivityChooseWordsFourBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class ChooseWordsFourActivity : AppCompatActivity() {

    private lateinit var binding : ActivityChooseWordsFourBinding
    private val db = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseWordsFourBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        initUI()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        leave_room()
    }

    override fun onPause() {
        super.onPause()
        leave_room()
    }

    override fun onResume() {
        super.onResume()
        val username = intent.getStringExtra("username")
        val mod = intent.getIntExtra("mod", 0)
        var roomList = listOf(
                "mod_1_rooms",
                "mod_2_rooms",
        )

        val users = ArrayList<String>()

        db.child(roomList[mod-1]).child("4").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.key
                    if (user != username) {
                        users.add(user!!)
                    }
                }
                val usersString = users.joinToString(", ")

                Toast.makeText(this@ChooseWordsFourActivity, usersString, Toast.LENGTH_SHORT).show()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        db.child(roomList[mod-1]).child("4").child(username!!).setValue(hashMapOf("is_playing" to true))

    }

    private fun leave_room() {
        val mod = intent.getIntExtra("mod", 0)
        val username = intent.getStringExtra("username")
        val roomList = listOf(
            "mod_1_rooms",
            "mod_2_rooms",
        )

        db.child(roomList[mod-1]).child("4").child(username!!).removeValue()
    }

    private fun initUI() {
        val username = intent.getStringExtra("username")
        binding.txtUsername4.text = username
        val request_to = intent.getStringExtra("request_to")
        val request_from = intent.getStringExtra("request_from")
        val mod = intent.getIntExtra("mod", 0)
        var textList = listOf(
            binding.et41,
            binding.et42,
            binding.et43,
            binding.et44
        )


        for(i in 0 until textList.size) {
            textList[i].inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
            textList[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    // Do nothing
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // Do nothing
                }

                override fun afterTextChanged(s: Editable?) {
                    val text = s.toString()
                    if (text != text.toUpperCase()) {
                        textList[i].setText(text.toUpperCase())
                        textList[i].setSelection(text.length) // Reset cursor position
                    }
                    s?.let  {
                        if (it.length == 1 && i < textList.size - 1) {
                            textList[i+1].requestFocus()
                        } else if (it.length == 0 && i > 0) {
                            textList[i-1].requestFocus()
                        } else {
                            // Do nothing
                        }
                    }
                }
            })
        }

        binding.btnConfirm4.setOnClickListener{
            var word = ""
            for(editText in textList) {
                word += editText.text.toString()
            }


            db.child("games").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val user_1_word = snapshot.child("user_1_word").value.toString()
                        val user_2_word = snapshot.child("user_2_word").value.toString()
                        if (user_1_word.length > 0 && user_2_word.length > 0) {
                            val intent = Intent(this@ChooseWordsFourActivity, FourGameActivity::class.java)
                            intent.putExtra("username", username)
                            intent.putExtra("mod", mod)
                            intent.putExtra("request_to", request_to)
                            intent.putExtra("request_from", request_from)
                            startActivity(intent)
                            finish()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Hata durumunda yapılacak işlemler
                }
            })

            GlobalScope.launch(Dispatchers.IO) {
                val url = "https://sozluk.gov.tr/gts_id?id=" + word
                val bodyText = URL(url).readText()
                withContext(Dispatchers.Main) {
                    //Toast.makeText(this@ChooseWordsFourActivity, bodyText, Toast.LENGTH_SHORT).show()
                    if (bodyText == """{"error":"Sonuç bulunamadı"}""") {
                        Toast.makeText(this@ChooseWordsFourActivity, "$word geçerli bir kelime değil!!!", Toast.LENGTH_SHORT).show()
                    } else {
                        db.child("games").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (snapshot in dataSnapshot.children) {
                                    val user_1 = snapshot.child("user_1").value.toString()
                                    val user_1_word = snapshot.child("user_1_word").value.toString()
                                    val user_2_word = snapshot.child("user_2_word").value.toString()
                                    if (user_1 == username) {
                                        db.child("games").child(snapshot.key!!).child("user_1_word").setValue(word)
                                    } else {
                                        db.child("games").child(snapshot.key!!).child("user_2_word").setValue(word)
                                    }

                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })

                        binding.textView5.visibility = android.view.View.INVISIBLE
                        binding.btn41.visibility = android.view.View.INVISIBLE
                        binding.et41.visibility = android.view.View.INVISIBLE
                        binding.btn42.visibility = android.view.View.INVISIBLE
                        binding.et42.visibility = android.view.View.INVISIBLE
                        binding.btn43.visibility = android.view.View.INVISIBLE
                        binding.et43.visibility = android.view.View.INVISIBLE
                        binding.btn44.visibility = android.view.View.INVISIBLE
                        binding.et44.visibility = android.view.View.INVISIBLE
                        binding.btnConfirm4.visibility = android.view.View.INVISIBLE
                        binding.txtWait.visibility = android.view.View.VISIBLE
                    }
                }
            }
        }

        val timer = object: CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.etKronometre.setText(secondsRemaining.toString())
            }

            override fun onFinish() {
                val mod = intent.getIntExtra("mod", 0)
                binding.etKronometre.setText("0")
                val intent = Intent(this@ChooseWordsFourActivity, ChooseWordsFourActivity::class.java)
                intent.putExtra("username", username)
                intent.putExtra("mod", mod)
                intent.putExtra("request_to", request_to)
                intent.putExtra("request_from", request_from)
                startActivity(intent)
            }
        }
        timer.start()

    }

}