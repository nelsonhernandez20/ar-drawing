# AR Drawing Gocho

App Android para **trazar dibujos** superponiendo una imagen de tu galería sobre el papel o la pantalla del teléfono.

## Descargar e instalar

1. Descarga el APK desde [`releases/ar-drawing-gocho-v1.0.apk`](releases/ar-drawing-gocho-v1.0.apk).
2. En el teléfono, activa **Instalar apps desconocidas** para el navegador o gestor de archivos que uses.
3. Abre el APK descargado y confirma la instalación.

> Requisito: Android **8.0 (API 26)** o superior.

## Características

- **Sin login** — entras directo a la app.
- **Imágenes propias** — solo desde la galería del dispositivo (no hay biblioteca interna).
- **Dos modos de dibujo:**
  - **Cámara:** superpone la imagen sobre lo que ve la cámara (ideal con trípode o teléfono apoyado).
  - **Pantalla:** coloca papel sobre la pantalla y sigue las líneas como retroiluminado.
- **Controles en el lienzo:**
  - Opacidad de la imagen guía
  - Escala (slider + pellizco)
  - Bloqueo de posición/tamaño
  - Reinicio de transformación
  - Cámara frontal/trasera y linterna (solo modo cámara)
  - Panel de herramientas plegable (acordeón)
- **Apoyo al desarrollador** — enlace a [Ko-fi](https://ko-fi.com/gocholabs).

## Flujo de uso

1. Elige el modo (cámara o pantalla).
2. Pulsa **Continuar** y selecciona una imagen de la galería.
3. Ajusta opacidad, tamaño y posición de la referencia.
4. Bloquea la imagen cuando esté lista y dibuja.
5. Pulsa **Finalizar** para salir del lienzo.

## Especificaciones técnicas

| Campo | Valor |
|-------|--------|
| **Nombre** | AR Drawing Gocho |
| **Package** | `com.ardrawing.trace` |
| **Versión** | 1.0 (versionCode 1) |
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

## Estructura del proyecto

```
app/src/main/java/com/ardrawing/trace/
├── MainActivity.kt          # Selección de modo y galería
├── DrawingActivity.kt       # Lienzo, cámara y herramientas
├── DrawingMode.kt
└── BitmapLoadHelper.kt
```

## Licencia y autor

Proyecto personal — [Gocho Labs / Ko-fi](https://ko-fi.com/gocholabs).
