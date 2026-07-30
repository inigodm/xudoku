# Skill: tdd-nueva-funcionalidad

## Uso

Activar solo al crear funcionalidad nueva.

No usar para:
- bugs;
- refactors;
- cambios sin nueva lógica.

## Flujo obligatorio

### 1. Definir comportamiento

Antes de programar aclarar:

- entrada;
- salida/efecto;
- casos límite;
- capa afectada (`model` / `ui` / `ViewModel`).

Si falta información, preguntar.

### 2. RED: test primero

Crear el test antes del código de producción.

Ubicación:

| Cambio | Test |
|---|---|
| `model/SudokuBoard.kt` | `SudokuBoardTest.kt` |
| `model/SudokuGenerator.kt` | `SudokuGeneratorTest.kt` |
| `model/Difficulty.kt` | `SudokuGeneratorTest.kt` o `DifficultyTest.kt` |
| `ui/viewmodel/GameViewModel.kt` | `GameViewModelTest.kt` |
| Nueva clase model | `<Clase>Test.kt` |

El test debe:
- tener nombre descriptivo en backticks;
- seguir Arrange / Act / Assert;
- fallar por ausencia de implementación, no por error del test.

### 3. GREEN: mínima implementación

Implementar solo lo necesario para que el test pase.

No:
- añadir APIs futuras;
- añadir abstracciones innecesarias;
- implementar más casos de los cubiertos.

`model/` no puede importar `android.*` ni `androidx.*`.

### 4. Ampliar cobertura

Añadir tests para:
- casos límite;
- errores;
- estados inesperados.

Repetir RED → GREEN.

### 5. REFACTOR

Solo con todos los tests en verde:

- mejorar naming;
- eliminar duplicación;
- extraer funciones si es necesario;
- añadir KDoc en APIs públicas de `model`.

Ejecutar:

```bash
./gradlew :app:test
```

### 6. Documentación

Si cambia estructura o API:
- actualizar documentación usando `actualizar-documentacion`.

## Reglas obligatorias

1. El test siempre se escribe antes que producción.
2. No refactorizar durante RED.
3. No escribir más código del necesario durante GREEN.
4. Mantener los tests existentes en verde.
5. No introducir Android en `model/`.
6. Una funcionalidad nueva en `model/` requiere TDD.