@echo off
if "%OS%" =="Windows_NT" setlocal
if "%OS%"=="Windows_NT" set PARABUILD_HOME=%~dp0..
if not "%PARABUILD_HOME%" == "" goto gotHome
set PARABUILD_HOME=.
if exist "%PARABUILD_HOME%\bin\parabuild.bat" goto okHome
set PARABUILD_HOME=..
:gotHome
if exist "%PARABUILD_HOME%\bin\parabuild.bat" goto okHome
echo The PARABUILD_HOME environment variable is not defined correctly.
echo This environment variable is needed to run this program.
echo The PARABUILD_HOME environment variable should point to Parabuild installation directory.
goto end
:okHome
call "%PARABUILD_HOME%\bin\parabuild.bat" stop
:end
