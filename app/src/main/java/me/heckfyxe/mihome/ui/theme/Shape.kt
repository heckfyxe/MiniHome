package me.heckfyxe.mihome.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

// Fully-rounded shape for buttons, switches, selected chips, and the nav indicator.
// (Buttons/Switch already default to a fully-rounded shape; provided here for custom controls.)
val PillShape = RoundedCornerShape(percent = 50)