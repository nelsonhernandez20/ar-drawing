# AR Draw: Easy Sketching

App Android para **trazar dibujos** superponiendo una imagen de tu galería sobre el papel o la pantalla del teléfono.

## Descargar e instalar

1. Descarga el APK desde [`releases/ar-drawing-gocho-v1.4.apk`](releases/ar-drawing-gocho-v1.4.apk).
2. En el teléfono, activa **Instalar apps desconocidas** para el navegador o gestor de archivos que uses.
3. Abre el APK descargado y confirma la instalación.

> Requisito: Android **8.0 (API 26)** o superior.

## Características

- **Sin login** — entras directo a la app.
- **Biblioteca de plantillas** — imágenes gratis y premium (Supabase).
- **Suscripción Premium** — Google Play Billing (`premium_monthly`), sin cuenta propia.
- **Imágenes propias** — galería del dispositivo siempre disponible.
- **Dos modos de dibujo:**
  - **Cámara:** superpone la imagen sobre lo que ve la cámara (ideal con trípode o teléfono apoyado).
  - **Pantalla:** coloca papel sobre la pantalla y sigue las líneas como retroiluminado.
- **Controles en el lienzo:**
  - Opacidad
  - Zoom (slider + pellizco estable)
  - Rotación 90°
  - Orientación vertical u horizontal
  - Bloqueo de posición/tamaño
  - Reinicio de transformación
  - Cámara frontal/trasera y flash (modo cámara)
  - Panel de herramientas plegable
- **Sin anuncios.**

## Flujo de uso

1. Elige el modo (cámara o pantalla).
2. Pulsa **Continuar** y abre la **Biblioteca** (gratis / premium) o **Usar mi imagen**.
3. Ajusta opacidad, zoom y rotación de la referencia.
4. Bloquea la imagen cuando esté lista y dibuja.
5. Pulsa atrás para salir del lienzo.

## Especificaciones técnicas

| Campo | Valor |
|-------|--------|
| **Nombre** | AR Draw: Easy Sketching |
| **Package** | `com.ardrawing.trace` |
| **Versión** | 1.4 (versionCode 5) |
| **minSdk** | 26 (Android 8.0) |
| **targetSdk** | 35 |
| **Lenguaje** | Kotlin |
| **UI** | Material 3, View Binding |
| **Cámara** | CameraX |
| **Permisos** | Cámara (modo cámara), linterna (opcional) |

## Compilar desde código

```bash
./gradlew :app:assembleDebug
```

APK de debug: `app/build/outputs/apk/debug/app-debug.apk`

```bash
./gradlew :app:assembleRelease
```

APK de release: `app/build/outputs/apk/release/app-release.apk`

## Supabase (catálogo de imágenes)

1. Ejecuta [`supabase/schema.sql`](supabase/schema.sql) en el SQL Editor.
2. Crea bucket Storage `templates` (público) con carpetas `free/` y `premium/`.
3. En `local.properties` (no subir a git):

```properties
supabase.url=https://TU_PROYECTO.supabase.co
supabase.anon_key=TU_ANON_KEY
```

## Google Play (suscripción)

1. En Play Console crea suscripción `premium_monthly`.
2. Publica en pista interna para probar Billing (no funciona con APK suelto).
3. Añade licencias de prueba con tu Gmail.

## Estructura del proyecto

```
app/src/main/java/com/ardrawing/trace/
├── MainActivity.kt          # Selección de modo
├── LibraryActivity.kt       # Biblioteca + paywall
├── DrawingActivity.kt       # Lienzo, cámara y herramientas
├── BillingRepository.kt     # Google Play Billing
├── CatalogRepository.kt     # Catálogo Supabase
├── DrawingMode.kt
└── BitmapLoadHelper.kt
```

## Licencia y autor

Proyecto personal.
