package com.example.andrzejzuzak.visiondummyapp

import android.app.Application
import android.content.Context
import com.example.andrzejzuzak.visiondummyapp.auth.signin.SignInActivity
import com.example.andrzejzuzak.visiondummyapp.auth.signup.SignUpActivity
import com.example.andrzejzuzak.visiondummyapp.menu.MenuActivity
import com.example.andrzejzuzak.visiondummyapp.ocr.OcrActivity
import com.example.andrzejzuzak.visiondummyapp.results.history.ResultsHistoryActivity
import com.facebook.FacebookSdk
import com.facebook.LoggingBehavior
import com.facebook.appevents.AppEventsLogger
import dagger.Component
import dagger.Module
import dagger.Provides
import timber.log.Timber
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class)])
interface DaggerGraph {

    fun inject(signInActivity: SignInActivity)

    fun inject(menuActivity: MenuActivity)

    fun inject(signUpActivity: SignUpActivity)

    fun inject(ocrActivity: OcrActivity)

    fun inject(resultsHistoryActivity: ResultsHistoryActivity)

}

@Module
class AppModule(private val context: Context) {

    @Provides
    fun providesAppContext() = context

}

class LotterixApplication: Application() {

    lateinit var daggerGraph: DaggerGraph

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        daggerGraph = DaggerDaggerGraph.builder().appModule(AppModule(applicationContext)).build()

        FacebookSdk.sdkInitialize(applicationContext)
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }

        AppEventsLogger.activateApp(this)
    }

}