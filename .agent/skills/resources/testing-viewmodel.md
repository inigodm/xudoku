# Skill: testing-viewmodel

## Propósito

Documentar la infraestructura de tests del ViewModel para que **ningún agente futuro tenga que releer `build.gradle.kts`, `libs.versions.toml` ni el código del ViewModel** solo para saber cómo escribir un test.

---

## Cuándo usar esta skill

- Escribir un test nuevo para `GameViewModel`.
- Añadir un caso de uso al `GameViewModelTest.kt`.
- Depurar un test del ViewModel que falla inesperadamente.
- Entender por qué se usa `UnconfinedTestDispatcher`.

---

## Estado actual de dependencias de test

| Dependencia | Versión | En catalog como |
|---|---|---|
| `junit:junit` | 4.13.2 | `libs.junit` |
| `kotlinx-coroutines-test` | 1.7.3 | `libs.kotlinx.coroutines.test` |
| `app.cash.turbine:turbine` | 1.1.0 | `libs.turbine` |

Todas son `testImplementation` en `app/build.gradle.kts`. No se necesita emulador.

---

## Localización de archivos de test

| Archivo | Ruta |
|---|---|
| Tests del ViewModel | `app/src/test/java/com/inigo/xudoku/ui/GameViewModelTest.kt` |
| Tests del modelo | `app/src/test/java/com/inigo/xudoku/model/` |
| Comando para correr solo VM tests | `./gradlew :app:test --tests "com.inigo.xudoku.ui.GameViewModelTest"` |
| Comando para todos los tests | `./gradlew :app:test` |

---

## Por qué se inyecta el dispatcher

`GameViewModel` usa `viewModelScope.launch { withContext(ioDispatcher) { ... } }` para generar puzzles en background. En producción `ioDispatcher = Dispatchers.Default`.

En tests, se pasa `UnconfinedTestDispatcher` como `ioDispatcher`. Esto hace que:
1. `viewModelScope.launch {}` corra **eagerly** (porque Main = UnconfinedTestDispatcher).
2. `withContext(ioDispatcher)` también corra **de forma síncrona** en el mismo hilo de test.
3. No hay que esperar tiempos reales — `advanceUntilIdle()` lo drena todo.

```kotlin
// Producción (comportamiento por defecto, sin cambios)
val vm = GameViewModel()

// Test (pasa el dispatcher de test)
val vm = GameViewModel(ioDispatcher = testDispatcher)
```

---

## Boilerplate estándar de cada clase de test

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var vm: GameViewModel  // ← a nivel de clase para cancelar en @After

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        vm = GameViewModel(ioDispatcher = testDispatcher)
    }

    @After
    fun teardown() {

        Dispatchers.resetMain()
    }

    @Test
    fun `nombre descriptivo en español`() = runTest(testDispatcher) {
        vm.startGame(Difficulty.VERY_EASY)
        // assertions sobre vm.xxx.value ...
        // CRÍTICO: cancela el viewModelScope (y con él el timerJob).
        // Sin esto el proceso queda bloqueado después del test: el timer (while(true)+delay)
        // permanece suspendido en el scheduler virtual y el JVM no termina aunque el test pase.
        // viewModelScope NO es hijo del scope de runTest → runTest no lo cancela automáticamente.
        // se tiene que hacer aqui pq tiene que ejecutarse ANTES del @After, sino no lo coge
        vm.viewModelScope.cancel()   }
}
```

---

## ⚠️ NUNCA usar `advanceUntilIdle()` en tests de `GameViewModel`

`startGame()` llama a `startTimer()`, que lanza un `while(true) { delay(1_000L) }` en `viewModelScope`.
`advanceUntilIdle()` intenta drenar el scheduler hasta que no quedan tareas: como el timer siempre pone una nueva `delay` al completar la anterior, **el scheduler nunca llega a idle** y el test se cuelga indefinidamente.

Con `UnconfinedTestDispatcher` **no es necesario** `advanceUntilIdle()`: las coroutines corren de forma *eager* (síncrona), así que el estado ya está actualizado cuando retorna el método del ViewModel.

```kotlin
// ❌ MAL — se cuelga por el timer infinito
vm.startGame(Difficulty.VERY_EASY)
advanceUntilIdle()

// ✅ BIEN — el estado ya está listo con UnconfinedTestDispatcher
vm.startGame(Difficulty.VERY_EASY)
assertEquals(0, vm.mistakes.value)
```

---

## Sin Turbine vs Con Turbine

### Sin Turbine — para verificar estado final

```kotlin
vm.startGame(Difficulty.VERY_EASY)
advanceUntilIdle()

assertEquals(0, vm.mistakes.value)   // acceso directo a .value
assertFalse(vm.isLoading.value)
```

**Cuándo:** Solo importa el estado final, sin transiciones intermedias.

### Con Turbine — para verificar la secuencia de emisiones

```kotlin
vm.selectedCell.test {
    assertNull(awaitItem())          // valor inicial del StateFlow
    vm.selectCell(3, 5)
    assertEquals(Pair(3, 5), awaitItem())
    cancelAndIgnoreRemainingEvents()
}
```

**Cuándo:** Hay estados intermedios que importan (ej: `isLoading` true → false), o se quiere verificar que un StateFlow emite exactamente N veces.

---

## Cobertura actual de tests del ViewModel

Ver `docs/casos-de-uso.md` para el estado `Tested: ✅/❌` de cada CU.

---

## Invariantes del test setup

1. **Siempre** llamar `Dispatchers.resetMain()` en `@After` — si no, el dispatcher queda contaminado entre tests.
2. **Siempre** usar `runTest(testDispatcher)` — no `runBlocking`, que bloquea el hilo real.
3. **Siempre** llamar `cancelAndIgnoreRemainingEvents()` al final de un bloque `turbine.test {}`.
4. **Nunca** importar `android.*` en archivos de test en `test/` (JVM only) — si necesitas algo de Android, va en `androidTest/`.
