package com.firstprojects.pokedexapp.data.remote.responses


import com.google.gson.annotations.SerializedName

data class Gameİndice(
    @SerializedName("game_index")
    val gameİndex: Int?,
    @SerializedName("version")
    val version: Version?
)