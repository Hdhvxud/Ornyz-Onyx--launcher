// SPDX-License-Identifier: GPL-3.0-or-later
package dev.victorialauncher.ui.applist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
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
import kotlin.math.exp
import kotlin.math.roundToInt

private const val EDGE_INSET_DP = 28f
private const val BELL_AMPLITUDE_DP = 112f
private const val LETTER_HALF_DP = 10f
const val SCRUB_BUBBLE_INSET_DP = EDGE_INSET_DP + BELL_AMPLITUDE_DP + LETTER_HALF_DP + 18f

/**
 * Niagara-inspired alphabet rail. Letter displacement and scale follow the finger continuously,
 * with a large active letter and a smooth curve through nearby letters.
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
    val fingerY = scrubY()
    val spacingPx = band.heightPx / letters.size
    val sigmaPx = 2.6f * spacingPx
    val bellPx = BELL_AMPLITUDE_DP * density
    val activeIndex = fingerY?.let {
        ScrubberGeometry.indexForY(it, band.topPx, band.heightPx, letters.size)
    } ?: -1
    val accent = MaterialTheme.colorScheme.primary
    val activeText = MaterialTheme.colorScheme.onPrimary
    val idleText = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .width(56.dp)
            .fillMaxHeight(),
    ) {
        letters.forEachIndexed { index, letter ->
            val centerY = ScrubberGeometry.letterCenterY(
                index, band.topPx, band.heightPx, letters.size
            )
            val distancePx = fingerY?.let { it - centerY }
            val gain = if (distancePx == null || sigmaPx <= 0f) {
                0f
            } else {
                exp(-(distancePx * distancePx) / (2f * sigmaPx * sigmaPx))
            }
            val selected = index == activeIndex
            val outward = (bellPx + pullPx()) * gain

            Box(
                modifier = Modifier
                    .align(if (side == EdgeSide.LEFT) Alignment.TopStart else Alignment.TopEnd)
                    .offset {
                        val x = if (side == EdgeSide.LEFT) outward else -outward
                        IntOffset(x.roundToInt(), (centerY - spacingPx / 2f).roundToInt())
                    }
                    .width(34.dp)
                    .height((spacingPx / density).dp)
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
                    fontSize = if (selected) 18.sp else 11.sp + (2.sp * gain),
                    lineHeight = 18.sp,
                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                )
            }
        }
    }
}
