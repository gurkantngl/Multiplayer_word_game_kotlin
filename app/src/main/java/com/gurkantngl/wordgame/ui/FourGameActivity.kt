package com.gurkantngl.wordgame.ui

import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collection.mutableVectorOf
import androidx.compose.ui.text.toUpperCase
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.gurkantngl.wordgame.R
import com.gurkantngl.wordgame.databinding.ActivityFourGameBinding
import java.util.concurrent.atomic.AtomicInteger

class FourGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFourGameBinding
    private val db = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFourGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        var username = intent.getStringExtra("username")
        var mod = intent.getIntExtra("mod", 0)
        setEditTexts(0)
    }
    private fun setEditTexts(hak: Int){
        var textList1 = listOf<EditText>(
            binding.et411, binding.et412, binding.et413, binding.et414,
        )
        var textList2 = listOf<EditText>(
            binding.et421, binding.et422, binding.et423, binding.et424,
        )
        var textList3 = listOf<EditText>(
            binding.et431, binding.et432, binding.et433, binding.et434,
        )
        var textList4 = listOf<EditText>(
            binding.et441, binding.et442, binding.et443, binding.et444,
        )

        if (hak == 0) {
            editOff(textList2)
            editOff(textList3)
            editOff(textList4)
            word(textList1, hak)
        }else if (hak== 1) {
            editOff(textList1)
            editOff(textList3)
            editOff(textList4)
            word(textList2, hak)
        }else if (hak== 2) {
            editOff(textList1)
            editOff(textList2)
            editOff(textList4)
            word(textList3, hak)
        }else if (hak == 3) {
            editOff(textList1)
            editOff(textList2)
            editOff(textList3)
            word(textList4, hak)
        }
    }

    private fun word(textList : List<EditText>, hak : Int) {
        var username = intent.getStringExtra("username")
        textList[textList.size-1].setOnEditorActionListener{ v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                db.child("games").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            var question = ""
                            var word = ""
                            val user_1 = snapshot.child("user_1").value.toString()
                            val user_1_word = snapshot.child("user_1_word").value.toString()
                            val user_2 = snapshot.child("user_2").value.toString()
                            val user_2_word = snapshot.child("user_2_word").value.toString()
                            if (user_1 == username) {
                                question = user_2_word
                            }else {
                                question = user_1_word
                            }
                            for (editText in textList) {
                                word += editText.text.toString()
                            }
                            if (word == question) {
                                for (editText in textList) {
                                    editText.background.setColorFilter(ContextCompat.getColor(this@FourGameActivity, R.color.green), PorterDuff.Mode.SRC_IN)
                                }
                            }else {
                                for(i in 0 until textList.size) {
                                    val indices = mutableListOf<Int>()
                                    for ((index, value) in question.withIndex()) {
                                        if (value.toString() == textList[i].text.toString()) {
                                            indices.add(index)
                                        }
                                    }
                                    if (indices.contains(i)) {
                                        textList[i].background.setColorFilter(
                                            ContextCompat.getColor(
                                                this@FourGameActivity,
                                                R.color.green
                                            ), PorterDuff.Mode.SRC_IN
                                        )
                                    }
                                }
                            }


                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

                Toast.makeText(this, "Enter a basıldı", Toast.LENGTH_SHORT).show()
                setEditTexts(hak+1)
                true
            } else {
                false
            }
        }
        for (i in 0 until textList.size) {
            textList[i].setEnabled(true)
            textList[i].inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
            textList[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    val text = s.toString()
                    if (text != text.toUpperCase()) {
                        textList[i].setText(text.toUpperCase())
                        textList[i].setSelection(text.length)
                    }
                    s?.let {
                        if (it.length == 1 && i < textList.size - 1) {
                            textList[i+1].requestFocus()
                        }else if (it.length == 0 && i > 0) {
                            textList[i-1].requestFocus()
                        }else {
                            // Do nothing
                        }
                    }
                }
            })
        }
    }

    private fun editOff(textList : List<EditText>) {
        for (i in 0 until textList.size) {
            textList[i].setEnabled(false)
        }
    }

}