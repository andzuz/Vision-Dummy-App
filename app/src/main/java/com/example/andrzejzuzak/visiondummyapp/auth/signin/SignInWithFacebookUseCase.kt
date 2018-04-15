package com.example.andrzejzuzak.visiondummyapp.auth.signin

import com.example.andrzejzuzak.visiondummyapp.auth.AuthRepository
import com.example.andrzejzuzak.visiondummyapp.core.LotterixError
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class SignInWithFacebookUseCase @Inject constructor(private val repository: AuthRepository) {

    fun signIn(accessToken: String): Observable<String> {
        return repository.signInWithFacebook(accessToken)
                .map {
                    if (it.isSuccessful) {
                        it.body()?.token ?: ""
                    } else {
                        throw LotterixError(it.code())
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
    }

}