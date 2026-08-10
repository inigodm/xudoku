# PROJECT_INDEX.md

## Archivos relevantes

### Capa de modelo (`model/`)

| Archivo | Qué hace | Cuándo tocarlo |
|---|---|---|
| `app/src/main/java/com/inigo/xudoku/model/SudokuBoard.kt` | Define el tablero 9×9 (`Array<IntArray>`), operadores `get`/`set`, validación de constraints (fila/col/caja), solver por backtracking (`solve`, `solveFrom`), contador de soluciones (`countSolutions`), generador de tablero completo aleatorio (`generateComplete`) | Nunca sin leer `.agent/skills/tablero-core.md` y pasar los 19 tests |
| `app/src/main/java/com/inigo/xudoku/model/SudokuGenerator.kt` | Genera puzzles jugables con solución única: rellena tablero completo, elimina celdas una a una verificando que `countSolutions(2) == 1`. Contiene también `SudokuGame(puzzle, solution)` | Nunca sin leer `.agent/skills/generador.md` y pasar los 19 tests |
| `app/src/main/java/com/inigo/xudoku/model/Difficulty.kt` | Enum con 5 niveles (`VERY_EASY`→40 celdas visibles, …, `HARDEST`→17). Los valores están calibrados; 17 es el mínimo teórico con solución única | Solo si se añade un nuevo nivel de dificultad; no cambiar los existentes |
| `app/src/main/java/com/inigo/xudoku/model/scoring/ScoreConfig.kt` | Objeto con constantes configurables del sistema de puntuación (base, multiplicadores, bonus y penalizaciones) | Al balancear la economía del juego o cambiar recompensas |
| `app/src/main/java/com/inigo/xudoku/model/scoring/ScoreManager.kt` | Gestor con estado que calcula los puntos por movimiento y puntuación final, aplicando reglas de `ScoreConfig` | Al cambiar la lógica de cálculo de puntuación |
| `app/src/main/java/com/inigo/xudoku/model/progression/ProgressionModels.kt` | Define entidades como `Rank` (con soporte i18n vía `nameResId`), `League` (con referencia visual `@DrawableRes iconResId`), `BonusType` (para bonus de XP en partidas) y el estado global de progreso `ProgressionState` | Al modificar el sistema de ligas, rangos o bonificadores de XP |
| `app/src/main/java/com/inigo/xudoku/model/progression/ProgressionConfig.kt` | Configuración matemática del sistema de progresión (fórmula de nivel base, exponentes, XP por partida) | Al balancear la curva de aprendizaje y XP |
| `app/src/main/java/com/inigo/xudoku/model/progression/ProgressionManager.kt` | Gestor sin estado que calcula XP por partida, determina nivel basado en XP total, rangos, e interacciones con rachas (streaks) | Al cambiar las reglas matemáticas de conversión de puntos a XP |
| `app/src/main/java/com/inigo/xudoku/model/history/SudokuGameResult.kt` | Entidad Room (`@Entity`) que guarda el resultado completo de una partida para historial, estadísticas y futura sincronización online | Al añadir nuevos datos a persistir |

### Capa de Datos (Persistencia y Repositorios)

| Archivo | Qué hace | Cuándo tocarlo |
|---|---|---|
| `app/src/main/java/com/inigo/xudoku/data/local/XudokuDatabase.kt` | Configuración de RoomDatabase y `TypeConverters` (UUID, Enum, Dates) | Al añadir nuevas Entidades a la DB |
| `app/src/main/java/com/inigo/xudoku/data/local/SudokuGameResultDao.kt` | DAO con queries SQL para obtener histórico, mejores tiempos, puntuaciones, etc | Al requerir nuevas consultas |
| `app/src/main/java/com/inigo/xudoku/data/repository/GameHistoryRepository.kt` | Interfaz y su implementación concreta (`RoomGameHistoryRepository`) para acceder a los datos de Room | Al añadir lógica de negocio relacionada con la base de datos |
| `app/src/main/java/com/inigo/xudoku/data/repository/ProgressionRepository.kt` | Interfaz y su implementación en SharedPreferences (`SharedPreferencesProgressionRepository`) para persistir XP, Rachas (Streaks), y último día jugado | Al modificar qué datos de progresión y experiencia de usuario persistimos de manera local |

