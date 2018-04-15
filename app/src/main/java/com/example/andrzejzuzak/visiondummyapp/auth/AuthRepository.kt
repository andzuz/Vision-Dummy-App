package com.example.andrzejzuzak.visiondummyapp.auth

import com.example.andrzejzuzak.visiondummyapp.networking.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class AuthRepository @Inject constructor(private val services: Services) {

    fun signIn(email: String, password: String): Observable<Response<SignInResponseDto>> {
        val requestBody = SignInRequestDto(email, password)
        return services.authApi.signIn(requestBody).subscribeOn(Schedulers.io())
    }

    fun signInWithFacebook(accessToken: String): Observable<Response<SignInResponseDto>> {
        val requestBody = FacebookSignInRequestDto(accessToken)
        return services.authApi.signInWithFacebook(requestBody).subscribeOn(Schedulers.io())
    }

    fun signUp(email: String, password: String): Observable<Response<SignUpResponseDto>> {
        val requestBody = SignUpRequestDto(email, password)
        return services.authApi.signUp(requestBody).subscribeOn(Schedulers.io())
    }

}