package com.android.chatapp.feature_authentication.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.feature_authentication.presentation.login.LoginScreen
import com.android.chatapp.feature_authentication.presentation.reset_password.ResetPasswordScreen
import com.android.chatapp.feature_authentication.presentation.user_info.UserInfoScreen
import com.android.chatapp.feature_authentication.presentation.util.Screen
import com.android.chatapp.feature_chat.presentation.chatActivity
import com.android.chatapp.ui.theme.ChatAppTheme
import dagger.hilt.android.AndroidEntryPoint

val Context.authActivity
    get() = startActivity(Intent(this.applicationContext, AuthActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    })

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {
    val viewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.keepDrawn
            }
        }
        setContent {
            val authState by viewModel.authState.collectAsState(initial = AuthState.LOADING)
            when (authState) {
                AuthState.LOADING -> Unit
                AuthState.LOGIN -> {
                    viewModel.removeSplash()
                    AuthApp(launchActivity = { launch -> this@AuthActivity.launch() })
                }
                AuthState.LOGGED_IN -> chatActivity
            }
        }
    }
}

@Composable
fun AuthApp(launchActivity: (Context.() -> Unit) -> Unit) {
    ChatAppTheme {
        val navController = rememberNavController()
        AuthNavHost(
            navController = navController,
            launchActivity = launchActivity
        )
    }
}


@Composable
fun AuthNavHost(
    navController: NavHostController,
    launchActivity: (Context.() -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val innerPadding = Modifier.padding(MaterialTheme.spacing.medium)
    NavHost(
        navController = navController,
        startDestination = Screen.LOGIN.route,
        modifier = modifier
    ) {
        composable(route = Screen.LOGIN.route) {
            LoginScreen(
                modifier = innerPadding,
                navigate = { screen -> navController.navigate(screen.route) },
                launchChat = { launchActivity { chatActivity } }
            )
        }
        composable(route = Screen.USER_INFO.route) {
            UserInfoScreen(
                modifier = innerPadding,
                launchActivity = launchActivity
            )
        }
        composable(route = Screen.RESET_PASSWORD.route) {
            ResetPasswordScreen(
                onPopBackStack = { navController.popBackStack() },
                modifier = innerPadding
            )
        }

    }
}
