# PROJECT_INDEX.md

## Archivos relevantes

| Archivo | Qué hace | Cuándo tocarlo |
|---|---|---|
| `app/src/main/java/com/inigo/xudoku/model/SudokuBoard.kt` | Define el tablero 9×9 (`Array<IntArray>`), operadores `get`/`set`, validación de constraints (fila/col/caja), solver por backtracking (`solve`, `solveFrom`), contador de soluciones (`countSolutions`), generador de tablero completo aleatorio (`generateComplete`) | Nunca sin leer `.agent/skills/tablero-core.md` y pasar los 19 tests |
| `app/src/main/java/com/inigo/xudoku/model/SudokuGenerator.kt` | Genera puzzles jugables con solución única: rellena tablero completo, elimina celdas una a una verificando que `countSolutions(2) == 1`. Contiene también `SudokuGame(puzzle, solution)` | Nunca sin leer `.agent/skills/generador.md` y pasar los 19 tests |
| `app/src/main/java/com/inigo/xudoku/model/Difficulty.kt` | Enum con 5 niveles (`VERY_EASY`→40 celdas visibles, …, `HARDEST`→17). Los valores están calibrados; 17 es el mínimo teórico con solución única | Solo si se añade un nuevo nivel de dificultad; no cambiar los existentes |
| `app/src/main/java/com/inigo/xudoku/MainActivity.kt` | Entry point de Compose. Actualmente solo muestra un placeholder "Hello Android". Aquí irá la raíz del árbol de UI cuando se construya | Al implementar la primera pantalla del juego |
| `app/src/main/java/com/inigo/xudoku/ui/theme/Color.kt` | Define la paleta de colores del tema Material3 (actualmente boilerplate Purple/Pink de Android Studio) | Al personalizar la paleta visual del juego |
| `app/src/main/java/com/inigo/xudoku/ui/theme/Theme.kt` | Define `XudokuTheme` con soporte dark/light y dynamic color (Android 12+) | Al cambiar el sistema de temas o desactivar dynamic color |
| `app/src/main/java/com/inigo/xudoku/ui/theme/Type.kt` | Define la escala tipográfica Material3 (actualmente solo `bodyLarge` con fuente por defecto) | Al añadir tipografía personalizada (e.g. Google Fonts) |
| `app/src/test/java/com/inigo/xudoku/model/SudokuBoardTest.kt` | 8 tests unitarios JVM: `isValid` (3 casos), `solve` (2 casos), `generateComplete` (3 casos), `copy` | Al cambiar la API pública de `SudokuBoard`; los tests deben seguir en verde |
| `app/src/test/java/com/inigo/xudoku/model/SudokuGeneratorTest.kt` | 11 tests unitarios JVM: unicidad de solución, constraints del tablero solución, recuento de celdas visibles por dificultad, independencia puzzle/solution | Al cambiar `SudokuGenerator` o `SudokuGame`; los tests deben seguir en verde |
| `app/src/test/java/com/inigo/xudoku/ExampleUnitTest.kt` | Placeholder de Android Studio (`assertEquals(4, 2+2)`). Sin valor real | Puede eliminarse en cualquier momento |
| `app/src/androidTest/java/com/inigo/xudoku/ExampleInstrumentedTest.kt` | Placeholder de test instrumentado. Sin valor real | Puede eliminarse o reemplazarse con tests de UI Compose cuando existan |
| `app/build.gradle.kts` | Dependencias del módulo: Compose BOM, Material3, Activity Compose, JUnit. `compileSdk 36`, `minSdk 24` | Al añadir nuevas dependencias (ViewModel, Navigation, etc.) |
| `app/src/main/AndroidManifest.xml` | Declara `MainActivity` como launcher. App de actividad única | Al añadir permisos, deep links o actividades adicionales |
| `build.gradle.kts` (raíz) | Solo declara los plugins AGP y Kotlin Compose a nivel de proyecto | Raramente; solo si se actualiza la versión de AGP o Kotlin |
| `settings.gradle.kts` | Define el nombre del proyecto (`xudoku`) y los repositorios Maven. Incluye el módulo `:app` | Al añadir nuevos módulos Gradle |

---

## Flujo de datos

```
Difficulty (enum)
      │  visibleCells: Int
      ▼
SudokuGenerator.generateGame(difficulty, random)
      │  1. SudokuBoard.generateComplete(random)  → tablero 9×9 completamente relleno
      │  2. removeCells(puzzle, targetVisible)    → elimina celdas verificando countSolutions(2)==1
      ▼
SudokuGame(puzzle: SudokuBoard, solution: SudokuBoard)
      │  puzzle  → tablero con celdas vacías (EMPTY=0) que el jugador debe completar
      │  solution → copia independiente con el tablero completo para verificar respuestas
      ▼
[UI — aún no implementada]
      │  Leerá puzzle para renderizar el tablero inicial
      │  Comparará entradas del usuario contra solution para validar
      │  Usará un ViewModel (por crear) para gestionar el estado mutable de juego
```

> Las invariantes que nunca deben romperse: (1) `puzzle` siempre tiene exactamente una solución, (2) `puzzle` y `solution` son copias independientes (`deep copy` vía `Array(SIZE) { cells[it].copyOf() }`), (3) las celdas visibles de `puzzle` coinciden con los valores de `solution`.
