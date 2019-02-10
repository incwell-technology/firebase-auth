package com.incwellventure.firebaseapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.incwellventure.auth.AuthUser
import com.incwellventure.auth.Constant.Companion.AUTH_USER


class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var user: AuthUser = intent.getParcelableExtra(AUTH_USER)
        Toast.makeText(this, "user email" + user.email, Toast.LENGTH_LONG).show()
    }

    companion object {
        fun start(context: Activity, user: AuthUser) {
            var intent = Intent(context, HomeActivity::class.java)
            intent.putExtra(AUTH_USER, user)
            context.startActivity(intent)
        }
    }

}
