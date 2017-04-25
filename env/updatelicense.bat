@echo off

rem Validate
echo DEVENV Info  : Updating Devenv license

if exist %JAVA_HOME%\bin\jar.exe goto check_devenv
  echo DEVENV Error : jar.exe utility not found. Make sure JAVA_HOME points to correct Java installation.
  goto error_end

:check_devenv
if exist .\devenv.jar goto check_license
  echo DEVENV Error : devenv.jar file not found. Make sure this script is executed from the same directory where devenv.jar is placed.
  goto error_end

:check_license
if exist .\devenv.lic goto update_license
  echo DEVENV Error : devenv.lic Devenv license file not found. Make sure this script is executed from the same directory where devenv.jar and devenv.lic is placed.
  goto error_end


rem Update
:update_license
%JAVA_HOME%\bin\jar.exe -uf devenv.jar devenv.lic

if errorlevel 1 goto error_end
  echo DEVENV Info  : Devenv license has been sussessfuly updated.
  pause
  goto end


:error_end
  echo DEVENV Error : Devenv license has not been updated - see messages above.
  pause
  goto end
  
rem Exit
:end
