package com.example.andrzejzuzak.visiondummyapp.auth.signin

import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.andrzejzuzak.visiondummyapp.LotterixApplication
import com.example.andrzejzuzak.visiondummyapp.R
import com.example.andrzejzuzak.visiondummyapp.auth.signup.SignUpActivity
import com.example.andrzejzuzak.visiondummyapp.extensions.launchActivity
import com.example.andrzejzuzak.visiondummyapp.menu.MenuActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton
import javax.inject.Inject



class SignInActivity: AppCompatActivity(), SignInPresentation {

    @Inject lateinit var presenter: SignInPresenter

    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as LotterixApplication).daggerGraph.inject(this)
        presenter.onCreate(this)

        if (presenter.isLoggedIn()) {
            proceedWhenLoggedIn()
        } else {
            setContentView(R.layout.activity_sign_in)
            setupUi()
            setupFacebook()
        }
    }

    private fun setupFacebook() {
        LoginManager.getInstance().logOut()
        callbackManager = CallbackManager.Factory.create()
        facebookLoginButton.setReadPermissions("email", "public_profile")
        facebookLoginButton.registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
            override fun onCancel() {
                // nothing
            }

            override fun onSuccess(result: LoginResult?) {
                presenter.signInWithFacebook(result?.accessToken?.token ?: "")
            }

            override fun onError(error: FacebookException?) {
                showFailure(R.string.error_generic_message)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setupUi() {
        signInButton.setOnClickListener {
            presenter.signIn(emailEditText.text.toString(), passwordEditText.text.toString())
        }

        signUpButton.setOnClickListener {
            launchActivity<SignUpActivity>()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun proceedWhenLoggedIn() {
        launchActivity<MenuActivity>()
        finish()
    }

    override fun showProgress() {
        progressView.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressView.visibility = View.GONE
    }

    override fun showFailure(@StringRes strResId: Int) {
        LoginManager.getInstance().logOut()
        hideProgress()
        alert(messageResource = strResId, titleResource = R.string.error_title) {
            yesButton {}
        }.show()
    }

}