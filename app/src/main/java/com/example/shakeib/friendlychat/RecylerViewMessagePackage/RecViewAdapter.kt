package com.example.shakeib.friendlychat.RecylerViewMessagePackage

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.shakeib.friendlychat.R

class RecViewAdapter(var data:ArrayList<FriendlyMessage>) : RecyclerView.Adapter<RecViewAdapter.RevViewHolder>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RevViewHolder = RevViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.message_layout,p0,false))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(p0: RevViewHolder, p1: Int) {
        p0.authorText.text = data[p1].name
        if(data[p1].text !=null){
            p0.messageText.text = data[p1].text
        }else{
            p0.messageText.visibility = View.GONE
        }
        if(p0.photoImageView !=null){
            p0.photoImageView.visibility = View.VISIBLE
            Glide.with(p0.photoImageView.context).load(data[p1].photoUrl).into(p0.photoImageView)
        }else{
            p0.photoImageView.visibility = View.GONE
        }
    }

    inner class RevViewHolder(view:View): RecyclerView.ViewHolder(view){
        var authorText = view.findViewById<TextView>(R.id.nameTextView)
        var messageText = view.findViewById<TextView>(R.id.messageTextView)
        var photoImageView = view.findViewById<ImageView>(R.id.photoImageView)
    }

}