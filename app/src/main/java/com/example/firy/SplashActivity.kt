package com.example.firy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if(FirebaseAuth.getInstance().currentUser == null){
            startActivity<SignInActivity>()
        }else{
            startActivity<MainActivity>()
        }
        finish()
    }
}