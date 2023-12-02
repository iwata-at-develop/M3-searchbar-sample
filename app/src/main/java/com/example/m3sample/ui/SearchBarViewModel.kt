package com.example.m3sample.ui

import android.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchBarViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: SearchBarEvent) {
        when (event) {
            is SearchBarEvent.QueryChange -> {
                var isQuerying = false
                val pokemonList = mutableListOf<Pokemon>()
                viewModelScope.launch {
                    if (event.query.isNotEmpty()) {
                        isQuerying = true
                        pokemonList.addAll(
                            _pokemonList.filter {
                                it.name.startsWith(
                                    prefix = event.query,
                                    ignoreCase = true
                                ) || it.nameOfKana.startsWith(
                                    prefix = event.query,
                                    ignoreCase = true
                                )
                            }
                        )
                    } else {
                        pokemonList.addAll(_pokemonList)
                    }

                    _uiState.value =
                        _uiState.value.copy(
                            query = event.query,
                            isQuerying = isQuerying,
                            pokemonList = pokemonList
                        )
                }
            }

            is SearchBarEvent.Select -> {
                event.pokemon.let {
                    _history.remove(it)
                    _history.add(index = 0, it)
                }

                _uiState.value =
                    _uiState.value.copy(
                        query = event.pokemon.name,
                        isQuerying = true,
                        selected = event.pokemon,
                        searchHistory = _history,
                        pokemonList = listOf(event.pokemon)
                    )
            }

            is SearchBarEvent.Back -> {
                _uiState.value =
                    _uiState.value.copy(
                        selected = null
                    )
            }

            is SearchBarEvent.Cancel -> {
                _uiState.value =
                    _uiState.value.copy(
                        query = "",
                        isQuerying = false,
                        selected = null,
                        searchHistory = _history,
                        pokemonList = _pokemonList
                    )
            }

        }
    }
}

data class UiState(
    val query: String = "",
    val isQuerying: Boolean = false,
    val selected: Pokemon? = null,
    val searchHistory: List<Pokemon> = _history,
    val pokemonList: List<Pokemon> = _pokemonList,
)

sealed class SearchBarEvent {
    data class QueryChange(val query: String) : SearchBarEvent()
    data class Select(val pokemon: Pokemon) : SearchBarEvent()
    object Back : SearchBarEvent()
    object Cancel : SearchBarEvent()
}

data class Pokemon(
    val no: Int = 0,
    val name: String,
    val types: List<PokemonType>,
    val imageUrl: String,
    val nameOfKana: String,
)

enum class PokemonType(val kana:String, val color:Color) {
    FIRE("ほのお", Color.valueOf(Color.parseColor("#F44336"))),
    WATER("みず", Color.valueOf(Color.parseColor("#2196F3"))),
    GRASS("くさ", Color.valueOf(Color.parseColor("#4CAF50"))),
    POISON("どく", Color.valueOf(Color.parseColor("#9C27B0"))),
    FLYING("ひこう", Color.valueOf(Color.parseColor("#00BCD4"))),
}

private val _history = mutableListOf<Pokemon>()
private val _pokemonList = listOf(
    Pokemon(
        1,
        "フシギダネ",
        listOf(PokemonType.GRASS, PokemonType.POISON),
        "https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png",
        "ふしぎだね"
    ),
    Pokemon(
        2,
        "フシギソウ",
        listOf(PokemonType.GRASS, PokemonType.POISON),
        "https://assets.pokemon.com/assets/cms2/img/pokedex/full/002.png",
        "ふしぎそう"
    ),
    Pokemon(
        3,
        "フシギバナ",
        listOf(PokemonType.GRASS, PokemonType.POISON),
        "https://assets.pokemon.com/assets/cms2/img/pokedex/full/003.png",
        "ふしぎばな"
    ),
    Pokemon(
        4,
        "ヒトカゲ",
        listOf(PokemonType.FIRE),
        "https://assets.pokemon.com/assets/cms2/img/pokedex/full/004.png",
        "ひとかげ"
    ),
    Pokemon(
        5,
        "リザード",
        listOf(PokemonType.FIRE),
        "https://assets.pokemon.com/assets/cms2/img/pokedex/full/005.png",
        "りざーど"
    ),
    Pokemon(
        6,
        "リザードン",
        listOf(PokemonType.FIRE, PokemonType.FLYING),
        "https://assets.pokemon.com/assets/cms2/img/pokedex/full/006.png",
        "りざーどん"
    ),
    Pokemon(
        7,
        "ゼニガメ",
        listOf(PokemonType.WATER),
        "https://assets.pokemon.com/assets/cms2/img/pokedex/full/007.png",
        "ぜにがめ"
    ),
    Pokemon(
        8,
        "カメール",
        listOf(PokemonType.WATER),
        "https://assets.pokemon.com/assets/cms2/img/pokedex/full/008.png",
        "かめーる"
    ),
    Pokemon(
        9,
        "カメックス",
        listOf(PokemonType.WATER),
        "https://assets.pokemon.com/assets/cms2/img/pokedex/full/009.png",
        "かめっくす"
    ),
)

