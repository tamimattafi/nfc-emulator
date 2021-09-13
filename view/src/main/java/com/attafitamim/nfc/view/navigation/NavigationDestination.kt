package com.attafitamim.nfc.view.navigation

import android.util.Base64
import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import com.attafitamim.nfc.common.utils.asJsonBytes
import com.attafitamim.nfc.common.utils.readFromJson
import java.io.Serializable

abstract class NavigationDestination {

    private val asStringArgument: String get()
        = Base64.encodeToString(this.asJsonBytes, Base64.URL_SAFE)

    @Composable
    abstract fun Present(navController: NavHostController)

    companion object {
        private const val DESTINATION_ARGUMENT_KEY = "destination_argument"
        private const val ARGUMENT_PATH_PREFIX = "{"
        private const val ARGUMENT_PATH_POSTFIX = "}"
        private const val ROUTE_SEPARATOR = "/"

        private val namedArgument: NamedNavArgument
            get() = navArgument(DESTINATION_ARGUMENT_KEY) {
                type = NavType.StringType
            }

        val <T : NavigationDestination> Class<T>.rawRoute: String get() =
            routeWithArgumentPaths(
                screenName = name,
                navigationArgument = namedArgument
            )

        fun <T : NavigationDestination> NavGraphBuilder.navigationDestination(
            navController: NavHostController,
            clazz: Class<T>
        ) {
            val namedArguments = listOf(namedArgument)
            composable(
                route = clazz.rawRoute,
                arguments = namedArguments
            ) { navBackStackEntry ->
                val destination = navBackStackEntry.getDestination(clazz)
                destination.Present(navController)
            }
        }

        private fun <T : NavigationDestination> NavBackStackEntry.getDestination(clazz: Class<T>): T {
            val argument = arguments?.getString(DESTINATION_ARGUMENT_KEY) ?: return clazz.newInstance()
            return fromStringArgument(clazz, argument)
        }

        private fun <T : NavigationDestination> fromStringArgument(clazz: Class<T>, argument: String): T {
            val jsonArgument = Base64.decode(argument, Base64.URL_SAFE)
            return jsonArgument.readFromJson(clazz)
        }

        private fun argumentPath(key: String) =
            StringBuilder().append(
                ARGUMENT_PATH_PREFIX,
                key,
                ARGUMENT_PATH_POSTFIX
            )

        private fun routeWithArgumentPaths(
            screenName: String,
            navigationArgument: NamedNavArgument
        ): String = StringBuilder().append(
            screenName,
            ROUTE_SEPARATOR,
            argumentPath(key = navigationArgument.name)
        ).toString()

        fun <T : NavigationDestination> NavHostController.toDestination(destination: T) {
            val screenName = destination.javaClass.name
            val argument = destination.asStringArgument

            val routeWithArgument = StringBuilder().append(
                screenName,
                ROUTE_SEPARATOR,
                argument
            ).toString()

            navigate(routeWithArgument)
        }
    }
}