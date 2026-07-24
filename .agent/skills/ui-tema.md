# Skill: ui-tema

## Cuándo usar esta skill

Actívala cuando la petición del usuario implique:
- Cambiar los colores de la app
- Añadir o cambiar tipografía
- Activar/desactivar dynamic color (Material You)
- Forzar modo oscuro o modo claro
- Crear la UI de la pantalla principal del juego
- Añadir nuevos Composables o pantallas
- Configurar navegación entre pantallas
- Crear un ViewModel para el estado del juego

## Archivos relevantes

| Archivo | Rol |
|---|---|
| `app/src/main/java/com/inigo/xudoku/ui/theme/Color.kt` | Paleta de colores del tema Material3 |
| `app/src/main/java/com/inigo/xudoku/ui/theme/Theme.kt` | `XudokuTheme` — envuelve toda la app con el ColorScheme y Typography |
| `app/src/main/java/com/inigo/xudoku/ui/theme/Type.kt` | Escala tipográfica Material3 |
| `app/src/main/java/com/inigo/xudoku/MainActivity.kt` | Entry point — aquí se llama a `XudokuTheme { ... }` y se monta la primera pantalla |

## Estado actual de la UI

```
MainActivity.kt     → Placeholder "Hello Android". Sin lógica de juego.
ui/theme/Color.kt   → Boilerplate Android Studio (Purple/Pink). Sin personalizar.
ui/theme/Theme.kt   → XudokuTheme funcional con dark/light y dynamic color.
ui/theme/Type.kt    → Solo bodyLarge con FontFamily.Default.

NO existen todavía:
  - ui/screen/       (pantalla del juego)
  - ui/components/   (componentes del tablero, teclado numérico, etc.)
  - GameViewModel    (estado mutable del juego)
```

## Cómo cambiar la paleta de colores

Edita `Color.kt` y `Theme.kt`. Usa los slots semánticos de Material3:

```kotlin
// Color.kt — define tus colores
val SudokuBlue80 = Color(0xFFAEC6E8)
val SudokuBlue40 = Color(0xFF1A5C9B)

// Theme.kt — asígnalos a los slots semánticos
private val DarkColorScheme = darkColorScheme(
    primary = SudokuBlue80,
    // ...
)
private val LightColorScheme = lightColorScheme(
    primary = SudokuBlue40,
    // ...
)
```

> Si `dynamicColor = true` (por defecto en Android 12+), los colores de `Color.kt` son ignorados en favor del wallpaper del usuario. Para forzar tu paleta, pon `dynamicColor = false` en `XudokuTheme`.

## Cómo añadir tipografía personalizada (Google Fonts)

1. Añadir dependencia en `app/build.gradle.kts`:
   ```kotlin
   implementation(libs.androidx.compose.ui.text.google.fonts)
   ```
2. Definir la fuente en `Type.kt`:
   ```kotlin
   val provider = GoogleFont.Provider(
       providerAuthority = "com.google.android.gms.fonts",
       providerPackage = "com.google.android.gms",
       certificates = R.array.com_google_android_gms_fonts_certs
   )
   val NunitoFont = GoogleFont("Nunito")
   val NunitoFontFamily = FontFamily(Font(googleFont = NunitoFont, fontProvider = provider))
   ```

## Cómo estructurar la pantalla del juego (cuando se implemente)

```
ui/
├── theme/              ← existente
├── screen/
│   └── GameScreen.kt   ← Composable raíz de la pantalla del juego
├── components/
│   ├── SudokuGrid.kt   ← Componente que renderiza el tablero 9×9
│   ├── NumberPad.kt    ← Teclado numérico 1-9
│   └── GameTopBar.kt   ← Barra superior (dificultad, temporizador, botón nuevo juego)
└── viewmodel/
    └── GameViewModel.kt ← Estado mutable: celda seleccionada, entradas del jugador, errores
```

## ViewModel — patrón recomendado

```kotlin
// GameViewModel.kt — usar lifecycle-viewmodel de AndroidX
// NO poner lógica de generación aquí; llamar a SudokuGenerator desde un coroutine

class GameViewModel : ViewModel() {
    private val _game = MutableStateFlow<SudokuGame?>(null)
    val game: StateFlow<SudokuGame?> = _game

    fun newGame(difficulty: Difficulty) {
        viewModelScope.launch(Dispatchers.Default) {
            _game.value = SudokuGenerator.generateGame(difficulty)
        }
    }
}
```

Añadir a `app/build.gradle.kts`:
```kotlin
implementation(libs.androidx.lifecycle.viewmodel.compose)
```

## Reglas de la capa UI

1. **La UI no accede a `cells` directamente** — siempre usa el operador `board[row, col]`.
2. **No instanciar `SudokuGenerator` en un Composable** — la generación es costosa (~segundos en HARDEST); hacerlo en un `ViewModel` con `Dispatchers.Default`.
3. **No mutar `SudokuGame.puzzle` ni `SudokuGame.solution`** — son la fuente de verdad inmutable. El estado del jugador (qué ha escrito en cada celda) va en el ViewModel.
4. **`XudokuTheme` envuelve toda la app** — no crear sub-temas locales; usar los colores de `MaterialTheme.colorScheme`.

## Tests a correr después de cambios en UI

```bash
# Tests unitarios del ViewModel (cuando exista):
./gradlew :app:test --tests "com.inigo.xudoku.viewmodel.*"

# Tests instrumentados de Compose (cuando existan):
./gradlew connectedAndroidTest
```

Los cambios en `ui/theme/` no tienen tests automáticos actualmente — verificar visualmente en emulador o dispositivo.
