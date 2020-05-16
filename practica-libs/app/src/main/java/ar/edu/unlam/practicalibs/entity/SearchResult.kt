package ar.edu.unlam.practicalibs.entity

import ItunesResult
import com.google.gson.annotations.SerializedName

data class SearchResult(
    @SerializedName("results") val results: Array<ItunesResult>,
    @SerializedName("resultCount") val resultCount: Int
)