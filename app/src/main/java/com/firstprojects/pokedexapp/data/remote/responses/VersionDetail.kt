package com.firstprojects.pokedexapp.data.remote.responses


import com.google.gson.annotations.SerializedName

data class VersionDetail(
    @SerializedName("rarity")
    val rarity: Int?,
    @SerializedName("version")
    val version: VersionX?
)