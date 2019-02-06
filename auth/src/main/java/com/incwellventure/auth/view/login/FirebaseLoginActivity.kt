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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.incwellventure.auth.Auth
import com.incwellventure.auth.AuthUser
import com.incwellventure.auth.Constant
import com.incwellventure.auth.Constant.Companion.RC_SIGN_IN
import com.incwellventure.auth.R
import com.incwellventure.auth.utils.AppUtils
import com.incwellventure.auth.utils.SnackbarUtils.Companion.notifyUser
import kotlinx.android.synthetic.main.activity_firebase_login.*

class FirebaseLoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

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
                        auth.currentUser?.photoUrl.toString()
                    )
                    var intent = Intent()
                    intent.putExtra(Constant.AUTH_USER, user)
                    notifyUser(this, getString(R.string.msg_login_success))

                    delay {
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                } else {
                    notifyUser(this, it.exception?.message.toString())
                }
            }
        }


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Auth.webClientKey)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        gmail.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, Constant.RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                delay {
                    setResult(Activity.RESULT_OK, AppUtils.getUserIntent(AppUtils.toUser(account!!)))
                    finish()
                }
            } catch (e: ApiException) {
                notifyUser(this, e.localizedMessage)
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
}
