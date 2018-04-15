package com.example.andrzejzuzak.visiondummyapp.menu

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.andrzejzuzak.visiondummyapp.LotterixApplication
import com.example.andrzejzuzak.visiondummyapp.R
import com.example.andrzejzuzak.visiondummyapp.auth.signin.SignInActivity
import com.example.andrzejzuzak.visiondummyapp.extensions.PreferenceHelper
import com.example.andrzejzuzak.visiondummyapp.extensions.launchActivity
import com.example.andrzejzuzak.visiondummyapp.ocr.OcrActivity
import com.example.andrzejzuzak.visiondummyapp.results.history.ResultsHistoryActivity
import kotlinx.android.synthetic.main.activity_menu.*
import javax.inject.Inject

class MenuActivity: AppCompatActivity() {

    @Inject lateinit var prefs: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        (application as LotterixApplication).daggerGraph.inject(this)
        setupListeners()
    }

    private fun setupListeners() {
        postLotteryResultsButton.setOnClickListener {
            launchActivity<OcrActivity>()
        }

        logoutButton.setOnClickListener {
            prefs.cleanupWhenLoggingOut()
            launchActivity<SignInActivity>()
            finish()
        }

        showHistoryButton.setOnClickListener {
            launchActivity<ResultsHistoryActivity>()
        }
    }

}
