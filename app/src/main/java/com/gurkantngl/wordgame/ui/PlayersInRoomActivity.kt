package com.gurkantngl.wordgame.ui

import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gurkantngl.wordgame.R
import com.gurkantngl.wordgame.databinding.ActivityPlayersInRoomBinding

class PlayersInRoomActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPlayersInRoomBinding
    private val db = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPlayersInRoomBinding.inflate(layoutInflater)

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
        val room_number = intent.getIntExtra("roomNumber", 0)
        var roomList = listOf(
            "mod_1_rooms",
            "mod_2_rooms",
        )

        val users = ArrayList<String>()

        db.child(roomList[mod-1]).child(room_number.toString()).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.key
                    if (user != username) {
                        users.add(user!!)
                    }
                }
                val usersString = users.joinToString(", ")

                Toast.makeText(this@PlayersInRoomActivity, usersString, Toast.LENGTH_SHORT).show()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        db.child(roomList[mod-1]).child(room_number.toString()).child(username!!).setValue(hashMapOf("is_playing" to false))

    }

    private fun leave_room() {
        val mod = intent.getIntExtra("mod", 0)
        val username = intent.getStringExtra("username")
        val room_number = intent.getIntExtra("roomNumber", 0)
        val roomList = listOf(
            "mod_1_rooms",
            "mod_2_rooms",
        )

        db.child(roomList[mod-1]).child(room_number.toString()).child(username!!).removeValue()
    }

    private fun initUI () {
        val users = ArrayList<String>()

        val username = intent.getStringExtra("username")
        val mod = intent.getIntExtra("mod", 0)
        val roomNumber = intent.getIntExtra("roomNumber", 0)
        binding.txtModInfo.text = "Mod $mod - $roomNumber harf"
        var roomList = listOf(
            "mod_1_rooms",
            "mod_2_rooms",
        )

        db.child(roomList[mod-1]).child(roomNumber.toString()).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                users.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.key
                    val is_playing = snapshot.child("is_playing").getValue(Boolean::class.java) ?: false
                    var play = ""
                    if (is_playing == true) {
                        play = " - oyunda"
                    }else {
                        play = " - aktif"
                    }
                    if (user != username) {
                        users.add(user!! + play)
                    }
                }
                val usersString = users.joinToString(", ")
                Toast.makeText(this@PlayersInRoomActivity, usersString, Toast.LENGTH_SHORT).show()

                if (!users.isEmpty()) {
                    binding.txtEmpty.visibility = View.GONE
                }else {
                    binding.txtEmpty.visibility = View.VISIBLE
                }

                val layoutManager = LinearLayoutManager(this@PlayersInRoomActivity)
                binding.recyclerView.layoutManager = layoutManager

                val adapter = RecyclerAdapter(users, username!!)
                binding.recyclerView.adapter = adapter

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        db.child("requests").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val requestTo = dataSnapshot.child("request_to").getValue(String::class.java)
                val requestFrom = dataSnapshot.child("request_from").getValue(String::class.java)
                val requestId = dataSnapshot.key
                if (requestTo == username) {
                    Toast.makeText(this@PlayersInRoomActivity, "Oynama isteği var", Toast.LENGTH_SHORT).show()

                    lateinit var alertDialog: AlertDialog // alertDialog'ı tanımla

                    // CountDownTimer'ı tanımla
                    val countDownTimerobject = object : CountDownTimer(10000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).text = "Kabul Et (${millisUntilFinished / 1000})"
                        }

                        override fun onFinish() {
                            alertDialog.dismiss()
                            db.child("requests").child(requestId.toString()).child("request_status").setValue(2)
                        }
                    }

                    val builder = AlertDialog.Builder(this@PlayersInRoomActivity)
                    builder.setTitle("Oynama İsteği")
                    builder.setMessage("${dataSnapshot.child("request_from").getValue(String::class.java)} sizi oyununa davet ediyor.\nKabul ediyor musunuz?")
                    builder.setPositiveButton("Kabul Et") { dialog, which ->
                        db.child("requests").child(requestId.toString()).child("request_status").setValue(1)
                        countDownTimerobject.cancel()
                    }
                    builder.setNegativeButton("Reddet") { dialog, which ->
                        db.child("requests").child(requestId.toString()).child("request_status").setValue(2)
                        countDownTimerobject.cancel()
                    }
                    alertDialog = builder.create() // alertDialog'ı oluştur
                    alertDialog.show()

                    // CountDownTimer'ı başlat
                    countDownTimerobject.start()
                }else if (requestFrom == username) {
                    Toast.makeText(this@PlayersInRoomActivity, "Oynama isteği gönderildi", Toast.LENGTH_SHORT).show()

                    lateinit var alertDialog: AlertDialog // alertDialog'ı tanımla
                    var builder = AlertDialog.Builder(this@PlayersInRoomActivity)
                    builder.setTitle("Oynama İsteği")
                    // CountDownTimer'ı tanımla
                    val countDownTimerobject = object : CountDownTimer(10000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            alertDialog.setMessage("${dataSnapshot.child("request_to").getValue(String::class.java)} kişisine istek gönderildi.\n Yanıt Bekleniyor...(${millisUntilFinished / 1000})")
                        }

                        override fun onFinish() {
                            alertDialog.dismiss()
                            db.child("requests").child(requestId.toString()).child("request_status").setValue(2)
                        }
                    }
                    

                    builder.setMessage("${dataSnapshot.child("request_to").getValue(String::class.java)} kişisine istek gönderildi.\nYanıt Bekleniyor...")
                    alertDialog = builder.create() // alertDialog'ı oluştur
                    alertDialog.show()

                    // CountDownTimer'ı başlat
                    countDownTimerobject.start()
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // Veri değiştiğinde burası çağrılır.
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                // Veri silindiğinde burası çağrılır.
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // Veri taşındığında burası çağrılır.
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Veri okuma hatası oluştuğunda burası çağrılır.
                // databaseError, hatayı açıklar.
            }
        })

    }
}