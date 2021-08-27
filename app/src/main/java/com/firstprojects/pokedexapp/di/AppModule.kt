package com.firstprojects.pokedexapp.di

import com.firstprojects.pokedexapp.data.remote.PokeAPI
import com.firstprojects.pokedexapp.repository.PokemonRepository
import com.firstprojects.pokedexapp.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePokemonRepository(
        api : PokeAPI
    ) = PokemonRepository(api)

    @Singleton
    @Provides
    fun providePokemonAPI() : PokeAPI {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokeAPI::class.java)
    }


}