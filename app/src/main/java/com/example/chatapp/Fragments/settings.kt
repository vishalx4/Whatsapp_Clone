package com.example.chatapp.Fragments


import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.example.chatapp.Model.User1

import com.example.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.popup.view.*
import kotlinx.android.synthetic.main.social_links.view.*

/**
 * A simple [Fragment] subclass.
 */
class settings : Fragment() {

    var RequestCode:Int = 303
    private var imageUri: Uri? = null


    private var mUser:FirebaseUser? = null
    private var userRef:DocumentReference? = null
    private var firestoreInstance:FirebaseFirestore? = null
    private var storageRef:StorageReference? = null

    private var dialogBuilder:AlertDialog.Builder? = null
    private var dialog:AlertDialog? = null
    private var mFacebook:String? = null
    private var mInstagram:String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view:View =  inflater.inflate(R.layout.fragment_settings, container, false)

        mUser = FirebaseAuth.getInstance().currentUser
        firestoreInstance = FirebaseFirestore.getInstance()
        userRef = firestoreInstance!!.collection("UserData").document(mUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("UserProfileImages")

        userRef!!.addSnapshotListener{ snapshot , e ->

            if(e!=null){
                Log.d("Error","Listen Failed "+e)
            }
            if(snapshot!=null && snapshot.exists()){
                var user = snapshot.toObject(User1::class.java)

                userName_settings.text = user?.userName
                status_settings.text = user?.status

                Picasso.get()
                    .load(user!!.profile)
                    .placeholder(R.drawable.ic_user)
                    .into(image_settings)
            }
        }

        view.changeImage_settings.setOnClickListener{
            pickImage()
        }

        view.editStatus.setOnClickListener{
            editData("status")
        }

        view.editUsername.setOnClickListener{
            editData("userName")
        }

        view.facebook.setOnClickListener {
            socialLinks("facebook")
        }

        view.instagram.setOnClickListener {
            socialLinks("instagram")
        }
        view.facebook.setOnLongClickListener {
            userRef!!.addSnapshotListener{ snapshot,e ->
                if(e!=null){
                    Log.d("Error","Listen Failed "+e)
                }
                if(snapshot!=null && snapshot.exists()){
                    var user = snapshot.toObject(User1::class.java)
                    mFacebook = user!!.facebook
                    var brouserIntent:Intent = Intent(Intent.ACTION_VIEW, Uri.parse(mFacebook))
                    startActivity(brouserIntent)
                }
            }
            true
        }


        view.instagram.setOnLongClickListener {
            userRef!!.addSnapshotListener{ snapshot,e ->
                if(e!=null){
                    Log.d("Error","Listen Failed "+e)
                }
                if(snapshot!=null && snapshot.exists()){
                    var user = snapshot.toObject(User1::class.java)
                    mInstagram = user!!.instagram
                    var brouserIntent:Intent = Intent(Intent.ACTION_VIEW, Uri.parse(mInstagram))
                    startActivity(brouserIntent)
                }
            }
            true
        }


        return view
    }

    private fun socialLinks(temp:String?) {
        var view = layoutInflater.inflate(R.layout.social_links,null)
        var linkSocial = view.linksET
        dialogBuilder = AlertDialog.Builder(context!!).setView(view)
        dialog = dialogBuilder!!.create()
        dialog?.show()
        view.saveSocialLinksBtn.setOnClickListener {
            var link = linkSocial.text.toString()
            userRef?.update(mapOf(
                temp to link
            ))
            dialog!!.dismiss()
            var ft = fragmentManager!!.beginTransaction()
            if (Build.VERSION.SDK_INT >= 26) {
                ft.setReorderingAllowed(false)
            }
            ft.detach(this).attach(this).commit()
        }
    }


    private fun editData(temp:String?) {
        var view = layoutInflater.inflate(R.layout.popup, null)
        var status = view.statusEt
        dialogBuilder = AlertDialog.Builder(context!!).setView(view)
        dialog = dialogBuilder!!.create()
        dialog?.show()
        view.saveStatusBtn.setOnClickListener {
            var statusString = status.text.toString()
            userRef?.update(mapOf(
                temp to statusString
            ))
            dialog!!.dismiss()
            var ft = fragmentManager!!.beginTransaction()
            if (Build.VERSION.SDK_INT >= 26) {
                ft.setReorderingAllowed(false);
            }
            ft.detach(this).attach(this).commit()
        }

    }


    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RequestCode && resultCode == Activity.RESULT_OK) {
            imageUri =data!!.data
            uploadImage()
        }
    }

    private fun uploadImage() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("image uploading...")
        progressBar.show()
        if(imageUri!=null){
            val fileRef =storageRef!!.child(mUser!!.uid).child("profile.jpg")
            var uploadTask = fileRef.putFile(imageUri!!)
            val urlTask = uploadTask.continueWithTask{task ->
                if(!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                fileRef.downloadUrl
            }.addOnCompleteListener{
                task ->
                if(task.isSuccessful){
                    val downloadUrl = task.result
                    var url = downloadUrl.toString()
                    userRef?.update(mapOf(
                        "profile" to url
                    ))
                    progressBar.dismiss()
                }
            }

        }
    }

}
