## i18n UI
1. **No hardcodear:** Todo texto UI va en `res/values/strings.xml` (Inglés por defecto).
2. **Traducciones:** Modifica siempre también `values-es/strings.xml` (Castellano) y `values-eu/strings.xml` (Euskera).
3. **ViewModels:** Pasan `@StringRes` (`R.string.x`) y sus `args`, no `Strings` formados.
4. **Compose:** Usa `stringResource(id, args)`.
