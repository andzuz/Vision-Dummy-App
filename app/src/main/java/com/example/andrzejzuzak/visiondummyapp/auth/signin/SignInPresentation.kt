package com.example.andrzejzuzak.visiondummyapp.auth.signin

import com.example.andrzejzuzak.visiondummyapp.core.LoadFailurePresentation

interface SignInPresentation: LoadFailurePresentation {

    fun proceedWhenLoggedIn()

}