### Inyección de Dependencias

| Archivo | Qué hace | Cuándo tocarlo |
|---|---|---|
| `app/src/main/java/com/inigo/xudoku/di/AppModule.kt` | Módulo de Koin que inyecta la Base de datos, DAO, Repositorios y ViewModels | Al añadir nuevas dependencias |
| `app/src/main/java/com/inigo/xudoku/XudokuApplication.kt` | Entry point de la aplicación que inicializa Koin | Raramente |

### Capa de UI (`ui/`)

| Archivo | Qué hace | Cuándo tocarlo |
|---|---|---|
| `app/src/main/java/com/inigo/xudoku/MainActivity.kt` | Entry point: `enableEdgeToEdge()` + `XudokuTheme { XudokuNavGraph() }`. Sin lógica de negocio | Solo si se añaden permisos o se cambia el punto de entrada |
| `app/src/main/java/com/inigo/xudoku/ui/XudokuNavGraph.kt` | Grafo de navegación con `NavHost`: define rutas `splash`, `difficulty`, `game/{difficultyName}`, `victory/{seconds}/{mistakes}/{difficultyName}/{score}`, `game_over/{seconds}/{mistakes}`, `stats`, `profile`. Instancia `GameViewModel` en el destino `game/` | Al añadir nuevas pantallas/rutas o cambiar parámetros de navegación |
| `app/src/main/java/com/inigo/xudoku/ui/GameViewModel.kt` | ViewModel con toda la lógica de estado mutable de una partida: `cells`, `notes`, `selectedCell`, `isNotesMode`, `mistakes`, `elapsedSeconds`, `isCompleted`, `isLoading`, `currentScore`, `completedNumbers`, `scoreEvents`. Acciones: `startGame`, `selectCell`, `enterNumber`, `clearSelectedCell`, `toggleNotesMode`, `undoLastMove`, `requestHint`. Utiliza `ScoreManager`. Inyecta `GameHistoryRepository`, `ProgressionRepository` y guarda en SQLite (`saveGameResult`). Maneja auto-borrado de notas al completarse un número, y la agregación de XP y Streaks al finalizar. | Al añadir nuevo estado de juego (ej: power-ups) o reglas del tablero |
| `app/src/main/java/com/inigo/xudoku/ui/StatsViewModel.kt` | ViewModel para la pantalla de estadísticas. Inyecta `GameHistoryRepository` para cargar el histórico, calcular win rate, streaks, evolución de puntos y separar por dificultad. | Al cambiar la lógica de cálculo de estadísticas |
| `app/src/main/java/com/inigo/xudoku/ui/ProgressionViewModel.kt` | ViewModel global que provee el estado de progresión actualizado (Nivel, Rango, XP, Streaks) para ser consumido por las diferentes pantallas de UI. | Al necesitar reflejar nuevos estados o propiedades del jugador |
| `app/src/main/java/com/inigo/xudoku/ui/theme/Color.kt` | Paleta completa "Vivid Logic / Deep Galactic" — 30+ constantes de color (Primary, Secondary, Tertiary, superficies, errores). Fuente de verdad de colores | Al cambiar la paleta; leer `design/style-tokens.md` antes |
| `app/src/main/java/com/inigo/xudoku/ui/theme/Theme.kt` | `XudokuTheme` con `darkColorScheme` fijo (sin dynamic color). Asigna todos los slots semánticos de Material3 | Al cambiar el sistema de temas |
| `app/src/main/java/com/inigo/xudoku/ui/theme/Type.kt` | Escala tipográfica Material3 con Quicksand (headers, números del grid) y Montserrat (labels, cuerpo). Usa Google Fonts downloadable con `R.array.com_google_android_gms_fonts_certs` | Al añadir estilos tipográficos o cambiar fuentes |

