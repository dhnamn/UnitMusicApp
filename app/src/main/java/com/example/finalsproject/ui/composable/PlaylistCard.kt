package com.example.finalsproject.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.finalsproject.model.Playlist
import com.example.finalsproject.utils.Utils
import com.example.finalsproject.utils.base64ToImageBitmap

@Composable
fun PlaylistCard(playlist: Playlist, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ElevatedCard(
        onClick = onClick,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        modifier = modifier
    ) {
        val width = 144.dp
        Column(
            modifier = Modifier
                .padding(8.dp)
                .width(width)
        ) {
            val bitmap by remember {
                mutableStateOf(Utils.base64ToImageBitmap(playlist.imgBase64))
            }
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier
                    .size(width)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = playlist.title,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                lineHeight = TextUnit(value = 1.1f, type = TextUnitType.Em),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )
        }
    }
}

