package com.example.andrzejzuzak.visiondummyapp.auth.signup

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.andrzejzuzak.visiondummyapp.LotterixApplication
import com.example.andrzejzuzak.visiondummyapp.R
import com.example.andrzejzuzak.visiondummyapp.auth.signin.SignInActivity
import com.example.andrzejzuzak.visiondummyapp.extensions.launchActivity
import com.example.andrzejzuzak.visiondummyapp.menu.MenuActivity
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton
import javax.inject.Inject

class SignUpActivity: AppCompatActivity(), SignUpPresentation {

    @Inject lateinit var presenter: SignUpPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        (application as LotterixApplication).daggerGraph.inject(this)
        presenter.onCreate(this)
        initListeners()
    }

    private fun initListeners() {
        signUpButton.setOnClickListener {
            if (passwordEditText.text.isEmpty()
                    || password2EditText.text.isEmpty()
                    || passwordEditText.text.toString() != password2EditText.text.toString()) {
                showFailure(R.string.register_passwords_dont_match_error)
            } else {
                presenter.signUp(emailEditText.text.toString(), passwordEditText.text.toString())
            }
        }

        goBackButton.setOnClickListener {
            launchActivity<SignInActivity>()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun showProgress() {
        progressView.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressView.visibility = View.GONE
    }

    override fun showFailure(strResId: Int) {
        hideProgress()
        alert(messageResource = strResId, titleResource = R.string.error_title) {
            yesButton {}
        }.show()
    }

    override fun proceedWhenSignedUp() {
        launchActivity<MenuActivity>()
        finish()
    }

}