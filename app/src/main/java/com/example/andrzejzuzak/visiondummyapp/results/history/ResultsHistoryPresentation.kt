package com.example.andrzejzuzak.visiondummyapp.results.history

import com.example.andrzejzuzak.visiondummyapp.core.LoadFailurePresentation
import com.example.andrzejzuzak.visiondummyapp.networking.ResultResponseDto

interface ResultsHistoryPresentation: LoadFailurePresentation {

    fun showItems(items: List<ResultResponseDto>)

}