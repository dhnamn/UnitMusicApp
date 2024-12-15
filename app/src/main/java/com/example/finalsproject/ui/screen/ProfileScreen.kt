package com.example.finalsproject.ui.screen

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finalsproject.R
import com.example.finalsproject.model.FetchStatus
import com.example.finalsproject.model.User
import com.example.finalsproject.ui.viewmodel.ProfileScreenViewModel
import com.example.finalsproject.utils.Utils
import com.example.finalsproject.utils.base64ToImageBitmap
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.extension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    viewModel: ProfileScreenViewModel,
    navBack: () -> Unit,
    navToUserPlaylistList: () -> Unit,
    navToFavoriteList: () -> Unit,
    navToAbout: () -> Unit,
    onLogOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopBar(navBack = navBack, modifier = Modifier.statusBarsPadding())
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val scope = CoroutineScope(Dispatchers.IO)
            val context = LocalContext.current
            val imagePicker = rememberFilePickerLauncher(type = PickerType.Image) { result ->
                scope.launch {
                    if (result == null) {
                        return@launch
                    }
                    val data = result.readBytes()
                    viewModel.uploadAvatar(data, result.extension)
                }
            }
            ProfileHeader(
                user = state.user,
                onClickAvatar = {
                    if (
                        ActivityCompat.checkSelfPermission(
                            context,
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                READ_MEDIA_IMAGES
                            } else {
                                READ_EXTERNAL_STORAGE
                            }
                        )
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    READ_MEDIA_IMAGES
                                } else {
                                    READ_EXTERNAL_STORAGE
                                }
                            ),
                            2
                        )
                    } else {
                        imagePicker.launch()
                    }
                }
            )
            ProfileMenuSection(
                menuItems = listOf(
                    MenuItem(
                        onClick = navToUserPlaylistList,
                        icon = Icons.AutoMirrored.Rounded.List,
                        title = stringResource(R.string.my_playlists)
                    ),
                    MenuItem(
                        onClick = navToFavoriteList,
                        icon = Icons.Default.Favorite,
                        title = stringResource(R.string.favorites)
                    )
                )
            )
            ProfileMenuSection(
                menuItems = listOf(
                    MenuItem(
                        onClick = navToAbout,
                        icon = Icons.Default.Info,
                        title = stringResource(R.string.about),
                        subtitle = stringResource(R.string.view_contributors)
                    ),
                    MenuItem(
                        onClick = {
                            viewModel.logOut()
                            onLogOut()
                        },
                        icon = Icons.AutoMirrored.Rounded.Logout,
                        title = stringResource(R.string.log_out),
                    )
                )
            )
        }
        val context = LocalContext.current
        var currentToast by remember { mutableStateOf<Toast?>(null) }
        LaunchedEffect(state.generalStatus) {
            val text = when (val status = state.generalStatus) {
                FetchStatus.Failed -> context.getString(R.string.request_not_sent)
                is FetchStatus.Ready -> status.data
                else -> null
            }
            if (text == null) {
                return@LaunchedEffect
            }
            currentToast?.cancel()
            currentToast = Toast.makeText(context, text, Toast.LENGTH_SHORT).apply { show() }
            viewModel.clearGeneralStatus()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    navBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = stringResource(R.string.app_name))
            }
        },
        navigationIcon = {
            IconButton(onClick = navBack) {
                Icon(
                    imageVector = Icons.Rounded.ChevronLeft,
                    contentDescription = stringResource(R.string.navigate_back)
                )
            }
        },
        modifier = modifier
    )
}

@Composable
private fun ProfileHeader(
    user: FetchStatus<User>,
    onClickAvatar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(onClick = onClickAvatar),
            contentAlignment = Alignment.Center
        ) {
            if (user is FetchStatus.Ready && user.data.avatarImgBase64 != null) {
                var bitmap by remember { mutableStateOf(ImageBitmap(1, 1)) }
                LaunchedEffect(user.data.avatarImgBase64) {
                    bitmap = Utils.base64ToImageBitmap(user.data.avatarImgBase64)
                }
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(60.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        when (user) {
            is FetchStatus.Ready -> {
                Text(text = user.data.username, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Text(text = user.data.email, fontSize = 16.sp, color = Color.Gray)
            }

            else -> {
                Box(
                    Modifier
                        .size(200.dp, 20.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
                Box(
                    Modifier
                        .size(280.dp, 20.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
            }
        }
    }
}

@Composable
private fun ProfileMenuSection(menuItems: List<MenuItem>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray)
            .padding(24.dp)
    ) {
        menuItems.forEach { item ->
            ProfileMenuItem(menuItem = item)
        }
    }
}

@Composable
private fun ProfileMenuItem(
    menuItem: MenuItem,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = menuItem.onClick,
        colors = ButtonDefaults.textButtonColors().copy(contentColor = Color.Black),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Icon(
            imageVector = menuItem.icon,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(24.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = menuItem.title, fontWeight = FontWeight.Medium, fontSize = 20.sp)
            menuItem.subtitle?.let { Text(text = it, fontSize = 14.sp, color = Color.Gray) }
        }
    }
}

private data class MenuItem(
    val onClick: () -> Unit,
    val icon: ImageVector,
    val title: String,
    val subtitle: String? = null,
)