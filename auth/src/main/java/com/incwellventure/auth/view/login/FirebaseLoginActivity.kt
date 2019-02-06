package com.incwellventure.auth.view.login

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.incwellventure.auth.R
import kotlinx.android.synthetic.main.activity_firebase_login.*

class FirebaseLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_login)
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
                        enableLogin()
                    }

                })
            }
        }
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
}
