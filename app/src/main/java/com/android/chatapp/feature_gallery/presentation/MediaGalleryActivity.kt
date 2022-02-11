package com.android.chatapp.feature_gallery.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.chatapp.R
import com.android.chatapp.core.domain.util.launchMobileSettings
import com.android.chatapp.core.presentation.util.ParcelableNavType
import com.android.chatapp.core.presentation.util.assign
import com.android.chatapp.core.presentation.util.with
import com.android.chatapp.core.presentation.util.spacing
import com.android.chatapp.feature_gallery.domain.model.LocalMedia
import com.android.chatapp.feature_gallery.domain.model.MediaFolder
import com.android.chatapp.feature_gallery.presentation.components.TitleTopBar
import com.android.chatapp.feature_gallery.presentation.cropper.CropperScreen
import com.android.chatapp.feature_gallery.presentation.folder_list.FolderListScreen
import com.android.chatapp.feature_gallery.presentation.media_list.MediaListScreen
import com.android.chatapp.feature_gallery.presentation.util.GallerySettings
import com.android.chatapp.feature_gallery.presentation.util.Screen
import com.android.chatapp.ui.theme.ChatAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint

/** Activity Keys **/
const val GALLERY_SETTINGS = "media_gallery_settings"
const val GALLERY_RESULT = "gallery_result"

/** Navigation Keys **/
const val MEDIA_FOLDER = "media_folder"
const val IMAGE_ITEM = "image_item"


class MediaGallery : ActivityResultContract<GallerySettings, LocalMedia?>() {
    override fun createIntent(context: Context, input: GallerySettings): Intent =
        Intent(context, MediaGalleryActivity::class.java).apply {
            putExtra(GALLERY_SETTINGS, input)
        }

    override fun parseResult(resultCode: Int, intent: Intent?): LocalMedia? =
        intent.takeIf { resultCode == Activity.RESULT_OK }?.getParcelableExtra(GALLERY_RESULT)
}


@AndroidEntryPoint
class MediaGalleryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        setContent {
            MediaGalleryApp(
                navigateToSettingsScreen = ::launchMobileSettings,
                settings = intent.getParcelableExtra(GALLERY_SETTINGS)!!,
                postMedia = { key, media ->
                    setResult(Activity.RESULT_OK, Intent().apply {
                        putExtra(key, media)
                    })
                    finish()
                },
                cancel = {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            )
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }
}

@Composable
fun MediaGalleryApp(
    navigateToSettingsScreen: () -> Unit,
    settings: GallerySettings,
    postMedia: (key: String, media: LocalMedia) -> Unit,
    cancel: () -> Unit,
) {
    ChatAppTheme {
        MediaGalleryAppPermissions(
            navigateToSettingsScreen = navigateToSettingsScreen,
            cancel = cancel
        ) {
            val navController = rememberNavController()
            AuthNavHost(
                navController = navController,
                settings = settings,
                postMedia = postMedia,
                cancel = cancel
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MediaGalleryAppPermissions(
    navigateToSettingsScreen: () -> Unit,
    cancel: () -> Unit,
    content: @Composable () -> Unit
) {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(key1 = lifecycle) {
        val permissionsObserver = createPermissionsObserver(permissionsState)
        lifecycle.addObserver(permissionsObserver)
        onDispose {
            lifecycle.removeObserver(permissionsObserver)
        }
    }
    when {
        permissionsState.allPermissionsGranted -> content()
        permissionsState.shouldShowRationale || !permissionsState.permissionRequested ->
            PermissionsRationalContent(
                permissionsState = permissionsState,
                cancel = cancel
            )
        else -> PermissionsDeniedContent(
            navigateToSettingsScreen = navigateToSettingsScreen,
            cancel = cancel
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
fun createPermissionsObserver(
    permissionsState: MultiplePermissionsState,
) = LifecycleEventObserver { _, event ->
    if (event == Lifecycle.Event.ON_START) {
        permissionsState.launchMultiplePermissionRequest()
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsRationalContent(
    permissionsState: MultiplePermissionsState,
    cancel: () -> Unit
) {
    PermissionsNotGrantedBody(
        content = stringResource(id = R.string.gen_gallery_rejected_msg),
        actionContent = stringResource(id = R.string.gen_request_permission_btn),
        action = { permissionsState.launchMultiplePermissionRequest() },
        cancel = cancel
    )
}

@Composable
fun PermissionsDeniedContent(
    navigateToSettingsScreen: () -> Unit,
    cancel: () -> Unit
) {
    PermissionsNotGrantedBody(
        content = stringResource(id = R.string.gen_gallery_do_not_show_again_msg),
        actionContent = stringResource(id = R.string.gen_open_settings_btn),
        action = navigateToSettingsScreen,
        cancel = cancel
    )
}

@Composable
fun PermissionsNotGrantedBody(
    content: String,
    actionContent: String,
    action: () -> Unit,
    cancel: () -> Unit
) {
    Scaffold(
        topBar = {
            TitleTopBar(
                title = stringResource(id = R.string.gen_gallery_title),
                onBackClicked = cancel
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(all = MaterialTheme.spacing.medium)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = content,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.subtitle1
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            Button(
                onClick = action
            ) {
                Text(actionContent)
            }
        }
    }
}


@Composable
fun AuthNavHost(
    modifier: Modifier = Modifier,
    settings: GallerySettings,
    postMedia: (key: String, media: LocalMedia) -> Unit,
    cancel: () -> Unit,
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = "${Screen.FOLDER_LIST.route}{$GALLERY_SETTINGS}",
    ) {
        composable(
            route = "${Screen.FOLDER_LIST.route}{$GALLERY_SETTINGS}",
            arguments = listOf(
                navArgument(name = GALLERY_SETTINGS) {
                    type = ParcelableNavType<GallerySettings>()
                    defaultValue = settings
                }
            )
        ) {
            FolderListScreen(
                modifier = modifier,
                openFolder = { folder -> Screen.MEDIA_LIST.route assign folder to settings with navController },
                cancel = cancel
            )
        }
        composable(
            route = "${Screen.MEDIA_LIST.route}{$MEDIA_FOLDER}/{$GALLERY_SETTINGS}",
            arguments = listOf(
                navArgument(name = GALLERY_SETTINGS) {
                    type = ParcelableNavType<GallerySettings>()
                },
                navArgument(name = MEDIA_FOLDER) {
                    type = ParcelableNavType<MediaFolder>()
                }
            )
        ) {
            MediaListScreen(
                modifier = modifier,
                postMedia = postMedia,
                cropImage = { image -> Screen.CROPPER.route assign image to settings with navController },
                popBackStack = { navController.popBackStack() }
            )
        }
        composable(
            route = "${Screen.CROPPER.route}{$IMAGE_ITEM}/{$GALLERY_SETTINGS}",
            arguments = listOf(
                navArgument(name = GALLERY_SETTINGS) {
                    type = ParcelableNavType<GallerySettings>()
                },
                navArgument(name = IMAGE_ITEM) {
                    type = ParcelableNavType<LocalMedia>()
                }
            )
        ) {
            CropperScreen(
                postMedia = postMedia,
                popBackStack = { navController.popBackStack() }
            )
        }
    }
}