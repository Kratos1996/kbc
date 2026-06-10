package com.ishan.kbc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ishan.kbc.R
import com.ishan.kbc.domain.model.LifelineType
import com.ishan.kbc.ui.theme.Gold
import com.ishan.kbc.ui.theme.SurfaceVariant

@Composable
fun LifelineBar(
    available: Set<LifelineType>,
    onUse: (LifelineType) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp),
    ) {
        LifelineType.entries.forEach { type ->
            val enabled = type in available
            val bg = if (enabled) Gold else SurfaceVariant
            val fg = if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            Text(
                text = labelFor(type),
                color = fg,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(bg)
                    .then(if (enabled) Modifier.clickable { onUse(type) } else Modifier)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }
    }
}

@Composable
private fun labelFor(type: LifelineType): String = when (type) {
    LifelineType.FiftyFifty -> "50:50"
    LifelineType.Audience -> stringResource(R.string.msg_lifeline_audience)
    LifelineType.Phone -> stringResource(R.string.msg_lifeline_phone)
    LifelineType.Expert -> stringResource(R.string.msg_lifeline_expert)
    LifelineType.Flip -> stringResource(R.string.msg_lifeline_flip)
}
