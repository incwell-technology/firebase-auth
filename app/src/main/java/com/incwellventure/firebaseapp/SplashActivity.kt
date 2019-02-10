package com.incwellventure.firebaseapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.incwellventure.auth.AuthUser
import com.incwellventure.auth.Constant
import com.incwellventure.auth.FireBaseAuth
import com.incwellventure.auth.view.login.FirebaseLoginActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (FireBaseAuth.getUser() == null) {
            FirebaseLoginActivity.start(this, 201)
        } else {
            lunchActivity(FireBaseAuth.getUser())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 201) {
            var user = data!!.extras[Constant.AUTH_USER] as AuthUser
            lunchActivity(user)
        }
    }

    private fun lunchActivity(user: AuthUser) {
        HomeActivity.start(this, user)
        finish()
    }
}
