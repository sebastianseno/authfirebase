package com.javanesia.sebastian.chatfirebase

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        btn_register.setOnClickListener {
            perfomRegister()
        }

        iv_photo_register.setOnClickListener{
            Log.d("MainActivity" , "image pressed")

            val intent = Intent (Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent , 0)

        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int , data: Intent ?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {

            Log.d( "MainActivity" , "photo was selected")

            selectedPhotoUri = data.data
            //Show image asset to UI
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            circle_View_Regiter.setImageBitmap(bitmap)
            iv_photo_register.alpha = 0f


        }
    }

     private fun perfomRegister() {
        val password = tv_password_register.text.toString()
        val email = tv_email_register.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter valid email and password", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("MainActivity", "Email is = " + email)
        Log.d("MainActivity", "Your password = $password")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email , password )
            .addOnCompleteListener {
//                if (!it.isSuccessful)
//                    Toast.makeText(this, "Register Successfully", Toast.LENGTH_SHORT).show()
//                    Log.d("MainActivity", "Succsessfully registered! Your ID : ${it.result?.user?.uid}")
                uploadImageDatabasefirebase()
            }
            .addOnFailureListener{
                Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageDatabasefirebase() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("MainActivity" , "Sukses upload image : ${it.metadata?.path}")

                savedDataToFirebase(it.toString())

            ref.downloadUrl.addOnSuccessListener {
                it.toString()
                Log.d("MainActivity", "File Location : $it")
                }
            }
    }
    private fun savedDataToFirebase(profileImageUrl:String) {

        val email = tv_email_register.text.toString()
        val username = tv_username_register.text.toString()
        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, username , profileImageUrl, email)

        ref.setValue(user)
            .addOnCompleteListener() {
                if (!it.isSuccessful)
                Toast.makeText(this, "Registrasi Sukses", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener{
                
            }

    }

}
        class User(val uid: String = "",val username: String = "", val profileImageUrl: String="" , val email: String = "" ) {

    }