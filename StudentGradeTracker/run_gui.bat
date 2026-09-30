@echo off
if not exist "bin\com\codealpha\gradetracker\Main.class" (
    echo Compiling project first...
    call build.bat
)
echo Launching GUI Mode...
java -cp bin com.codealpha.gradetracker.Main --gui
