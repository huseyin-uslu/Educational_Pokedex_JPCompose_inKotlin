package com.firstprojects.pokedexapp.data.remote.responses


import com.google.gson.annotations.SerializedName

data class İconsX(
    @SerializedName("front_default")
    val frontDefault: String?,
    @SerializedName("front_female")
    val frontFemale: Any?
)