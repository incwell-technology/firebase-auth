package com.incwellventure.firebaseapp

import android.app.Application
import com.incwellventure.auth.Auth

class AuthApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Auth.init(this,getString(R.string.default_web_client_id))
    }
}