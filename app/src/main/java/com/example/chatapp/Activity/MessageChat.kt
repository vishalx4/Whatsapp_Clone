package com.example.chatapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.Model.User1
import com.example.chatapp.Model.UserChat
import com.example.chatapp.R
import com.example.chatapp.view.ChatFromItem
import com.example.chatapp.view.ChatToItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_message_chat.*

class MessageChat : AppCompatActivity() {

    var user2Id: String? = null
    var userId: String? = null
    var mUser: FirebaseUser? = null
    var mUserSender: User1? = null
    var mUserReceiver: User1? = null
    var adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        recyclerView_chat.adapter = adapter
        recyclerView_chat.layoutManager = LinearLayoutManager(this)

        mUser = FirebaseAuth.getInstance().currentUser
        var intent = getIntent()
        userId = mUser!!.uid
        user2Id = intent.getStringExtra("user2Id")

        UserSender_getInfo()
        UserReceiver_getInfo()
        ListenForMessage()
    }

    private fun UserSender_getInfo() {
        FirebaseFirestore.getInstance().collection("UserData").document(userId.toString())
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d("error", "Error $e")
                }
                if (snapshot != null && snapshot.exists()) {
                    mUserSender = snapshot.toObject(User1::class.java)
                    Log.d("user1 =>> ",mUserSender?.userEmail)
                }
            }
    }

    private fun UserReceiver_getInfo() {
        FirebaseFirestore.getInstance().collection("UserData").document(user2Id.toString())
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d("error", "Error $e")
                }
                if (snapshot != null && snapshot.exists()) {
                    mUserReceiver = snapshot.toObject(User1::class.java)
                    Log.d("user2 data =>>", "hello there")
                    Picasso.get()
                        .load(mUserReceiver?.profile)
                        .placeholder(R.drawable.ic_user)
                        .into(user_image_messageChat)
                    username_messageChat.text = mUserReceiver?.userName
                }
            }
    }

    private fun ListenForMessage() {

        var fromId = userId
        var toId = user2Id
        val ref = FirebaseDatabase.getInstance()
            .getReference("/user-message/$fromId/$toId")

        if (fromId == null) return
        
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val chat = snapshot.getValue(UserChat::class.java)
                if(chat!=null){


                    Log.d("id ==>>>> ",""+chat.senderId+" "+FirebaseAuth.getInstance().uid)

                    if(chat.senderId == FirebaseAuth.getInstance().uid){
                        Log.d("message from chat =>> ", ""+chat.message)
                        adapter.add(ChatFromItem(chat.message,mUserSender))
                    }
                    else{
                        adapter.add(ChatToItem(chat.message,mUserReceiver))
                    }
                }

                recyclerView_chat.scrollToPosition(adapter.itemCount-1)
            }
            override fun onCancelled(error: DatabaseError) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
        })
    }


    fun sendMessage(view: View) {

        if(TextUtils.isEmpty(textMessage_messageChat.text.toString())){
            Toast.makeText(this,"message is empty",Toast.LENGTH_SHORT).show()
        }
        else{

            var fromId = userId
            var toId = user2Id
            if (fromId == null) return

            val FromReference = FirebaseDatabase.getInstance()
                .getReference("/user-message/$fromId/$toId").push()

            val ToReference = FirebaseDatabase.getInstance()
                .getReference("/user-message/$toId/$fromId").push()


            val chat = UserChat(fromId,toId,FromReference.key,textMessage_messageChat.text.toString())

            FromReference.setValue(chat)
                .addOnSuccessListener {
                    Log.d("message send ==>>> ", "Saved our chat message: ${FromReference.key}")
                    textMessage_messageChat.text.clear()
                    recyclerView_chat.scrollToPosition(adapter.itemCount-1)
                }
            ToReference.setValue(chat)

            val fromLatestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
            fromLatestMessageRef.setValue(chat)

            val toLatestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
            toLatestMessageRef.setValue(chat)


        }
    }




}



