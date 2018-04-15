package com.example.andrzejzuzak.visiondummyapp.auth.signup

import com.example.andrzejzuzak.visiondummyapp.auth.AuthRepository
import com.example.andrzejzuzak.visiondummyapp.core.LotterixError
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class SignUpUseCase @Inject constructor(private val repository: AuthRepository) {

    fun signUp(email: String, password: String): Observable<String> {
        return repository.signUp(email, password)
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