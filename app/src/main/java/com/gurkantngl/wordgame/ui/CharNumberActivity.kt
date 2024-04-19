package com.gurkantngl.wordgame.ui

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gurkantngl.wordgame.R
import com.gurkantngl.wordgame.databinding.ActivityCharNumberBinding

class CharNumberActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCharNumberBinding
    private val db = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCharNumberBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        initUI()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }



    private fun initUI () {
        val username = intent.getStringExtra("username")
        val mod = intent.getIntExtra("mod", 0)
       binding.btnNumber4.setOnClickListener {
           Toast.makeText(this, "mod: $mod, kelime: 4", Toast.LENGTH_SHORT).show()
           join_room(4)

           val intent = Intent(this, PlayersInRoomActivity::class.java)
           intent.putExtra("mod", mod)
           intent.putExtra("username", username)
           intent.putExtra("roomNumber", 4)
           startActivity(intent)

       }
        binding.btnNumber5.setOnClickListener {
            Toast.makeText(this, "mod: $mod, kelime: 5", Toast.LENGTH_SHORT).show()
            join_room(5)
            val intent = Intent(this, PlayersInRoomActivity::class.java)
            intent.putExtra("mod", mod)
            intent.putExtra("username", username)
            intent.putExtra("roomNumber", 5)
            startActivity(intent)
       }
        binding.btnNumber6.setOnClickListener {
            Toast.makeText(this, "mod: $mod, kelime: 6", Toast.LENGTH_SHORT).show()
            join_room(6)
            val intent = Intent(this, PlayersInRoomActivity::class.java)
            intent.putExtra("mod", mod)
            intent.putExtra("username", username)
            intent.putExtra("roomNumber", 6)
            startActivity(intent)
        }
        binding.btnNumber7.setOnClickListener {
            Toast.makeText(this, "mod: $mod, kelime: 7", Toast.LENGTH_SHORT).show()
            join_room(7)
            val intent = Intent(this, ChooseWordsSevenActivity::class.java)
            intent.putExtra("mod", mod)
            intent.putExtra("username", username)
            intent.putExtra("roomNumber", 7)
            startActivity(intent)
        }
    }

    private fun join_room(roomNumber : Int) {
        val username = intent.getStringExtra("username")
        val mod = intent.getIntExtra("mod", 0)

        var roomList = listOf(
            "mod_1_rooms",
            "mod_2_rooms",
        )

        val users = ArrayList<String>()
        db.child(roomList[mod-1]).child(roomNumber.toString()).child(username!!).setValue(hashMapOf("is_playing" to false))
    }

}