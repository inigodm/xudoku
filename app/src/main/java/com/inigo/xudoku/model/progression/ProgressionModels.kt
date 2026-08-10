package com.inigo.xudoku.model.progression

import androidx.annotation.StringRes
import androidx.annotation.DrawableRes
import com.inigo.xudoku.R
enum class League(val colorName: String, @DrawableRes val iconResId: Int) {
    BRONZE("Cobre / Bronce", R.drawable.ic_league_bronze),
    SILVER("Plata", R.drawable.ic_league_silver),
    GOLD("Oro", R.drawable.ic_league_gold),
    PLATINUM("Platino", R.drawable.ic_league_platinum),
    DIAMOND("Diamante", R.drawable.ic_league_diamond),
    MYTHIC("Amatista / Mítico", R.drawable.ic_league_mythic),
    SUPREME("Galáctico", R.drawable.ic_league_supreme)
}

data class Rank(
    @StringRes val nameResId: Int,
    val league: League,
    val minLevel: Int,
    val maxLevel: Int
)

enum class BonusType(val percentageValue: Double) {
    PERFECT_GAME(0.15),
    FAST_COMPLETION(0.10),
    PERSONAL_BEST(0.10),
    BLIND_SUDOKU(0.15)
}

object RanksList {
    val ranks = listOf(
        Rank(R.string.rank_novato,         League.BRONZE,   1, 4),
        Rank(R.string.rank_aprendiz,       League.BRONZE,   5, 8),
        Rank(R.string.rank_aficionado,     League.BRONZE,   9, 12),
        Rank(R.string.rank_aspirante,      League.BRONZE,   13, 16),
        Rank(R.string.rank_pensador,       League.SILVER,   17, 20),
        Rank(R.string.rank_logico,         League.SILVER,   21, 24),
        Rank(R.string.rank_analista,       League.SILVER,   25, 28),
        Rank(R.string.rank_calculador,     League.SILVER,   29, 32),
        Rank(R.string.rank_tactico,        League.GOLD,     33, 36),
        Rank(R.string.rank_estratega,      League.GOLD,     37, 40),
        Rank(R.string.rank_resolutor,      League.GOLD,     41, 44),
        Rank(R.string.rank_especialista,   League.GOLD,     45, 48),
        Rank(R.string.rank_profesional,    League.PLATINUM, 49, 52),
        Rank(R.string.rank_experto,        League.PLATINUM, 53, 56),
        Rank(R.string.rank_veterano,       League.PLATINUM, 57, 60),
        Rank(R.string.rank_maestro,        League.PLATINUM, 61, 64),
        Rank(R.string.rank_gran_maestro,   League.DIAMOND,  65, 68),
        Rank(R.string.rank_ilustre,        League.DIAMOND,  69, 72),
        Rank(R.string.rank_erudito,        League.DIAMOND,  73, 76),
        Rank(R.string.rank_sabio,          League.DIAMOND,  77, 80),
        Rank(R.string.rank_virtuoso,       League.MYTHIC,   81, 84),
        Rank(R.string.rank_elite,          League.MYTHIC,   85, 88),
        Rank(R.string.rank_leyenda,        League.MYTHIC,   89, 92),
        Rank(R.string.rank_mito,           League.MYTHIC,   93, 96),
        Rank(R.string.rank_xudoku_supremo, League.SUPREME,  97, 100)
    )
}
