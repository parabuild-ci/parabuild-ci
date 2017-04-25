@echo off
if "%OS%"=="Windows_NT" setlocal
rem ---------------------------------------------------------------------------
rem Start/Stop Script for the Parabuild Manager
rem
rem Configuration variables
rem
rem   JAVA_HOME              If Parabuild installation does not include JRE, JAVA_HOME
rem                          must point at your Java Development Kit installation.
rem                          Parabuild requires JDK version 1.4.2.
rem
rem   PARABUILD_HOME         May point at your Parabuild installation directory.
rem                          If not set, this script will attempt to detect Parabuild
rem                          home directory.
rem ---------------------------------------------------------------------------
set PARABUILD_HOME=


rem Parabuild Java settings
set JAVA_OPTS=-Xms100m -Xmx300m -Dparabuild.active.build.id.validation.enabled=true

rem Get NT Parabuild home
if "%OS%"=="Windows_NT" set PARABUILD_HOME=%~dp0..

rem Guess PARABUILD_HOME if not defined
if not "%PARABUILD_HOME%" == "" goto gotHome
set PARABUILD_HOME=.
if exist "%PARABUILD_HOME%\bin\parabuild.bat" goto okHome
set PARABUILD_HOME=..

:gotHome
if exist "%PARABUILD_HOME%\bin\parabuild.bat" goto okHome
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
rem Set the default -Djava.endorsed.dirs argument
set JAVA_ENDORSED_DIRS=%PARABUILD_HOME%\lib\common\endorsed

rem Set standard CLASSPATH
rem Note that there are no quotes as we do not want to introduce random quotes into the CLASSPATH
set CLASSPATH=%PARABUILD_JAVA_HOME%\lib\tools.jar

rem Set standard command for invoking Java.
rem Note that NT requires a window name argument when using start.
rem Also note the quoting as JAVA_HOME may contain spaces.
set _RUNJAVA="%PARABUILD_JAVA_HOME%\bin\java"
set _RUNJAVAW="%PARABUILD_JAVA_HOME%\bin\javaw"

set CLASSPATH=%CLASSPATH%;%PARABUILD_HOME%\bin\bootstrap.jar
set PARABUILD_TMPDIR=%PARABUILD_HOME%\etc\temp

rem Execute the command
set _EXECJAVA=%_RUNJAVA%
set MAINCLASS=org.apache.catalina.startup.Bootstrap
set ACTION=start
set SECURITY_POLICY_FILE=
set DEBUG_OPTS=

if ""%1"" == ""run"" goto doRun
if ""%1"" == ""start"" goto doStart
if ""%1"" == ""stop"" goto doStop

echo Usage:  parabuild ( commands ... )
echo commands:
echo   run               Start Parabuild in the current window
echo   start             Start Parabuild in a separate window
echo   stop              Stop Parabuild
goto end

:doRun
shift
if not ""%1"" == ""-security"" goto execCmd
shift
echo Using Security Manager
set SECURITY_POLICY_FILE=%PARABUILD_HOME%\etc\conf\catalina.policy
goto execCmd

:doStart
shift
if not "%OS%" == "Windows_NT" goto noTitle
set _EXECJAVA=start "Parabuild" %_RUNJAVA%
goto gotTitle
:noTitle
set _EXECJAVA=start %_RUNJAVA%
:gotTitle
if not ""%1"" == ""-security"" goto execCmd
shift
echo Using Security Manager
set SECURITY_POLICY_FILE=%PARABUILD_HOME%\etc\conf\catalina.policy
goto execCmd

:doStop
shift
set ACTION=stop
goto execCmd

:execCmd
rem Get remaining unshifted command line arguments and save them in the
set CMD_LINE_ARGS=
:setArgs
if ""%1""=="""" goto doneSetArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setArgs
:doneSetArgs

rem Execute Java with the applicable properties
if not "%JPDA%" == "" goto doJpda
if not "%SECURITY_POLICY_FILE%" == "" goto doSecurity
%_EXECJAVA% %JAVA_OPTS% %PARABUILD_OPTS% %DEBUG_OPTS% -Djava.endorsed.dirs="%JAVA_ENDORSED_DIRS%" -classpath "%CLASSPATH%" -Dcatalina.base="%PARABUILD_HOME%\etc" -Dcatalina.home="%PARABUILD_HOME%\lib" -Djava.io.tmpdir="%PARABUILD_TMPDIR%" %MAINCLASS% %CMD_LINE_ARGS% %ACTION%
goto end
:doSecurity
%_EXECJAVA% %JAVA_OPTS% %PARABUILD_OPTS% %DEBUG_OPTS% -Djava.endorsed.dirs="%JAVA_ENDORSED_DIRS%" -classpath "%CLASSPATH%" -Djava.security.manager -Djava.security.policy=="%SECURITY_POLICY_FILE%" -Dcatalina.base="%PARABUILD_HOME%\etc" -Dcatalina.home="%PARABUILD_HOME%\lib" -Djava.io.tmpdir="%PARABUILD_TMPDIR%" %MAINCLASS% %CMD_LINE_ARGS% %ACTION%
goto end

:end
