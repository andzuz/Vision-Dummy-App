package com.example.andrzejzuzak.visiondummyapp.core

import android.support.annotation.StringRes

interface LoadFailurePresentation {

    fun showProgress()

    fun hideProgress()

    fun showFailure(@StringRes strResId: Int)

}