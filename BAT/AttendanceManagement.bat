@echo off
setlocal EnableDelayedExpansion
title Smart Campus Event Management - Attendance Management
cd /d "%~dp0\.."

set "CP=Java\bin;Java\Java Dependencies\*"

where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java runtime not found on PATH! Please install Java SE 17+.
    pause
    exit /b 1
)

if not exist "Java\bin\SmartCampusEventManagementSystem.class" (
    echo [INFO] Compiling Java classes...
    if not exist "Java\bin" mkdir "Java\bin"
    javac -cp "Java\Java Dependencies\*" -d "Java\bin" "Java\SmartCampusEventManagementSystem.java" "Java\Main Application\*.java" "Java\Student Management\*.java" "Java\Event Management\*.java" "Java\Event Registration Management\*.java" "Java\Attendance Management\*.java" "Java\Participation Management\*.java" "Java\Dashboard\*.java" "Java\Reports and Analysis\*.java" "Java\Database Connectivity\*.java"
)

java -cp "%CP%" SmartCampusEventManagementSystem --module attendance
echo.
pause
