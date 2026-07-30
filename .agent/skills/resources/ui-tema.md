# Skill: ui-tema

## Cuándo usar esta skill

Usar para:
- Crear o modificar pantallas o Composables.
- Cambiar navegación.
- Cambiar tema, colores o tipografía.
- Modificar `GameViewModel` desde la capa UI.

---

## Leer

Según la tarea, solo los archivos necesarios:

| Área | Archivo |
|---|---|
| Tema | `ui/theme/Color.kt`, `Theme.kt`, `Type.kt` |
| Navegación | `ui/XudokuNavGraph.kt` |
| Estado | `ui/GameViewModel.kt` |
| Pantallas | `ui/screen/...` |
| Componentes | `ui/components/...` |

Consultar `design/style-tokens.md` antes de modificar colores.

---

## Invariantes

1. Toda la UI debe estar envuelta por `XudokuTheme`.
2. Los colores provienen de `ui/theme/Color.kt`; no usar colores hardcodeados.
3. La UI trabaja sobre el estado expuesto por `GameViewModel`; no acceder directamente a `SudokuBoard`.
4. No instanciar `SudokuGenerator` desde un Composable.
5. `SudokuGame` es inmutable; el estado mutable pertenece al ViewModel.
6. Todas las pantallas y componentes reutilizables deben tener `@Preview` (no `private`).

---

## Navegación

Las rutas están definidas en `ui/XudokuNavGraph.kt`.

Si se añade una pantalla:

1. Crear el Composable.
2. Registrar la ruta.
3. Conectar la navegación.
4. Añadir `@Preview`.
5. Actualizar la documentación si el usuario lo solicita.

---

## Tipografía

El proyecto ya está configurado para Google Fonts.

No eliminar `app/src/main/res/values/font_certs.xml`.

---

## Tests

Después de cambios en UI:

    ./gradlew :app:test
    ./gradlew assembleDebug

Si existen tests instrumentados:

    ./gradlew connectedAndroidTest

Verificar también los `@Preview`.

---

## Ver también

- `buenas-practicas.md`
- `actualizar-documentacion.md`