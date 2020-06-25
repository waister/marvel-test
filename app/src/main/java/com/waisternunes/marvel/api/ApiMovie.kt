package com.waisternunes.marvel.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ApiMovie (
    @SerializedName("title") val title : String,
    @SerializedName("year") val year : Int,
    @SerializedName("rated") val rated : String,
    @SerializedName("released") val released : String,
    @SerializedName("runtime") val runtime : String,
    @SerializedName("genre") val genre : String,
    @SerializedName("director") val director : String,
    @SerializedName("writer") val writer : String,
    @SerializedName("actors") val actors : String,
    @SerializedName("plot") val plot : String,
    @SerializedName("poster") val poster : String
) : Serializable