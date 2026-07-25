# Skill: ui-tema

## Cuándo usar esta skill

Cambiar colores; añadir/cambiar tipografía; activar/desactivar dynamic color (Material You);
forzar modo oscuro/claro; crear UI de la pantalla principal; nuevos Composables o pantallas;
configurar navegación; crear ViewModel de estado del juego.

---

## Estado actual de la UI

```
MainActivity.kt          → enableEdgeToEdge() + XudokuTheme { XudokuNavGraph() }
ui/theme/Color.kt        → Paleta "Vivid Logic / Deep Galactic" completa (30+ colores)
ui/theme/Theme.kt        → XudokuTheme con darkColorScheme fijo (sin dynamic color)
ui/theme/Type.kt         → Quicksand (headers/grid) + Montserrat (labels/cuerpo), via Google Fonts

ui/XudokuNavGraph.kt     → NavHost con rutas: splash / difficulty / game / victory / stats / profile
ui/GameViewModel.kt      → Estado de partida: cells, notes, mistakes, timer, historial

ui/screen/
  SplashScreen.kt        ✅ Con @Preview
  DifficultyScreen.kt    ✅ Con @Preview
  GameScreen.kt          ✅ Con @Preview (usa GameViewModel real)
  VictoryScreen.kt       ✅ Con @Preview (datos muestra: 08:45, 0 errores, Difícil, 24.580 pts)
  StatsScreen.kt         ⚠️ Sin @Preview — datos placeholder, sin persistencia
  ProfileScreen.kt       ⚠️ Sin @Preview — datos placeholder, sin autenticación

ui/components/
  SudokuGrid.kt          ✅ Grid 9×9 con estados de celda, notas, separadores de caja
  NumberPad.kt           ✅ Teclado 1-9 con efecto 3D táctil
  DifficultyCard.kt      ✅ Tarjeta con efecto 3D press y colores por dificultad
  XudokuBottomBar.kt     ✅ Bottom nav 4 tabs (Badges deshabilitado)
```

---

## Estructura de carpetas (estado real)

```
ui/
├── theme/
│   ├── Color.kt        ← 30+ constantes "Vivid Logic"
│   ├── Theme.kt        ← XudokuTheme (dark, sin dynamic color)
│   └── Type.kt         ← Quicksand + Montserrat via Google Fonts
├── screen/
│   ├── SplashScreen.kt
│   ├── DifficultyScreen.kt
│   ├── GameScreen.kt
│   ├── VictoryScreen.kt
│   ├── StatsScreen.kt
│   └── ProfileScreen.kt
├── components/
│   ├── SudokuGrid.kt
│   ├── NumberPad.kt
│   ├── DifficultyCard.kt
│   └── XudokuBottomBar.kt
├── GameViewModel.kt
└── XudokuNavGraph.kt
```

---

## Cómo cambiar la paleta de colores

Edita `Color.kt` — es la fuente de verdad. **Siempre consulta `design/style-tokens.md` antes**
para no desviarse del design system "Vivid Logic". Los colores semánticos clave:

```kotlin
// Colores principales
val Primary          = Color(0xFF7C4DFF)   // Violeta — acento principal, selección, botones CTA
val Secondary        = Color(0xFFAA52FF)   // Violeta claro
val Tertiary         = Color(0xFF00E5FF)   // Cyan — números jugador, progress bars, temporizador

// Superficies (de más oscura a más clara)
val Background           = Color(0xFF0E0E1A)
val SurfaceContainerLow  = Color(0xFF1A1A2E)
val SurfaceContainer     = Color(0xFF1F1F35)
val SurfaceContainerHigh = Color(0xFF2A2A45)
```

> `XudokuTheme` usa **dark color scheme fijo** — `dynamicColor` está desactivado intencionalmente.

---

## Cómo añadir tipografía personalizada (Google Fonts)

El sistema ya está configurado. Para añadir una fuente nueva:

```kotlin
// Type.kt — añadir la fuente
val NuevaFuente = GoogleFont("Nombre")
val NuevaFuenteFamily = FontFamily(Font(googleFont = NuevaFuente, fontProvider = provider))

// Asignarla a un estilo tipográfico
val Typography = Typography(
    displayLarge = TextStyle(fontFamily = NuevaFuenteFamily, ...)
)
```

