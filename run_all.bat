@echo off
setlocal EnableDelayedExpansion
title Smart Campus Event Management System - Master Suite Launcher
cd /d "%~dp0"

:: Auto-detect Python binary
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

:MENU
cls
echo ===============================================================================
echo            SMART CAMPUS EVENT MANAGEMENT AND PARTICIPATION SYSTEM
echo                      SCENARIO 6 - MASTER LAUNCHER
echo ===============================================================================
echo.
echo   [1] Software Engineering (View Architecture, Documentation and Schema)
echo   [2] Database Management System (Run interactive SQLite DBMS)
echo   [3] Java Programming (Run Centralized Smart Campus System)
echo   [4] Foundation of Data Science (Run Python Data Analysis and EDA)
echo   [5] View Visuals and Diagrams Dashboard (Open in Web Browser)
echo   [6] Exit
echo.
echo ===============================================================================
set "CHOICE="
set /p "CHOICE=Enter your choice (1-6): "

if not defined CHOICE (
    exit /b 0
)

if "%CHOICE%"=="1" goto SE_OPTION
if "%CHOICE%"=="2" goto DBMS_OPTION
if "%CHOICE%"=="3" goto JAVA_OPTION
if "%CHOICE%"=="4" goto DS_OPTION
if "%CHOICE%"=="5" goto DASHBOARD_OPTION
if "%CHOICE%"=="6" goto EXIT_OPTION

echo.
echo [ERROR] Invalid selection. Please enter a number between 1 and 6.
goto MENU

:SE_OPTION
cls
echo ===============================================================================
echo                    [1] SOFTWARE ENGINEERING AND SYSTEM ARCHITECTURE
echo ===============================================================================
echo.
echo Opening SRS Document (SRS_Document.md), README.md, and DBMS Documentation...
start "" "%~dp0SRS_Document.md"
start "" "%~dp0README.md"
start "" "%~dp0DBMS\Database Documentation\DBMS_Documentation.md"
echo.
echo Documentation opened successfully.
echo.
pause
goto MENU

:DBMS_OPTION
cls
echo ===============================================================================
echo             [2] DATABASE MANAGEMENT SYSTEM (25ITT31)
echo ===============================================================================
echo.
pushd "%~dp0BAT"
call DatabaseManagement.bat
popd
cd /d "%~dp0"
echo.
echo Returned from Database Management System session.
pause
goto MENU

:JAVA_OPTION
cls
echo ===============================================================================
echo                    [3] JAVA PROGRAMMING (25ITT32)
echo ===============================================================================
echo.
pushd "%~dp0BAT"
call SmartCampusEventManagement.bat
popd
cd /d "%~dp0"
echo.
echo Returned from Java Programming session.
pause
goto MENU

:DS_OPTION
cls
echo ===============================================================================
echo              [4] FOUNDATION OF DATA SCIENCE (25ITC31)
echo ===============================================================================
echo.
pushd "%~dp0BAT"
call PythonAnalysis.bat
popd
cd /d "%~dp0"
echo.
echo Returned from Foundation of Data Science session.
pause
goto MENU

:DASHBOARD_OPTION
cls
echo ===============================================================================
echo               [5] VISUALS AND DIAGRAMS WEB DASHBOARD
echo ===============================================================================
echo.
echo Scanning and updating visual assets dashboard...
"%PY_CMD%" "%~dp0Python\generate_event_dashboard.py"
if %ERRORLEVEL% NEQ 0 (
    echo [WARN] Python scan encountered a warning. Checking for existing dashboard...
)
if exist "%~dp0visuals_dashboard.html" (
    echo.
    echo [SUCCESS] Opening Visuals Dashboard in your default browser...
    start "" "%~dp0visuals_dashboard.html"
) else (
    echo [ERROR] visuals_dashboard.html could not be found or generated!
)
echo.
pause
goto MENU

:EXIT_OPTION
cls
echo ===============================================================================
echo Exiting Smart Campus Event Management System Master Suite. Goodbye!
echo ===============================================================================
exit /b 0
