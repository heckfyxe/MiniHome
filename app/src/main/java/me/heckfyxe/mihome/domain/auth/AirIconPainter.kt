package me.heckfyxe.mihome.domain.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.heckfyxe.mihome.ui.theme.MiHomeTheme

@Composable
fun rememberAirIconPainter(alpha: Float = 1f): Painter {
    val airPainter = rememberVectorPainter(Icons.Default.Air)
    val bgColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = alpha)
    val iconTint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = alpha)

    return remember(airPainter, bgColor, iconTint) {
        object : Painter() {
            override val intrinsicSize = Size.Unspecified

            override fun DrawScope.onDraw() {
                drawRoundRect(
                    color = bgColor,
                    cornerRadius = CornerRadius(size.minDimension * 0.25f)
                )
                val padding = size.width * 0.4f
                translate(padding / 2, padding / 2) {
                    with(airPainter) {
                        draw(
                            Size(size.width - padding, size.height - padding),
                            colorFilter = ColorFilter.tint(iconTint)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun AirIconPreview() {
    MiHomeTheme {
        Image(modifier = Modifier.size(100.dp), painter = rememberAirIconPainter(), contentDescription = null)
    }
}
