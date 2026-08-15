# Skill: analisis-critico

## Uso

Activar **siempre** que el usuario pida implementar una característica, refactorización, cambio de arquitectura o instrucción técnica donde sea oportuno evaluar si es una buena idea, analizar sus riesgos/impacto o proponer alternativas más razonables.

## Proceso de Evaluación

1. **Análisis Técnico y de Arquitectura**:
   - **Mantenibilidad y Complejidad**: ¿La propuesta añade complejidad innecesaria o sobreingeniería?
   - **Reglas del Proyecto**: ¿Respeta la separación de capas (`model`, `ui`, `ViewModel`), reglas de `AGENTS.md` o flujo TDD?
   - **Buenas Prácticas**: ¿Hay formas más idiomáticas en Kotlin / Jetpack Compose o soluciones nativas más limpias?
   - **Impacto**: ¿Afecta a tests existentes, rendimiento o experiencia de usuario (UX)?

2. **Formulación de Challenge y Alternativas**:
   - Si se identifican riesgos, ineficiencias o alternativas mejores:
     - **Explicar el motivo**: Justificar de forma clara y basada en arquitectura o calidad de código.
     - **Presentar alternativa(s)**: Describir la opción recomendada con sus pros y contras respecto a la propuesta original.
     - **Pedir confirmación/criterio**: Permitir al usuario decidir el camino a seguir antes de realizar cambios mayores.

## Reglas

- Ser siempre constructivo, objetivo y directo.
- Fundamentar el challenge en principios de diseño de software (SOLID, DRY, Clean Code, guías de Android/Compose).
- Si la instrucción viola reglas inviolables del proyecto (ej. añadir dependencias Android en `model/`), notificarlo obligatoriamente antes de proceder.
