package com.example.andrzejzuzak.visiondummyapp.extensions

import android.content.Intent
import android.support.v7.app.AppCompatActivity

inline fun <reified T: AppCompatActivity> AppCompatActivity.launchActivity() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}
