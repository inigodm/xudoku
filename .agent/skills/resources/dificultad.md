# Skill: dificultad

## Uso

Activar cuando se cambien niveles de dificultad, `visibleCells` o su exposición en UI.

## Archivos relevantes

- `model/Difficulty.kt` → enum de niveles, valores de celdas visibles (`visibleCells`) y límite de pistas (`maxHints`).
- `model/SudokuGenerator.kt` → consume `visibleCells` como objetivo de generación.
- `SudokuGeneratorTest.kt` → tests relacionados.


## Valores actuales

```kotlin
    VERY_EASY(40, 3), // ~40 celdas visibles, 3 pistas máx
    EASY(30, 3),      // ~30 celdas visibles, 3 pistas máx
    MEDIUM(25, 2),    // ~25 celdas visibles, 2 pistas máx
    HARD(20, 1),      // ~20 celdas visibles, 1 pista máx
    HARDEST(17, 0)    // 17 celdas visibles, 0 pistas máx
```

## Invariantes que no se pueden romper

1. **No usar valores inferiores a 17.**
2. Los niveles deben mantener **orden descendente** de visibleCells.
3. **visibleCells es un objetivo**, no una garantía exacta de resultado.
4. Añadir un nivel nuevo requiere revisar when(difficulty) y tests asociados.

## Cómo añadir un nuevo nivel (ejemplo)

Después de añadirlo:
1. Actualizar cualquier `when(difficulty)` exhaustivo en la UI si existe.
2. Añadir un test en `SudokuGeneratorTest` que verifique el recuento de celdas para el nuevo nivel.
3. Correr `./gradlew test` para confirmar que los tests existentes siguen en verde.

## Después de cambios

```bash
./gradlew :app:test --tests "com.inigo.xudoku.model.SudokuGeneratorTest"
```