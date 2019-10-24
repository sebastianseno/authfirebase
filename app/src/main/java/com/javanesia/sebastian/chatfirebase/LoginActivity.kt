package com.javanesia.sebastian.chatfirebase

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_login.*
import java.time.Instant

class LoginActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login.setOnClickListener {
           PerformLogin()

        }

        tv_signup_login.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }

    }

    private fun PerformLogin () {

        val email = tv_username_login.text.toString()
        val password = tv_password_login.text.toString()

        if (email.isEmpty() || password.isEmpty() ) {
            Toast.makeText(this, "Please enter valid user name and password" , Toast.LENGTH_SHORT).show()
            return

        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful)
                    Log.d("LoginActivity" , "Sukses Login")

                    val intent = Intent(this, MassagesList::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
            }
            .addOnFailureListener(this) {
                Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()



            }


    }

}