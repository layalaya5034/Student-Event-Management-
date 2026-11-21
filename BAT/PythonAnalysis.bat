@echo off
setlocal EnableDelayedExpansion
title Smart Campus Event Management - Python Data Analysis
cd /d "%~dp0\.."

set "PY_CMD=python"
python --version >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    if exist "C:\Users\sugirthan\AppData\Local\Programs\Python\Python313\python.exe" (
        set "PY_CMD=C:\Users\sugirthan\AppData\Local\Programs\Python\Python313\python.exe"
    ) else (
        where py >nul 2>nul
        if %ERRORLEVEL% EQU 0 (
            set "PY_CMD=py"
        )
    )
)

echo ===============================================================================
echo            SMART CAMPUS EVENT MANAGEMENT - PYTHON DATA ANALYSIS
echo ===============================================================================
echo Active Python Engine: %PY_CMD%
echo.

"%PY_CMD%" "Python\run_python_analysis.py"
echo.
pause
