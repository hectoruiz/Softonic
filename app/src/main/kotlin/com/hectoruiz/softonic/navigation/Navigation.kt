package com.hectoruiz.softonic.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hectoruiz.domain.commons.Constants.PARAM_NAME
import com.hectoruiz.domain.commons.ErrorState
import com.hectoruiz.ui.appdetail.AppDetailScreen
import com.hectoruiz.ui.appdetail.AppDetailViewModel
import com.hectoruiz.ui.applist.AppListScreen
import com.hectoruiz.ui.applist.AppListViewModel
import com.hectoruiz.ui.splashscreen.SplashScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = START_DESTINATION) {
        composable(route = START_DESTINATION) {
            SplashScreen {
                navController.navigate(LIST_SCREEN) {
                    popUpTo(START_DESTINATION) {
                        inclusive = true
                    }
                }
            }
        }
        composable(route = LIST_SCREEN) {
            val appListViewModel: AppListViewModel = koinViewModel()
            //val appListViewModel = hiltViewModel<AppListViewModel>()
            val loading by appListViewModel.loading.collectAsStateWithLifecycle()
            val apps by appListViewModel.apps.collectAsStateWithLifecycle()
            val error by appListViewModel.error.collectAsStateWithLifecycle(ErrorState.NoError)

            AppListScreen(
                apps = apps,
                loading = loading,
                error = error,
                onSearchApps = { appListViewModel.searchApps(it) },
                navigateToDetail = {
                    navController.navigate("$DETAIL_SCREEN/$it")
                }
            )
        }
        composable(
            route = "$DETAIL_SCREEN/{$PARAM_NAME}",
            arguments = listOf(navArgument(PARAM_NAME) { type = NavType.StringType })
        ) {
            val appDetailViewModel: AppDetailViewModel = koinViewModel()
            //val appDetailViewModel = hiltViewModel<AppDetailViewModel>()
            val loading by appDetailViewModel.loading.collectAsStateWithLifecycle()
            val appDetail by appDetailViewModel.appDetail.collectAsStateWithLifecycle()
            val error by appDetailViewModel.error.collectAsStateWithLifecycle(ErrorState.NoError)

            AppDetailScreen(
                appDetail = appDetail,
                loading = loading,
                error = error,
            )
        }
    }
}

private const val START_DESTINATION = "splash"
private const val LIST_SCREEN = "appList"
private const val DETAIL_SCREEN = "appDetail"
