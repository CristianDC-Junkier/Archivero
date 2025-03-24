@echo off
setlocal

:: Configurar variables
set APP_NAME=Archivero
set INPUT_DIR=launcher
set MAIN_JAR=Archivero-1.1.jar
set MAIN_CLASS=archivero.app.App
set MODULE_PATH=launcher\javafx
set MODULES=javafx.controls,javafx.fxml
set OUTPUT_DIR=target\dist
set ICON_WIN=launcher\Almonte.ico
set ICON_MAC=launcher\Almonte.icns
set APPDATA_DIR=%APPDATA%\Archivero

:: Generar instalador para Windows (.exe)
echo Generando instalador para Windows...
jpackage ^
    --name "%APP_NAME%" ^
    --input "%INPUT_DIR%" ^
    --main-jar "%MAIN_JAR%" ^
    --main-class "%MAIN_CLASS%" ^
    --type exe ^
    --module-path "%MODULE_PATH%" ^
    --add-modules "%MODULES%" ^
    --dest "%OUTPUT_DIR%" ^
    --win-shortcut ^
    --win-menu ^
    --win-menu-group "Archivero" ^
    --icon "%ICON_WIN%" ^
    --app-version "1.1" ^
    --vendor "Ayuntamiento de Almonte" ^
    --copyright "© 2025 Ayuntamiento de Almonte - Cristian Delgado Cruz. Todos los derechos reservados." ^
    --description "Proyecto Archivero: Gestor de archivos de expedientes" ^
    --win-dir-chooser ^
    --win-upgrade-uuid "bb1dd4c4-4186-41ad-9398-694496c3efb1" ^
    --resource-dir "%APPDATA_DIR%" 

:: Generar instalador para macOS (.dmg)
echo Generando instalador para macOS...
jpackage ^
    --name "%APP_NAME%" ^
    --input "%INPUT_DIR%" ^
    --main-jar "%MAIN_JAR%" ^
    --main-class "%MAIN_CLASS%" ^
    --type dmg ^
    --module-path "%MODULE_PATH%" ^
    --add-modules "%MODULES%" ^
    --dest "%OUTPUT_DIR%" ^
    --icon "%ICON_MAC%" ^
    --app-version "2.0" ^
    --vendor "Ayuntamiento de Almonte" ^
    --copyright "© 2025 Ayuntamiento de Almonte - Cristian Delgado Cruz. Todos los derechos reservados." ^
    --description "Proyecto Archivero: Gestor de archivos de expedientes" ^
    --win-upgrade-uuid "bb1dd4c4-4186-41ad-9398-694496c3efb1" ^
    --resource-dir "%APPDATA_DIR%" 

echo.
echo Paquetes creados en %OUTPUT_DIR%
pause
endlocal






