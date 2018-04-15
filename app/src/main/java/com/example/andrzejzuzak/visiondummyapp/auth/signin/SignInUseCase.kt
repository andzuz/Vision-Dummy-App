package com.example.andrzejzuzak.visiondummyapp.auth.signin

import com.example.andrzejzuzak.visiondummyapp.auth.AuthRepository
import com.example.andrzejzuzak.visiondummyapp.core.LotterixError
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class SignInUseCase @Inject constructor(private val repository: AuthRepository) {

    fun signIn(email: String, password: String): Observable<String> {
        return repository.signIn(email, password)
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