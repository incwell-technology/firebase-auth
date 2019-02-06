package com.incwellventure.firebaseapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.incwellventure.auth.AuthUser
import com.incwellventure.auth.Constant
import com.incwellventure.auth.view.login.FirebaseLoginActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startActivityForResult(Intent(this, FirebaseLoginActivity::class.java), 201)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 201) {
            var user = data?.getParcelableExtra(Constant.AUTH_USER) as AuthUser
            Toast.makeText(this, "Hi " + user.email, Toast.LENGTH_LONG).show()
        }
    }
}
