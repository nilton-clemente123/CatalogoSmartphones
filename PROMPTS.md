# Fase 2: mejora asistida por IA

- Proyecto: Catálogo de Smartphones.
- Rama: `mejora-ia`.
- Fecha de esta sesión: 11 de noviembre de 2026.
- Herramienta: asistente de IA en el flujo de Cline de VS Code.
- Alcance: pruebas de `ListaViewModel`, prevención de sincronizaciones simultáneas,
  exclusión del archivo local de Firebase y documentación.

Este registro contiene las dos solicitudes reales de esta conversación. No se
atribuyen consultas previas ni aportes de otro integrante. Los resultados de
pruebas descritos corresponden a comandos ejecutados en esta sesión, no a pruebas
manuales de aceptación.

## Consulta 1: revisión del documento y propuesta antes de modificar

- **Fecha:** 11 de noviembre de 2026.
- **Herramienta:** asistente de IA en Cline / VS Code.
- **Prompt:**

  > en base a este documento, necesito hacer los commits necesarios que piden para la rama mejora-ia , antes de hacer los cambios dime exactamente lo que haras por favor

  Se adjuntó el documento `GLAB-S11-JLEONS-2026-1 (1).pdf`.

- **Qué respondió (resumen):** revisó el PDF y el estado local del repositorio.
  Identificó R12 (al menos 3 pruebas unitarias del ViewModel de lista con repositorio
  falso), R13 (mejora asistida por IA documentada), el mínimo de 3 commits en
  `mejora-ia` y la exigencia de `PROMPTS.md`. Propuso 4 commits: exclusión de la
  configuración local de Firebase, pruebas del ViewModel, protección contra
  sincronizaciones simultáneas y documentación. Detectó que Firebase estaba
  versionado, que faltaban pruebas reales del ViewModel y que había cambios locales
  de dependencias de pruebas.
- **Qué usamos / qué cambiamos y por qué:** se aceptó el plan de cuatro commits.
  Se decidió conservar los cambios de dependencias existentes, no rediseñar la
  interfaz y no modificar `main`, publicar la rama, abrir el Pull Request ni crear
  el tag en esta etapa. La exclusión de Firebase se realiza sin borrar la copia
  local ni reescribir el historial.
- **Qué aprendimos:** los requisitos de la fase no se cumplen solo por crear una
  rama o varios commits: se necesitan pruebas de comportamiento, una mejora
  verificable y un registro de decisiones. Un archivo que ya está versionado no
  deja de estarlo simplemente por agregarlo a `.gitignore`.

## Consulta 2: implementación y creación de commits

- **Fecha:** 11 de noviembre de 2026.
- **Herramienta:** asistente de IA en Cline / VS Code.
- **Prompt:**

  > bien, realiza los commits por favor

- **Qué respondió (resumen):** implementó el plan aprobado. Agregó un repositorio
  falso basado en `MutableStateFlow`, un notificador que registra los mensajes,
  una regla para el dispatcher principal y pruebas con JUnit y
  `kotlinx-coroutines-test`. Conservó un `Job` en `ListaViewModel` para ignorar
  refrescos repetidos mientras la sincronización está activa.
- **Qué usamos / qué cambiamos y por qué:**
  - Se reutilizaron JUnit y las dependencias de corrutinas de prueba ya preparadas,
    sin agregar frameworks de mocks; se eliminó la declaración duplicada de JUnit.
  - Se crearon primero 8 pruebas del comportamiento existente y se ejecutaron con éxito.
  - Se agregó una novena prueba de regresión para llamadas simultáneas. Antes de
    corregir el código falló con `expected:<1> but was:<3>`, confirmando que se
    iniciaban tres sincronizaciones.
  - Después de agregar la protección con `Job`, la misma prueba pasó. También
    verifica llamadas mientras la petición está suspendida y que se puede volver
    a sincronizar al terminar. La prueba de reintento comprueba la recuperación
    después de un error.
  - Los ViewModels de prueba se limpian mediante `ViewModelStore` al finalizar
    cada caso para cancelar sus observadores.
  - Se conservó Room como fuente de verdad y se evitó introducir `Context`,
    Firebase o servicios reales en las pruebas.
- **Qué aprendimos:** una prueba que falla antes de la corrección aporta evidencia
  del defecto que se está corrigiendo. Controlar las corrutinas con un dispatcher
  de prueba y una respuesta suspendida permite probar estados y concurrencia sin
  esperas reales. Los falsos validan el contrato del ViewModel, no la integración
  real de Room, Firebase ni los permisos de notificaciones.

## Pruebas implementadas

1. Estado inicial `Cargando` mientras se espera la primera sincronización.
2. Sincronización exitosa, publicación de datos y notificación con la cantidad.
3. Sincronización exitosa sin productos: lista vacía.
4. Sin conexión y sin datos locales: error, sin notificación de éxito.
5. Sin conexión y con datos locales: lista conservada y aviso, sin notificar éxito.
6. Error HTTP sin datos locales: mensaje con código del servidor.
7. Reintento exitoso después de un error de conexión.
8. Cambio de favorito delegado al repositorio y reflejado en la lista.
9. Prevención de sincronizaciones simultáneas y nueva sincronización al terminar.

## Validación ejecutada

```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:assembleDebug
./gradlew clean :app:testDebugUnitTest :app:assembleDebug
git diff --check
```

- 9 pruebas de `ListaViewModel` aprobadas, sin fallos ni omisiones.
- La prueba de ejemplo existente también pasó: 10 pruebas unitarias en total.
- Compilación de depuración y generación del APK correctas, también después de `clean`.
- Se verificó que la copia local de `google-services.json` existe y está ignorada.
- Gradle mostró advertencias sobre una opción experimental, el target JDK 25 de
  Kotlin y dos iconos deprecados ya existentes; no impidieron la compilación.
- No se ejecutaron las 14 pruebas manuales de aceptación en emulador o dispositivo.

## Reflexión y pendientes

La IA ayudó a convertir los requisitos del documento en cambios pequeños y
comprobables. La mejora elegida es acotada: evita trabajo y notificaciones
duplicadas sin sustituir la arquitectura existente. Las pruebas hacen explícito
el comportamiento esperado y facilitan explicar la solución en la sustentación.

La evidencia automatizada no permite afirmar que se aprobaron todas las pruebas
de aceptación. Antes de entregar se deben completar la verificación manual, las
capturas reales, la publicación de `mejora-ia`, el Pull Request hacia `main` y la
versión `v1.0` con su APK. El archivo de Firebase sigue presente en el historial
anterior; esta sesión solo deja de versionarlo a partir de la rama de mejora.