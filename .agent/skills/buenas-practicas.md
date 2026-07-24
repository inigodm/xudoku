# Skill: buenas-practicas

## Cuándo usar esta skill

Actívala siempre que vayas a **escribir o refactorizar código** en este proyecto, independientemente del área. Es la referencia de calidad de código para xudoku.

---

## Principios SOLID aplicados a este proyecto

### S — Single Responsibility
- Cada clase hace una sola cosa: `SudokuBoard` representa el estado del tablero y sus operaciones; `SudokuGenerator` genera puzzles; la UI solo muestra.
- Un `ViewModel` gestiona el estado mutable de la sesión de juego; no mete lógica de generación de puzzles dentro.
- No añadas métodos de UI o presentación en `model/`; no añadas lógica de negocio en Composables.

### O — Open/Closed
- `Difficulty` se puede extender con nuevos valores sin modificar `SudokuGenerator` (consume `difficulty.visibleCells` genéricamente).
- Los Composables deben ser parametrizables con lambdas/callbacks en lugar de acoplarse a un ViewModel específico. Ejemplo: `SudokuGrid(board: SudokuBoard, onCellClick: (row, col) -> Unit)`.

### L — Liskov Substitution
- Aplica principalmente si en el futuro se extraen interfaces. Si `SudokuBoard` implementa una interfaz `IBoard`, cualquier subclase/implementación debe respetar los mismos contratos (`isValid` asume celda vacía, `countSolutions` no muta, etc.).

### I — Interface Segregation
- Preferir funciones/lambdas pequeñas como parámetros en vez de interfaces grandes. En Compose, los callbacks son funciones: `onCellSelected: (Int, Int) -> Unit`, no `interface CellListener`.

### D — Dependency Inversion
- El `ViewModel` no debe instanciar `SudokuGenerator` directamente si se quiere testear en aislamiento; mejor inyectar una función/lambda: `class GameViewModel(private val generate: (Difficulty) -> SudokuGame = SudokuGenerator::generateGame)`.
- En `model/`, `java.util.Random` ya se inyecta como parámetro en `generateGame(difficulty, random)` — mantener este patrón.

---

## Código limpio — reglas concretas para Kotlin/Compose

### Naming
```kotlin
// ✅ Nombres que expresan intención
val selectedCell: Pair<Int, Int>? = null
fun isGivenCell(row: Int, col: Int): Boolean

// ❌ Nombres que requieren comentario para entenderse
val sc: Pair<Int, Int>? = null
fun check(r: Int, c: Int): Boolean
```

### Funciones
- **Máximo ~20 líneas por función**. Si crece más, extraer subfunciones con nombre descriptivo.
- **Sin efectos secundarios ocultos**: una función que devuelve `Boolean` no debe mutar estado silenciosamente.
- **Un nivel de abstracción por función**: no mezclar lógica de negocio con lógica de presentación en el mismo método.

### Inmutabilidad
```kotlin
// ✅ val por defecto
val board = SudokuBoard.generateComplete()

// ❌ var solo si es imprescindible y está justificado
var board = SudokuBoard()  // ¿por qué var aquí?
```

### Composables
```kotlin
// ✅ Composable puro: solo recibe datos y emite eventos
@Composable
fun SudokuCell(
    value: Int,
    isSelected: Boolean,
    isGiven: Boolean,
    isError: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
)

// ❌ Composable que accede al ViewModel directamente (no testeable, no reutilizable)
@Composable
fun SudokuCell(viewModel: GameViewModel, row: Int, col: Int)
```

### Gestión de estado en Compose
- Estado mutable **solo en el ViewModel** o en la función Composable raíz de la pantalla.
- Pasar estado **hacia abajo** como parámetros; eventos **hacia arriba** como lambdas.
- Usar `remember` solo para estado efímero de UI (animaciones, foco); el estado real del juego va en el ViewModel.

```kotlin
// ✅ Patrón correcto
@Composable
fun GameScreen(viewModel: GameViewModel = viewModel()) {
    val gameState by viewModel.state.collectAsStateWithLifecycle()
    GameContent(
        state = gameState,
        onCellClick = viewModel::onCellSelected,
        onNumberInput = viewModel::onNumberInput
    )
}

@Composable
fun GameContent(state: GameState, onCellClick: (Int, Int) -> Unit, onNumberInput: (Int) -> Unit) {
    // Solo layout y rendering, sin lógica
}
```

---

## Manejo de errores

- **No usar excepciones para control de flujo** — usar `require`/`check` solo para precondiciones (como ya hace `SudokuBoard.set`).
- **Sealed classes para resultados con error** en lugar de nullables ambiguos:
  ```kotlin
  sealed class GameResult {
      data class Success(val game: SudokuGame) : GameResult()
      data class Error(val reason: String) : GameResult()
  }
  ```
- **En la UI**, manejar estados de carga con `UiState`:
  ```kotlin
  sealed class GameUiState {
      object Loading : GameUiState()
      data class Ready(val game: SudokuGame, val playerBoard: Array<IntArray>) : GameUiState()
      data class Error(val message: String) : GameUiState()
  }
  ```

---

## Tests

### Reglas de los tests existentes (no romper)
- Nombres en backticks, descriptivos en español o inglés: `` `generated puzzle has exactly one solution` ``
- Un solo `assert` conceptual por test (pueden ser varios `assertEquals` si verifican la misma cosa)
- Usar `Random(seed)` fijo para reproducibilidad: `Random(42)`, `Random(99)`, etc.
- No usar `Thread.sleep` ni coroutines en tests de `model/` (lógica pura, síncrona)

### Nuevos tests — qué cubrir
- **Test de comportamiento, no de implementación**: testear qué devuelve una función, no cómo lo calcula.
- **Tests del ViewModel**: usar `kotlinx-coroutines-test` con `TestDispatcher` para funciones `suspend`.
- **Tests de Composables**: usar `ComposeTestRule` para tests de UI cuando existan pantallas.

### Estructura AAA (Arrange-Act-Assert)
```kotlin
@Test
fun `player input on given cell is rejected`() {
    // Arrange
    val game = SudokuGenerator.generateGame(Difficulty.EASY, Random(1))
    val viewModel = GameViewModel { game }

    // Act
    val firstGivenRow = 0
    val firstGivenCol = (0 until 9).first { !game.puzzle.isEmpty(firstGivenRow, it) }
    viewModel.onCellSelected(firstGivenRow, firstGivenCol)
    viewModel.onNumberInput(5)

    // Assert
    assertEquals(game.puzzle[firstGivenRow, firstGivenCol], viewModel.state.value.playerBoard[firstGivenRow][firstGivenCol])
}
```

---

## Checklist antes de hacer commit

- [ ] ¿Las funciones nuevas tienen KDoc si son públicas en `model/`?
- [ ] ¿Se usó `val` en lugar de `var` donde era posible?
- [ ] ¿Los Composables nuevos son puros (sin acceso directo a ViewModel)?
- [ ] ¿Se corrió `./gradlew test` y los 19 tests están en verde?
- [ ] ¿Las funciones tienen un solo nivel de abstracción?
- [ ] ¿Hay algún `TODO` pendiente que deba resolverse antes de mergear?
- [ ] ¿Se actualizó `PROJECT_INDEX.md` si se añadieron archivos nuevos? (ver skill `actualizar-documentacion.md`)
