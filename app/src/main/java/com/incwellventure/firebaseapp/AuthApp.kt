package com.incwellventure.firebaseapp

import android.app.Application
import com.incwellventure.auth.FireBaseAuth

class AuthApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FireBaseAuth.init(this,getString(R.string.default_web_client_id))
    }
}