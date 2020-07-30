package com.example.chatapp.Fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.Activity.MessageChat
import com.example.chatapp.Model.User1
import com.example.chatapp.Model.UserChat
import com.example.chatapp.R
import com.example.chatapp.view.UserchatsPart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.auth.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_chats.*


class Chats : Fragment() {

    private var layoutManager:LinearLayoutManager? = null
    lateinit var recyclerView: RecyclerView
    val adapter = GroupAdapter<GroupieViewHolder>()
    val latestMessagesMap = HashMap<String, UserChat>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view =  inflater.inflate(R.layout.fragment_chats, container, false)

        layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recyclerView_chatFragment)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))


        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(context,MessageChat::class.java)
            val row = item as UserchatsPart
            intent.putExtra("user2Id",row.chatPartnerUser!!.uid)
            startActivity(intent)
        }


        listenForLatestMessages()
        return view
    }

    private fun refreshRecyclerViewMessages(){

        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(UserchatsPart(it))
        }

    }


    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")

        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(UserChat::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(UserChat::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onCancelled(error: DatabaseError) {

            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
        })
    }
}
