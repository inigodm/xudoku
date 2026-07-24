# Skill: dificultad

## Cuándo usar esta skill

Actívala cuando la petición del usuario implique:
- Añadir o renombrar un nivel de dificultad
- Cambiar el número de celdas visibles de un nivel existente
- Exponer la dificultad en la UI (selector, label, etc.)
- Entender por qué un nivel genera puzzles más o menos difíciles de lo esperado

## Archivos relevantes

| Archivo | Rol |
|---|---|
| `app/src/main/java/com/inigo/xudoku/model/Difficulty.kt` | **Archivo principal** — enum de niveles |
| `app/src/main/java/com/inigo/xudoku/model/SudokuGenerator.kt` | Consume `difficulty.visibleCells` en `generateGame` |
| `app/src/test/java/com/inigo/xudoku/model/SudokuGeneratorTest.kt` | Tests de recuento de celdas por dificultad |

## Valores actuales

```kotlin
enum class Difficulty(val visibleCells: Int) {
    VERY_EASY(40),   // ~40 celdas visibles de 81
    EASY(30),        // ~30 celdas visibles
    MEDIUM(25),      // ~25 celdas visibles
    HARD(20),        // ~20 celdas visibles
    HARDEST(17)      // 17 celdas visibles — mínimo teórico con solución única
}
```

## Invariantes que no se pueden romper

1. **No bajar de 17 en ningún nivel** — 17 es el mínimo teórico probado matemáticamente para un puzzle de Sudoku 9×9 con solución única. Un valor inferior puede hacer que `removeCells` no alcance nunca el target y la generación entre en bucle o devuelva puzzles con múltiples soluciones.
2. **Los valores deben ser estrictamente decrecientes** — cada nivel debe tener menos celdas visibles que el anterior para mantener la semántica de "más difícil".
3. **`visibleCells` es el *target*, no un valor exacto** — `removeCells` puede no alcanzar el target en puzzles muy difíciles (especialmente `HARDEST`); el generador devuelve el mejor resultado posible tras `MAX_RETRIES = 5` intentos.

## Cómo añadir un nuevo nivel (ejemplo)

```kotlin
// Añadir entre HARD y HARDEST:
enum class Difficulty(val visibleCells: Int) {
    VERY_EASY(40),
    EASY(30),
    MEDIUM(25),
    HARD(20),
    EXPERT(18),   // ← nuevo nivel
    HARDEST(17)
}
```

Después de añadirlo:
1. Actualizar cualquier `when(difficulty)` exhaustivo en la UI si existe.
2. Añadir un test en `SudokuGeneratorTest` que verifique el recuento de celdas para el nuevo nivel.
3. Correr `./gradlew test` para confirmar que los tests existentes siguen en verde.

## Tests a correr después de cambios

```bash
./gradlew :app:test --tests "com.inigo.xudoku.model.SudokuGeneratorTest"
```

Tests relevantes:
- `` `VERY_EASY produces roughly 40 visible cells` ``
- `` `EASY produces roughly 30 visible cells` ``
- `` `HARD produces at most 25 visible cells` ``
