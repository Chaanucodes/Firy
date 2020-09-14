package com.example.firy.service

import android.util.Log
import com.example.firy.util.FirestoreUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.lang.NullPointerException

class MyFirebaseMessagingService : FirebaseMessagingService(){
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        if(FirebaseAuth.getInstance().currentUser!= null)
            addTokenToFirestore(p0)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if(remoteMessage.notification!=null){
            //TODO: Show notification
            Log.d("FCM", "${remoteMessage.data}")
        }
    }

    companion object{
        fun addTokenToFirestore(newRegistrationToken : String?){
            if(newRegistrationToken == null) throw NullPointerException(" Token received is null")

            FirestoreUtil.getFCMRegistrationTokens { tokens->
                if (tokens.contains(newRegistrationToken))
                    return@getFCMRegistrationTokens

                tokens.add(newRegistrationToken)
                FirestoreUtil.setFCMRegistrationTokens(tokens)
            }
        }
    }
}