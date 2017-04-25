@echo off
if "%OS%"=="Windows_NT" setlocal
rem ---------------------------------------------------------------------------
rem Parabuild command line interface
rem
rem Usage:
rem
rem    paracmd.bat -a <host:port> -u <user> -p <password> -b <build id> -c <command>
rem
rem "stop" command stops running build; "start" command starts build run; "clean"
rem command requests clean checkout for the next buid run.
rem
rem Configuration variables
rem
rem   JAVA_HOME       If Parabuild installation does not include JRE, JAVA_HOME
rem                   must point at your Java Development Kit installation.
rem                   Parabuild requires JDK version 1.4.2.
rem
rem   PARABUILD_HOME  May point at your Parabuild installation directory.
rem                   If not set, this script will attempt to detect Parabuild
rem                   home directory.
rem ---------------------------------------------------------------------------
set PARABUILD_HOME=


rem Parabuild Java settings
set JAVA_OPTS=-Xms10m -Xmx10m

rem Get NT Parabuild home
if "%OS%"=="Windows_NT" set PARABUILD_HOME=%~dp0..

rem Guess PARABUILD_HOME if not defined
if not "%PARABUILD_HOME%" == "" goto gotHome
set PARABUILD_HOME=.
if exist "%PARABUILD_HOME%\bin\paracmd.bat" goto okHome
set PARABUILD_HOME=..

:gotHome
if exist "%PARABUILD_HOME%\bin\paracmd.bat" goto okHome
echo The PARABUILD_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end

:okHome
rem Get bundled JRE
if exist "%PARABUILD_HOME%\jre\bin\java.exe" set PARABUILD_JAVA_HOME=%PARABUILD_HOME%\jre
if "%PARABUILD_JAVA_HOME%" == "" set PARABUILD_JAVA_HOME=%JAVA_HOME%
if not "%PARABUILD_JAVA_HOME%" == "" goto gotJavaHome
echo The JAVA_HOME environment variable is not defined.
echo This environment variable is needed to run this program.
goto end

:gotJavaHome
if not exist "%PARABUILD_JAVA_HOME%\bin\java.exe" goto noJavaHome
if not exist "%PARABUILD_JAVA_HOME%\bin\javaw.exe" goto noJavaHome
goto okJavaHome

:noJavaHome
echo The JAVA_HOME environment variable is not defined correctly.
echo This environment variable is needed to run this program.
goto end

:okJavaHome
rem Set standard command for invoking Java.
rem Note that NT requires a window name argument when using start.
rem Also note the quoting as JAVA_HOME may contain spaces.
set _RUNJAVA="%PARABUILD_JAVA_HOME%\bin\java"
set _RUNJAVAW="%PARABUILD_JAVA_HOME%\bin\javaw"

set PARABUILD_TMPDIR=%PARABUILD_HOME%\etc\temp

rem Execute the command
set _EXECJAVA=%_RUNJAVA%
set SECURITY_POLICY_FILE=
set DEBUG_OPTS=

:setupArguments
if ""%1""=="""" goto doneSetupArguments
set PARACMD_LINE_ARGS=%PARACMD_LINE_ARGS% %1
shift
goto setupArguments
:doneSetupArguments

rem Execute Java with the applicable properties
if not "%SECURITY_POLICY_FILE%" == "" goto doSecurity
%_EXECJAVA% %JAVA_OPTS% %PARABUILD_OPTS% %DEBUG_OPTS% -jar "%PARABUILD_HOME%\bin\paracmd.jar" %PARACMD_LINE_ARGS%
goto end
:doSecurity
%_EXECJAVA% %JAVA_OPTS% %PARABUILD_OPTS% %DEBUG_OPTS% -Djava.security.manager -Djava.security.policy=="%SECURITY_POLICY_FILE%" -jar "%PARABUILD_HOME%\bin\paracmd.jar" %PARACMD_LINE_ARGS%
goto end
:end
if "%OS%"=="Windows_NT" endlocal
