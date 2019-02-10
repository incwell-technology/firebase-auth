package com.incwellventure.auth

import android.app.Application
import android.content.Context
import com.facebook.FacebookSdk
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.incwellventure.auth.utils.AppUtils

class FireBaseAuth : Application() {
    companion object {
        var webClientKey: String = ""
        fun init(context: Context, webClientKey: String) {
            FirebaseApp.initializeApp(context)
            this.webClientKey = webClientKey
            FacebookSdk.sdkInitialize(getApplicationContext())
            AppEventsLogger.activateApp(context)
        }

        fun getUser(): AuthUser? {
            return if (FirebaseAuth.getInstance().currentUser == null) null
            else AppUtils.toUser(FirebaseAuth.getInstance().currentUser!!)
        }

        fun logout() {
            FirebaseAuth.getInstance().signOut()
        }
    }
}