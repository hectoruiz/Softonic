package com.hectoruiz.ui.appdetail

import androidx.activity.ComponentActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hectoruiz.domain.commons.ErrorState
import com.hectoruiz.domain.models.AppDetailModel
import com.hectoruiz.domain.models.Reliability
import com.hectoruiz.ui.R
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDetailScreenTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var appDetail = AppDetailModel(
        name = "CallApp: Caller ID, Call Blocker & Recording Calls",
        icon = "icon",
        updated = "2005-04-07",
        version = "2.3.0",
        pegi = "PEGI-3",
        size = 12121,
        description = "description",
        reliability = Reliability.TRUSTED,
        developerName = "Bryan Reynolds",
        developerEmail = "bryan.reynolds@callapp.com",
        keywords = "#telegram #messenger #communication",
        path = "path"
    )

    @Test
    fun showLoaderWhenItsLoading() {
        rule.setContent { AppDetailScreen(appDetail, true, ErrorState.NoError) }
        rule.onNodeWithTag(TAG_APP_DETAIL_CIRCULAR_PROGRESS_INDICATOR).assertExists()
    }

    @Test
    fun hideLoaderWhenItsNotLoading() {
        rule.setContent { AppDetailScreen(appDetail, false, ErrorState.NoError) }
        rule.onNodeWithTag(TAG_APP_DETAIL_CIRCULAR_PROGRESS_INDICATOR).assertDoesNotExist()
    }

    @Test
    fun isShowingAppDetail() {
        rule.setContent { AppDetailScreen(appDetail, false, ErrorState.NoError) }

        rule.onNodeWithTag(TAG_APP_DETAIL_ITEM).assertIsDisplayed()
    }

    @Test
    fun notShowingAppDetail() {
        appDetail = appDetail.copy(size = 0)

        rule.setContent { AppDetailScreen(appDetail, false, ErrorState.NoError) }

        rule.onNodeWithTag(TAG_APP_DETAIL_ITEM).assertDoesNotExist()
    }

    @Test
    fun trustedIconShowed() {
        appDetail = appDetail.copy(reliability = Reliability.TRUSTED)
        rule.setContent { AppDetailScreen(appDetail, false, ErrorState.NoError) }

        rule.onNodeWithTag(Icons.Default.CheckCircle.toString()).assertIsDisplayed()
    }

    @Test
    fun unknownIconShowed() {
        appDetail = appDetail.copy(reliability = Reliability.UNKNOWN)
        rule.setContent { AppDetailScreen(appDetail, false, ErrorState.NoError) }

        rule.onNodeWithTag(Icons.Default.Warning.toString()).assertIsDisplayed()
    }

    @Test
    fun noInformationIconShowed() {
        appDetail = appDetail.copy(reliability = Reliability.NO_DATA)
        rule.setContent { AppDetailScreen(appDetail, false, ErrorState.NoError) }

        rule.onNodeWithTag(Icons.Default.Info.toString()).assertIsDisplayed()
    }

    @Test
    fun buttonReadMoreShowed() {
        appDetail = appDetail.copy(description = "One\nTwo\nThree\nFour\nFive\nSix")
        rule.setContent { AppDetailScreen(appDetail, false, ErrorState.NoError) }

        val readMoreText = rule.activity.getString(R.string.read_more)

        rule.onNodeWithText(readMoreText).assertExists()
    }

    @Test
    fun buttonReadMoreNotShowed() {
        appDetail = appDetail.copy(description = "short description")
        rule.setContent { AppDetailScreen(appDetail, false, ErrorState.NoError) }

        val readMoreText = rule.activity.getString(R.string.read_more)

        rule.onNodeWithText(readMoreText).assertDoesNotExist()
    }

    @Test
    fun buttonReadLessShowed() {
        appDetail = appDetail.copy(description = "One\nTwo\nThree\nFour\nFive\nSix")

        rule.setContent { AppDetailScreen(appDetail, false, ErrorState.NoError) }

        val readMoreText = rule.activity.getString(R.string.read_more)
        val readLessText = rule.activity.getString(R.string.read_less)

        rule.onNodeWithText(readMoreText).performClick()
        rule.onNodeWithText(readLessText).assertIsDisplayed()
    }

    @Test
    fun isShowingNetworkError() {
        rule.setContent { AppDetailScreen(appDetail, false, ErrorState.NetworkError()) }

        val networkErrorMessage = rule.activity.getString(R.string.network_error)
        rule.onNodeWithText(networkErrorMessage).assertIsDisplayed()
    }

    @Test
    fun isShowingUnknownError() {
        rule.setContent { AppDetailScreen(appDetail, false, ErrorState.Unknown) }

        val unknownErrorMessage = rule.activity.getString(R.string.default_error)
        rule.onNodeWithText(unknownErrorMessage).assertIsDisplayed()
    }
}


