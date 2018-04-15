package com.example.andrzejzuzak.visiondummyapp.auth.signin

import com.example.andrzejzuzak.visiondummyapp.R
import com.example.andrzejzuzak.visiondummyapp.core.LotterixError
import com.example.andrzejzuzak.visiondummyapp.extensions.PreferenceHelper
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SignInPresenter @Inject constructor(private val signInUseCase: SignInUseCase,
                                          private val signInWithFacebookUseCase: SignInWithFacebookUseCase,
                                          private val prefs: PreferenceHelper) {

    private val compositeDisposable = CompositeDisposable()
    private var presentation: SignInPresentation? = null

    fun onCreate(presentation: SignInPresentation) {
        this.presentation = presentation
    }

    fun onDestroy() {
        compositeDisposable.dispose()
        presentation = null
    }

    fun signIn(email: String, password: String) {
        presentation?.showProgress()

        compositeDisposable.add(
                signInUseCase.signIn(email, password)
                        .subscribe({ token ->
                            if (!token.isEmpty()) {
                                prefs.putAuthToken(token)
                                presentation?.hideProgress()
                                presentation?.proceedWhenLoggedIn()
                            } else {
                                presentation?.showFailure(R.string.error_generic_message)
                            }
                        }, { error ->
                            if (error is LotterixError && error.isUnauthorizedError()) {
                                presentation?.showFailure(R.string.login_error_wrong_credentials)
                            } else if (error is LotterixError && error.isBadRequestError()) {
                                presentation?.showFailure(R.string.login_error_validation)
                            } else {
                                presentation?.showFailure(R.string.error_generic_message)
                            }
                        }))
    }

    fun signInWithFacebook(accessToken: String) {
        presentation?.showProgress()

        compositeDisposable.add(
                signInWithFacebookUseCase.signIn(accessToken)
                        .subscribe({ token ->
                            if (!token.isEmpty()) {
                                prefs.putAuthToken(token)
                                presentation?.hideProgress()
                                presentation?.proceedWhenLoggedIn()
                            } else {
                                presentation?.showFailure(R.string.error_generic_message)
                            }
                        }, { error ->
                            presentation?.showFailure(R.string.error_generic_message)
                        })
        )
    }

    fun isLoggedIn(): Boolean {
        return prefs.hasAuthToken()
    }

}
