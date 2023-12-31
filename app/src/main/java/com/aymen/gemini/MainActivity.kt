package com.aymen.gemini

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.request.ImageRequest
import com.aymen.gemini.ui.theme.GeminiTheme
import com.aymen.gemini.ui.theme.Grey
import com.aymen.gemini.util.ImageHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GeminiTheme {
                GeminiChatScreen()
            }
        }
    }
}

@Composable
fun GeminiChatScreen() {
    val context = LocalContext.current

    val imageRequestBuilder = ImageRequest.Builder(context)

    val imageLoader = ImageLoader.Builder(context).build()

    val coroutineScope = rememberCoroutineScope()

    val viewModel = viewModel<GeminiViewModel>()

    val state = viewModel.state

    val focusManager = LocalFocusManager.current

    val snackBarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
    ) {
        it.forEach { uri ->
            coroutineScope.launch {
                ImageHelper.scaleDownBitmap(uri, imageRequestBuilder, imageLoader)
                    ?.let { bitmap ->
                        viewModel.onEvent(GeminiEvent.OnImageChange(bitmap))
                    }
            }
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is GeminiEffect.ShowSnackBar -> {
                    scope.launch {
                        snackBarHostState.showSnackbar(effect.message)
                    }
                }
            }
        }
    }

    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snackBarHostState, snackbar = {
            Snackbar(
                snackbarData = it,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.secondary
            )
        })
    }, topBar = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(MaterialTheme.colorScheme.primary)
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.gemini_logo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Gemini",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "online",
                    color = Grey,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }, bottomBar = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp)
        ) {
            TextField(
                value = state.text,
                onValueChange = { text ->
                    viewModel.onEvent(GeminiEvent.OnTextChange(text))
                },
                trailingIcon = {
                    if (state.text.isEmpty()) {
                        IconButton(onClick = { galleryLauncher.launch("image/*") }) {
                            Icon(
                                painter = painterResource(id = R.drawable.image_icon),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            focusManager.clearFocus()
                            viewModel.onEvent(GeminiEvent.OnGetImage)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.send_icon),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedTrailingIconColor = MaterialTheme.colorScheme.secondary,
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                placeholder = {
                    Text(
                        text = "Ask question...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            )
        }
    }) {

        Column(
            modifier = Modifier
                .padding(it)
        ) {

            if (state.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            LazyColumn(
                modifier = Modifier,
                reverseLayout = true
            ) {
                items(state.messages) { message ->
                    MessageItem(message)
                }
            }
        }
    }
}