### Pantallas (`ui/screen/`)

| Archivo | Qué hace | Cuándo tocarlo |
|---|---|---|
| `app/src/main/java/com/inigo/xudoku/ui/screen/SplashScreen.kt` | Splash animado (~2 s): logo + "SUDOKU" con animación de escala y fade, luego llama `onSplashComplete`. Incluye `@Preview` | Al cambiar la animación de entrada o el logo |
| `app/src/main/java/com/inigo/xudoku/ui/screen/DifficultyScreen.kt` | Selección de dificultad: TopAppBar (← SUDOKU ⚙), logo con glow, 4 `DifficultyCard` 3D, barra de progreso global con gradient. Incluye `@Preview` | Al añadir dificultades o cambiar la selección |
| `app/src/main/java/com/inigo/xudoku/ui/screen/GameScreen.kt` | Pantalla de juego activo: TopAppBar centrado con chip de dificultad + timer en segunda fila, HUD de mistakes + **score** + level, `SudokuGrid`, toolbar de acciones, `NumberPad`. Evalúa `viewModel.getFinalScore()` al ganar y maneja el fin de juego al llegar a 3 errores. Pasa eventos de puntos al grid. Incluye `@Preview` | Al cambiar el layout de juego; **conecta con `GameViewModel`** |
| `app/src/main/java/com/inigo/xudoku/ui/screen/VictoryScreen.kt` | Victoria: TopAppBar, confeti (4 colores brand), badge NUEVA MARCA, puntuación, StatCards con íconos (timer/cancel), card de dificultad con dots, barra XP gradient, botones "Siguiente Nivel" y "Menú Principal". Tab Badges activo en bottom nav. Incluye `@Preview` con datos reales | Al cambiar el layout de victoria |
| `app/src/main/java/com/inigo/xudoku/ui/screen/StatsScreen.kt` | Estadísticas reales del jugador: cabecera, chips de filtro, logros totales, gráfica de evolución, stat cards individuales y recent flow. Conectado a `StatsViewModel` para mostrar los datos de Room. | Al cambiar el layout de las estadísticas |
| `app/src/main/java/com/inigo/xudoku/ui/screen/ProfileScreen.kt` | Perfil de usuario: Avatar dinámico estilo Origami que cambia en base a la Liga del jugador y muestra su sub-rango (I, II, III, IV), stats (partidas/win rate/streak), account settings con 3 filas, botón de logout. Datos parcialmente reales a través de ProgressionState. | Al implementar autenticación o actualizar visualización de avatares |

### Componentes (`ui/components/`)

| Archivo | Qué hace | Cuándo tocarlo |
|---|---|---|
| `app/src/main/java/com/inigo/xudoku/ui/components/SudokuGrid.kt` | Grid 9×9 con estados: celda seleccionada (ring Primary), celdas con mismo número (highlight Tertiary), celdas del mismo bloque/fila/col (tinte suave), errores (rojo), notas (`NoteGrid` 3×3), animación exacta de puntos en aciertos vía `ScoreAnimationEvent`. Líneas separadoras de bloque via Canvas | Al cambiar el aspecto visual del tablero |
| `app/src/main/java/com/inigo/xudoku/ui/components/NumberPad.kt` | Teclado numérico 5+5 (1–5 en fila 1, 6–9+borrar en fila 2) con efecto 3D táctil (`animateDpAsState`). El número activo se resalta en Tertiary. `DeleteKey` usa SecondaryContainer | Al cambiar el layout o aspecto del teclado |
| `app/src/main/java/com/inigo/xudoku/ui/components/DifficultyCard.kt` | Tarjeta de dificultad con efecto 3D press (`offset(y = offsetY)` animado). Datos visuales en `DifficultyVisuals` (colores, label, icono). Helper `difficultyVisuals()` mapea `Difficulty` → colores correctos | Al cambiar colores o layout de las tarjetas de dificultad |
| `app/src/main/java/com/inigo/xudoku/ui/components/XudokuBottomBar.kt` | Bottom nav con 4 tabs (Play/Stats/Badges/Profile). Tab activo: círculo `SecondaryContainer`. Badges deshabilitado (sin pantalla). Tab seleccionado recibe pill circular. Utiliza nombres externalizados para i18n. | Al añadir tabs o cambiar iconos |

