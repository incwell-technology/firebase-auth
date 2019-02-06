package com.incwellventure.auth.view.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.incwellventure.auth.AuthUser
import com.incwellventure.auth.Constant
import com.incwellventure.auth.R
import kotlinx.android.synthetic.main.activity_firebase_login.*

class FirebaseLoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_login)
        auth = FirebaseAuth.getInstance()
        var root = container
        val childCount = container.childCount
        for (i in 0 until childCount) {
            if (root.getChildAt(i) is TextInputLayout) {
                var textInputLayout = root.getChildAt(i) as TextInputLayout
                textInputLayout.editText?.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        textInputLayout.error = null
                        if (s!!.isEmpty()) {
                            textInputLayout.error =
                                String.format(
                                    getString(R.string.err_msg_empty_field),
                                    textInputLayout.hint.toString()
                                )
                        }
                        if (textInputLayout.id == email.id && !isValidEmail()) {
                            textInputLayout.error = getString(R.string.err_msg_invalid_email)
                        }
                        enableLogin()
                    }

                })
            }
        }

        login.setOnClickListener {
            auth.signInWithEmailAndPassword(
                email.editText?.text.toString().trim(),
                password.editText?.text.toString().trim()
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    var user = AuthUser(
                        auth.currentUser?.displayName,
                        auth.currentUser?.email,
                        auth.currentUser?.photoUrl.toString(),
                        auth.currentUser?.phoneNumber
                    )
                    var intent = Intent()
                    intent.putExtra(Constant.AUTH_USER, user)
                    notifyUser(getString(R.string.msg_login_success))

                    delay {
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                } else {
                    notifyUser(it.exception?.message.toString())
                }
            }
        }
    }

    private fun delay(execute: () -> Unit) {
        Handler().postDelayed({
            execute()
        }, Constant.DELAY)
    }

    private fun enableLogin() {
        login.isEnabled = isValid()
    }

    private fun isValid(): Boolean {
        return isValidEmail() && isValidPassword()
    }


    private fun isValidEmail(): Boolean {
        return !TextUtils.isEmpty(email.editText?.text.toString().trim()) &&
                Patterns.EMAIL_ADDRESS.matcher(email.editText?.text.toString().trim()).matches()
    }

    private fun isValidPassword(): Boolean {
        return !TextUtils.isEmpty(password.editText?.text.toString().trim())
    }

    private fun notifyUser(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }
}
