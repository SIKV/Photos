package com.github.sikv.photos.search.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.sikv.photos.compose.ui.BackAction
import com.github.sikv.photos.compose.ui.DynamicPhotoItem
import com.github.sikv.photos.compose.ui.Spacing
import com.github.sikv.photos.compose.ui.SwitchLayoutAction
import com.github.sikv.photos.domain.ListLayout
import com.github.sikv.photos.domain.Photo
import com.github.sikv.photos.search.R
import com.github.sikv.photos.search.SearchUiState
import com.github.sikv.photos.search.SearchViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun SearchScreen(
    onBackClick: () -> Unit,
    onPhotoClick: (Photo) -> Unit,
    onPhotoAttributionClick: (Photo) -> Unit,
    onPhotoActionsClick: (Photo) -> Unit,
    onSharePhotoClick: (Photo) -> Unit,
    onDownloadPhotoClick: (Photo) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { uiState.photoSources.size })
    val selectedTabIndex by remember { derivedStateOf { pagerState.currentPage } }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            TopBar(
                onBackClick = onBackClick,
                uiState = uiState,
                scrollBehavior = scrollBehavior,
                viewModel = viewModel
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                uiState.photoSources.forEachIndexed { index, photoSource ->
                    Tab(
                        text = { Text(photoSource.title) },
                        selected = selectedTabIndex == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
            SearchContent(
                pagerState = pagerState,
                uiState = uiState,
                onPhotoClick = onPhotoClick,
                onPhotoAttributionClick = onPhotoAttributionClick,
                onPhotoActionsClick = onPhotoActionsClick,
                onSharePhotoClick = onSharePhotoClick,
                onDownloadPhotoClick = onDownloadPhotoClick,
                viewModel = viewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onBackClick: () -> Unit,
    uiState: SearchUiState,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: SearchViewModel
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        // TODO: Do not request focus when the screen is resumed.
        focusRequester.requestFocus()
    }

    TopAppBar(
        navigationIcon = { BackAction(onBackClick = onBackClick) },
        title = {
            TextField(
                value = uiState.query ?: "",
                onValueChange = { newQuery ->
                    viewModel.onSearchQueryChange(newQuery)
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.performSearch()
                        focusManager.clearFocus()
                    }
                ),
                singleLine = true,
                placeholder = { Text(text = stringResource(R.string.search_for_photos)) },
                trailingIcon = {
                    if (uiState.query.isNullOrBlank().not()) {
                        IconButton(
                            onClick = {
                                viewModel.onSearchQueryChange("")
                                focusRequester.requestFocus()
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_close_22dp),
                                contentDescription = stringResource(R.string.clear)
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
            )
        },
        actions = {
            SwitchLayoutAction(
                listLayout = uiState.listLayout,
                onSwitchLayoutClick = viewModel::switchListLayout
            )
        },
        scrollBehavior = scrollBehavior,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SearchContent(
    pagerState: PagerState,
    uiState: SearchUiState,
    onPhotoClick: (Photo) -> Unit,
    onPhotoAttributionClick: (Photo) -> Unit,
    onPhotoActionsClick: (Photo) -> Unit,
    onSharePhotoClick: (Photo) -> Unit,
    onDownloadPhotoClick: (Photo) -> Unit,
    viewModel: SearchViewModel
) {
    HorizontalPager(state = pagerState) { index ->
        val photoSource = uiState.photoSources[index]
        val photos = uiState.photos[photoSource]?.collectAsLazyPagingItems()

        if (photos == null) {
            // Display 'Start search' state if needed.
        } else {
            when (photos.loadState.refresh) {
                is LoadState.Error -> Error()
                LoadState.Loading -> Loading()
                is LoadState.NotLoading -> Photos(
                    listLayout = uiState.listLayout,
                    photos = photos,
                    onPhotoClick = onPhotoClick,
                    onPhotoAttributionClick = onPhotoAttributionClick,
                    onPhotoActionsClick = onPhotoActionsClick,
                    onSharePhotoClick = onSharePhotoClick,
                    onDownloadPhotoClick = onDownloadPhotoClick,
                    viewModel = viewModel,
                )
            }
        }
    }
}

@Composable
private fun Photos(
    listLayout: ListLayout,
    photos: LazyPagingItems<Photo>,
    onPhotoClick: (Photo) -> Unit,
    onPhotoAttributionClick: (Photo) -> Unit,
    onPhotoActionsClick: (Photo) -> Unit,
    onSharePhotoClick: (Photo) -> Unit,
    onDownloadPhotoClick: (Photo) -> Unit,
    viewModel: SearchViewModel
) {
    if (photos.itemCount == 0) {
        NoResults()
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(listLayout.spanCount)
        ) {
            items(photos.itemCount) { index ->
                photos[index]?.let { photo ->
                    val isFavorite by viewModel.isFavorite(photo)
                        .collectAsStateWithLifecycle(initialValue = false)

                    DynamicPhotoItem(
                        photo = photo,
                        isFavorite = isFavorite,
                        listLayout = listLayout,
                        onPhotoClick = onPhotoClick,
                        onPhotoAttributionClick = onPhotoAttributionClick,
                        onPhotoActionsClick = onPhotoActionsClick,
                        onToggleFavoriteClick = viewModel::toggleFavorite,
                        onSharePhotoClick = onSharePhotoClick,
                        onDownloadPhotoClick = onDownloadPhotoClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun Loading() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun NoResults() {
    Box(
        contentAlignment =  Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.Two)
    ) {
        Text(
            text = stringResource(R.string.no_search_results),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Error() {
    Box(
        contentAlignment =  Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.Two)
    ) {
        Text(
            text = stringResource(R.string.error),
            textAlign = TextAlign.Center
        )
    }
}
