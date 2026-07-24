# Skill: tdd-nueva-funcionalidad

## Cuándo usar esta skill

Actívala cuando el usuario pida **crear una funcionalidad nueva**, por ejemplo:
- "quiero que el jugador pueda pedir una pista"
- "añade un temporizador al juego"
- "implementa el modo de selección de dificultad"
- "quiero que se detecten los errores del jugador en tiempo real"

**No la actives para bugfixes ni refactors** — solo para funcionalidad nueva que aún no existe.

---

## El ciclo TDD: Red → Green → Refactor

```
1. RED    → Escribir el test que describe el comportamiento deseado (falla porque no existe código)
2. GREEN  → Escribir el mínimo código necesario para que el test pase
3. REFACTOR → Limpiar el código sin romper los tests
```

**Regla de oro: nunca escribas código de producción sin un test en rojo primero.**

---

## Protocolo paso a paso

### Paso 1 — Entender la funcionalidad

Antes de escribir nada, responde estas preguntas (si no están claras, pregunta al usuario):

1. ¿Qué entrada recibe la funcionalidad?
2. ¿Qué salida o efecto produce?
3. ¿Qué casos límite o de error hay que cubrir?
4. ¿Afecta a `model/` (lógica pura) o solo a la UI/ViewModel?

---

### Paso 2 — RED: escribir el test primero (lo ejecutara el usuario y te dara el feedback para que continues o arregles el test)

Crea el test en el archivo de test correspondiente **antes de tocar el código de producción**.

**¿Dónde va el test?**

| Si la funcionalidad es en... | Test va en... |
|---|---|
| `model/SudokuBoard.kt` | `SudokuBoardTest.kt` |
| `model/SudokuGenerator.kt` | `SudokuGeneratorTest.kt` |
| `model/Difficulty.kt` | `SudokuGeneratorTest.kt` o nuevo `DifficultyTest.kt` |
| `ui/viewmodel/GameViewModel.kt` | nuevo `GameViewModelTest.kt` en `test/` |
| Clase nueva en `model/` | nuevo `NombreClaseTest.kt` en `test/model/` |

**Formato del test** — backtick, nombre descriptivo en español o inglés, sin implementación todavía:

```kotlin
@Test
fun `solicitar pista devuelve una celda vacía con su valor correcto`() {
    // TODO: implementar
    // given
    // when
    // then
}
```

O con la estructura AAA ya esbozada aunque el código no compile aún:

```kotlin
@Test
fun `solicitar pista devuelve una celda vacía con su valor correcto`() {
    // Arrange
    val game = SudokuGenerator.generateGame(Difficulty.EASY, Random(42))
    val hintProvider = HintProvider(game)  // clase aún no creada

    // Act
    val hint = hintProvider.getHint()      // método aún no creado

    // Assert
    assertNotNull(hint)
    assertTrue(game.puzzle.isEmpty(hint.row, hint.col))
    assertEquals(game.solution[hint.row, hint.col], hint.value)
}
```

**Verifica que el test falla** por la razón correcta (clase/método no existe), no por un error de lógica:

```bash
./gradlew :app:test --tests "com.inigo.xudoku.model.HintProviderTest"
# Esperado: compilation error o NoClassDefFoundError — eso es RED correcto
```

---

### Paso 3 — GREEN: mínimo código para pasar el test

Escribe **solo lo necesario** para que el test en rojo pase. Nada más.

Principios en este paso:
- La implementación puede ser fea, hardcoded, o naive — eso está bien en GREEN
- No añadas métodos extra "por si acaso" — solo lo que el test requiere
- Si necesitas una clase nueva en `model/`, no añadas imports de Android

Ejemplo de implementación mínima (puede ser naive):

