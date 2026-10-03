package com.example.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

/** Decode once per asset, never inside the animation draw loop. */
@Composable
fun rememberGameBitmap(path: String): ImageBitmap {
    val context = LocalContext.current
    return remember(context, path) {
        context.assets.open("images/$path").use { stream ->
            requireNotNull(BitmapFactory.decodeStream(stream)) { "Invalid game image: $path" }.asImageBitmap()
        }
    }
}

@Composable
fun GameArt(
    path: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit
) {
    Image(rememberGameBitmap(path), contentDescription, modifier, contentScale = contentScale)
}
