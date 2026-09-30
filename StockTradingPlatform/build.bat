@echo off
echo ===================================================
echo Compiling CodeAlpha Stock Trading Platform (Task 2)
echo ===================================================
if not exist "bin" mkdir bin
javac -encoding UTF-8 -d bin -sourcepath src src/com/codealpha/stocktrading/Main.java
if %ERRORLEVEL% equ 0 (
    echo Compilation Successful! Output classes in /bin
) else (
    echo Compilation Failed!
)
pause
