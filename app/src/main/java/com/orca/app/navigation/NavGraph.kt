package com.orca.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.orca.app.ui.screens.developer.DeveloperScreen
import com.orca.app.ui.screens.developer.base64.Base64Screen
import com.orca.app.ui.screens.developer.hash.HashScreen
import com.orca.app.ui.screens.developer.hex.HexScreen
import com.orca.app.ui.screens.developer.json.JsonScreen
import com.orca.app.ui.screens.developer.jwt.JwtScreen
import com.orca.app.ui.screens.developer.url.UrlScreen
import com.orca.app.ui.screens.device.DeviceScreen
import com.orca.app.ui.screens.device.info.DeviceInfoScreen
import com.orca.app.ui.screens.device.network.DeviceNetworkScreen
import com.orca.app.ui.screens.device.system.DeviceSystemScreen
import com.orca.app.ui.screens.favorites.FavoritesScreen
import com.orca.app.ui.screens.home.HomeScreen
import com.orca.app.ui.screens.network.NetworkScreen
import com.orca.app.ui.screens.network.cidr.CidrScreen
import com.orca.app.ui.screens.network.dns.DnsScreen
import com.orca.app.ui.screens.network.httpheaders.HttpHeadersScreen
import com.orca.app.ui.screens.network.ping.PingScreen
import com.orca.app.ui.screens.network.portscanner.PortScannerScreen
import com.orca.app.ui.screens.network.ssl.SslScreen
import com.orca.app.ui.screens.network.subnet.SubnetScreen
import com.orca.app.ui.screens.network.whois.WhoisScreen
import com.orca.app.ui.screens.search.SearchScreen
import com.orca.app.ui.screens.security.SecurityScreen
import com.orca.app.ui.screens.security.hmac.HmacScreen
import com.orca.app.ui.screens.security.password.PasswordScreen
import com.orca.app.ui.screens.security.rot.RotScreen
import com.orca.app.ui.screens.security.xor.XorScreen
import com.orca.app.ui.screens.settings.SettingsScreen

private const val ANIM_DURATION = 300

@Composable
fun OrcaNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Routes.HOME,
) {
    val navigateToTool: (String) -> Unit = { route -> navController.navigate(route) }
    val goBack: () -> Unit = { navController.popBackStack() }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(ANIM_DURATION)) +
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(ANIM_DURATION))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(ANIM_DURATION)) +
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(ANIM_DURATION))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(ANIM_DURATION)) +
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(ANIM_DURATION))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(ANIM_DURATION)) +
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(ANIM_DURATION))
        },
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onCategoryClick = navigateToTool,
                onSearchClick = { navController.navigate(Routes.SEARCH) },
            )
        }

        composable(Routes.SEARCH) {
            SearchScreen(onToolClick = navigateToTool)
        }

        composable(Routes.FAVORITES) {
            FavoritesScreen(onToolClick = navigateToTool)
        }

        composable(Routes.SETTINGS) { SettingsScreen() }

        composable(Routes.NETWORK) {
            NetworkScreen(onBack = goBack, onToolClick = navigateToTool)
        }
        composable(Routes.NETWORK_PING) { PingScreen(onBack = goBack) }
        composable(Routes.NETWORK_DNS) { DnsScreen(onBack = goBack) }
        composable(Routes.NETWORK_HTTP_HEADERS) { HttpHeadersScreen(onBack = goBack) }
        composable(Routes.NETWORK_PORT_SCANNER) { PortScannerScreen(onBack = goBack) }
        composable(Routes.NETWORK_SSL) { SslScreen(onBack = goBack) }
        composable(Routes.NETWORK_WHOIS) { WhoisScreen(onBack = goBack) }
        composable(Routes.NETWORK_CIDR) { CidrScreen(onBack = goBack) }
        composable(Routes.NETWORK_SUBNET) { SubnetScreen(onBack = goBack) }

        composable(Routes.DEVELOPER) {
            DeveloperScreen(onBack = goBack, onToolClick = navigateToTool)
        }
        composable(Routes.DEVELOPER_BASE64) { Base64Screen(onBack = goBack) }
        composable(Routes.DEVELOPER_HEX) { HexScreen(onBack = goBack) }
        composable(Routes.DEVELOPER_HASH) { HashScreen(onBack = goBack) }
        composable(Routes.DEVELOPER_JWT) { JwtScreen(onBack = goBack) }
        composable(Routes.DEVELOPER_JSON) { JsonScreen(onBack = goBack) }
        composable(Routes.DEVELOPER_URL) { UrlScreen(onBack = goBack) }

        composable(Routes.SECURITY) {
            SecurityScreen(onBack = goBack, onToolClick = navigateToTool)
        }
        composable(Routes.SECURITY_PASSWORD) { PasswordScreen(onBack = goBack) }
        composable(Routes.SECURITY_ROT) { RotScreen(onBack = goBack) }
        composable(Routes.SECURITY_XOR) { XorScreen(onBack = goBack) }
        composable(Routes.SECURITY_HMAC) { HmacScreen(onBack = goBack) }

        composable(Routes.DEVICE) {
            DeviceScreen(onBack = goBack, onToolClick = navigateToTool)
        }
        composable(Routes.DEVICE_INFO) { DeviceInfoScreen(onBack = goBack) }
        composable(Routes.DEVICE_NETWORK) { DeviceNetworkScreen(onBack = goBack) }
        composable(Routes.DEVICE_SYSTEM) { DeviceSystemScreen(onBack = goBack) }
    }
}
