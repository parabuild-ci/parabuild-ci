# Configuration variables:
#
#   JAVA_HOME              If Parabuild installation does not include JRE, JAVA_HOME
#                          must point at your Java Development Kit installation.
#                          Parabuild requires JDK version 1.4.2.
#
#   PARABUILD_HOME         May point at your Parabuild installation directory.
#                          If not set, this script will attempt to detect Parabuild
#                          home directory.

# OS specific support.
cygwin=false
os400=false
case "`uname`" in
CYGWIN*) cygwin=true;;
OS400*) os400=true;;
esac

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`
PARABUILD_HOME=`cd "$PRGDIR/.." ; pwd`
if [ -r "$PARABUILD_HOME"/bin/setenv.sh ]; then
  . "$PARABUILD_HOME"/bin/setenv.sh
fi


# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$PARABUILD_HOME" ] && PARABUILD_HOME=`cygpath --unix "$PARABUILD_HOME"`
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
  [ -n "$JSSE_HOME" ] && JSSE_HOME=`cygpath --path --unix "$JSSE_HOME"`
fi

# Get location of bundled JRE
if [ -r "$PARABUILD_HOME"/jre/bin/java ]; then
  PARABUILD_JAVA_HOME="$PARABUILD_HOME"/jre
fi

# For OS400
if $os400; then
  # Set job priority to standard for interactive (interactive - 6) by using
  # the interactive priority - 6, the helper threads that respond to requests
  # will be running at the same priority as interactive jobs.
  COMMAND='chgjob job('$JOBNAME') runpty(6)'
  system $COMMAND

  # Enable multi threading
  export QIBM_MULTI_THREADED=Y
fi

# Use standard Java environment variables if necessary
if [ -z "$PARABUILD_JAVA_HOME" ]; then
  PARABUILD_JAVA_HOME="$JAVA_HOME"
fi

# Make sure prerequisite environment variables are set
if [ -z "$PARABUILD_JAVA_HOME" ]; then
  echo "The JAVA_HOME environment variable is not defined"
  echo "This environment variable is needed to run this program"
  exit 1
fi

# Make sure that PARABUILD_JAVA_HOME points to correct location
if [ ! -r "$PARABUILD_JAVA_HOME"/bin/java ]; then
  echo "The JAVA_HOME environment variable is not defined correctly"
  echo "This environment variable is needed to run this program"
  exit 2
fi

# Set the default -Djava.endorsed.dirs argument
JAVA_ENDORSED_DIRS="$PARABUILD_HOME"/lib/common/endorsed

# Set standard CLASSPATH
CLASSPATH="$PARABUILD_JAVA_HOME"/lib/tools.jar

# Set standard commands for invoking Java.
_RUNJAVA="$PARABUILD_JAVA_HOME"/bin/java

# Add on extra jar files to CLASSPATH
if [ -n "$JSSE_HOME" ]; then
  CLASSPATH="$CLASSPATH":"$JSSE_HOME"/lib/jcert.jar:"$JSSE_HOME"/lib/jnet.jar:"$JSSE_HOME"/lib/jsse.jar
fi
CLASSPATH="$CLASSPATH":"$PARABUILD_HOME"/bin/bootstrap.jar

if [ -z "$PARABUILD_TMPDIR" ] ; then
  # Define the java.io.tmpdir to use for Parabuild
  PARABUILD_TMPDIR="$PARABUILD_HOME"/etc/temp
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  JAVA_HOME=`cygpath --path --windows "$JAVA_HOME"`
  PARABUILD_HOME=`cygpath --path --windows "$PARABUILD_HOME"`
  PARABUILD_TMPDIR=`cygpath --path --windows "$PARABUILD_TMPDIR"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  JSSE_HOME=`cygpath --path --windows "$JSSE_HOME"`
fi

# Parabuild Java settings
JAVA_OPTS="-Xms100m -Xmx200m -Djava.awt.headless=true"

# Set Parabuild options
PARABUILD_OPTS=

# Execute the command
if [ "$1" = "run" ]; then

  shift
  if [ "$1" = "-security" ] ; then
    echo "Using Security Manager"
    shift
    exec "$_RUNJAVA" $JAVA_OPTS $PARABUILD_OPTS \
      -Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" -classpath "$CLASSPATH" \
      -Djava.security.manager \
      -Djava.security.policy=="$PARABUILD_HOME"/etc/conf/catalina.policy \
      -Dcatalina.base="$PARABUILD_HOME"/etc \
      -Dcatalina.home="$PARABUILD_HOME"/lib \
      -Djava.io.tmpdir="$PARABUILD_TMPDIR" \
      org.apache.catalina.startup.Bootstrap "$@" start
  else
    exec "$_RUNJAVA" $JAVA_OPTS $PARABUILD_OPTS \
      -Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" -classpath "$CLASSPATH" \
      -Dcatalina.base="$PARABUILD_HOME"/etc \
      -Dcatalina.home="$PARABUILD_HOME"/lib \
      -Djava.io.tmpdir="$PARABUILD_TMPDIR" \
      org.apache.catalina.startup.Bootstrap "$@" start
  fi

elif [ "$1" = "start" ] ; then

  shift
  touch "$PARABUILD_HOME"/logs/parabuild-console.log
  if [ "$1" = "-security" ] ; then
    echo "Using Security Manager"
    shift
    "$_RUNJAVA" $JAVA_OPTS $PARABUILD_OPTS \
      -Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" -classpath "$CLASSPATH" \
      -Djava.security.manager \
      -Djava.security.policy=="$PARABUILD_HOME"/etc/conf/catalina.policy \
      -Dcatalina.base="$PARABUILD_HOME"/etc \
      -Dcatalina.home="$PARABUILD_HOME"/lib \
      -Djava.io.tmpdir="$PARABUILD_TMPDIR" \
      org.apache.catalina.startup.Bootstrap "$@" start \
      >> "$PARABUILD_HOME"/logs/parabuild-console.log 2>&1 &

      if [ ! -z "$PARABUILD_PID" ]; then
        echo $! > $PARABUILD_PID
      fi
  else
    "$_RUNJAVA" $JAVA_OPTS $PARABUILD_OPTS \
      -Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" -classpath "$CLASSPATH" \
      -Dcatalina.base="$PARABUILD_HOME"/etc \
      -Dcatalina.home="$PARABUILD_HOME"/lib \
      -Djava.io.tmpdir="$PARABUILD_TMPDIR" \
      org.apache.catalina.startup.Bootstrap "$@" start \
      >> "$PARABUILD_HOME"/logs/parabuild-console.log 2>&1 &

      if [ ! -z "$PARABUILD_PID" ]; then
        echo $! > $PARABUILD_PID
      fi
  fi

elif [ "$1" = "stop" ] ; then

  shift
  exec "$_RUNJAVA" $JAVA_OPTS $PARABUILD_OPTS \
    -Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" -classpath "$CLASSPATH" \
    -Dcatalina.base="$PARABUILD_HOME"/etc \
    -Dcatalina.home="$PARABUILD_HOME"/lib \
    -Djava.io.tmpdir="$PARABUILD_TMPDIR" \
    org.apache.catalina.startup.Bootstrap "$@" stop

else

  echo "Usage: parabuild.sh ( commands ... )"
  echo "commands:"
  echo "  run               Start Parabuild in the current window"
  echo "  start             Start Parabuild in background"
  echo "  stop              Stop Parabuild"
  exit 0

fi
