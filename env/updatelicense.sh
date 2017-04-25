# !/bin/sh
#

# Validate
echo 'DEVENV Info  : Updating Devenv license'

DEVENV_OK=true

if $DEVENV_OK && [ ! -f "$JAVA_HOME/bin/jar" ] ; then
  echo 'DEVENV Error : jar utility not found. Make sure JAVA_HOME points to correct Java installation.'
  DEVENV_OK=false
fi

if $DEVENV_OK && [ ! -f "./devenv.jar" ] ; then
  echo 'DEVENV Error : devenv.jar file not found. Make sure this script is executed from the same directory where devenv.jar is placed.'
  DEVENV_OK=false
fi

if $DEVENV_OK && [ ! -f "./devenv.lic" ] ; then
  echo 'DEVENV Error : devenv.lic Devenv license file not found. Make sure this script is executed from the same directory where devenv.jar and devenv.lic is placed.'
  DEVENV_OK=false
fi


# Update
if $DEVENV_OK ; then
  $JAVA_HOME/bin/jar -uf ./devenv.jar ./devenv.lic
  [ $? -ne 0 ] && DEVENV_OK=false
fi


# Finish
if $DEVENV_OK ; then
  echo 'DEVENV Info  : Devenv license has been sussessfuly updated.'
else
  echo 'DEVENV Error : Devenv license has not been updated - see messages above.'
fi

unset DEVENV_OK
