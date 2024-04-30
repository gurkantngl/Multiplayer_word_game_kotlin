package com.gurkantngl.wordgame.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gurkantngl.wordgame.R

class RecyclerAdapter (val usersList : ArrayList<String>, val currentUser : String) :
    RecyclerView.Adapter<RecyclerAdapter.UsersVH>() {
    private val db = Firebase.database.reference
    class UsersVH (itemView : View) : RecyclerView.ViewHolder(itemView) {
        val recyclerViewTextView: TextView = itemView.findViewById(R.id.recyclerViewTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row, parent, false)
        return UsersVH(itemView)
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    override fun onBindViewHolder(holder: UsersVH, position: Int) {
        holder.recyclerViewTextView.text = usersList[position]
        holder.recyclerViewTextView.setOnClickListener{
            // 0 -> pending
            // 1 -> accepted
            // 2 -> rejected
            val user = usersList[position].substringBefore("-").trim()
            val requestMap = hashMapOf(
                "request_to" to user,
                "request_from" to currentUser,
                "request_status" to 0
            )
            db.child("requests").push().setValue(requestMap)
        }
    }

}