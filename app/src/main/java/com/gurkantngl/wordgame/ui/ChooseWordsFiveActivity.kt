package com.gurkantngl.wordgame.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gurkantngl.wordgame.R
import com.gurkantngl.wordgame.databinding.ActivityChooseWordsFiveBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class ChooseWordsFiveActivity : AppCompatActivity() {

    private  lateinit var binding : ActivityChooseWordsFiveBinding
    private val db = Firebase.database.reference
    private lateinit var timer: CountDownTimer
    private lateinit var gameListener: ValueEventListener
    private lateinit var wordListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseWordsFiveBinding.inflate(layoutInflater)
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
        timer.cancel()
        if (::gameListener.isInitialized) {
            db.child("games").removeEventListener(gameListener)
        }
        if (::wordListener.isInitialized) {
            db.child("games").removeEventListener(wordListener)
        }
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
        db.child(roomList[mod-1]).child("5").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.key
                    if (user != username) {
                        users.add(user!!)
                    }
                }
                val usersString = users.joinToString(", ")

                Toast.makeText(this@ChooseWordsFiveActivity, usersString, Toast.LENGTH_SHORT).show()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        db.child(roomList[mod-1]).child("5").child(username!!).setValue(hashMapOf("is_playing" to false))
    }

    private fun leave_room() {
        val mod = intent.getIntExtra("mod", 0)
        val username = intent.getStringExtra("username")
        val roomList = listOf(
            "mod_1_rooms",
            "mod_2_rooms",
        )

        db.child(roomList[mod-1]).child("5").child(username!!).removeValue()
    }

    private fun initUI() {
        val username = intent.getStringExtra("username")
        binding.txtUsername.text = username
        val request_to = intent.getStringExtra("request_to")
        val request_from = intent.getStringExtra("request_from")
        val mod = intent.getIntExtra("mod", 0)
         var textList = listOf(
                binding.et1,
                binding.et2,
                binding.et3,
                binding.et4,
                binding.et5
         )

        for (i in 0 until textList.size) {
            textList[i].filters = arrayOf<InputFilter>(InputFilter.LengthFilter(1))
        }

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

        binding.btnConfirm.setOnClickListener{
            var word = ""
            for(editText in textList) {
                word += editText.text.toString()
            }


            db.child("games").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var user_1_word = ""
                    var user_2_word = ""
                    for (snapshot in dataSnapshot.children) {
                        user_1_word = snapshot.child("user_1_word").value.toString()
                        user_2_word = snapshot.child("user_2_word").value.toString()
                    }
                    if (user_1_word.length > 0 && user_2_word.length > 0) {
                        val username = intent.getStringExtra("username")
                        val mod = intent.getIntExtra("mod", 0)
                        val intent = Intent(this@ChooseWordsFiveActivity, FiveGameActivity::class.java)
                        intent.putExtra("username", username)
                        intent.putExtra("mod", mod)
                        startActivity(intent)
                        finish()
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
                    if (bodyText == """{"error":"Sonuç bulunamadı"}""") {
                        Toast.makeText(this@ChooseWordsFiveActivity, "$word geçerli bir kelime değil!!!", Toast.LENGTH_SHORT).show()
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

                        binding.txtInfo.visibility = View.INVISIBLE
                        binding.et1.visibility = View.INVISIBLE
                        binding.et2.visibility = View.INVISIBLE
                        binding.et3.visibility = View.INVISIBLE
                        binding.et4.visibility = View.INVISIBLE
                        binding.et5.visibility = View.INVISIBLE
                        binding.btnConfirm.visibility = View.INVISIBLE
                        binding.txtWait5.visibility = View.VISIBLE
                    }
                }
            }
        }
        timer = object: CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.etKronometre5.setText(secondsRemaining.toString())
            }

            override fun onFinish() {

                gameListener = db.child("games").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            val user_1 = snapshot.child("user_1").value.toString()
                            val user_2 = snapshot.child("user_2").value.toString()
                            val user_1_word = snapshot.child("user_1_word").value.toString()
                            val user_2_word = snapshot.child("user_2_word").value.toString()

                            if(user_1_word.length == 0 && user_2_word.length > 0) {
                                val intent = Intent(this@ChooseWordsFiveActivity, Winner1Activity::class.java)
                                intent.putExtra("username", username)
                                intent.putExtra("winner", user_2)
                                startActivity(intent)
                                finish()
                            }else if (user_2_word.length == 0 && user_1_word.length > 0) {
                                val intent = Intent(this@ChooseWordsFiveActivity, Winner1Activity::class.java)
                                intent.putExtra("username", username)
                                intent.putExtra("winner", user_1)
                                startActivity(intent)
                                finish()
                            }else {
                                binding.etKronometre5.setText("0")
                                val intent =
                                    Intent(this@ChooseWordsFiveActivity, ChooseWordsFiveActivity::class.java)
                                intent.putExtra("username", username)
                                intent.putExtra("mod", mod)
                                intent.putExtra("request_to", request_to)
                                intent.putExtra("request_from", request_from)
                                timer.cancel()
                                startActivity(intent)
                                finish()
                            }
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
        }
        timer.start()
    }
}