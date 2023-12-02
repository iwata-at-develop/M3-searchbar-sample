package com.example.m3sample.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.m3sample.R

@Composable
fun SearchBarScreen(modifier: Modifier = Modifier) {

    val viewModel = viewModel<SearchBarViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val listLazyListState = rememberLazyListState()

    Box(modifier = modifier.fillMaxSize()) {
        Box(modifier = modifier.windowInsetsPadding(WindowInsets.statusBars)) {
            SearchBarContent(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                uiState = uiState,
                onEvent = viewModel::onEvent
            )

            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp),
                state = listLazyListState,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val pokemonList = uiState.pokemonList

                items(pokemonList) {
                    PokemonListItem(
                        pokemon = it,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarContent(
    uiState: UiState,
    onEvent: (SearchBarEvent) -> Unit,
    modifier: Modifier = Modifier
) {

    var active by rememberSaveable { mutableStateOf(false) }

    val query = uiState.query
    val isQuerying = uiState.isQuerying
    val result: List<Pokemon> = if (isQuerying) {
        uiState.pokemonList
    } else {
        uiState.searchHistory
    }

    DockedSearchBar(
        modifier = modifier,
        query = query,
        onQueryChange = {
            onEvent(SearchBarEvent.QueryChange(it))
        },
        onSearch = { active = false },
        active = active,
        onActiveChange = {
            active = it
        },
        leadingIcon = {
            if (active) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.back),
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .clickable {
                            onEvent(SearchBarEvent.Back)
                            active = false
                        },
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.search),
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
        },
        trailingIcon = {
            if (isQuerying) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = stringResource(id = R.string.cancel),
                    modifier = Modifier
                        .padding(12.dp)
                        .size(32.dp)
                        .clickable {
                            onEvent(SearchBarEvent.Cancel)
                            active = false
                        },
                )
            }
        },
        placeholder = { Text(text = stringResource(id = R.string.search_pokemon_name)) },
    ) {
        // Search result shown when active
        if (result.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(items = result) { pokemon ->
                    // Search result
                    ListItem(
                        headlineContent = { Text(text = pokemon.name) },
                        leadingContent = {
                            if(isQuerying) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(id = R.string.search),
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = stringResource(id = R.string.history),
                                )
                            }
                        },
                        modifier = Modifier.clickable {
                            onEvent(SearchBarEvent.Select(pokemon))
                            active = false
                        }
                    )
                }
            }
        } else {
            if (isQuerying) {
                Text(
                    text = stringResource(id = R.string.not_found),
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Text(
                    text = stringResource(id = R.string.no_search_history),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PokemonListItem(
    pokemon: Pokemon,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(CardDefaults.shape)
            .combinedClickable(
                onClick = { /* to go detail screen */ },
                onLongClick = { /* to selected */ }
            )
            .clip(CardDefaults.shape),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = pokemon.imageUrl,
                    contentDescription = "",
                    modifier = Modifier
                        .height(56.dp)
                        .width(56.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = pokemon.name,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Icon(
                        imageVector = Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                val white150 = Color.White.copy(alpha = 0.5f)
                pokemon.types.forEach {
                    Row(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Color(it.color.toArgb()),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .background(
                                color = white150.compositeOver(Color(it.color.toArgb())),
                                shape = RoundedCornerShape(20.dp)
                            )
                    ) {
                        Text(
                            text = it.kana,
                            color = Color(it.color.toArgb()),
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                }
            }
        }
    }
}