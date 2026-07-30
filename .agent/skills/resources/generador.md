# Skill: generador

## Uso

Activar cuando se cambie:
- `SudokuGenerator.generateGame`
- `removeCells`
- generación reproducible (`seed`)
- `MAX_RETRIES`
- estructura de `SudokuGame`
- lógica relacionada con unicidad de puzzles.

## Archivos relevantes

- `model/SudokuGenerator.kt` → `SudokuGame` y generación.
- `model/Difficulty.kt` → objetivo de celdas visibles.
- `model/SudokuBoard.kt` → `countSolutions` usado para garantizar unicidad.
- `SudokuGeneratorTest.kt` → tests de generación.

## Modelo

```kotlin
data class SudokuGame(
    val puzzle: SudokuBoard,
    val solution: SudokuBoard
)
```
SudokuGame es inmutable. El estado del jugador pertenece al ViewModel, no aquí.

## Invariantes
1. puzzle debe tener exactamente una solución.
2. No eliminar la comprobación de unicidad mediante countSolutions.
3. puzzle y solution deben ser copias independientes.
4. Las celdas visibles de puzzle deben coincidir con solution.
5. removeCells debe trabajar sobre una copia, nunca sobre la solución.
6. visibleCells es un objetivo; puede no alcanzarse en dificultades extremas.

## Caso HARDEST

visibleCells = 17 es un objetivo difícil de alcanzar. No tratar como bug que el resultado final no llegue exactamente al objetivo si se mantiene la unicidad.

## Tests

Ejecutar:
```
./gradlew :app:test --tests "com.inigo.xudoku.model.SudokuGeneratorTest
```
