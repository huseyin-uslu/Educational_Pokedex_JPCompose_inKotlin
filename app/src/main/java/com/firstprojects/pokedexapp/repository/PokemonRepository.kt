package com.firstprojects.pokedexapp.repository

import android.annotation.SuppressLint
import android.util.Log
import com.firstprojects.pokedexapp.data.remote.PokeAPI
import com.firstprojects.pokedexapp.data.remote.responses.Pokemon
import com.firstprojects.pokedexapp.data.remote.responses.PokemonList
import com.firstprojects.pokedexapp.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PokemonRepository @Inject constructor( //already its functions return Resource<T> for every scene
    private val api : PokeAPI
) {

    suspend fun getPokemonList(limit : Int, offset : Int) : Resource<PokemonList>{
        //return a must
        val response = try{
            api.getPokemonList(limit,offset)
        }catch (e : Exception){
            return Resource.Error("An unknown error occured.")
        }
        return Resource.Success(response)
    }

    suspend fun getPokemonInfo(pokemonName : String) : Resource<Pokemon> {
        val response = try{
            api.getPokemonInfo(pokemonName)
        }catch (e: Exception){
            return Resource.Error("An unknown error occured.")
        }
        return Resource.Success(response)
    }
}