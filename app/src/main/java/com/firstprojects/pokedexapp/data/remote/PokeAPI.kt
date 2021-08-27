package com.firstprojects.pokedexapp.data.remote

import com.firstprojects.pokedexapp.data.remote.responses.Pokemon
import com.firstprojects.pokedexapp.data.remote.responses.PokemonList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeAPI{

    //https://pokeapi.co/api/v2/ -> base url
    //pokemon -> get request

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit : Int,
        @Query("offset") offset : Int
    ) : PokemonList

    @GET("pokemon/{name}")
    suspend fun getPokemonInfo(
        @Path("name") name: String
    ) : Pokemon
}