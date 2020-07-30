package com.example.chatapp.Activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.chatapp.Dashboard
import com.example.chatapp.Model.User1
import com.example.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {

    private var mUser:FirebaseAuth? = null
    private var firestoreInstance:FirebaseFirestore? = null
    private var databaseRef : DatabaseReference? = null
    var userId:String? = null
    var userEmail:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mUser = FirebaseAuth.getInstance()
        firestoreInstance = FirebaseFirestore.getInstance()
    }

    fun RegisterAccountBtn(view: View) {
        if(passwordET.text.toString().length < 6){
            passwordET.error = "password must be greater than 6 characters"
        }
        else{
            if (TextUtils.isEmpty(passwordET.text.toString())&&
                TextUtils.isEmpty(usernameET.text.toString())&&
                TextUtils.isEmpty(emailET.text.toString())) {
                Toast.makeText(this,"plz fill all the fields",Toast.LENGTH_SHORT).show()
            }
            else{

                val progressBar = ProgressDialog(this)
                progressBar.setMessage("RegisteringUser...")
                progressBar.show()


                mUser!!.createUserWithEmailAndPassword(emailET.text.toString(),passwordET.text.toString()).addOnCompleteListener{
                    task ->
                    if(task.isSuccessful){
                        Toast.makeText(this,"Account created Successfully",Toast.LENGTH_SHORT).show()
                        var user =FirebaseAuth.getInstance().currentUser

                        userId = user!!.uid
                        userEmail = user.email

                        var mUser = User1(userId.toString(),userEmail.toString(),usernameET.text.toString(),"Recently joined","not set","not set","not set")
                        firestoreInstance?.collection("UserData")?.document(userId!!)?.set(mUser)!!.addOnCompleteListener{
                            task ->
                            if(task.isSuccessful){
                                progressBar.dismiss()
                                startActivity(Intent(this,Dashboard::class.java))
                                finish()
                            }else{
                                Toast.makeText(this,"Failed to save data",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    else{
                        Toast.makeText(this,"Failed to create Account",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    fun gotoLoginActivity(view: View) {
        startActivity(Intent(this,Login::class.java))
    }


    override fun onStart() {
        super.onStart()

       if(mUser?.currentUser!=null){
            startActivity(Intent(this,Dashboard::class.java))
            finish()
        }

    }

}
