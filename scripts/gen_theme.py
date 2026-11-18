from scripts.gen_step1_configs import w

w('app/src/main/java/com/mdesigns/khata/core/theme/Color.kt', '''
package com.mdesigns.khata.core.theme

import androidx.compose.ui.graphics.Color

val PrimaryTeal = Color(0xFF006A60)
val PrimaryTealDark = Color(0xFF53DBC9)
val PrimaryTealContainer = Color(0xFF70F7E4)
val OnPrimaryTealContainer = Color(0xFF00201C)

val SecondaryContainer = Color(0xFFCCE8E2)
val OnSecondaryContainer = Color(0xFF05201C)

val DebitRed = Color(0xFFD32F2F)
val DebitRedContainer = Color(0xFFFFDAD6)
val OnDebitRedContainer = Color(0xFF410002)

val CreditGreen = Color(0xFF2E7D32)
val CreditGreenContainer = Color(0xFFD5E8D4)
val OnCreditGreenContainer = Color(0xFF002204)

val NeutralGray = Color(0xFF757575)
val BackgroundLight = Color(0xFFFAFDFB)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFDAE5E1)

val BackgroundDark = Color(0xFF191C1B)
val SurfaceDark = Color(0xFF191C1B)
val SurfaceVariantDark = Color(0xFF3F4947)

val OutlineLight = Color(0xFF6F7977)
val OutlineDark = Color(0xFF899390)
''')

w('app/src/main/java/com/mdesigns/khata/core/theme/Type.kt', '''
package com.mdesigns.khata.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 30.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
)
''')

w('app/src/main/java/com/mdesigns/khata/core/theme/Shape.kt', '''
package com.mdesigns.khata.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)
''')

w('app/src/main/java/com/mdesigns/khata/core/theme/Theme.kt', '''
package com.mdesigns.khata.core.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryTealDark,
    onPrimary = BackgroundDark,
    primaryContainer = PrimaryTeal,
    onPrimaryContainer = PrimaryTealContainer,
    secondary = SecondaryContainer,
    onSecondary = OnSecondaryContainer,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryTeal,
    onPrimary = SurfaceLight,
    primaryContainer = PrimaryTealContainer,
    onPrimaryContainer = OnPrimaryTealContainer,
    secondary = SecondaryContainer,
    onSecondary = OnSecondaryContainer,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    outline = OutlineLight
)

@Composable
fun MDesignsKhataTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
''')
print("Theme generated")
