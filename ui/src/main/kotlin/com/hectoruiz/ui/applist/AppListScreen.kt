package com.hectoruiz.ui.applist

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hectoruiz.domain.commons.Constants.DELAY_USER_SEARCH_TIME_MS
import com.hectoruiz.domain.commons.Constants.MAX_RATE
import com.hectoruiz.domain.commons.ErrorState
import com.hectoruiz.domain.models.AppModel
import com.hectoruiz.ui.R
import com.hectoruiz.ui.applist.theme.SoftonicTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val appsPreview = listOf(
    AppModel(
        name = "CallApp: Caller ID, Call Blocker & Recording Calls",
        packageName = "whatsapp.package",
        icon = "icon",
        numDownloads = 47190,
        averageStats = 4.5F,
        totalStats = 2301,
    ),
    AppModel(
        name = "Telegram",
        packageName = "telegram.package",
        icon = "icon",
        numDownloads = 1231,
        averageStats = 2.75F,
        totalStats = 111,
    )
)

@Preview(showBackground = true)
@Composable
fun AppListScreenPreview() {
    SoftonicTheme {
        AppListScreen(
            apps = appsPreview,
            loading = false,
            error = ErrorState.NoError,
            onSearchApps = {},
            navigateToDetail = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AppListScreen(
    apps: List<AppModel>,
    loading: Boolean,
    error: ErrorState,
    onSearchApps: (String) -> Unit,
    navigateToDetail: (String) -> Unit,
) {
    val groupedApps = apps.groupBy { it.name[0] }.toSortedMap()
    val snackBarHostState = remember { SnackbarHostState() }
    val networkError = stringResource(R.string.network_error)
    val defaultError = stringResource(R.string.default_error)

    LaunchedEffect(error) {
        when (error) {
            is ErrorState.NetworkError -> {
                launch {
                    snackBarHostState.showSnackbar(
                        message = networkError,
                        duration = SnackbarDuration.Short
                    )
                }
            }

            ErrorState.NoError -> {}

            ErrorState.Unknown -> {
                launch {
                    snackBarHostState.showSnackbar(
                        message = defaultError,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) })
    { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppSearch(onSearchApps)
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.testTag(TAG_APP_LIST),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    groupedApps.forEach { (initial, apps) ->
                        stickyHeader {
                            Text(text = initial.toString())
                        }

                        items(apps) { app ->
                            AppItem(app = app, navigateToDetail = navigateToDetail)
                        }
                    }
                }
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag(TAG_APP_LIST_CIRCULAR_PROGRESS_INDICATOR)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppSearchPreview() {
    SoftonicTheme {
        AppSearch {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSearch(onSearchApps: (String) -> Unit) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(searchText) {
        if (searchText.text.isBlank()) return@LaunchedEffect

        delay(DELAY_USER_SEARCH_TIME_MS)
        onSearchApps(searchText.text)
    }

    OutlinedTextField(
        value = searchText,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(TAG_APP_SEARCH),
        onValueChange = { searchText = it },
        label = { Text(stringResource(id = R.string.search_app)) },
        trailingIcon = {
            if (searchText.text.isNotBlank()) {
                IconButton(
                    onClick = {
                        searchText = searchText.copy(text = "")
                    },
                    modifier = Modifier.testTag(TAG_APP_DELETE_SEARCH)
                ) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "",
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AppItemPreview() {
    SoftonicTheme {
        AppItem(
            app = AppModel(
                name = "CallApp: Caller ID, Call Blocker & Recording Calls",
                packageName = "whatsapp.package",
                icon = "icon",
                numDownloads = 47190,
                averageStats = 4.5F,
                totalStats = 2301,
            ),
            navigateToDetail = {}
        )
    }
}

@Composable
fun AppItem(app: AppModel, navigateToDetail: (String) -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .testTag(TAG_APP_LIST_ITEM)
        .clickable { navigateToDetail(app.packageName) }) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AsyncImage(
                model = app.icon,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.CenterVertically)
                    .clip(CircleShape),
                placeholder = painterResource(id = R.drawable.baseline_apps_outage_24),
                error = painterResource(id = R.drawable.baseline_apps_outage_24),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = app.name, textAlign = TextAlign.Center)
                AppRate(app.averageStats, app.totalStats)
                Text(
                    text = pluralStringResource(
                        id = R.plurals.number_downloads,
                        count = app.numDownloads, app.numDownloads
                    ), fontWeight = FontWeight.Thin
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppRatePreview() {
    SoftonicTheme {
        AppRate(averageStats = 4.6F, totalStats = 1231)
    }
}

@Composable
fun AppRate(averageStats: Float, totalStats: Int) {
    val color = remember { Animatable(Color.Unspecified) }
    LaunchedEffect(Unit) {
        color.animateTo(Color.Yellow, animationSpec = tween(1000))
    }

    val filledStars = remember { averageStats.toInt() }
    val clipStar by remember {
        derivedStateOf {
            filledStars + 1
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(MAX_RATE) {
            if (it < filledStars) {
                FilledRate(
                    color = color.value,
                    modifier = Modifier.testTag(TAG_APP_LIST_ITEM_FILLED_RATE)
                )
            } else if (it < clipStar) {
                Box {
                    EmptyRate(color = color.value)
                    FilledRate(color = color.value, modifier = Modifier
                        .drawWithContent {
                            clipRect(right = size.width * (1 - (clipStar - averageStats))) {
                                this@drawWithContent.drawContent()
                            }
                        }
                        .testTag(TAG_APP_LIST_ITEM_CLIPPED_RATE))
                }
            } else {
                EmptyRate(color = color.value)
            }
        }
        Text(
            text = stringResource(id = R.string.number_stats, totalStats),
            fontWeight = FontWeight.Thin,
            fontSize = MaterialTheme.typography.labelSmall.fontSize,
        )
    }
}

@Composable
fun FilledRate(color: Color, modifier: Modifier) {
    Icon(
        imageVector = Icons.Default.Favorite,
        modifier = modifier,
        contentDescription = null,
        tint = color
    )
}

@Composable
fun EmptyRate(color: Color) {
    Icon(
        imageVector = Icons.Default.FavoriteBorder,
        contentDescription = null,
        tint = color,
        modifier = Modifier.testTag(TAG_APP_LIST_ITEM_EMPTY_RATE)
    )
}

const val TAG_APP_LIST_CIRCULAR_PROGRESS_INDICATOR = "appListCircularProgressIndicator"
const val TAG_APP_LIST = "appList"
const val TAG_APP_LIST_ITEM = "appListItem"
const val TAG_APP_LIST_ITEM_EMPTY_RATE = "appListItemEmptyRate"
const val TAG_APP_LIST_ITEM_FILLED_RATE = "appListItemFilledRate"
const val TAG_APP_LIST_ITEM_CLIPPED_RATE = "appListItemClippedRate"
const val TAG_APP_SEARCH = "appSearch"
const val TAG_APP_DELETE_SEARCH = "deleteSearch"