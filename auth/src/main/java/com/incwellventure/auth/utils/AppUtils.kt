package com.incwellventure.auth.utils

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import com.incwellventure.auth.AuthUser
import com.incwellventure.auth.Constant

class AppUtils {
    companion object {
        fun getUserIntent(user: AuthUser): Intent {
            var intent = Intent()
            intent.putExtra(Constant.AUTH_USER, user)
            return intent
        }

        fun toUser(user: FirebaseUser): AuthUser {
            return AuthUser(user.displayName, user.email, user.photoUrl.toString())
        }

        fun toUser(account: GoogleSignInAccount): AuthUser {
            return AuthUser(account.displayName, account.email, account.photoUrl.toString())
        }
    }
}