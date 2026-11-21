@echo off
title Push Smart Campus Event Management to GitHub
cd /d "%~dp0"
echo ===============================================================================
echo        PUSH SMART CAMPUS EVENT MANAGEMENT TO GITHUB REPOSITORY
echo ===============================================================================
echo Remote URL: https://github.com/layalaya5034/Student-Event-Management-.git
echo Branch    : main
echo.
git push -u origin main
echo.
if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Project successfully pushed to GitHub!
) else (
    echo [ERROR] Push failed. If prompted, please complete browser sign-in or enter your GitHub Personal Access Token (PAT).
)
echo.
pause
