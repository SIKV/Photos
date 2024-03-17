package com.github.sikv.photos.recommendations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.sikv.photos.common.ui.NetworkImage
import com.github.sikv.photos.common.ui.rememberViewInteropNestedScrollConnection
import com.github.sikv.photos.domain.Photo

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RecommendationsScreen(
    isRefreshing: Boolean,
    photos: List<Photo>,
    onPhotoPressed: (Photo) -> Unit,
    isNextPageLoading: Boolean,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
    ) {
        if (!isRefreshing && !isNextPageLoading && photos.isEmpty()) {
            NoRecommendations(
                onRefreshPressed = onRefresh
            )
        } else {
            val pullRefreshState = rememberPullRefreshState(
                refreshing = isRefreshing,
                onRefresh = onRefresh
            )
            Box(
                modifier = Modifier
                    .pullRefresh(pullRefreshState)
            ) {
                Recommendations(
                    photos = photos,
                    onPhotoPressed = onPhotoPressed,
                    isNextPageLoading = isNextPageLoading,
                    onLoadMore = onLoadMore,
                )
                PullRefreshIndicator(
                    refreshing = isRefreshing,
                    state =  pullRefreshState,
                    modifier = Modifier
                        .align(Alignment.TopCenter),
                )
            }
        }
    }
}

@Composable
private fun NoRecommendations(
    onRefreshPressed: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(24.dp),
    ) {
        Text(
            text = stringResource(id = R.string.no_recommendations),
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(id = R.string.no_recommendations_description),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRefreshPressed) {
            Text(text = stringResource(id = R.string.refresh))
        }
    }
}

// TODO: This grid is a bit laggy during scrolling. Need to improve its performance.
@Composable
private fun Recommendations(
    photos: List<Photo>,
    onPhotoPressed: (Photo) -> Unit,
    isNextPageLoading: Boolean,
    onLoadMore: () -> Unit,
    cellsCount: Int = 3,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(cellsCount),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        // https://gist.github.com/chrisbanes/053189c31302269656c1979edf418310
        modifier = Modifier
            .nestedScroll(rememberViewInteropNestedScrollConnection()),
        content = {
            items(photos.size) { index ->
                if (index == photos.lastIndex && !isNextPageLoading) {
                    onLoadMore()
                }
                NetworkImage(
                    imageUrl = photos[index].getPhotoPreviewUrl(),
                    loading = {
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .background(colorResource(id = R.color.colorPlaceholder))
                        )
                    },
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clickable {
                            onPhotoPressed(photos[index])
                        }
                )
            }
            if (isNextPageLoading) {
                item(span = { GridItemSpan(cellsCount) }) {
                    LinearProgressIndicator()
                }
            }
        }
    )
}
