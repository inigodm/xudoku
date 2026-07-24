# Skill: generador

## Cuándo usar esta skill

Actívala cuando la petición del usuario implique:
- Modificar cómo se generan los puzzles (`SudokuGenerator.generateGame`)
- Cambiar la estrategia de eliminación de celdas (`removeCells`)
- Entender por qué un puzzle tiene o no solución única
- Añadir semillas (`seed`) para puzzles reproducibles
- Cambiar el número de reintentos (`MAX_RETRIES`)
- Añadir campos a `SudokuGame` o cambiar su estructura
- Depurar puzzles que parecen tener múltiples soluciones

## Archivos relevantes

| Archivo | Rol |
|---|---|
| `app/src/main/java/com/inigo/xudoku/model/SudokuGenerator.kt` | **Archivo principal** — `SudokuGame`, `SudokuGenerator` |
| `app/src/main/java/com/inigo/xudoku/model/Difficulty.kt` | Parámetro de entrada: cuántas celdas deben quedar visibles |
| `app/src/main/java/com/inigo/xudoku/model/SudokuBoard.kt` | `countSolutions(2)` es la pieza clave del algoritmo |
| `app/src/test/java/com/inigo/xudoku/model/SudokuGeneratorTest.kt` | 11 tests que cubren la lógica de generación |

## Modelo de datos

```kotlin
// SudokuGame: inmutable, data class, NO convertir a clase mutable
data class SudokuGame(
    val puzzle: SudokuBoard,    // tablero con huecos — lo juega el usuario
    val solution: SudokuBoard   // tablero completo — copia independiente
)
```

## Algoritmo de generación (no cambiar sin entender las implicaciones)

```
1. SudokuBoard.generateComplete(random)
      → tablero 9×9 completamente relleno y válido (backtracking aleatorio)

2. puzzle = solution.copy()   ← deep copy, son objetos independientes

3. removeCells(puzzle, difficulty.visibleCells, random):
      → baraja todas las 81 posiciones aleatoriamente
      → para cada posición:
          backup = board[row, col]
          board.clear(row, col)
          if board.countSolutions(2) != 1:
              board[row, col] = backup   // restaurar: sin esta celda hay ambigüedad
          else:
              filledCount--
      → para cuando filledCount <= targetVisible

4. Si tras un pase no se alcanza el target (común en HARDEST):
      → retiene el mejor resultado (menor filledCount)
      → reintenta hasta MAX_RETRIES = 5 veces
```

## Invariantes que no se pueden romper

1. **`puzzle` siempre tiene exactamente 1 solución** — garantizado por `countSolutions(2) == 1` en cada eliminación. Si se cambia `2` por otro valor o se elimina la comprobación, la garantía desaparece.
2. **`puzzle` y `solution` son copias independientes** — `solution.copy()` en el paso 2. Mutarlos en la UI no debe afectar al otro.
3. **Las celdas visibles de `puzzle` coinciden con `solution`** — probado en `` `puzzle visible cells match solution` ``.
4. **`SudokuGame` es `data class`** — no añadir estado mutable. Si la UI necesita rastrear el progreso del jugador (entradas, errores, tiempo), créar un `GameState` o `ViewModel` separado.
5. **`removeCells` recibe una copia del tablero**, no la solución original — si se le pasa la solución directamente y se muta, el `SudokuGame` retornado tendrá un `solution` incompleto.

## Caso especial: HARDEST (17 celdas)

`HARDEST` tiene `visibleCells = 17`, que es el **mínimo teórico** con solución única. En la práctica, `removeCells` raramente llega a 17 en un solo pase; por eso existe el bucle de `MAX_RETRIES`. El juego retorna el mejor resultado aunque no alcance exactamente 17. Esto es comportamiento correcto y esperado — no es un bug.

## Tests a correr después de cambios

```bash
./gradlew :app:test --tests "com.inigo.xudoku.model.SudokuGeneratorTest"
```

Tests críticos:
- `` `generated puzzle has exactly one solution` `` — el más importante; valida la unicidad
- `` `puzzle and solution are independent copies` `` — valida que no hay alias
- `` `puzzle visible cells match solution` `` — valida coherencia entre puzzle y solución
- `` `HARD produces at most 25 visible cells` `` — valida que la dificultad tiene efecto real
