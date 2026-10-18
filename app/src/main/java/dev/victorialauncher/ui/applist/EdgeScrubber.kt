// SPDX-License-Identifier: GPL-3.0-or-later
package dev.victorialauncher.ui.applist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.victorialauncher.data.EdgeSide
import kotlin.math.roundToInt

private const val EDGE_INSET_DP = 28f
private const val BELL_AMPLITUDE_DP = 112f
private const val LETTER_HALF_DP = 10f
const val SCRUB_BUBBLE_INSET_DP = EDGE_INSET_DP + BELL_AMPLITUDE_DP + LETTER_HALF_DP + 18f

/**
 * A compact, evenly spaced alphabet pod inspired by Niagara's orderly edge index.
 *
 * The pill stays still instead of bowing around the finger. The selected letter gets the
 * system's Material You primary colour, which is derived from the wallpaper on Android 12+.
 * Pointer coordinates are read only to choose the active segment, keeping fast scrubs cheap.
 */
@Composable
fun EdgeScrubber(
    letters: List<Char>,
    scrubY: () -> Float?,
    pullPx: () -> Float,
    band: ScrubBand,
    side: EdgeSide,
    modifier: Modifier = Modifier,
) {
    if (letters.isEmpty()) return
    val density = LocalDensity.current.density
    val activeIndex = scrubY()?.let {
        ScrubberGeometry.indexForY(it, band.topPx, band.heightPx, letters.size)
    } ?: -1
    val accent = MaterialTheme.colorScheme.primary
    val activeText = MaterialTheme.colorScheme.onPrimary
    val idleText = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .width(50.dp)
            .fillMaxHeight(),
    ) {
        Surface(
            modifier = Modifier
                .align(if (side == EdgeSide.LEFT) Alignment.TopStart else Alignment.TopEnd)
                .offset { IntOffset(0, band.topPx.roundToInt()) }
                .width(42.dp)
                .height((band.heightPx / density).dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.58f),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.42f),
            ),
            shadowElevation = 8.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 3.dp, vertical = 5.dp),
            ) {
                letters.forEachIndexed { index, letter ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(vertical = 1.dp)
                            .background(
                                color = if (index == activeIndex) accent.copy(alpha = 0.96f) else Color.Transparent,
                                shape = RoundedCornerShape(50),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = letter.toString(),
                            color = if (index == activeIndex) activeText else idleText.copy(alpha = 0.88f),
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                        )
                    }
                }
            }
        }
    }
}
