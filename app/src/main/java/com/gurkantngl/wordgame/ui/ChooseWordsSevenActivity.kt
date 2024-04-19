package com.gurkantngl.wordgame.ui

import android.os.Bundle
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


    fun isTurkishWord(word: String): Boolean {
        val url = "https://sozluk.gov.tr/gts?ara=kelime"
        return try {
            val document: Document = Jsoup.connect(url).get()
            val elementText = document.body().text()
            val expectedText = "{\"error\":\"Sonuç bulunamadı\"}"
            elementText != expectedText
        } catch (e: IOException) {
            false
        }
    }

    private fun initUI() {
        val username = intent.getStringExtra("username")
        binding.txtUsername7.text = username
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

        for(editText in textList) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    // Do nothing
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // Do nothing
                }

                override fun afterTextChanged(s: Editable) {
                    val text = s.toString()
                    if (text != text.toUpperCase()) {
                        editText.setText(text.toUpperCase())
                        editText.setSelection(text.length) // Reset cursor position
                    }
                }
            })
        }

        binding.btnConfirm7.setOnClickListener{
            var word = ""
            for (editText in textList) {
                word += editText.text.toString()

            }
            val isWord = isTurkishWord(word.lowercase())
            Toast.makeText(this, "$word + $isWord", Toast.LENGTH_SHORT).show()
        }
    }
}