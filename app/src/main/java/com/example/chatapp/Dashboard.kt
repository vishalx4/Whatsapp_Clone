package com.example.chatapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.i
import android.view.Menu
import android.view.MenuItem
import android.widget.Adapter
import com.example.chatapp.Activity.Register
import com.example.chatapp.Adapters.Adapter1
import com.example.chatapp.Model.User1
import com.example.chatapp.view.status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_dashboard.*

class Dashboard : AppCompatActivity() {

    private var mUser:FirebaseAuth? = null
    private var firestoreInstance:FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        setSupportActionBar(toolbar_dashboard)
        supportActionBar!!.title = ""

        view_pager.adapter =Adapter1(supportFragmentManager)
        tabs.setupWithViewPager(view_pager)
        tabs.setTabTextColors(Color.WHITE,Color.BLACK)


        firestoreInstance = FirebaseFirestore.getInstance()
        mUser =FirebaseAuth.getInstance()

        var userId = mUser!!.currentUser!!.uid

        var userRef = firestoreInstance?.collection("UserData")?.document(userId!!)

        // get a username and image from database
        userRef!!.addSnapshotListener{snapshot,e ->
            if(e!=null){
                Log.d("Error","Listen Failed "+e)
            }
            if(snapshot!=null && snapshot.exists()){

                val user = snapshot.toObject(User1::class.java)
                username.text = user!!.userName

                Picasso.get()
                    .load(user.profile)
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .into(user_image)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        mUser!!.signOut()
        startActivity(Intent(this,Register::class.java))
        finish()
        return true

    }

    override fun onStart() {
        super.onStart()
        status = true
    }

    override fun onPause() {
        super.onPause()
        status = false
        Log.d("value =>>",""+status)
    }

    override fun onDestroy() {
        super.onDestroy()
        status = false
        Log.d("value =>>",""+status)
    }
}