```kotlin
// HintProvider.kt — GREEN mínimo
class HintProvider(private val game: SudokuGame) {

    data class Hint(val row: Int, val col: Int, val value: Int)

    fun getHint(): Hint? {
        for (r in 0 until SudokuBoard.SIZE) {
            for (c in 0 until SudokuBoard.SIZE) {
                if (game.puzzle.isEmpty(r, c)) {
                    return Hint(r, c, game.solution[r, c])
                }
            }
        }
        return null
    }
}
```

Verifica que el test pasa:

```bash
./gradlew :app:test --tests "com.inigo.xudoku.model.HintProviderTest"
# Esperado: BUILD SUCCESSFUL — eso es GREEN
```

---

### Paso 4 — Añadir más tests para los casos límite

Con el caso básico en verde, añade tests para los casos que podrían romper la implementación:

```kotlin
@Test
fun `solicitar pista cuando el puzzle está completo devuelve null`() { ... }

@Test
fun `solicitar pista no revela una celda ya visible`() { ... }

@Test
fun `pistas sucesivas no repiten la misma celda`() { ... }  // si aplica
```

Repite el ciclo RED → GREEN para cada test nuevo.

---

### Paso 5 — REFACTOR: limpiar sin romper

Con todos los tests en verde, mejora el código aplicando las reglas de `.agent/skills/buenas-practicas.md`:

- Extrae subfunciones si el método tiene más de ~20 líneas
- Mejora naming si algo no es claro
- Elimina duplicación
- Añade KDoc si la función es pública en `model/`

Después del refactor, vuelve a correr **todos** los tests (no solo los nuevos):

```bash
./gradlew :app:test
# Todos los tests — los 19 originales + los nuevos — deben estar en verde
```

---

### Paso 6 — Actualizar documentación

Si se crearon archivos nuevos o cambió la API:

1. Pide al usuario que active el skill `actualizar-documentacion.md`
2. O hazlo directamente si es obvio: añade la fila del nuevo archivo en `PROJECT_INDEX.md` y crea/actualiza el skill correspondiente

---

## Reglas que no se pueden saltar

1. **El test va primero, siempre.** No hay excepciones.
2. **El test debe fallar por la razón correcta** antes de escribir producción. Si ya pasa sin código, el test es inútil.
3. **No escribas más código de producción del que pide el test.** El YAGNI ("You Aren't Gonna Need It") aplica en GREEN.
4. **No refactorices en RED.** Refactoriza solo cuando estás en verde.
5. **Los 19 tests originales deben seguir en verde** al terminar. Si alguno falla, el refactor rompió algo.
6. **Nada de `android.*` en `model/`** aunque el test lo pida — si la funcionalidad necesita Android, va en ViewModel o en la capa UI.

---

## Ejemplo completo: "detectar si el jugador completó el puzzle"

```kotlin
// PASO 2 — RED: test primero
@Test
fun `puzzle se considera completo cuando todas las celdas coinciden con la solución`() {
    val game = SudokuGenerator.generateGame(Difficulty.EASY, Random(1))
    // Simular que el jugador rellenó todo correctamente
    val playerBoard = game.solution.copy()
    assertTrue(game.isSolvedBy(playerBoard))  // método aún no existe → RED
}

@Test
fun `puzzle no se considera completo si hay una celda incorrecta`() {
    val game = SudokuGenerator.generateGame(Difficulty.EASY, Random(1))
    val playerBoard = game.solution.copy()
    playerBoard[0, 0] = (playerBoard[0, 0] % 9) + 1  // valor incorrecto
    assertFalse(game.isSolvedBy(playerBoard))
}

// PASO 3 — GREEN: mínimo en SudokuGame o clase helper
fun SudokuGame.isSolvedBy(playerBoard: SudokuBoard): Boolean {
    for (r in 0 until SudokuBoard.SIZE) {
        for (c in 0 until SudokuBoard.SIZE) {
            if (playerBoard[r, c] != solution[r, c]) return false
        }
    }
    return true
}

// PASO 5 — REFACTOR: en este caso ya es suficientemente limpio.
// Añadir KDoc:
/**
 * Returns true if [playerBoard] matches the solution in every cell.
 */
```
