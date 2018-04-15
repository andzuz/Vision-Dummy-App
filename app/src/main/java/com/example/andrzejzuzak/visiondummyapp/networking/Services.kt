package com.example.andrzejzuzak.visiondummyapp.networking

import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Services @Inject constructor() {

    private fun createOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)
        return builder.build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
                .baseUrl("https://murmuring-thicket-20729.herokuapp.com/")
                .client(createOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val resultsApi: ResultsApi by lazy {
        retrofit.create(ResultsApi::class.java)
    }

}

data class SignInRequestDto(val email: String, val password: String)
data class SignUpRequestDto(val email: String, val password: String)
data class FacebookSignInRequestDto(@SerializedName("access_token") val accessToken: String)
data class SignInResponseDto(val token: String)
data class SignUpResponseDto(val token: String)

interface AuthApi {

    @POST("users/signin")
    fun signIn(@Body body: SignInRequestDto): Observable<Response<SignInResponseDto>>

    @POST("users/oauth/facebook")
    fun signInWithFacebook(@Body body: FacebookSignInRequestDto): Observable<Response<SignInResponseDto>>

    @POST("users/signup")
    fun signUp(@Body body: SignUpRequestDto): Observable<Response<SignUpResponseDto>>

}

data class PostResultRequestDto(val numbers: List<String>)
data class ResultResponseDto(val numbers: List<String>, val lotteryDate: String,
                             val matchedNumbersCount: Int)
data class SingleResultResponseDto(val resourceValue: ResultResponseDto)
data class ResultsHistoryResponseDto(val resourceValue: List<ResultResponseDto>)

interface ResultsApi {

    @POST("results")
    fun postResult(@Body body: PostResultRequestDto, @Header("Authorization") authToken: String):
            Observable<Response<SingleResultResponseDto>>

    @GET("results")
    fun getResultsHistory(@Header("Authorization") authToken: String): Observable<Response<ResultsHistoryResponseDto>>

}