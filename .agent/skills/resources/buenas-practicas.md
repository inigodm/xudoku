# Skill: buenas-practicas

## Uso

Activar siempre que se escriba o refactorice código.

## Arquitectura

- Mantener separación entre `model`, `ui` y `ViewModel`.
- `model/` debe permanecer como lógica pura JVM, sin dependencias Android.
- No poner lógica de negocio en Composables.
- No poner lógica de UI o presentación en `model/`.
- El estado mutable del juego pertenece al ViewModel.

## Diseño

- Cada clase debe tener una única responsabilidad.
- Preferir inyección de dependencias frente a instanciación directa cuando facilite tests.
- Preferir funciones/lambdas pequeñas frente a interfaces grandes.
- Diseñar componentes UI parametrizables mediante estado + callbacks.

## Kotlin

- Preferir `val` frente a `var`.
- Usar nombres descriptivos.
- Mantener funciones pequeñas y con un único nivel de abstracción.
- Evitar efectos secundarios ocultos.
- No usar excepciones como flujo normal de control.
- Usar `require`/`check` solo para validar precondiciones.

## Compose

- Los Composables deben ser puros:
  - reciben estado por parámetros;
  - emiten eventos mediante lambdas.
- El acceso al ViewModel debe estar limitado al nivel raíz de la pantalla.
- `remember` solo para estado efímero de UI.
- El estado real de la aplicación debe vivir fuera del Composable.

## Estados y errores

- Preferir `sealed class` para representar estados o resultados con error.
- Evitar nullables ambiguos cuando exista un estado explícito.

## Tests

- Testear comportamiento, no implementación.
- Mantener tests deterministas.
- Usar `Random(seed)` cuando la aleatoriedad afecte a tests.
- Usar nombres descriptivos en backticks.
- Mantener estructura Arrange / Act / Assert.
- No usar `Thread.sleep` en tests.

## Antes de finalizar

Comprobar:

- No se han mezclado capas.
- No hay lógica de negocio en UI.
- Se mantiene la inmutabilidad donde aplica.
- Los tests relevantes pasan.