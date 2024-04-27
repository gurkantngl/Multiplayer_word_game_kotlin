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
import androidx.compose.ui.text.toLowerCase
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.play.integrity.internal.i
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gurkantngl.wordgame.R
import com.gurkantngl.wordgame.databinding.ActivityChooseWordsSevenBinding
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException


class ChooseWordsSevenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseWordsSevenBinding
    private val db = Firebase.database.reference
    private lateinit var timer: CountDownTimer
    private lateinit var gameListener: ValueEventListener
    private lateinit var wordListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseWordsSevenBinding.inflate(layoutInflater)
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

        db.child(roomList[mod-1]).child("7").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.key
                    if (user != username) {
                        users.add(user!!)
                    }
                }
                val usersString = users.joinToString(", ")

                Toast.makeText(this@ChooseWordsSevenActivity, usersString, Toast.LENGTH_SHORT).show()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        db.child(roomList[mod-1]).child("7").child(username!!).setValue(hashMapOf("is_playing" to false))
    }


    private fun leave_room() {
        val mod = intent.getIntExtra("mod", 0)
        val username = intent.getStringExtra("username")
        val roomList = listOf(
            "mod_1_rooms",
            "mod_2_rooms",
        )

        db.child(roomList[mod - 1]).child("7").child(username!!).removeValue()
    }

    private fun initUI() {
        val username = intent.getStringExtra("username")
        binding.txtUsername7.text = username
        val request_to = intent.getStringExtra("request_to")
        val request_from = intent.getStringExtra("request_from")
        val mod = intent.getIntExtra("mod", 0)
        var textList = listOf(
            binding.et71,
            binding.et72,
            binding.et73,
            binding.et74,
            binding.et75,
            binding.et76,
            binding.et77
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

                override fun afterTextChanged(s: Editable) {
                    val text = s.toString()
                    if (text != text.toUpperCase()) {
                        textList[i].setText(text.toUpperCase())
                        textList[i].setSelection(text.length) // Reset cursor position
                    }
                    s?.let {
                        if (it.length == 1 &&  i < textList.size-1) {
                            textList[i + 1].requestFocus()
                        } else if (it.length == 0 && i > 0) {
                            textList [i - 1].requestFocus()
                        } else {
                            // Do nothing
                        }
                    }
                }
            })
        }

        binding.btnConfirm7.setOnClickListener{
            var word = ""
            for (editText in textList) {
                word += editText.text.toString()
            }
            wordListener = db.child("games").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var user_1_words = ""
                    var user_2_words = ""
                    for (snapshot in dataSnapshot.children) {
                        user_1_words = snapshot.child("user_1_words").value.toString()
                        user_2_words = snapshot.child("user_2_words").value.toString()
                    }
                    if (user_1_words.length > 0 && user_2_words.length > 0) {
                        val username = intent.getStringExtra("username")
                        val mod = intent.getIntExtra("mod", 0)
                        val intent = Intent(this@ChooseWordsSevenActivity, SevenGameActivity::class.java)
                        intent.putExtra("username", username)
                        intent.putExtra("mod", mod)
                        intent.putExtra("request_to", request_to)
                        intent.putExtra("request_from", request_from)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }
}