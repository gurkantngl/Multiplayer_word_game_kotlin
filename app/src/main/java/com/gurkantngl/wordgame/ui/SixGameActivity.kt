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
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gurkantngl.wordgame.R
import com.gurkantngl.wordgame.databinding.ActivityFiveGameBinding
import com.gurkantngl.wordgame.databinding.ActivitySixGameBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class SixGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySixGameBinding
    private val db = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySixGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }
    private fun initUI() {
        var username = intent.getStringExtra("username")
        var mod = intent.getIntExtra("mod", 0)
        setEditTexts(0)
    }
    private fun setEditTexts(hak:Int){
        var textList1 = listOf<EditText>(
            binding.et611, binding.et612, binding.et613, binding.et614, binding.et615, binding.et616
        )
        var textList2 = listOf<EditText>(
            binding.et621, binding.et622, binding.et623, binding.et624, binding.et625, binding.et626
        )
        var textList3 = listOf<EditText>(
            binding.et631, binding.et632, binding.et633, binding.et634, binding.et635, binding.et636
        )
        var textList4 = listOf<EditText>(
            binding.et641, binding.et642, binding.et643, binding.et644, binding.et645, binding.et646
        )
        var textList5 = listOf<EditText>(
            binding.et651, binding.et652, binding.et653, binding.et654, binding.et655, binding.et656
        )
        var textList6 = listOf<EditText>(
            binding.et661, binding.et662, binding.et663, binding.et664, binding.et665, binding.et666
        )
        if (hak == 0) {
            editOff(textList2)
            editOff(textList3)
            editOff(textList4)
            editOff(textList5)
            editOff(textList6)
            word(textList1, hak)
        } else if (hak == 1) {
            editOff(textList1)
            editOff(textList3)
            editOff(textList4)
            editOff(textList5)
            editOff(textList6)
            word(textList2, hak)
        } else if (hak == 2) {
            editOff(textList1)
            editOff(textList2)
            editOff(textList4)
            editOff(textList5)
            editOff(textList6)
            word(textList3, hak)
        } else if (hak == 3) {
            editOff(textList1)
            editOff(textList2)
            editOff(textList3)
            editOff(textList5)
            editOff(textList6)
            word(textList4, hak)
        } else if (hak == 4) {
            editOff(textList1)
            editOff(textList2)
            editOff(textList3)
            editOff(textList4)
            editOff(textList6)
            word(textList5, hak)
        } else if (hak == 5) {
            editOff(textList1)
            editOff(textList2)
            editOff(textList3)
            editOff(textList4)
            editOff(textList5)
            word(textList6, hak)
        }
    }
    private fun word(textList : List<EditText>, hak : Int) {
        val username = intent.getStringExtra("username")
        textList[textList.size-1].setOnEditorActionListener{ v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                db.child("games").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            var question = ""
                            var word = ""
                            val user_1 = snapshot.child("user_1").value.toString()
                            val user_1_word = snapshot.child("user_1_word").value.toString()
                            val user_2_word = snapshot.child("user_2_word").value.toString()
                            if (user_1 == username) {
                                question = user_2_word
                            }else {
                                question = user_1_word
                            }
                            for (editText in textList) {
                                word += editText.text.toString()
                            }
                            GlobalScope.launch(Dispatchers.IO) {
                                val url = "https://sozluk.gov.tr/gts_id?id=" + word
                                val bodyText = URL(url).readText()
                                withContext(Dispatchers.Main) {
                                    if (bodyText == """{"error":"Sonuç bulunamadı"}""") {
                                        Toast.makeText(this@SixGameActivity, "$word geçerli bir kelime değil!!!", Toast.LENGTH_SHORT).show()
                                        for (editText in textList) {
                                            editText.text = null
                                        }
                                    } else {
                                        if (word == question) {
                                            for (editText in textList) {
                                                editText.background.setColorFilter(ContextCompat.getColor(this@SixGameActivity, R.color.green), PorterDuff.Mode.SRC_IN)
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
                                                            this@SixGameActivity,
                                                            R.color.green
                                                        ), PorterDuff.Mode.SRC_IN
                                                    )
                                                } else if (question.contains((textList[i].text.toString())) && !indices.contains(i)) {
                                                    textList[i].background.setColorFilter(
                                                        ContextCompat.getColor(
                                                            this@SixGameActivity,
                                                            R.color.yellow
                                                        ), PorterDuff.Mode.SRC_IN
                                                    )
                                                } else {
                                                    textList[i].background.setColorFilter(
                                                        ContextCompat.getColor(
                                                            this@SixGameActivity,
                                                            R.color.gray
                                                        ), PorterDuff.Mode.SRC_IN
                                                    )
                                                }
                                            }
                                            setEditTexts(hak+1)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })


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