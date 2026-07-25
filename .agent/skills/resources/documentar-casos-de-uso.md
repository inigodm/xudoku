# Skill: documentar-casos-de-uso

## Propósito y por qué existe esta skill por separado

Este skill documenta **comportamiento** (qué puede hacer el usuario y cómo fluye eso por las capas).
Es distinto de `actualizar-documentacion`, que documenta **estructura** (qué archivos existen y para qué).

**La razón principal de esta documentación es ahorrar tokens.** Antes de escribir tests o ampliar funcionalidad, el agente lee el catálogo y sabe exactamente qué casos de uso existen, sus precondiciones y efectos observables, sin necesidad de releer el ViewModel ni las pantallas. Esto evita contexto redundante en cada sesión.

---

## Cuándo usar esta skill

Actívala cuando:
- El usuario pide **escribir tests** para lógica de ViewModel o casos de uso.
- El usuario dice "documenta los casos de uso" o "actualiza el catálogo de CU".
- Se va a añadir un **caso de uso nuevo** y hay que integrarlo en el catálogo antes de implementarlo.
- El usuario quiere saber qué acciones están disponibles en el juego.

**No la actives** para cambios puramente de layout/UI sin lógica nueva, ni para cambios en `model/`.

---

## Dónde vive el catálogo

```
.agent/docs/casos-de-uso.md
```

Si el archivo no existe, créalo con la plantilla de abajo.

---

## Formato del catálogo

Cada caso de uso sigue esta ficha mínima:

```markdown
### CU-XX — Nombre del caso de uso

| Campo          | Valor |
|----------------|-------|
| Actor          | Jugador / Sistema |
| Trigger        | Qué acción del usuario o evento del sistema lo dispara |
| Precondición   | Estado mínimo que debe existir para que el CU sea válido |
| Método VM      | `GameViewModel.nombreFuncion()` (o N/A) |
| Postcondición  | Qué cambia en el estado observable después |
| Efecto UI      | Qué ve el usuario en pantalla |
| Tested         | ✅ / ❌ |
```

Mantén los IDs (`CU-01`, `CU-02`…) estables. Si un CU se elimina, marca `[ELIMINADO]` y no renueves el ID.

---

## Protocolo para documentar

### Paso 1 — Leer el catálogo existente

```
.agent/docs/casos-de-uso.md
```

Si no existe, continúa al paso 2 para crearlo desde cero.

### Paso 2 — Identificar los casos de uso en el código

Leer **solo** estos archivos (no más):

| Archivo | Por qué |
|---|---|
| `ui/GameViewModel.kt` | Fuente de verdad de las acciones disponibles |
| `ui/XudokuNavGraph.kt` | Flujos de navegación entre pantallas |
| `ui/screen/GameScreen.kt` | Acciones que dispara el jugador en pantalla |

No leas pantallas estáticas (StatsScreen, ProfileScreen, VictoryScreen) a menos que el usuario lo pida explícitamente; esas no tienen lógica de juego.

### Paso 3 — Redactar o actualizar las fichas

Una ficha por método público del ViewModel + un CU por flujo de navegación relevante (ej: "completar puzzle → ir a Victoria").

### Paso 4 — Marcar tests pendientes

En cada ficha, `Tested: ❌` si no hay test unitario que lo cubra. Tras escribir el test, actualizar a `✅`.

### Paso 5 — Confirmar con el usuario

Mostrar resumen compacto:
```
✅ Creado/Actualizado .agent/docs/casos-de-uso.md
   - X fichas nuevas
   - Y fichas actualizadas
   - Z fichas con Tested: ❌ (pendientes de test)
```

---

## Lo que esta skill NO hace

- No escribe los tests (eso lo hace el agente siguiendo `tdd-nueva-funcionalidad.md`).
- No documenta la estructura de archivos (eso es `actualizar-documentacion.md`).
- No modifica código de producción.
