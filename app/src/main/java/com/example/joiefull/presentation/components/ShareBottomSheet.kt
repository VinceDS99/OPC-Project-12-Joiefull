package com.example.joiefull.presentation.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.joiefull.R

data class SocialNetwork(
    val name: String,
    val packageName: String,
    val iconRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareBottomSheet(
    clothesId: Int,
    clothesName: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var comment by remember { mutableStateOf("") }
    val articleUrl = "https://joiefull.app/article/$clothesId"

    val socialNetworks = listOf(
        SocialNetwork("Facebook",   "com.facebook.katana",   R.drawable.ic_facebook),
        SocialNetwork("X",          "com.twitter.android",   R.drawable.ic_twitter),
        SocialNetwork("Instagram",  "com.instagram.android", R.drawable.ic_instagram),
        SocialNetwork("WhatsApp",   "com.whatsapp",          R.drawable.ic_whatsapp),
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Partager \"$clothesName\"",
                style = MaterialTheme.typography.titleMedium
            )

            // Champ commentaire avec label accessible
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF888888), RoundedCornerShape(10.dp))
                    .padding(12.dp)
                    .semantics { contentDescription = "Champ commentaire. Contenu actuel : ${if (comment.isBlank()) "vide" else comment}" }
            ) {
                if (comment.isEmpty()) {
                    Text(
                        text = "Ajouter un commentaire…",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                }
                BasicTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 60.dp),
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                )
            }

            Text(
                text = "Partager via",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF444444)
            )

            // Boutons réseaux sociaux — 48dp minimum
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                socialNetworks.forEach { network ->
                    SocialButton(
                        network = network,
                        onClick = {
                            shareToNetwork(
                                context = context,
                                packageName = network.packageName,
                                text = buildShareText(clothesName, comment, articleUrl)
                            )
                            onDismiss()
                        }
                    )
                }
            }

            OutlinedButton(
                onClick = {
                    shareGeneric(context, buildShareText(clothesName, comment, articleUrl))
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Autres applications", fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun SocialButton(network: SocialNetwork, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp) // taille minimale 48dp
        ) {
            Icon(
                painter = painterResource(id = network.iconRes),
                contentDescription = "Partager sur ${network.name}",
                modifier = Modifier.size(36.dp),
                tint = Color.Unspecified
            )
        }
        Text(text = network.name, fontSize = 12.sp, color = Color(0xFF444444))
    }
}

private fun buildShareText(name: String, comment: String, url: String): String =
    if (comment.isBlank()) "Découvre \"$name\" sur Joiefull !\n$url"
    else "$comment\n\nDécouvre \"$name\" sur Joiefull !\n$url"

private fun shareToNetwork(context: Context, packageName: String, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
        setPackage(packageName)
    }
    try { context.startActivity(intent) } catch (e: Exception) { shareGeneric(context, text) }
}

private fun shareGeneric(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Partager via…"))
}