### Internacionalización (i18n)

| Archivo | Qué hace | Cuándo tocarlo |
|---|---|---|
| `app/src/main/res/values/strings.xml` | Textos base en Inglés (idioma por defecto si no hay coincidencia). Centraliza todos los strings de la aplicación. | Siempre que se añada o modifique un texto visible en UI |
| `app/src/main/res/values-es/strings.xml` | Traducciones al Castellano | Al actualizar la base en inglés |
| `app/src/main/res/values-eu/strings.xml` | Traducciones al Euskera | Al actualizar la base en inglés |

> **Regla de Internacionalización:** Todos los textos en la interfaz deben extraerse a estos XMLs y mostrarse mediante `stringResource(R.string...)`. En la capa modelo, cuando una entidad de negocio represente un concepto nombrable (ej: `Rank`), debe almacenar un `@StringRes val nameResId: Int` para mapear el ID del texto en lugar de guardar directamente un `String` fijo, manteniendo así el soporte completo de multilenguaje en Compose.

### Tests

| Archivo | Qué hace | Cuándo tocarlo |
|---|---|---|
| `app/src/test/java/com/inigo/xudoku/model/SudokuBoardTest.kt` | 8 tests unitarios JVM: `isValid` (3 casos), `solve` (2 casos), `generateComplete` (3 casos), `copy` | Al cambiar la API pública de `SudokuBoard`; los tests deben seguir en verde |
| `app/src/test/java/com/inigo/xudoku/model/SudokuGeneratorTest.kt` | 11 tests unitarios JVM: unicidad de solución, constraints del tablero solución, recuento de celdas visibles por dificultad, independencia puzzle/solution | Al cambiar `SudokuGenerator` o `SudokuGame`; los tests deben seguir en verde |
| `app/src/test/java/com/inigo/xudoku/ui/ScoreTest.kt` | Tests unitarios JVM validando lógica interna de `ScoreManager`: multiplicadores de tiempo, bonus de región y triple combo, y cálculo final | Al cambiar la fórmula de puntos |
| `app/src/test/java/com/inigo/xudoku/model/progression/ProgressionManagerTest.kt` | Tests unitarios JVM validando los cálculos matemáticos de `ProgressionManager` (XP de partida, Nivel según XP total, curva de requerimientos) | Al cambiar el sistema matemático de progresión |
| `app/src/test/java/com/inigo/xudoku/ui/ProgressionViewModelTest.kt` | Tests unitarios JVM validando el comportamiento asíncrono y la gestión de flujos (StateFlow, agregación de XP, reseteo de rachas) | Al alterar lógica asíncrona de niveles en UI |
| `app/src/test/java/com/inigo/xudoku/ExampleUnitTest.kt` | Placeholder de Android Studio (`assertEquals(4, 2+2)`). Sin valor real | Puede eliminarse en cualquier momento |
| `app/src/androidTest/java/com/inigo/xudoku/ExampleInstrumentedTest.kt` | Placeholder de test instrumentado. Sin valor real | Puede eliminarse o reemplazarse con tests de UI Compose cuando existan |

### Build y configuración

