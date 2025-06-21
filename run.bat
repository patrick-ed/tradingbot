@echo off
echo =================== BUILDING ===================
call gradlew.bat shadowJar
if %errorlevel% neq 0 (
    echo Build failed. Aborting.
    exit /b %errorlevel%
)
echo =================== RUNNING ====================
java -jar .\build\libs\tradingbot-1.0-SNAPSHOT-all.jar