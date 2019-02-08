package com.incwellventure.auth.view.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.incwellventure.auth.Auth
import com.incwellventure.auth.AuthUser
import com.incwellventure.auth.Constant
import com.incwellventure.auth.Constant.Companion.RC_SIGN_IN
import com.incwellventure.auth.R
import com.incwellventure.auth.utils.AppUtils
import com.incwellventure.auth.utils.SnackbarUtils.Companion.notifyUser
import com.incwellventure.auth.view.base.BaseActivity
import com.incwellventure.auth.view.password.FirebaseForgotPasswordActivity
import com.incwellventure.auth.view.signup.FirebaseEmailSignupActivity
import kotlinx.android.synthetic.main.activity_firebase_login.*

class FirebaseLoginActivity : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private var TAG = FirebaseLoginActivity::class.java.simpleName
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_login)

        auth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()
        login.isEnabled = false

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
            if (isValid()) {
                showProgressDialog()
                auth.signInWithEmailAndPassword(
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
        }


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Auth.webClientKey)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        gmail.setOnClickListener {
            signIn()
        }

        facebook.setOnClickListener {
            var loginManager = LoginManager.getInstance()
            loginManager.logInWithReadPermissions(this, listOf("email", "public_profile"))
            loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    handleFacebookAccessToken(token = result!!.accessToken)
                }

                override fun onCancel() {
                }

                override fun onError(error: FacebookException?) {
                    notifyUser(this@FirebaseLoginActivity, error!!.localizedMessage)
                }

            })
        }



        email_signup.setOnClickListener {
            startActivity(Intent(this, FirebaseEmailSignupActivity::class.java))
        }

        forget_password.setOnClickListener {
            startActivity(Intent(this, FirebaseForgotPasswordActivity::class.java))
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, Constant.RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                fireBaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                notifyUser(this, e.localizedMessage)
            }
        }
    }

    private fun fireBaseAuthWithGoogle(acct: GoogleSignInAccount) {
        showProgressDialog()
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    delay {
                        setResult(Activity.RESULT_OK, AppUtils.getUserIntent(AppUtils.toUser(user!!)))
                        finish()
                    }
                } else {
                    notifyUser(this, task.exception!!.localizedMessage)
                }
                hideProgressDialog()
            }
    }


    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")
        showProgressDialog()
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    Toast.makeText(this, "username " + user?.displayName, Toast.LENGTH_LONG).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                hideProgressDialog()
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
        return !email.editText?.text.toString().trim().isNullOrEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email.editText?.text.toString().trim()).matches()
    }

    private fun isValidPassword(): Boolean {
        return !TextUtils.isEmpty(password.editText?.text.toString().trim())
    }
}
