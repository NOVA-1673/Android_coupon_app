package com.ex.nfcoupon.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ex.nfcoupon.feature.owner.OwnerScanScreen
import com.ex.nfcoupon.feature.role.RoleSelectScreen
import com.ex.nfcoupon.feature.user.UserCouponScreen

object Routes {
    const val ROLE = "role"
    const val USER = "user"
    const val OWNER = "owner"
}

@Composable
fun AppNavHost() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Routes.ROLE) {
        composable(Routes.ROLE) {
            RoleSelectScreen(
                onUser = { nav.navigate(Routes.USER) },
                onOwner = { nav.navigate(Routes.OWNER) }
            )
        }
        composable(Routes.USER) {
            UserCouponScreen(
                onBack = { nav.popBackStack() }
            )
        }
        composable(Routes.OWNER) {
            OwnerScanScreen(
                onBack = { nav.popBackStack() }
            )
        }
    }
}
