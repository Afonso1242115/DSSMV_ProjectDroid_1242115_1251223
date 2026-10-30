@echo off
setlocal
set "ROOT=%~dp0.."
set "OUT=%ROOT%\build\model-tests"
if not exist "%OUT%" mkdir "%OUT%"

javac -encoding UTF-8 -d "%OUT%" "%ROOT%\app\src\main\java\pt\isep\dssmv\campuslf\model\FoundItem.java" "%ROOT%\tools\ModelSmokeTest.java"
if errorlevel 1 exit /b 1

java -cp "%OUT%" ModelSmokeTest
