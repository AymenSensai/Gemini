package com.aymen.gemini

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun MessageItem(message: Message) {

    val fromUser = message.role == "user"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (fromUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = if (fromUser) 16.dp else 0.dp,
                        topEnd = if (fromUser) 0.dp else 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    )
                )
                .background(if (fromUser) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary)
                .padding(start = 10.dp, top = 5.dp, bottom = 10.dp, end = 10.dp)
        ) {
            Text(
                text = if (fromUser) "You" else "Gemini",
                style = MaterialTheme.typography.bodySmall,
                color = if (fromUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (fromUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
            )
            message.image?.let { image ->
                Spacer(modifier = Modifier.height(4.dp))
                AsyncImage(
                    model = image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(150.dp).clip(RoundedCornerShape(8.dp))
                )
            }
        }
    }
}