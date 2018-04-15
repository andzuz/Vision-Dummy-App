package com.example.andrzejzuzak.visiondummyapp.results.history

import com.example.andrzejzuzak.visiondummyapp.core.LotterixError
import com.example.andrzejzuzak.visiondummyapp.networking.ResultResponseDto
import com.example.andrzejzuzak.visiondummyapp.results.ResultsRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class GetResultsHistoryUseCase @Inject constructor(private val repository: ResultsRepository) {

    fun getResultsHistory(authToken: String): Observable<List<ResultResponseDto>> {
        return repository.getResultsHistory(authToken)
                .map {
                    if (it.isSuccessful) {
                        it.body()?.resourceValue!!
                    } else {
                        throw LotterixError(it.code())
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
    }

}