**Prerequisito**: `R.array.com_google_android_gms_fonts_certs` ya existe en `app/src/main/res/values/font_certs.xml`.
**No borrar ni regenerar ese archivo.**

---

## Cómo crear una pantalla nueva

1. Crear `ui/screen/NombreScreen.kt` con su función `@Composable`
2. Añadir la ruta en `XudokuNavGraph.kt` con `composable("ruta") { NombreScreen(...) }`
3. Conectar callbacks de navegación
4. Añadir `@Preview` al final del archivo (ver patrón más abajo)
5. Actualizar `PROJECT_INDEX.md`

---

## Patrón de @Preview obligatorio

Todas las pantallas y componentes reutilizables deben tener un `@Preview`.
**La función Preview NO puede ser `private`** — el runtime de Compose la invoca por reflexión.

```kotlin
// ── Preview ──────────────────────────────────────────────────────────────────

@androidx.compose.ui.tooling.preview.Preview(
    name           = "NombreScreen",
    showBackground = true,
    device         = "spec:width=393dp,height=851dp,dpi=420"
)
@androidx.compose.runtime.Composable
fun PreviewNombreScreen() {   // <-- NO private
    com.inigo.xudoku.ui.theme.XudokuTheme {
        NombreScreen(
            // parámetros con datos de muestra representativos
        )
    }
}
```

---

## GameViewModel — API pública actual

```kotlin
class GameViewModel : ViewModel() {
    // StateFlows observables
    val cells: StateFlow<Array<Array<CellState>>>
    val notes: StateFlow<Map<Pair<Int, Int>, Set<Int>>>
    val selectedCell: StateFlow<Pair<Int, Int>?>
    val isNotesMode: StateFlow<Boolean>
    val mistakes: StateFlow<Int>
    val elapsedSeconds: StateFlow<Int>
    val isCompleted: StateFlow<Boolean>
    val isLoading: StateFlow<Boolean>

    // Acciones
    fun startGame(difficulty: Difficulty)   // lanza generación en Dispatchers.Default
    fun selectCell(row: Int, col: Int)
    fun enterNumber(number: Int)            // valida vs solution, cuenta errores
    fun clearSelectedCell()
    fun toggleNotesMode()
    fun undoLastMove()
    fun requestHint()                       // rellena una celda vacía de la solution
}
```

**No instanciar `SudokuGenerator` directamente en un Composable** — siempre a través del ViewModel.

---

## Reglas de la capa UI

1. **La UI no accede a `SudokuBoard` directamente** — usa `CellState` del ViewModel.
2. **No instanciar `SudokuGenerator` en un Composable** — la generación ocurre en `Dispatchers.Default` dentro del ViewModel.
3. **No mutar `SudokuGame.puzzle` ni `SudokuGame.solution`** — son inmutables. El estado mutable va en el ViewModel.
4. **`XudokuTheme` envuelve toda la app** — no crear sub-temas locales; usar `MaterialTheme.colorScheme` y `MaterialTheme.typography`.
5. **Todos los colores vienen de `ui/theme/Color.kt`** — no usar `Color(0xFFxxxxxx)` sueltos en los Composables.
6. **Funciones `@Preview` nunca son `private`** — el framework las invoca por reflexión.
7. **`SudokuGame` es un `data class` inmutable** — si necesitas estado de juego mutable, añádelo al ViewModel.

---

## Navegación — rutas actuales

```
"splash"                                   → SplashScreen
"difficulty"                               → DifficultyScreen
"game/{difficultyName}"                    → GameScreen (difficultyName = Difficulty.name)
"victory/{seconds}/{mistakes}/{difficultyName}/{score}" → VictoryScreen
"stats"                                    → StatsScreen
"profile"                                  → ProfileScreen
```

---

## Tests a correr después de cambios en UI

```bash
# Tests unitarios del ViewModel:
./gradlew :app:test

# Compilación (verifica que no hay errores de Compose):
./gradlew assembleDebug

# Tests instrumentados de Compose (requiere emulador):
./gradlew connectedAndroidTest
```

Los cambios en `ui/theme/` y pantallas no tienen tests automáticos actualmente —
verificar visualmente con el @Preview de Android Studio o en emulador.
