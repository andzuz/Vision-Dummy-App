package com.example.andrzejzuzak.visiondummyapp.results.history

import com.example.andrzejzuzak.visiondummyapp.R
import com.example.andrzejzuzak.visiondummyapp.core.LotterixError
import com.example.andrzejzuzak.visiondummyapp.extensions.PreferenceHelper
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class ResultsHistoryPresenter @Inject constructor(private val prefs: PreferenceHelper,
                                                  private val resultsHistoryUseCase: GetResultsHistoryUseCase) {

    private val compositeDisposable = CompositeDisposable()
    private var presentation: ResultsHistoryPresentation? = null

    fun onCreate(presentation: ResultsHistoryPresentation) {
        this.presentation = presentation
    }

    fun onDestroy() {
        compositeDisposable.dispose()
        presentation = null
    }

    fun getHistory() {
        presentation?.showProgress()

        compositeDisposable.add(
                resultsHistoryUseCase.getResultsHistory(prefs.getAuthToken())
                        .subscribe({ items ->
                            if (items.isEmpty()) {
                                presentation?.showFailure(R.string.history_no_results_error)
                            } else {
                                presentation?.showItems(items)
                            }
                        }, { error ->
                            if (error is LotterixError && error.isNotFoundError()) {
                                presentation?.showFailure(R.string.history_no_results_error)
                            } else {
                                presentation?.showFailure(R.string.error_generic_message)
                            }
                        })
        )
    }

}