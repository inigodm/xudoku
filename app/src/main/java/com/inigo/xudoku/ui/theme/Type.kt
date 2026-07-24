package com.inigo.xudoku.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.inigo.xudoku.R

private val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val quicksand = GoogleFont("Quicksand")
private val montserrat = GoogleFont("Montserrat")

val QuicksandFamily = FontFamily(
    Font(googleFont = quicksand, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = quicksand, fontProvider = fontProvider, weight = FontWeight.Bold),
)

val MontserratFamily = FontFamily(
    Font(googleFont = montserrat, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = montserrat, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = montserrat, fontProvider = fontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = montserrat, fontProvider = fontProvider, weight = FontWeight.Bold),
)

val Typography = Typography(
    // Quicksand — display y títulos grandes
    displayLarge = TextStyle(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = (-0.02).em
    ),
    headlineLarge = TextStyle(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    // Quicksand — números del grid (via titleLarge, reutilizado)
    titleLarge = TextStyle(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        lineHeight = 28.sp
    ),
    // Montserrat — cuerpo y labels
    bodyLarge = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 28.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    labelLarge = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.05.em
    ),
    labelSmall = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)