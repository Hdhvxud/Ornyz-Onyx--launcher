// SPDX-License-Identifier: GPL-3.0-or-later
package dev.victorialauncher.ui.applist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.victorialauncher.data.EdgeSide
import kotlin.math.abs
import kotlin.math.roundToInt

private const val EDGE_INSET_DP = 28f
private const val BELL_AMPLITUDE_DP = 112f
private const val LETTER_HALF_DP = 10f
const val SCRUB_BUBBLE_INSET_DP = EDGE_INSET_DP + BELL_AMPLITUDE_DP + LETTER_HALF_DP + 18f

/**
 * Niagara-inspired alphabet scrubber: the active letter grows into a clear, prominent
 * accent bubble and nearby letters fan outward around the user's finger.
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
    val baseCellPx = band.heightPx / letters.size.coerceAtLeast(1)

    Box(
        modifier = modifier
            .width(56.dp)
            .fillMaxHeight(),
    ) {
        letters.forEachIndexed { index, letter ->
            val centerY = ScrubberGeometry.letterCenterY(
                index, band.topPx, band.heightPx, letters.size
            )
            val distance = if (activeIndex < 0) 99 else abs(index - activeIndex)
            val gain = when (distance) {
                0 -> 1f
                1 -> 0.58f
                2 -> 0.25f
                3 -> 0.08f
                else -> 0f
            }
            val selected = distance == 0 && activeIndex >= 0
            val fanPx = (BELL_AMPLITUDE_DP * density * gain) + pullPx() * gain

            Box(
                modifier = Modifier
                    .align(if (side == EdgeSide.LEFT) Alignment.TopStart else Alignment.TopEnd)
                    .offset {
                        val x = if (side == EdgeSide.LEFT) fanPx else -fanPx
                        IntOffset(x.roundToInt(), (centerY - baseCellPx / 2f).roundToInt())
                    }
                    .width(34.dp)
                    .height((baseCellPx / density).dp)
                    .graphicsLayer {
                        val scale = 1f + 0.62f * gain
                        scaleX = scale
                        scaleY = scale
                        alpha = 0.70f + 0.30f * gain
                    }
                    .padding(horizontal = 2.dp, vertical = 1.dp)
                    .background(
                        color = if (selected) accent else Color.Transparent,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = letter.toString(),
                    color = if (selected) activeText else idleText,
                    fontSize = if (selected) 18.sp else if (gain > 0f) 13.sp else 11.sp,
                    lineHeight = 18.sp,
                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                )
            }
        }
    }
}
