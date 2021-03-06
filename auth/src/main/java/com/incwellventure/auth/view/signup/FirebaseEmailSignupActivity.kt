package com.incwellventure.auth.view.signup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.incwellventure.auth.AuthUser
import com.incwellventure.auth.Constant
import com.incwellventure.auth.R
import com.incwellventure.auth.utils.AppUtils.Companion.isValidEmail
import com.incwellventure.auth.utils.SnackbarUtils.Companion.notifyUser
import com.incwellventure.auth.view.base.BaseActivity
import kotlinx.android.synthetic.main.activity_firebase_email_signup.*

class FirebaseEmailSignupActivity : BaseActivity() {
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
                            email.id -> if (!isValidEmail(email.editText?.text.toString().trim())) email.error =
                                getString(R.string.err_msg_invalid_email)
                            confirm_password.id -> if (!isValidPassword()) confirm_password.error =
                                getString(R.string.err_msg_invalid_password)
                        }
                        enableSignup()
                    }

                })
            }
        }


        signup.setOnClickListener {
            hideKeyboard(window.decorView)
            showProgressDialog()
            auth.createUserWithEmailAndPassword(
                email.editText?.text.toString().trim(),
                password.editText?.text.toString().trim()
            ).addOnCompleteListener {
                hideProgressDialog()
                if (it.isSuccessful) {
                    var user = AuthUser(
                        auth.currentUser?.displayName,
                        auth.currentUser?.email,
                        auth.currentUser?.photoUrl.toString()
                    )
                    var bundle = Bundle()
                    bundle.putParcelable(Constant.AUTH_USER, user)
                    intent.putExtras(bundle)
                    notifyUser(this, getString(R.string.msg_signup_success))

                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    notifyUser(this, it.exception?.message.toString())
                }
            }
        }
    }

    private fun delay(execute: () -> Unit) {
        Handler().postDelayed({
            execute()
        }, Constant.DELAY)
    }

    private fun enableSignup() {
        signup.isEnabled = isValid()
    }

    private fun isValid(): Boolean {
        return isValidEmail(email.editText?.text.toString().trim()) && isValidPassword()
    }


    private fun isValidPassword(): Boolean {
        return password.editText?.text.toString().trim() == confirm_password.editText?.text.toString().trim()
    }

    companion object {
        const val REQUEST_CODE = 301
        fun start(context: Activity) {
            var intent = Intent(context, FirebaseEmailSignupActivity::class.java)
            context.startActivityForResult(intent, REQUEST_CODE)
        }
    }
}
