package com.example.chatapp.Activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.chatapp.Dashboard
import com.example.chatapp.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {

    private var mUser:FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mUser = FirebaseAuth.getInstance()
    }


    fun LoginButton(view: View) {

        if (!TextUtils.isEmpty(passwordET_Login.text.toString()) &&
            !TextUtils.isEmpty(emailET_Login.text.toString())) {

            val progressBar = ProgressDialog(this)
            progressBar.setMessage("LoggingIn...")
            progressBar.show()


            mUser!!.signInWithEmailAndPassword(emailET_Login.text.toString(),passwordET_Login.text.toString()).addOnCompleteListener{
                task ->
                if(task.isSuccessful){
                    progressBar.dismiss()
                    Toast.makeText(this,"Login Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,Dashboard::class.java))
                    finish()
                }
                else{
                    progressBar.dismiss()
                    val snackbar:Snackbar = Snackbar.make(view,"user dose not exist",Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }
            }
        }
        else{
            Toast.makeText(this,"plz fill all the fields", Toast.LENGTH_SHORT).show()
        }

    }

    fun gotoRegisterActivity(view: View) {
        startActivity(Intent(this,Register::class.java))
        finish()
    }
}
