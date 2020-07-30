package com.example.chatapp.Adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.Activity.MessageChat
import com.example.chatapp.Model.User1
import com.example.chatapp.R
import com.example.chatapp.view.status
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.user_row.view.*

class AdapterSearch(private val context: Context, private val list:List<User1>)
    :RecyclerView.Adapter<AdapterSearch.ViewHolder>(){

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        var userNameTxt: TextView = itemView.userName_search
        var profileImageView: CircleImageView = itemView.userImage_search


        fun bindView(user:User1){
            userNameTxt.text = user.userName
            Picasso.get()
                .load(user.profile)
                .placeholder(R.drawable.ic_user)
                .into(profileImageView)

            Log.d("value ==>> ",""+status)

            itemView.setOnClickListener{
                Log.d("user Receiver =>> ","${user.userName}")
                var intent = Intent(context,MessageChat::class.java)
                intent.putExtra("user2Id",user.uid)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view:View = LayoutInflater.from(context).inflate(R.layout.user_row,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(list[position])
    }
}