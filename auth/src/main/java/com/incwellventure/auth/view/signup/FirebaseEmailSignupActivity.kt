package com.incwellventure.auth.view.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.incwellventure.auth.R
import kotlinx.android.synthetic.main.activity_firebase_email_signup.*

class FirebaseEmailSignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_email_signup)
        auth = FirebaseAuth.getInstance()

        signup.isEnabled = false

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

                        when (textInputLayout.id) {
                            email.id -> if (!isValidEmail()) email.error = getString(R.string.err_msg_invalid_email)
                            confirm_password.id -> if (!isValidPassword()) confirm_password.error =
                                getString(R.string.err_msg_invalid_password)
                        }
                        enableSignup()
                    }

                })
            }
        }

        signup.setOnClickListener {
            auth.createUserWithEmailAndPassword(
                email.editText?.text.toString().trim(),
                password.editText?.text.toString().trim()
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "success", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Fail", Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    private fun enableSignup() {
        signup.isEnabled = isValid()
    }

    private fun isValid(): Boolean {
        return isValidEmail() && isValidPassword()
    }

    private fun isValidEmail(): Boolean {
        return !TextUtils.isEmpty(email.editText?.text.toString().trim()) &&
                Patterns.EMAIL_ADDRESS.matcher(email.editText?.text.toString().trim()).matches()
    }

    private fun isValidPassword(): Boolean {
        return password.editText?.text.toString().trim() == confirm_password.editText?.text.toString().trim()
    }
}