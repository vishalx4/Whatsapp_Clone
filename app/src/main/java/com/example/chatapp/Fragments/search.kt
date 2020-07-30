package com.example.chatapp.Fragments


import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.Adapters.AdapterSearch
import com.example.chatapp.Model.User1

import com.example.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.auth.User
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*

/**
 * A simple [Fragment] subclass.
 */
class search : Fragment() {


    private var firebaseFirestore:FirebaseFirestore? = null

    private var searchAdapter: AdapterSearch? = null
    private var userList: List<User1>? = null
    private var recyclerView: RecyclerView? = null
    private var searchEditText: EditText? = null
    private var layoutManager:LinearLayoutManager? = null
    private var mUser:FirebaseUser? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view:View =  inflater.inflate(R.layout.fragment_search, container, false)

        mUser = FirebaseAuth.getInstance().currentUser

        userList = ArrayList()
        recyclerView = view.recycler_view_search
        layoutManager = LinearLayoutManager(context)
        retriveAllUsers()

        firebaseFirestore = FirebaseFirestore.getInstance()

        searchEditText = view.findViewById(R.id.search_EditText)

        searchEditText!!.addTextChangedListener(object : TextWatcher{

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUsers(s.toString().toLowerCase())
            }

        })




        return view
    }

    private fun retriveAllUsers() {
        var userId = FirebaseAuth.getInstance().currentUser!!.uid
        var userRef = FirebaseFirestore.getInstance().collection("UserData")

        userRef.addSnapshotListener{ snapshot,e ->
            if(e!=null){
                Log.d("Error","Listen Failed "+e)
            }
            for(i in snapshot!!.getDocumentChanges()){
                when( i.type){
                    DocumentChange.Type.ADDED ->{
                        var listDoc:List<DocumentSnapshot> = snapshot.documents
                        (userList as ArrayList<User1>).clear()

                        for(j in listDoc) {
                            var user = j.toObject(User1::class.java)
                            if( mUser!!.uid  != user!!.uid ){
                                (userList as ArrayList<User1>).add(user)
                            }
                        }
                        searchAdapter = AdapterSearch(context!!, userList!!)
                        recyclerView!!.layoutManager = layoutManager
                        recyclerView!!.adapter = searchAdapter
                    }
                    DocumentChange.Type.MODIFIED -> Log.d("tag =>>>","modified")
                    DocumentChange.Type.REMOVED -> Log.d("tag =>>>","removed")
                }
            }
        }

    }


    private fun searchForUsers(str:String?){

        firebaseFirestore!!.collection("UserData").orderBy("userName")
            .startAt(str).endAt("$str\uf8ff").get().addOnCompleteListener{
                if(it.isSuccessful){
                    (userList as ArrayList<User1>).clear()

                    if(TextUtils.isEmpty(searchEditText!!.text.toString())){
                        retriveAllUsers()
                    }
                    else{
                        userList = it.result!!.toObjects(User1::class.java)
                        searchAdapter = AdapterSearch(context!!, userList!!)
                        recyclerView!!.layoutManager = layoutManager
                        recyclerView!!.adapter = searchAdapter
                        searchAdapter!!.notifyDataSetChanged()
                    }
                }
                else{
                    Log.d("search error ==>> ","Error: ${it.exception!!.message}")
                }
            }
    }


}



