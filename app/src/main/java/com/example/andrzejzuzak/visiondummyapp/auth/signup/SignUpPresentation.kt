package com.example.andrzejzuzak.visiondummyapp.auth.signup

import com.example.andrzejzuzak.visiondummyapp.core.LoadFailurePresentation

interface SignUpPresentation: LoadFailurePresentation {

    fun proceedWhenSignedUp()

}