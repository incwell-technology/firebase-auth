package com.incwellventure.auth

import android.app.Application
import android.content.Context
import com.facebook.FacebookSdk
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.FirebaseApp

class Auth : Application() {

    companion object {
        var webClientKey: String = ""
        fun init(context: Context, webClientKey: String) {
            FirebaseApp.initializeApp(context)
            this.webClientKey = webClientKey
            FacebookSdk.sdkInitialize(getApplicationContext())
            AppEventsLogger.activateApp(context)
        }
    }
}