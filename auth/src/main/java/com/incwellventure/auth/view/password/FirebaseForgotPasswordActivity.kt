package com.incwellventure.auth.view.password

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.google.firebase.auth.FirebaseAuth
import com.incwellventure.auth.R
import com.incwellventure.auth.utils.AppUtils.Companion.isValidEmail
import com.incwellventure.auth.utils.SnackbarUtils.Companion.notifyUser
import com.incwellventure.auth.view.base.BaseActivity
import kotlinx.android.synthetic.main.activity_firebase_forgot_password.*

class FirebaseForgotPasswordActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_forgot_password)
        send.isEnabled = isValidEmail("")
        email.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                email.error = null
                if (!isValidEmail(s.toString())) {
                    email.error = getString(R.string.err_msg_invalid_email)
                }
                send.isEnabled = isValidEmail(s.toString())
            }
        })

        send.setOnClickListener {
            showProgressDialog()
            FirebaseAuth.getInstance()
                .sendPasswordResetEmail(email.editText?.text!!.trim().toString())
                .addOnCompleteListener {
                    hideProgressDialog()
                    if (it.isSuccessful) {
                        notifyUser(this, getString(R.string.msg_success_password_reset_link_sent))
                    } else {
                        notifyUser(this, it.exception!!.localizedMessage)
                    }
                }
        }
    }


}
