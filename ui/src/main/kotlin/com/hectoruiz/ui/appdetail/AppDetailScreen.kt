package com.hectoruiz.ui.appdetail

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hectoruiz.domain.commons.Constants.MAX_LINES
import com.hectoruiz.domain.commons.ErrorState
import com.hectoruiz.domain.models.AppDetailModel
import com.hectoruiz.domain.models.Reliability
import com.hectoruiz.ui.R
import com.hectoruiz.ui.applist.theme.SoftonicTheme
import kotlinx.coroutines.launch

private val appDetailPreview = AppDetailModel(
    name = "CallApp: Caller ID, Call Blocker & Recording Calls",
    icon = "icon",
    updated = "2005-04-07",
    version = "2.3.0",
    pegi = "PEGI-3",
    size = 1291,
    description = "\"Pure instant messaging — simple, fast, secure, and synced across all your devices. One of the world's top 10 most downloaded apps with over 500 million active users.\\n\\nFAST: Telegram is the fastest messaging app on the market, connecting people via a unique, distributed network of data centers around the globe.\\n\\nSYNCED: You can access your messages from all your phones, tablets and computers at once. Telegram apps are standalone, so you don’t need to keep your phone connected. Start typing on one device and finish the message from another. Never lose your data again.\\n\\nUNLIMITED: You can send media and files, without any limits on their type and size. Your entire chat history will require no disk space on your device, and will be securely stored in the Telegram cloud for as long as you need it. \\n\\nSECURE: We made it our mission to provide the best security combined with ease of use. Everything on Telegram, including chats, groups, media, etc. is encrypted using a combination of 256-bit symmetric AES encryption, 2048-bit RSA encryption, and Diffie–Hellman secure key exchange. \\n\\n100% FREE & OPEN: Telegram has a fully documented and free API for developers, open source apps and verifiable builds to prove the app you download is built from the exact same source code that is published. \\n\\nPOWERFUL: You can create group chats with up to 200,000 members, share large videos, documents of any type (.DOCX, .MP3, .ZIP, etc.) up to 2 GB each, and even set up bots for specific tasks. Telegram is the perfect tool for hosting online communities and coordinating teamwork.\\n\\nRELIABLE: Built to deliver your messages using as little data as possible, Telegram is the most reliable messaging system ever made. It works even on the weakest mobile connections. \\n\\nFUN: Telegram has powerful photo and video editing tools, animated stickers and emoji, fully customizable themes to change the appearance of your app, and an open stickerGIF platform to cater to all your expressive needs.\\n\\nSIMPLE: While providing an unprecedented array of features, we take great care to keep the interface clean. Telegram is so simple you already know how to use it.\\n\\nPRIVATE: We take your privacy seriously and will never give any third parties access to your data. You can delete any message you ever sent or received for both sides, at any time and without a trace. Telegram will never use your data to show you ads.\\n\\nFor those interested in maximum privacy, Telegram offers Secret Chats. Secret Chat messages can be programmed to self-destruct automatically from both participating devices. This way you can send all types of disappearing content — messages, photos, videos, and even files. Secret Chats use End-to-End Encryption to ensure that a message can only be read by its intended recipient.\\n\\nWe keep expanding the boundaries of what you can do with a messaging app. Don’t wait years for older messengers to catch up with Telegram — join the revolution today.\",",
    reliability = Reliability.TRUSTED,
    developerName = "Bryan Reynolds",
    developerEmail = "bryan.reynolds@callapp.com",
    keywords = "#telegram #messenger #communication",
    path = "path"
)

@Preview(showBackground = true)
@Composable
fun AppDetailScreenPreview() {
    SoftonicTheme {
        AppDetailScreen(
            appDetail = appDetailPreview,
            loading = false,
            error = ErrorState.NoError
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailScreen(
    appDetail: AppDetailModel,
    loading: Boolean,
    error: ErrorState,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val networkError = stringResource(R.string.network_error)
    val defaultError = stringResource(R.string.default_error)
    val icon = when (appDetail.reliability) {
        Reliability.TRUSTED -> {
            Icons.Default.CheckCircle
        }

        Reliability.UNKNOWN -> {
            Icons.Default.Warning
        }

        else -> {
            Icons.Default.Info
        }
    }
    var isCollapsedText by remember { mutableStateOf(true) }
    val resourceIdButton by remember {
        derivedStateOf { mutableIntStateOf(if (isCollapsedText) R.string.read_more else R.string.read_less) }
    }

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (!loading && appDetail.hasInitialInformation()) {
                Column(modifier = Modifier.testTag(TAG_APP_DETAIL_ITEM)) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        AsyncImage(
                            model = appDetail.icon,
                            contentDescription = null,
                            modifier = Modifier.clip(CircleShape),
                            placeholder = painterResource(id = R.drawable.baseline_apps_outage_24),
                            error = painterResource(id = R.drawable.baseline_apps_outage_24),
                        )
                        Text(
                            text = appDetail.keywords, fontWeight = FontWeight.Light,
                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = appDetail.name,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = stringResource(id = R.string.app_info),
                                fontWeight = FontWeight.Bold,
                                fontSize = MaterialTheme.typography.labelLarge.fontSize
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, MaterialTheme.colorScheme.onBackground)
                                    .padding(4.dp)
                            ) {
                                AppDetailInfoItem(R.string.last_update, appDetail.updated)
                                AppDetailInfoItem(R.string.app_version, appDetail.version)
                                AppDetailInfoItem(R.string.pegi_qualification, appDetail.pegi)
                                AppDetailInfoItem(R.string.app_size, appDetail.size.toString())
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(modifier = Modifier.animateContentSize()) {
                            if (isCollapsedText) {
                                Text(
                                    text = appDetail.description,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = MAX_LINES
                                )
                            } else {
                                Text(text = appDetail.description)
                            }
                        }
                        if (appDetail.description.lines().size > MAX_LINES) {
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(onClick = { isCollapsedText = !isCollapsedText }) {
                                Text(text = stringResource(id = resourceIdButton.intValue))
                            }
                        }
                    }
                }
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .testTag(icon.toString())
            )
            if (loading) CircularProgressIndicator(
                modifier = Modifier
                    .testTag(TAG_APP_DETAIL_CIRCULAR_PROGRESS_INDICATOR)
                    .align(Alignment.Center)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppDetailInfoItemPreview() {
    SoftonicTheme {
        AppDetailInfoItem(resourceId = R.string.app_size, value = "123131")
    }
}

@Composable
fun AppDetailInfoItem(resourceId: Int, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = resourceId),
            fontWeight = FontWeight.Normal,
            fontSize = MaterialTheme.typography.labelMedium.fontSize
        )
        Text(
            text = value,
            fontStyle = FontStyle.Normal,
            fontSize = MaterialTheme.typography.labelMedium.fontSize
        )
    }
}

const val TAG_APP_DETAIL_CIRCULAR_PROGRESS_INDICATOR = "appDetailCircularProgressIndicator"
const val TAG_APP_DETAIL_ITEM = "appDetailItem"