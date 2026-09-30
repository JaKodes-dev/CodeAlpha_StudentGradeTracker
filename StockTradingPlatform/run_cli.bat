@echo off
if not exist "bin\com\codealpha\stocktrading\Main.class" (
    echo Compiling project first...
    call build.bat
)
echo Launching Stock Trading CLI Terminal...
java -cp bin com.codealpha.stocktrading.Main --cli
pause
