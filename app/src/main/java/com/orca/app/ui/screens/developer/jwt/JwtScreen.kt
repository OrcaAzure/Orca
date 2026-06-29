package com.orca.app.ui.screens.developer.jwt

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orca.app.domain.developer.JwtDecodeResult
import com.orca.app.ui.components.AsyncToolContent
import com.orca.app.ui.components.OrcaButton
import com.orca.app.ui.components.OrcaTextField
import com.orca.app.ui.components.ResultCard
import com.orca.app.ui.components.ToolScaffold
import com.orca.app.ui.theme.OrcaSuccess

@Composable
fun JwtScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: JwtViewModel = hiltViewModel(),
) {
    val token by viewModel.token.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ToolScaffold(title = "JWT Decoder", onBack = onBack, modifier = modifier) {
        Text(
            text = "Decode header & payload — no verification",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 20.dp),
        )

        OrcaTextField(
            value = token,
            onValueChange = viewModel::onTokenChange,
            label = "JWT Token",
            placeholder = "eyJhbGciOiJIUzI1NiIs...",
            singleLine = false,
            onImeAction = viewModel::decode,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OrcaButton(text = "Decode", onClick = viewModel::decode)

        Spacer(modifier = Modifier.height(24.dp))

        AsyncToolContent(
            state = uiState,
            idleMessage = "Paste a JWT and tap Decode",
            successContent = { result -> JwtResultContent(result) },
        )
    }
}

@Composable
private fun JwtResultContent(result: JwtDecodeResult) {
    ResultCard {
        Text(
            text = "Header",
            style = MaterialTheme.typography.labelMedium,
            color = OrcaSuccess,
        )
        Text(
            text = result.header,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
        )

        Text(
            text = "Payload",
            style = MaterialTheme.typography.labelMedium,
            color = OrcaSuccess,
        )
        Text(
            text = result.payload,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
        )

        if (result.signature.isNotBlank()) {
            Text(
                text = "Signature",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = androidx.compose.ui.Alignment.Top,
            ) {
                androidx.compose.foundation.text.selection.SelectionContainer(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = result.signature,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                androidx.compose.material3.IconButton(
                    onClick = {
                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(result.signature))
                    }
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.ContentCopy,
                        contentDescription = "Copy signature",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
