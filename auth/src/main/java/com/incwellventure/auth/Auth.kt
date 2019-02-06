package com.incwellventure.auth

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp

class Auth : Application() {

    companion object {
        var webClientKey: String = ""
        fun init(context: Context, webClientKey: String) {
            FirebaseApp.initializeApp(context)
            this.webClientKey = webClientKey
        }
    }
}