@echo off
if not exist "bin\com\codealpha\gradetracker\Main.class" (
    echo Compiling project first...
    call build.bat
)
echo Launching CLI Terminal Mode...
java -cp bin com.codealpha.gradetracker.Main --cli
pause
