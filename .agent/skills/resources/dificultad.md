# Skill: dificultad

## Uso

Activar cuando se cambien niveles de dificultad, `visibleCells` o su exposición en UI.

## Archivos relevantes

- `model/Difficulty.kt` → enum de niveles y valores de dificultad.
- `model/SudokuGenerator.kt` → consume `visibleCells` como objetivo de generación.
- `SudokuGeneratorTest.kt` → tests relacionados.


## Valores actuales

```
    VERY_EASY(40),   // ~40 celdas visibles de 81
    EASY(30),        // ~30 celdas visibles
    MEDIUM(25),      // ~25 celdas visibles
    HARD(20),        // ~20 celdas visibles
    HARDEST(17)      // 17 celdas visibles — mínimo teórico con solución única
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