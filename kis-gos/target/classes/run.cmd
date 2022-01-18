@echo off


set JAVA_EXE=java.exe
set BOOTSTRAP="ApplicationBootstrap"
set DIR=%~dp0
call:escape_trailing_slash "%DIR%" "DIR"

SETLOCAL ENABLEDELAYEDEXPANSION

@REM if defined JAVA_HOME (
@REM     set "NEW_JAVA_HOME=%JAVA_HOME:"=%"
@REM     if not exist "!NEW_JAVA_HOME!\bin\!JAVA_EXE!" (
@REM         echo.
@REM         echo Error: "!NEW_JAVA_HOME!\bin\!JAVA_EXE!" is not found.
@REM         echo Please set the JAVA_HOME environment variable to the location of your Java installation.
@REM         echo.
@REM         goto error
@REM     )
@REM     set EXECUTOR_JAVA="!NEW_JAVA_HOME!\bin\%JAVA_EXE%"
@REM ) else (
@REM     for %%X in (%JAVA_EXE%) do (set FOUND_JAVA=%%~$PATH:X)
@REM     if defined FOUND_JAVA (
@REM         set EXECUTOR_JAVA="!FOUND_JAVA!"
@REM     ) else (
@REM         echo.
@REM         echo Error: JAVA_HOME environment variable is not set and Java is not found in PATH.
@REM         echo Please set the JAVA_HOME environment variable to the location of your Java installation.
@REM         echo.
@REM         goto error
@REM     )
@REM )

setlocal DisableDelayedExpansion

rem Multiple Java versions support
@REM for /f "tokens=3" %%g in ('%EXECUTOR_JAVA% -version 2^>^&1 ^| findstr /i version') do (
@REM     set JAVAVER=%%g
@REM )
set JAVAVER=%JAVAVER:"=%

set CODEPATH=-cp "%DIR%\lib\*"
for /f "delims=. tokens=1-3" %%v in ("%JAVAVER%") do (
    if %%v GEQ 9 (
        set JPMS_OPTS=--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED
    )
)

set ENCODING="cp437"

for /f "tokens=4" %%g in ('chcp') do (
    set CODEPAGE=%%g
)
if %CODEPAGE% EQU 866 (
    set ENCODING="cp866"
)
if %CODEPAGE% EQU 65001 (
    set ENCODING="UTF-8"
)


%JAVA11_HOME% %JPMS_OPTS% %CODEPATH% %BOOTSTRAP% %*

set ERROR_CODE=%ERRORLEVEL%

goto end

:error
rem -- If error occurred - place a flag
set ERROR_CODE=1
goto end

:escape_trailing_slash
rem -- Remove trailing backslash
rem -- %~1: String from which to remove a trailing slash
rem -- %~2: Result
SETLOCAL
set datapath=%~1
if %datapath:~-1%==\ set datapath=%datapath:~0,-1%
( ENDLOCAL & rem Result values
    if "%~2" NEQ "" set "%~2=%datapath%"
)
goto:eof

rem Exit
:end
IF %ERRORLEVEL% NEQ 0 (
    if %ERROR_CODE% NEQ 0 (
        set ERROR_CODE=ERRORLEVEL
    )
)
cmd /C exit /B %ERROR_CODE%
