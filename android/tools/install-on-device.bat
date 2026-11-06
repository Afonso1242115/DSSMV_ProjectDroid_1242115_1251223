@echo off
setlocal
cd /d "%~dp0..\.."
echo Building and installing Campus L^&F on the connected Android device...
call gradlew.bat :app:installDebug
if errorlevel 1 (
  echo Installation failed. Check USB debugging, device approval, Android SDK and local.properties.
  exit /b 1
)
echo Installation completed. Open Campus L^&F from the phone launcher.
