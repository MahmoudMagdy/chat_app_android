package com.android.chatapp.feature_chat.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.android.chatapp.feature_chat.presentation.chat_list.ChatListScreen
import com.android.chatapp.feature_chat.presentation.message_list.CHAT_ID
import com.android.chatapp.feature_chat.presentation.message_list.MessageListScreen
import com.android.chatapp.feature_chat.presentation.message_list.USER_ID
import com.android.chatapp.feature_chat.presentation.search_profiles.KEYWORD
import com.android.chatapp.feature_chat.presentation.search_profiles.SearchProfilesScreen
import com.android.chatapp.feature_chat.presentation.util.Screen
import com.android.chatapp.ui.theme.ChatAppTheme
import dagger.hilt.android.AndroidEntryPoint

val Context.chatActivity
    get() = startActivity(Intent(this.applicationContext, ChatActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    })


@AndroidEntryPoint
class ChatActivity : ComponentActivity() {
    lateinit var navController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            if (!::navController.isInitialized)
                navController = rememberNavController()
            ChatApp(navController) { launcher -> launcher() }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navController.handleDeepLink(intent)
    }
}


@Composable
fun ChatApp(navController: NavHostController, launch: (Context.() -> Unit) -> Unit) {
    ChatAppTheme {
        ChatNavHost(
            navController = navController,
            launch = launch
        )
    }
}

@Composable
fun ChatNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    launch: (Context.() -> Unit) -> Unit
) {
    val navigate: (String) -> Unit = remember { { route -> navController.navigate(route) } }
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.CHAT_LIST.route
    ) {
        composable(
            route = Screen.CHAT_LIST.route
        ) {
            ChatListScreen(
                launch = launch,
                navigate = navigate
            )
        }
        composable(
            route = "${Screen.PROFILE_SEARCH.route}{$KEYWORD}",
            arguments = listOf(
                navArgument(name = KEYWORD) {
                    type = NavType.StringType
                }
            )
        ) {
            SearchProfilesScreen(
                launch = launch,
                navigate = navigate
            )
        }
        composable(
            route = "${Screen.MESSAGE_LIST.route}?$CHAT_ID={$CHAT_ID}&$USER_ID={$USER_ID}",
            arguments = listOf(
                navArgument(name = CHAT_ID) {
                    type = NavType.LongType
                    defaultValue = NO_ID
                },
                navArgument(name = USER_ID) {
                    type = NavType.LongType
                    defaultValue = NO_ID
                }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern =
                        "chat-app://${Screen.MESSAGE_LIST.route}?$CHAT_ID={$CHAT_ID}&$USER_ID={$USER_ID}"
                }
            )
        ) {
            MessageListScreen(
                launch = launch,
                navigate = navigate,
                popBackStack = { navController.popBackStack() }
            )
        }
    }
}


fun routeToChat(cid: Long? = null, oid: Long? = null) =
    "${Screen.MESSAGE_LIST.route}?${if (cid != null) "$CHAT_ID=$cid" else "$USER_ID=$oid"}"

fun linkToChat(cid: Long? = null, oid: Long? = null) =
    "chat-app://${routeToChat(cid, oid)}"


const val NO_ID = -1L