package com.incwellventure.firebaseapp

import android.app.Application
import com.incwellventure.auth.Auth

class AuthApp : Application() {
    override fun onCreate() {
        Auth.init(this)
        super.onCreate()
    }
}