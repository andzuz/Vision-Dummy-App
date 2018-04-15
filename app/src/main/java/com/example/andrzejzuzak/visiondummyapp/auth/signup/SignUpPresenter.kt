package com.example.andrzejzuzak.visiondummyapp.auth.signup

import com.example.andrzejzuzak.visiondummyapp.R
import com.example.andrzejzuzak.visiondummyapp.core.LotterixError
import com.example.andrzejzuzak.visiondummyapp.extensions.PreferenceHelper
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SignUpPresenter @Inject constructor(private val prefs: PreferenceHelper,
                                          private val signUpUseCase: SignUpUseCase) {

    private val compositeDisposable = CompositeDisposable()
    private var presentation: SignUpPresentation? = null

    fun onCreate(presentation: SignUpPresentation) {
        this.presentation = presentation
    }

    fun onDestroy() {
        compositeDisposable.dispose()
        presentation = null
    }

    fun signUp(email: String, password: String) {
        presentation?.showProgress()

        compositeDisposable.add(
                signUpUseCase.signUp(email, password)
                        .subscribe({ token ->
                            if (!token.isEmpty()) {
                                prefs.putAuthToken(token)
                                presentation?.hideProgress()
                                presentation?.proceedWhenSignedUp()
                            } else {
                                presentation?.showFailure(R.string.error_generic_message)
                            }
                        }, { error ->
                            if (error is LotterixError && error.isForbiddenError()) {
                                presentation?.showFailure(R.string.register_email_in_use_error)
                            } else {
                                presentation?.showFailure(R.string.error_generic_message)
                            }
                        }))
    }

}