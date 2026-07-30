# Skill: tablero-core

## Uso

Activar cuando se modifique:
- `SudokuBoard`;
- validación de reglas;
- solver;
- `countSolutions`;
- generación de tablero completo;
- API pública del tablero.

## Archivos relevantes

- `model/SudokuBoard.kt` → implementación principal.
- `SudokuBoardTest.kt` → tests directos de la API.
- `SudokuGeneratorTest.kt` → usa el tablero indirectamente.

## Modelo

- Tablero interno: `Array<IntArray>` 9x9.
- Vacío: `EMPTY = 0`.
- Valores válidos: `1..9`.
- Acceso siempre: `board[row, col]`.

## API pública

No cambiar firmas sin actualizar tests.

- `board[row, col]` → lectura/escritura de celdas.
- `clear(row, col)` → establece `EMPTY`.
- `isEmpty(row, col)` → comprueba celda vacía.
- `isValid(row, col, number)` → valida una colocación; requiere celda vacía.
- `solve()` → resuelve in-place.
- `countSolutions(limit)` → cuenta soluciones sin mutar el tablero.
- `copy()` → deep copy.
- `generateComplete(random)` → genera tablero completo válido.

## Invariantes

- Nunca existen valores fuera de `0..9`.
- `isValid` solo se usa sobre celdas vacías.
- `countSolutions` nunca modifica el tablero original.
- `copy()` debe mantener independencia entre tableros.
- `generateComplete()` devuelve un tablero completo y válido.
- Los índices siempre son `row, col` (fila primero).

## Backtracking

Mantener el patrón actual:
- usar índice de avance en la recursión;
- no reiniciar la búsqueda desde el inicio en cada llamada;
- restaurar la celda tras un intento fallido.

## Tests

Ejecutar:

```bash
./gradlew :app:test --tests "com.inigo.xudoku.model.SudokuBoardTest"
./gradlew :app:test --tests "com.inigo.xudoku.model.SudokuGeneratorTest"
```

Los tests deben quedar en verde antes de continuar.