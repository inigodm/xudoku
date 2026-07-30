# Skill: documentar-casos-de-uso

## Uso

Activar cuando:
- El usuario pida documentar o actualizar casos de uso.
- El usuario quiera listar acciones disponibles del juego.
- Se creen tests de lógica de ViewModel.
- Se añada un caso de uso nuevo.

No activar para cambios solo visuales ni cambios en `model/`.

## Catálogo

Ubicación:
```.agent/docs/casos-de-uso.md```


Si no existe, crearlo.

El catálogo documenta comportamiento:
- acción del usuario;
- precondiciones;
- método del ViewModel;
- cambios observables;
- efecto UI;
- estado de tests.

Mantener IDs `CU-XX` estables. Si se elimina un caso, marcarlo como `[ELIMINADO]`.

## Proceso

1. Leer el catálogo existente.
2. Leer solo:
    - `ui/GameViewModel.kt`
    - `ui/XudokuNavGraph.kt`
    - `ui/screen/GameScreen.kt`
    - tests relacionados si existen.

No explorar otras pantallas ni hacer búsquedas globales.

3. Crear/actualizar fichas:
    - un CU por acción pública del ViewModel;
    - un CU por flujo relevante de navegación.

4. Actualizar `Tested` según exista cobertura.

## Final

Mostrar resumen:
- casos creados;
- casos modificados;
- casos pendientes de test.

## No hacer

- No escribir tests (usar skill de testing/TDD).
- No actualizar estructura de archivos (usar actualizar-documentacion).
- No modificar código de producción.
