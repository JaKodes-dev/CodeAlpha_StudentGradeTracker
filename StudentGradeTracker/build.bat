@echo off
echo ===================================================
echo Compiling CodeAlpha Student Grade Tracker (Task 1)
echo ===================================================
if not exist "bin" mkdir bin
javac -encoding UTF-8 -d bin -sourcepath src src/com/codealpha/gradetracker/Main.java
if %ERRORLEVEL% equ 0 (
    echo Compilation Successful! Output classes in /bin
) else (
    echo Compilation Failed!
)
pause
