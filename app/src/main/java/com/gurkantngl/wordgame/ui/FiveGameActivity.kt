package com.gurkantngl.wordgame.ui

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.gurkantngl.wordgame.R
import com.gurkantngl.wordgame.databinding.ActivityFiveGameBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class FiveGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFiveGameBinding
    private val db = Firebase.database.reference
    private var countDownTimer: CountDownTimer? = null
    private var timeLeft = 60000L //60 saniye

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFiveGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Oyunu Terk Et")
            .setMessage("Oyundan çıkmanız halinde oyunu kaybedeceksiniz. Çıkmak istiyorsanız onay butonuna basınız")
            .setPositiveButton("Onayla") { _, _ ->
                super.onBackPressed()
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                // 10 saniye kala uyarı ver
                if (timeLeft < 10000) {
                    val secondsRemaining = millisUntilFinished / 1000
                    binding.etTimer5.setText(secondsRemaining.toString())
                }
            }

            override fun onFinish() {
                val username = intent.getStringExtra("username")
                db.child("games").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for(snapshot in dataSnapshot.children) {
                            val user_1 = snapshot.child("user_1").getValue(String::class.java).toString()
                            val user_2 = snapshot.child("user_2").getValue(String::class.java).toString()
                            if (username == user_1 || username == user_2) {
                                snapshot.ref.child("time_over").setValue(username)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
                val intent = Intent(this@FiveGameActivity, TimeOverActivity::class.java)
                intent.putExtra("username", username)
                startActivity(intent)
                finish()
            }
        }.start()
    }
    fun resetTimer(){
        countDownTimer?.cancel()
        timeLeft = 60000
        binding.etTimer5.setText("")
        startTimer()
    }

    private fun initUI() {
        var username = intent.getStringExtra("username")
        db.child("games").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val timeOver = snapshot.child("time_over").getValue(String::class.java).toString()
                    if(timeOver != username && timeOver.length > 0) {
                        val intent = Intent(this@FiveGameActivity, Winner1Activity::class.java)
                        intent.putExtra("point", "")
                        intent.putExtra("username", username)
                        intent.putExtra("winner", username)
                        startActivity(intent)
                        finish()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Veri okuma hatası oluştuğunda burası çağrılır.
            }
        })

        // oyunu biri kazandığında
        db.child("games").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val user_1 = snapshot.child("user_1").getValue(String::class.java).toString()
                    val user_2 = snapshot.child("user_2").getValue(String::class.java).toString()
                    if (username == user_1 || username == user_2) {
                        val winner = snapshot.child("winner").value.toString()
                        if (winner == username) {
                            val point = 40 + (timeLeft /1000)
                            val p = "Puanınız: $point"
                            val intent = Intent(this@FiveGameActivity, Winner1Activity::class.java)
                            intent.putExtra("username", username)
                            intent.putExtra("winner", username)
                            intent.putExtra("point", p)
                            startActivity(intent)
                            finish()
                        } else if (winner.length > 0 && winner != username){
                            val point = 15 + (timeLeft /1000)
                            val p = "Puanınız: $point"
                            val intent = Intent(this@FiveGameActivity, Winner1Activity::class.java)
                            intent.putExtra("username", username)
                            intent.putExtra("winner", winner)
                            intent.putExtra("point", p)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        binding.btnRakip5.setOnClickListener{
            val intent = Intent(this, RivalScreenActivityFive::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        startTimer()
        setEditTexts(0)
    }
    private fun setEditTexts(hak: Int){
        var textList1 = listOf<EditText>(
            binding.et511, binding.et512, binding.et513, binding.et514, binding.et515
        )
        var textList2 = listOf<EditText>(
            binding.et521, binding.et522, binding.et523, binding.et524, binding.et525
        )
        var textList3 = listOf<EditText>(
            binding.et531, binding.et532, binding.et533, binding.et534, binding.et535
        )
        var textList4 = listOf<EditText>(
            binding.et541, binding.et542, binding.et543, binding.et544, binding.et545
        )
        var textList5 = listOf<EditText>(
            binding.et551, binding.et552, binding.et553, binding.et554, binding.et555
        )

        if (hak == 0) {
            editOff(textList2)
            editOff(textList3)
            editOff(textList4)
            editOff(textList5)
            word(textList1, hak)
        }else if (hak== 1) {
            editOff(textList1)
            editOff(textList3)
            editOff(textList4)
            editOff(textList5)
            word(textList2, hak)
        }else if (hak== 2) {
            editOff(textList1)
            editOff(textList2)
            editOff(textList4)
            editOff(textList5)
            word(textList3, hak)
        }else if (hak == 3) {
            editOff(textList1)
            editOff(textList2)
            editOff(textList3)
            editOff(textList5)
            word(textList4, hak)
        }else if (hak == 4) {
            editOff(textList1)
            editOff(textList2)
            editOff(textList3)
            editOff(textList4)
            word(textList5, hak)
        }
    }

    private fun word(textList : List<EditText>, hak : Int) {
        val username = intent.getStringExtra("username")
        textList[textList.size-1].setOnEditorActionListener{ v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                resetTimer()
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
                                val gameId = snapshot.key.toString()
                                val trialsRef = db.child("games").child(gameId).child("user_1_trials")
                                trialsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            val trialsList = ArrayList<String>()
                                            for (childSnapshot in dataSnapshot.children) {
                                                val trial = childSnapshot.getValue(String::class.java)
                                                if (trial != null) {
                                                    trialsList.add(trial)
                                                }
                                            }
                                            if (word != null) {
                                                trialsList.add(word)
                                                trialsRef.setValue(trialsList)
                                            }
                                        } else {
                                            val newTrialsList = arrayListOf(word)
                                            trialsRef.setValue(newTrialsList)
                                        }
                                    }
                                    override fun onCancelled(databaseError: DatabaseError) {
                                        println("loadPost:onCancelled ${databaseError.toException()}")
                                    }
                                })
                            }else {
                                question = user_1_word

                                val gameId = snapshot.key.toString()
                                val trialsRef = db.child("games").child(gameId).child("user_2_trials")
                                trialsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            val trialsList = ArrayList<String>()
                                            for (childSnapshot in dataSnapshot.children) {
                                                val trial = childSnapshot.getValue(String::class.java)
                                                if (trial != null) {
                                                    trialsList.add(trial)
                                                }
                                            }
                                            if (word != null) {
                                                trialsList.add(word)
                                                trialsRef.setValue(trialsList)
                                            }
                                        } else {
                                            val newTrialsList = arrayListOf(word)
                                            trialsRef.setValue(newTrialsList)
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        println("loadPost:onCancelled ${databaseError.toException()}")
                                    }
                                })
                            }
                            for (editText in textList) {
                                word += editText.text.toString()
                            }
                            GlobalScope.launch(Dispatchers.IO) {
                                val url = "https://sozluk.gov.tr/gts_id?id=" + word
                                val bodyText = URL(url).readText()
                                withContext(Dispatchers.Main) {
                                    if (bodyText == """{"error":"Sonuç bulunamadı"}""") {
                                        Toast.makeText(this@FiveGameActivity, "$word geçerli bir kelime değil!!!", Toast.LENGTH_SHORT).show()
                                        for (editText in textList) {
                                            editText.text = null
                                        }
                                    } else {
                                        if (word == question) {
                                            for (editText in textList) {
                                                editText.background.setColorFilter(ContextCompat.getColor(this@FiveGameActivity, R.color.green), PorterDuff.Mode.SRC_IN)
                                            }
                                        }else {
                                            if (word == question) {
                                                for (editText in textList) {
                                                    editText.background.setColorFilter(ContextCompat.getColor(this@FiveGameActivity, R.color.green), PorterDuff.Mode.SRC_IN)
                                                    db.child("games").addListenerForSingleValueEvent(object : ValueEventListener {
                                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                            for (snapshot in dataSnapshot.children) {
                                                                val user_1 = snapshot.child("user_1").value.toString()
                                                                val user_2 = snapshot.child("user_2").value.toString()

                                                                if(username == user_1 || username == user_2) {
                                                                    snapshot.ref.child("winner").setValue(username)
                                                                }
                                                            }
                                                        }

                                                        override fun onCancelled(error: DatabaseError) {
                                                            TODO("Not yet implemented")
                                                        }
                                                    })
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
                                                                this@FiveGameActivity,
                                                                R.color.green
                                                            ), PorterDuff.Mode.SRC_IN
                                                        )
                                                    } else if (question.contains((textList[i].text.toString())) && !indices.contains(i)) {
                                                        textList[i].background.setColorFilter(
                                                            ContextCompat.getColor(
                                                                this@FiveGameActivity,
                                                                R.color.yellow
                                                            ), PorterDuff.Mode.SRC_IN
                                                        )
                                                    } else {
                                                        textList[i].background.setColorFilter(
                                                            ContextCompat.getColor(
                                                                this@FiveGameActivity,
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
    private fun editOff(textList: List<EditText>) {
        for (i in 0 until textList.size) {
            textList[i].setEnabled(false)
        }
    }
}