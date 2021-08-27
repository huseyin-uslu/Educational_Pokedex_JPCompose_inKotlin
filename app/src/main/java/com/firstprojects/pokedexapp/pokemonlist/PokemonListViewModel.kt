package com.firstprojects.pokedexapp.pokemonlist

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.icu.lang.UCharacter.isDigit
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.firstprojects.pokedexapp.data.models.PokedexListEntry
import com.firstprojects.pokedexapp.repository.PokemonRepository
import com.firstprojects.pokedexapp.util.Constants.PAGE_SIZE
import com.firstprojects.pokedexapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
): ViewModel() {

    private var curPage = 0

    var pokemonList = mutableStateOf<List<PokedexListEntry>>(listOf())
    val loadError = mutableStateOf("")
    val isLoading = mutableStateOf(false)
    val endReached = mutableStateOf(false)

    var cachedPokemonList = listOf<PokedexListEntry>()
    var isSearchStarting = true
    var isSearching = mutableStateOf(false)

    init {
        loadPokemonPaginated()
    }

    fun searchPokemonList(query : String){

        val listToSearch = if(isSearchStarting){
            pokemonList.value
        }else{
            cachedPokemonList
        }

        viewModelScope.launch(Dispatchers.Default) {

            if(query.isEmpty()){
                pokemonList.value = cachedPokemonList
                isSearchStarting = true
                isSearching.value = false
                return@launch
            }

            val results = listToSearch.filter {
                it.pokemonName.contains(query.trim(),ignoreCase = true) || it.number.toString() == query.trim()
            }

            if(isSearchStarting){
                cachedPokemonList = pokemonList.value
                isSearchStarting = false
            }

            pokemonList.value = results
            isSearching.value = true

        }

    }

    fun loadPokemonPaginated(){
       viewModelScope.launch{
           isLoading.value = true
           val result = repository.getPokemonList(PAGE_SIZE, curPage * PAGE_SIZE)
           when(result){
               is Resource.Success -> {
                   endReached.value = ((curPage * PAGE_SIZE) >= result.data!!.count!!)
                   val pokedexEntries = result.data.results!!.mapIndexed { index, entry ->
                       val number = if(entry.url!!.endsWith("/")){
                           entry.url.dropLast(1).takeLastWhile {it ->
                               it.isDigit()}
                       }else{
                           entry.url.takeLastWhile {it ->
                               it.isDigit()}
                       }
                       val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$number.png"
                       PokedexListEntry(entry.name!!.capitalize(Locale.current),url,number.toInt())
                   }
                   curPage++
                   loadError.value = ""
                   isLoading.value = false
                   pokemonList.value += pokedexEntries
               }
               is Resource.Error -> {
                   loadError.value = result.message.toString()
                   isLoading.value = false
               }
           }


       }
    }


    fun calcDominantColor(drawable : Drawable, onFinished : (Color) -> Unit){
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888,true)

        Palette.from(bmp).generate{
            it?.dominantSwatch?.rgb?.let { colorValue ->
                onFinished(Color(colorValue))
            }
        }
    }
}