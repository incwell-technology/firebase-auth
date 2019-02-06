package com.incwellventure.auth

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp

class Auth : Application() {
    companion object {
        fun init(context: Context) {
            FirebaseApp.initializeApp(context)
        }
    }
}