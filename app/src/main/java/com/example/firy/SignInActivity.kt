package com.example.firy

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.example.firy.databinding.ActivitySignInBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.*
import org.jetbrains.anko.design.longSnackbar

class SignInActivity : AppCompatActivity() {

    private val SIGNTENT_CODE = 1
    private lateinit var binding : ActivitySignInBinding
    private val signInProviders = listOf(
        AuthUI.IdpConfig.EmailBuilder()
            .setAllowNewAccounts(true)
            .setRequireName(true)
            .build()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        animateLayout()

        binding.loginButton.setOnClickListener {
            val intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(signInProviders)
                .setLogo(R.drawable.ic_splash)
                .build()
            startActivityForResult(intent,SIGNTENT_CODE)
        }
    }

    private fun animateLayout(){
        val colorFrom = resources.getColor(R.color.colorAccent)
        val colorTo = resources.getColor(R.color.colorPrimaryDark)
        val colorLast = resources.getColor(R.color.colorPrimary)
        val colorAnimation =
            ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo, colorLast, colorFrom)
        colorAnimation.duration = 2500 // milliseconds
        colorAnimation.repeatCount = ValueAnimator.INFINITE
        colorAnimation.repeatMode = ValueAnimator.REVERSE

        colorAnimation.addUpdateListener {
                animator ->
        binding.signInActivityLayout.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SIGNTENT_CODE){
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK){
                val progressDialog = indeterminateProgressDialog("Setting up your account")
                startActivity(intentFor<MainActivity>().newTask().clearTask())
                progressDialog.dismiss()
            } else if(resultCode == Activity.RESULT_CANCELED){
                if (response == null) return

                when(response.error?.errorCode){
                    ErrorCodes.NO_NETWORK ->
                        Snackbar.make(binding.signInActivityLayout, "NO NETWORK", Snackbar.LENGTH_LONG)
                    ErrorCodes.UNKNOWN_ERROR ->
                        Snackbar.make(binding.signInActivityLayout, "UNKNOWN ERROR", Snackbar.LENGTH_LONG)
                }
            }
        }
    }
}