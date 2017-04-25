@ECHO OFF

::
:: Batch script to launch the visualgc command.
::

setlocal

if not "%JVMSTAT_HOME%"=="" GOTO CHECKJAVAHOME

:: get the full path name for this script
SET SCRIPT_DIR=%~f0%

:: substitute \bat\<cmd> with null to get the jvmstat home directory
SET JVMSTAT_HOME=%SCRIPT_DIR:\bat\visualgc.cmd=%

:CHECKJAVAHOME

if not "%JVMSTAT_JAVA_HOME%"=="" GOTO LAUNCH

:: find the location of the java command so we can locate the
:: the tools.jar archive. Note that this code assumes that java.exe
:: is found in a jkd installation directory, not in the windows
:: directory (eg. c:\WINNT\system32\java.exe).
call :which java.exe JAVAEXE

if not "%JAVAEXE%"=="" GOTO CHECK1
echo java.exe not found in path - check PATH
goto :EOF

:CHECK1

call :dirname "%JAVAEXE%" JAVABIN
call :dirname "%JAVABIN%" JVMSTAT_JAVA_HOME

if exist "%JVMSTAT_JAVA_HOME%\bin\jps.exe" GOTO CHECK2
echo %A J2SE 5.0 or later JDK is required to run this tool.
echo The java version found in the PATH is:
java -version
goto :EOF
                                                                                
:CHECK2
                                                                                
if exist "%JVMSTAT_JAVA_HOME%\lib\tools.jar" GOTO LAUNCH
echo The J2SE 5.0 JDK is required to run this tool.
goto :EOF

:LAUNCH

SET VISUALGC_JAR=%JVMSTAT_HOME%\jars\visualgc.jar
SET TOOLS_JAR=%JVMSTAT_JAVA_HOME%\lib\tools.jar

:: %0 no longer needed, so shift it out to allow one more
:: parameter. %* is available in winnt and later, but doesn't
:: work in earlier windows environemnts.
::
shift

:: Launch the command
::
java %VMARGS% -Xbootclasspath/p:"%TOOLS_JAR%" -jar "%VISUALGC_JAR%" %0 %1 %2 %3 %4 %5 %6 %7 %8 %9

GOTO :EOF

:which %cmd% location
:: performs something similar to the unix which command
setlocal
:: find the cmd given in %1 in the PATH
set res=%~$PATH:1%
endlocal & set "%2=%res%"
GOTO :EOF

:dirname %file% dirpart
:: performs something similar to the unix dirname command
setlocal
:: get just the drive and path components of the given file
set drivepath=%~dp1%
:: eliminate the trailing backslash
set res=%drivepath:~0,-1%
endlocal & set "%2=%res%"
GOTO :EOF
