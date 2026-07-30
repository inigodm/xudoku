# Skill: testing-viewmodel

## Cuándo usar esta skill

Usar para crear o modificar tests de `GameViewModel` o depurar fallos relacionados con coroutines.

---

## Leer

- `app/src/test/java/com/inigo/xudoku/ui/GameViewModelTest.kt`
- `ui/GameViewModel.kt` (solo si necesitas entender el comportamiento)

---

## Reglas

1. Inyectar `UnconfinedTestDispatcher` como `ioDispatcher`.
2. Usar `runTest(testDispatcher)`.
3. Llamar a `Dispatchers.setMain()` / `resetMain()`.
4. Cancelar `vm.viewModelScope` antes de terminar el test.
5. No usar `advanceUntilIdle()`.

---

## Trampa importante

`startGame()` inicia un timer infinito en `viewModelScope`.

Por ello:

- `advanceUntilIdle()` nunca termina.
- `runTest()` no cancela `viewModelScope`.
- Hay que llamar explícitamente a:

  vm.viewModelScope.cancel()

---

## Comando

    ./gradlew :app:test --tests "com.inigo.xudoku.ui.GameViewModelTest"

---

## Ver también

- `buenas-practicas.md`
- `tdd-nueva-funcionalidad.md`
- `.agent/docs/casos-de-uso.md`