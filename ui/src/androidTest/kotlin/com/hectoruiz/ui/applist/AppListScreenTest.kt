package com.hectoruiz.ui.applist

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hectoruiz.domain.commons.Constants.DELAY_USER_SEARCH_TIME_MS
import com.hectoruiz.domain.commons.ErrorState
import com.hectoruiz.domain.models.AppModel
import com.hectoruiz.ui.R
import io.mockk.MockKAnnotations
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppListScreenTest {

    init {
        MockKAnnotations.init(this)
    }

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var appModel = AppModel(
        name = "CallApp: Caller ID, Call Blocker & Recording Calls",
        packageName = APP_ITEM_PACKAGE_NAME,
        icon = "icon",
        numDownloads = 47190,
        averageStats = 4.5F,
        totalStats = 2301,
    )
    private val emptyAppList = emptyList<AppModel>()
    private val appList: List<AppModel> = listOf(
        appModel,
        AppModel(
            name = "Telegram",
            packageName = "telegram.package",
            icon = "icon",
            numDownloads = 1231,
            averageStats = 2.75F,
            totalStats = 111,
        )
    )

    @Test
    fun showLoaderWhenItsLoading() {
        rule.setContent {
            AppListScreen(
                apps = emptyAppList,
                loading = true,
                error = ErrorState.NoError,
                onSearchApps = {},
                navigateToDetail = {}
            )
        }
        rule.onNodeWithTag(TAG_APP_LIST_CIRCULAR_PROGRESS_INDICATOR).assertExists()
    }

    @Test
    fun hideLoaderWhenItsNotLoading() {
        rule.setContent {
            AppListScreen(
                apps = emptyAppList,
                loading = false,
                error = ErrorState.NoError,
                onSearchApps = {},
                navigateToDetail = {}
            )
        }
        rule.onNodeWithTag(TAG_APP_LIST_CIRCULAR_PROGRESS_INDICATOR).assertDoesNotExist()
    }

    @Test
    fun emptyAppsNotShowAppList() {
        rule.setContent {
            AppListScreen(
                apps = emptyAppList,
                loading = false,
                error = ErrorState.NoError,
                onSearchApps = {},
                navigateToDetail = {}
            )
        }
        rule.onNodeWithTag(TAG_APP_LIST).assertIsNotDisplayed()
    }

    @Test
    fun appsShowAppList() {
        rule.setContent {
            AppListScreen(
                apps = appList,
                loading = false,
                error = ErrorState.NoError,
                onSearchApps = {},
                navigateToDetail = {}
            )
        }
        rule.onNodeWithTag(TAG_APP_LIST).assertIsDisplayed()
        rule.onNodeWithTag(TAG_APP_LIST).performScrollToIndex(appList.size - 1)
        assertEquals(
            appList.size,
            rule.onAllNodes(hasTestTag(TAG_APP_LIST_ITEM)).fetchSemanticsNodes().size
        )
    }

    @Test
    fun userClickOnAppItem() {
        rule.setContent {
            AppListScreen(
                apps = appList,
                loading = false,
                error = ErrorState.NoError,
                onSearchApps = {},
                navigateToDetail = { assertEquals(APP_ITEM_PACKAGE_NAME, it) }
            )
        }
        rule.onNodeWithTag(TAG_APP_LIST).onChildAt(0).performClick()
    }

    @Test
    fun isShowingAppItem() {
        rule.setContent {
            AppListScreen(
                apps = appList,
                loading = false,
                error = ErrorState.NoError,
                onSearchApps = {},
                navigateToDetail = {}
            )
        }
        val itemsDisplayed =
            rule.onAllNodes(matcher = hasTestTag(TAG_APP_LIST_ITEM)).fetchSemanticsNodes().size

        assertEquals(appList.size, itemsDisplayed)
    }

    @Test
    fun notShowingAppItem() {
        rule.setContent {
            AppListScreen(
                apps = emptyAppList,
                loading = false,
                error = ErrorState.Unknown,
                onSearchApps = {},
                navigateToDetail = {}
            )
        }

        rule.onNodeWithTag(TAG_APP_LIST_ITEM).assertDoesNotExist()
    }

    @Test
    fun fiveStartsRateIsCorrectDisplayed() {
        appModel = appModel.copy(averageStats = 5F)
        rule.setContent {
            AppListScreen(
                apps = listOf(appModel),
                loading = false,
                error = ErrorState.NoError,
                onSearchApps = {},
                navigateToDetail = {}
            )
        }

        val filledStarsSize =
            rule.onAllNodes(
                useUnmergedTree = true,
                matcher = hasTestTag(TAG_APP_LIST_ITEM_FILLED_RATE)
            ).fetchSemanticsNodes().size
        assertEquals(5, filledStarsSize)
    }

    @Test
    fun fourStartsRateIsCorrectDisplayed() {
        appModel = appModel.copy(averageStats = 4F)
        rule.setContent {
            AppListScreen(
                apps = listOf(appModel),
                loading = false,
                error = ErrorState.NoError,
                onSearchApps = {},
                navigateToDetail = {}
            )
        }

        val filledStarsSize =
            rule.onAllNodes(
                useUnmergedTree = true,
                matcher = hasTestTag(TAG_APP_LIST_ITEM_FILLED_RATE)
            ).fetchSemanticsNodes().size
        val emptyStarsSize =
            rule.onAllNodes(
                useUnmergedTree = true,
                matcher = hasTestTag(TAG_APP_LIST_ITEM_EMPTY_RATE)
            ).fetchSemanticsNodes().size

        assertEquals(4, filledStarsSize)
        assertEquals(1, emptyStarsSize)
    }

    @Test
    fun threeAndAHalfStartsRateIsCorrectDisplayed() {
        appModel = appModel.copy(averageStats = 3.5F)
        rule.setContent {
            AppListScreen(
                apps = listOf(appModel),
                loading = false,
                error = ErrorState.NoError,
                onSearchApps = {},
                navigateToDetail = {}
            )
        }

        val filledStarsSize =
            rule.onAllNodes(
                useUnmergedTree = true,
                matcher = hasTestTag(TAG_APP_LIST_ITEM_FILLED_RATE)
            ).fetchSemanticsNodes().size
        val emptyStarsSize =
            rule.onAllNodes(
                useUnmergedTree = true,
                matcher = hasTestTag(TAG_APP_LIST_ITEM_EMPTY_RATE)
            ).fetchSemanticsNodes().size
        val clippedStarsSize =
            rule.onAllNodes(
                useUnmergedTree = true,
                matcher = hasTestTag(TAG_APP_LIST_ITEM_CLIPPED_RATE)
            ).fetchSemanticsNodes().size

        assertEquals(3, filledStarsSize)
        assertEquals(2, emptyStarsSize)
        assertEquals(1, clippedStarsSize)
    }

    @Test
    fun emptySearchIsShowed() {
        rule.setContent {
            AppListScreen(
                apps = emptyAppList,
                loading = false,
                error = ErrorState.NoError,
                onSearchApps = {},
                navigateToDetail = {}
            )
        }

        val label = rule.activity.getString(R.string.search_app)
        rule.onNodeWithText(label).assertIsDisplayed()
    }

    @Test
    fun emptySearchIsShowedAfterClickOnDeleteTextButton() {
        rule.setContent {
            AppListScreen(
                apps = emptyAppList,
                loading = false,
                error = ErrorState.NoError,
                onSearchApps = {},
                navigateToDetail = {}
            )
        }

        rule.onNodeWithTag(TAG_APP_SEARCH).performTextInput("searching app")
        rule.onNodeWithTag(TAG_APP_DELETE_SEARCH).performClick()
        rule.onNodeWithTag(TAG_APP_SEARCH).assertTextContains("")
    }

    @Test
    fun searchTextFiresCallToServe() {
        val searchText = "searching app"
        rule.setContent {
            AppListScreen(
                apps = emptyAppList,
                loading = false,
                error = ErrorState.NoError,
                onSearchApps = { assertEquals(searchText, it) },
                navigateToDetail = {}
            )
        }

        rule.onNodeWithTag(TAG_APP_SEARCH).performTextInput(searchText)

        runBlocking { delay(DELAY_USER_SEARCH_TIME_MS) }
    }

    @Test
    fun isShowingNetworkError() {
        rule.setContent {
            AppListScreen(
                apps = appList,
                loading = false,
                error = ErrorState.NetworkError(),
                onSearchApps = {},
                navigateToDetail = {}
            )
        }

        val networkErrorMessage = rule.activity.getString(R.string.network_error)
        rule.onNodeWithText(networkErrorMessage).assertIsDisplayed()
    }

    @Test
    fun isShowingUnknownError() {
        rule.setContent {
            AppListScreen(
                apps = appList,
                loading = false,
                error = ErrorState.Unknown,
                onSearchApps = {},
                navigateToDetail = {}
            )
        }

        val unknownErrorMessage = rule.activity.getString(R.string.default_error)
        rule.onNodeWithText(unknownErrorMessage).assertIsDisplayed()
    }

    private companion object {
        const val APP_ITEM_PACKAGE_NAME = "packageName"
    }
}
