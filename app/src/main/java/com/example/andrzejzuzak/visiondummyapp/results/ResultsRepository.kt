package com.example.andrzejzuzak.visiondummyapp.results

import com.example.andrzejzuzak.visiondummyapp.networking.PostResultRequestDto
import com.example.andrzejzuzak.visiondummyapp.networking.ResultsHistoryResponseDto
import com.example.andrzejzuzak.visiondummyapp.networking.Services
import com.example.andrzejzuzak.visiondummyapp.networking.SingleResultResponseDto
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class ResultsRepository @Inject constructor(private val services: Services) {

    fun postResult(result: PostResultRequestDto, authToken: String): Observable<Response<SingleResultResponseDto>> {
        return services.resultsApi.postResult(result, authToken).subscribeOn(Schedulers.io())
    }

    fun getResultsHistory(authToken: String): Observable<Response<ResultsHistoryResponseDto>> {
        return services.resultsApi.getResultsHistory(authToken).subscribeOn(Schedulers.io())
    }

}