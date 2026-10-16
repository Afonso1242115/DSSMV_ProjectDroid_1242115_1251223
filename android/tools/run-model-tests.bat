@echo off
setlocal
set OUT=build\model-tests
if exist "%OUT%" rmdir /s /q "%OUT%"
mkdir "%OUT%"

javac -d "%OUT%" app\src\main\java\com\campuslf\app\model\FoundItem.java tools\ModelSmokeTest.java
if errorlevel 1 exit /b 1

java -cp "%OUT%" ModelSmokeTest
