package com.firstprojects.pokedexapp.pokemondetail

import androidx.lifecycle.ViewModel
import com.firstprojects.pokedexapp.data.remote.responses.Pokemon
import com.firstprojects.pokedexapp.repository.PokemonRepository
import com.firstprojects.pokedexapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository : PokemonRepository
): ViewModel() {

    suspend fun getPokemonInfo(pokemonName : String) : Resource<Pokemon> {
        return repository.getPokemonInfo(pokemonName)
    }
}