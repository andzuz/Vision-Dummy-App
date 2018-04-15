package com.example.andrzejzuzak.visiondummyapp.results

import com.example.andrzejzuzak.visiondummyapp.core.LotterixError
import com.example.andrzejzuzak.visiondummyapp.networking.PostResultRequestDto
import com.example.andrzejzuzak.visiondummyapp.networking.ResultResponseDto
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class PostResultUseCase @Inject constructor(private val repository: ResultsRepository) {

    fun postResult(result: PostResultRequestDto, authToken: String): Observable<ResultResponseDto> {
        return repository.postResult(result, authToken)
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