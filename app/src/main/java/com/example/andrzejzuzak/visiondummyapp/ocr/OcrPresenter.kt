package com.example.andrzejzuzak.visiondummyapp.ocr

import com.example.andrzejzuzak.visiondummyapp.extensions.PreferenceHelper
import com.example.andrzejzuzak.visiondummyapp.networking.PostResultRequestDto
import com.example.andrzejzuzak.visiondummyapp.results.PostResultUseCase
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class OcrPresenter @Inject constructor(private val prefs: PreferenceHelper,
                                       private val postResultUseCase: PostResultUseCase) {

    private val compositeDisposable = CompositeDisposable()
    private var presentation: OcrPresentation? = null

    fun onCreate(presentation: OcrPresentation) {
        this.presentation = presentation
    }

    fun onDestroy() {
        compositeDisposable.dispose()
        presentation = null
    }

    fun postResult(result: PostResultRequestDto) {
        compositeDisposable.add(
                postResultUseCase.postResult(result, prefs.getAuthToken())
                        .subscribe({ matchedResponse ->
                            presentation?.showMatchedCount(matchedResponse.matchedNumbersCount)
                        }, { error ->
                            presentation?.showMatchedCount(-1)
                        })
        )
    }

}