| Archivo | Qué hace | Cuándo tocarlo |
|---|---|---|
| `app/build.gradle.kts` | Dependencias del módulo: Compose BOM, Material3, Activity Compose, Navigation Compose, ViewModel Compose, Google Fonts, JUnit. `compileSdk 36`, `minSdk 24` | Al añadir nuevas dependencias |
| `app/src/main/AndroidManifest.xml` | Declara `MainActivity` como launcher. App de actividad única | Al añadir permisos, deep links o actividades adicionales |
| `build.gradle.kts` (raíz) | Solo declara los plugins AGP y Kotlin Compose a nivel de proyecto | Raramente; solo si se actualiza la versión de AGP o Kotlin |
| `settings.gradle.kts` | Define el nombre del proyecto (`xudoku`) y los repositorios Maven. Incluye el módulo `:app` | Al añadir nuevos módulos Gradle |
| `design/style-tokens.md` | Documento canónico de todos los tokens visuales del design system "Vivid Logic": colores, tipografía, espaciados, radios, efectos glass, sombras, animaciones, iconos, mapeo Compose↔Stitch | Consultar antes de crear/modificar cualquier composable |
| `design/NAVIGATION_FLOW.md` | Diagrama de flujo de pantallas, transiciones animadas y datos que cada pantalla necesita del modelo | Consultar antes de modificar la navegación |

---

## Flujo de datos

```
Difficulty (enum)
      │  visibleCells: Int
      ▼
SudokuGenerator.generateGame(difficulty)   [Dispatchers.Default, en GameViewModel]
      │  1. SudokuBoard.generateComplete()  → tablero 9×9 completamente relleno
      │  2. removeCells(puzzle, targetVisible) → elimina celdas verificando countSolutions(2)==1
      ▼
SudokuGame(puzzle: SudokuBoard, solution: SudokuBoard)
      │  puzzle  → tablero con celdas vacías (EMPTY=0)
      │  solution → copia independiente con el tablero completo para verificar respuestas
      ▼
ScoreManager (internal)
      │  Calcula puntos base, multiplicador de velocidad/progreso, bonus (cajas/filas/columnas)
      ▼
GameViewModel
      │  _cells: StateFlow<Array<Array<CellState>>>   — estado de cada celda (valor, isGiven, isError)
      │  _notes: StateFlow<Map<Pair<Int,Int>, Set<Int>>> — notas en lápiz por celda
      │  _currentScore: StateFlow<Int> — puntuación acumulada real en vivo
      │  _scoreEvents: SharedFlow<ScoreAnimationEvent> — eventos para animar flotantes (+70, etc.)
      │  _selectedCell, _isNotesMode, _mistakes, _elapsedSeconds, _isCompleted, _isLoading, _isGameOver, _completedNumbers
      │  startGame() → genera puzzle en background, reinicia ScoreManager, guarda fecha inicio
      │  enterNumber() → valida vs solution, llama a ScoreManager si acierto, llama checkCompletion()
      │  undoLastMove() → historial de movimientos (ArrayDeque<GameMove>)
      │  saveGameResult() → guarda silenciosamente la partida en Room al hacer Game Over o Completar el tablero
      │  (En completado) → ProgressionManager calcula XP -> actualiza SharedPreferencesProgressionRepository
      ▼
GameScreen  →  SudokuGrid + NumberPad + ActionButtons
      │  isCompleted → navega a VictoryScreen(seconds, mistakes, difficulty, score)
      │  isGameOver  → navega a GameOverScreen(seconds, mistakes)
      ▼
VictoryScreen (datos pasados como argumentos de navegación)
      ├─ "Siguiente Nivel" → GameScreen (misma dificultad, nuevo puzzle)
      └─ "Menú Principal"  → DifficultyScreen
```
### Documentacion casos de uso

Si es necesario conocer los casos de uso se pueden consultar en el archivo ./docs/casos-de-uso.md.

> **Invariantes que nunca deben romperse:**
> 1. `puzzle` siempre tiene exactamente una solución
> 2. `puzzle` y `solution` son copias independientes (deep copy)
> 3. Las celdas visibles de `puzzle` coinciden con los valores de `solution`
> 4. `GameViewModel` es el único que muta el estado de juego — los Composables solo llaman a sus métodos públicos
> 5. Las funciones `@Preview` nunca deben ser `private` (el runtime de Compose no puede invocarlas)
