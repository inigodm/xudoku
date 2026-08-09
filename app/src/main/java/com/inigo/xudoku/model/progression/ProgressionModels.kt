package com.inigo.xudoku.model.progression

enum class League(val colorName: String) {
    BRONZE("Cobre / Bronce"),
    SILVER("Plata"),
    GOLD("Oro"),
    PLATINUM("Platino"),
    DIAMOND("Diamante"),
    MYTHIC("Mítico"),
    SUPREME("Supremo")
}

data class Rank(
    val name: String,
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
        Rank("Novato",         League.BRONZE,   1, 4),
        Rank("Aprendiz",       League.BRONZE,   5, 8),
        Rank("Aficionado",     League.BRONZE,   9, 12),
        Rank("Aspirante",      League.BRONZE,   13, 16),
        Rank("Pensador",       League.SILVER,   17, 20),
        Rank("Lógico",         League.SILVER,   21, 24),
        Rank("Analista",       League.SILVER,   25, 28),
        Rank("Calculador",     League.SILVER,   29, 32),
        Rank("Táctico",        League.GOLD,     33, 36),
        Rank("Estratega",      League.GOLD,     37, 40),
        Rank("Resolutor",      League.GOLD,     41, 44),
        Rank("Especialista",   League.GOLD,     45, 48),
        Rank("Profesional",    League.PLATINUM, 49, 52),
        Rank("Experto",        League.PLATINUM, 53, 56),
        Rank("Veterano",       League.PLATINUM, 57, 60),
        Rank("Maestro",        League.PLATINUM, 61, 64),
        Rank("Gran Maestro",   League.DIAMOND,  65, 68),
        Rank("Ilustre",        League.DIAMOND,  69, 72),
        Rank("Erudito",        League.DIAMOND,  73, 76),
        Rank("Sabio",          League.DIAMOND,  77, 80),
        Rank("Virtuoso",       League.MYTHIC,   81, 84),
        Rank("Élite",          League.MYTHIC,   85, 88),
        Rank("Leyenda",        League.MYTHIC,   89, 92),
        Rank("Mito",           League.MYTHIC,   93, 96),
        Rank("Xudoku Supremo", League.SUPREME,  97, 100)
    )
}
