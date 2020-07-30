package com.example.chatapp.view

import android.util.Log
import com.example.chatapp.Model.User1
import com.example.chatapp.Model.UserChat
import com.example.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_message_row.view.*
import kotlinx.android.synthetic.main.user_from_message.view.*
import kotlinx.android.synthetic.main.user_to_message.view.*


var status:Boolean? = null

class ChatFromItem(val text :String?, val user: User1?) : Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.user_from_message
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_sender_ET.text = text
        val uri = user!!.profile
            Picasso.get()
                .load(uri)
                .placeholder(R.drawable.ic_user)
                .into(viewHolder.itemView.senderImage)
    }

}

class ChatToItem(val text :String?, val user: User1?) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.user_to_message
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_receiver_ET.text = text
        val uri = user!!.profile
        Picasso.get()
            .load(uri)
            .placeholder(R.drawable.ic_user)
            .into(viewHolder.itemView.receiverImage)

    }

}


class UserchatsPart(val userInfo: UserChat) : Item<GroupieViewHolder>() {

    var chatPartnerUser :User1? = null


    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.latestMessage_chatFragment.text = userInfo.message

        var chatPartnerID:String?
        if(userInfo.senderId == FirebaseAuth.getInstance().uid){
            chatPartnerID = userInfo.receiverId
        }
        else{
            chatPartnerID = userInfo.senderId
        }

        val ref = FirebaseFirestore.getInstance()
            .collection("UserData")
            .document(chatPartnerID!!)

        ref.addSnapshotListener{snapshot,e ->
            if(e!=null){
                Log.d("Error","Listen Failed "+e)
            }
            if(snapshot!=null && snapshot.exists()){
                chatPartnerUser = snapshot.toObject(User1::class.java)
                viewHolder.itemView.username_chatFragment.text = chatPartnerUser?.userName
                var profileImageUrl = viewHolder.itemView.profileImage_chatFragment
                Picasso.get()
                    .load(chatPartnerUser?.profile)
                    .placeholder(R.drawable.ic_user)
                    .into(profileImageUrl)
            }
        }
    }

}

