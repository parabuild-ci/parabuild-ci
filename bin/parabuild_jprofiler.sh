#!/bin/sh

# Parabuild command line script

# Get path to the script
PRG="$0"
while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done
PRGDIR=`dirname "$PRG"`

# Set CATALINA_HOME
CATALINA_HOME=`cd "$PRGDIR/.." >/dev/null; pwd`

# Set CATALINA_BASE
CATALINA_BASE="$CATALINA_HOME"/etc

# Set CATALINA_OUT
CATALINA_OUT="$CATALINA_HOME"/logs/catalina.out

# Set CATALINA_OPTS
CATALINA_OPTS="-agentpath:/Applications/JProfiler.app/Contents/Resources/app/bin/macos/libjprofilerti.jnilib=port=8849 -Xms1024m -Xmx1024m -Djava.awt.headless=true -Dorg.apache.logging.log4j.simplelog.StatusLogger.level=TRACE -Dlog4j.configurationFile=log4j2.xml"


# Set LOGGING_CONFIG
LOGGING_CONFIG="-Djava.util.logging.config.file=$CATALINA_HOME/conf/logging.properties"

# Call Tomcat script with the same parameters
. "$CATALINA_HOME"/bin/catalina.sh $@