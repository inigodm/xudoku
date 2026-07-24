# Skill: tablero-core

## Cuándo usar esta skill

Actívala cuando la petición del usuario implique:
- Modificar o extender la clase `SudokuBoard`
- Cambiar la lógica de validación de constraints (fila, columna, caja 3×3)
- Cambiar el solver por backtracking (`solve`, `solveFrom`)
- Cambiar el contador de soluciones (`countSolutions`, `countFrom`)
- Cambiar el generador de tablero completo (`generateComplete`, `fillRandomised`)
- Añadir un nuevo operador o método de acceso al tablero
- Depurar un puzzle que no parece resolverse correctamente

## Archivos relevantes

| Archivo | Rol |
|---|---|
| `app/src/main/java/com/inigo/xudoku/model/SudokuBoard.kt` | **Archivo principal** — toda la lógica del tablero |
| `app/src/test/java/com/inigo/xudoku/model/SudokuBoardTest.kt` | 8 tests que cubren la API pública |
| `app/src/test/java/com/inigo/xudoku/model/SudokuGeneratorTest.kt` | Usa `SudokuBoard` indirectamente; puede fallar si se rompe la API |

## Modelo de datos

```kotlin
// Tablero interno: Array<IntArray>, 9×9 de Int
// EMPTY = 0, valores válidos 1..9
// Acceso siempre como board[row, col]  (row primero)

class SudokuBoard(
    private val cells: Array<IntArray> = Array(SIZE) { IntArray(SIZE) }
) {
    companion object {
        const val SIZE = 9       // dimensión del tablero
        const val BOX_SIZE = 3   // dimensión de cada subcaja
        const val EMPTY = 0      // celda vacía
    }
}
```

## API pública — no cambiar firmas sin actualizar tests

| Método | Comportamiento |
|---|---|
| `board[row, col]` | Lee el valor de la celda |
| `board[row, col] = v` | Escribe; lanza `IllegalArgumentException` si `v !in 0..9` |
| `clear(row, col)` | Pone la celda a `EMPTY` |
| `isEmpty(row, col)` | `true` si la celda es `EMPTY` |
| `isValid(row, col, number)` | `true` si `number` no viola ningún constraint; **asume que la celda está vacía antes de la llamada** |
| `solve()` | Resuelve el tablero in-place; `true` si hay solución, `false` si no |
| `countSolutions(limit)` | Cuenta soluciones hasta `limit` (por defecto 2) en una copia; **no muta el tablero original** |
| `copy()` | Deep copy: `Array(SIZE) { cells[it].copyOf() }` |
| `generateComplete(random)` | Companion. Genera un tablero 9×9 completamente relleno y válido con backtracking aleatorio |

## Invariantes que no se pueden romper

1. **`cells` nunca contiene un valor fuera de `0..9`** — el setter lo enforza con `require`.
2. **`isValid` asume celda vacía** — si la celda ya tiene valor, la llamada puede dar falso positivo. El contrato es: primero `clear`, luego `isValid`, luego escribir.
3. **`countSolutions` no muta el tablero original** — trabaja sobre `copy()`. Si alguna vez se cambia esto, el generador romperá la garantía de unicidad.
4. **`generateComplete` siempre devuelve un tablero completamente relleno** — `fillRandomised` usa backtracking y por la estructura del Sudoku siempre encontrará una solución si el tablero inicial está vacío.
5. **Los índices son `row`-first**: `board[row, col]`, `cells[row][col]`. Nunca invertir.

## Patrón de backtracking (no cambiar la estructura)

```kotlin
// Patrón correcto: usar `index` como punto de entrada,
// no iterar desde 0 en cada llamada recursiva.
private fun solveFrom(index: Int): Boolean {
    val nextEmpty = (index until SIZE * SIZE)
        .firstOrNull { isEmpty(it / SIZE, it % SIZE) }
        ?: return true  // no quedan celdas vacías → resuelto

    val row = nextEmpty / SIZE
    val col = nextEmpty % SIZE
    for (num in 1..SIZE) {
        if (isValid(row, col, num)) {
            cells[row][col] = num
            if (solveFrom(nextEmpty + 1)) return true
            cells[row][col] = EMPTY
        }
    }
    return false
}
```

## Tests a correr después de cambios

```bash
./gradlew :app:test --tests "com.inigo.xudoku.model.SudokuBoardTest"
./gradlew :app:test --tests "com.inigo.xudoku.model.SudokuGeneratorTest"
```

Los 19 tests deben estar en verde. Presta especial atención a:
- `` `copy creates an independent deep copy` `` — valida que no hay alias entre tableros
- `` `solve returns false for an unsolvable board` `` — valida el fast-fail del solver
- `` `countSolutions returns 1 for a fully solved board` `` — valida que un tablero completo tiene exactamente 1 solución
