package com.buildstack.recall.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.buildstack.recall.theme.GlassCardBackground
import com.buildstack.recall.theme.GlassCardBorder

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(GlassCardBackground)
            .border(
                width = 1.dp,
                color = GlassCardBorder,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(16.dp),
        content = content
    )
}
