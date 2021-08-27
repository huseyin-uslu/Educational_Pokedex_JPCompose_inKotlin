package com.firstprojects.pokedexapp.data.remote.responses


import com.google.gson.annotations.SerializedName

data class Heldİtem(
    @SerializedName("item")
    val item: İtem?,
    @SerializedName("version_details")
    val versionDetails: List<VersionDetail>?
)