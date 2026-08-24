package com.droidspaces.app.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * The shell every dialog in the app sits in: the platform width opt-out, and a
 * 24.dp Surface on surfaceContainer with a hairline border.
 *
 * It exists because the width had drifted five ways. Most dialogs filled the
 * width minus a 24.dp gutter, some took 0.92f, one took 0.95f, and the
 * notification permission dialog never opted out of the platform width at all,
 * so Android sized it and it sat visibly narrower than the rest.
 *
 * Only the shell is shared. The caller supplies its own content, including its
 * own Column, because dialogs genuinely differ inside: some scroll, some cap
 * their height, some pad differently. Pass those through [modifier], which is
 * applied after the standard width, so `Modifier.fillMaxHeight(0.8f)` works.
 *
 * [borderColor] is here for destructive dialogs, which outline in error.
 */
@Composable
fun DsDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .then(modifier),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            border = BorderStroke(1.dp, borderColor),
            tonalElevation = 0.dp,
            content = content
        )
    }
}
