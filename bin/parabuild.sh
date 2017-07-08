#!/bin/sh

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false
darwin=false
os400=false
hpux=false
case "`uname`" in
CYGWIN*) cygwin=true;;
Darwin*) darwin=true;;
OS400*) os400=true;;
HP-UX*) hpux=true;;
esac

# resolve links - $0 may be a softlink
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

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# Set PARABUILD_HOME
PARABUILD_HOME=`cd "$PRGDIR/.." >/dev/null; pwd`

# Set PARABUILD_BASE from PARABUILD_HOME
PARABUILD_BASE="$PARABUILD_HOME/etc"

# Ensure that any user defined CLASSPATH variables are not used on startup,
# but allow them to be specified in setenv.sh, in rare case when it is needed.
CLASSPATH=

if [ -r "$PARABUILD_BASE/bin/setenv.sh" ]; then
  . "$PARABUILD_BASE/bin/setenv.sh"
elif [ -r "$PARABUILD_HOME/bin/setenv.sh" ]; then
  . "$PARABUILD_HOME/bin/setenv.sh"
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$JRE_HOME" ] && JRE_HOME=`cygpath --unix "$JRE_HOME"`
  [ -n "$PARABUILD_HOME" ] && PARABUILD_HOME=`cygpath --unix "$PARABUILD_HOME"`
  [ -n "$PARABUILD_BASE" ] && PARABUILD_BASE=`cygpath --unix "$PARABUILD_BASE"`
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

# Ensure that neither PARABUILD_HOME nor PARABUILD_BASE contains a colon
# as this is used as the separator in the classpath and Java provides no
# mechanism for escaping if the same character appears in the path.
case $PARABUILD_HOME in
  *:*) echo "Using PARABUILD_HOME:   $PARABUILD_HOME";
       echo "Unable to start as PARABUILD_HOME contains a colon (:) character";
       exit 1;
esac
case $PARABUILD_BASE in
  *:*) echo "Using PARABUILD_BASE:   $PARABUILD_BASE";
       echo "Unable to start as PARABUILD_BASE contains a colon (:) character";
       exit 1;
esac

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

# Get standard Java environment variables
if $os400; then
  # -r will Only work on the os400 if the files are:
  # 1. owned by the user
  # 2. owned by the PRIMARY group of the user
  # this will not work if the user belongs in secondary groups
  . "$PARABUILD_HOME"/bin/setclasspath.sh
else
  if [ -r "$PARABUILD_HOME"/bin/setclasspath.sh ]; then
    . "$PARABUILD_HOME"/bin/setclasspath.sh
  else
    echo "Cannot find $PARABUILD_HOME/bin/setclasspath.sh"
    echo "This file is needed to run this program"
    exit 1
  fi
fi

# Add on extra jar files to CLASSPATH
if [ ! -z "$CLASSPATH" ] ; then
  CLASSPATH="$CLASSPATH":
fi
CLASSPATH="$CLASSPATH""$PARABUILD_HOME"/bin/bootstrap.jar

if [ -z "$PARABUILD_OUT" ] ; then
  PARABUILD_OUT="$PARABUILD_BASE"/logs/catalina.out
fi

if [ -z "$PARABUILD_TMPDIR" ] ; then
  # Define the java.io.tmpdir to use for Parabuild
  PARABUILD_TMPDIR="$PARABUILD_BASE"/temp
fi

# Add tomcat-juli.jar to classpath
# tomcat-juli.jar can be over-ridden per instance
if [ -r "$PARABUILD_BASE/bin/tomcat-juli.jar" ] ; then
  CLASSPATH=$CLASSPATH:$PARABUILD_BASE/bin/tomcat-juli.jar
else
  CLASSPATH=$CLASSPATH:$PARABUILD_HOME/bin/tomcat-juli.jar
fi

# Bugzilla 37848: When no TTY is available, don't output to console
have_tty=0
if [ "`tty`" != "not a tty" ]; then
    have_tty=1
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  JRE_HOME=`cygpath --absolute --windows "$JRE_HOME"`
  PARABUILD_HOME=`cygpath --absolute --windows "$PARABUILD_HOME"`
  PARABUILD_BASE=`cygpath --absolute --windows "$PARABUILD_BASE"`
  PARABUILD_TMPDIR=`cygpath --absolute --windows "$PARABUILD_TMPDIR"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
fi

JSSE_OPTS="-Djdk.tls.ephemeralDHKeySize=2048"
JAVA_OPTS="$JSSE_OPTS"

# Register custom URL handlers
# Do this here so custom URL handles (specifically 'war:...') can be used in the security policy
JAVA_OPTS="$JAVA_OPTS -Djava.protocol.handler.pkgs=org.apache.catalina.webresources"

# Set juli LogManager config file if it is present and an override has not been issued
if [ -z "$LOGGING_CONFIG" ]; then
  if [ -r "$PARABUILD_BASE"/conf/logging.properties ]; then
    LOGGING_CONFIG="-Djava.util.logging.config.file=$PARABUILD_BASE/conf/logging.properties"
  else
    # Bugzilla 45585
    LOGGING_CONFIG="-Dnop"
  fi
fi

if [ -z "$LOGGING_MANAGER" ]; then
  LOGGING_MANAGER="-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager"
fi

# Set UMASK unless it has been overridden
if [ -z "$UMASK" ]; then
    UMASK="0027"
fi
umask $UMASK

# Uncomment the following line to make the umask available when using the
# org.apache.catalina.security.SecurityListener
#JAVA_OPTS="$JAVA_OPTS -Dorg.apache.catalina.security.SecurityListener.UMASK=`umask`"

if [ -z "$USE_NOHUP" ]; then
    if $hpux; then
        USE_NOHUP="true"
    else
        USE_NOHUP="false"
    fi
fi
unset _NOHUP
if [ "$USE_NOHUP" = "true" ]; then
    _NOHUP=nohup
fi

# ----- Execute The Requested Command -----------------------------------------

# Bugzilla 37848: only output this if we have a TTY
if [ $have_tty -eq 1 ]; then
  echo "Using PARABUILD_BASE:   $PARABUILD_BASE"
  echo "Using PARABUILD_HOME:   $PARABUILD_HOME"
  echo "Using PARABUILD_TMPDIR: $PARABUILD_TMPDIR"
  if [ "$1" = "debug" ] ; then
    echo "Using JAVA_HOME:       $JAVA_HOME"
  else
    echo "Using JRE_HOME:        $JRE_HOME"
  fi
  echo "Using CLASSPATH:       $CLASSPATH"
  if [ ! -z "$PARABUILD_PID" ]; then
    echo "Using PARABUILD_PID:    $PARABUILD_PID"
  fi
fi

if [ "$1" = "jpda" ] ; then
  if [ -z "$JPDA_TRANSPORT" ]; then
    JPDA_TRANSPORT="dt_socket"
  fi
  if [ -z "$JPDA_ADDRESS" ]; then
    JPDA_ADDRESS="localhost:8000"
  fi
  if [ -z "$JPDA_SUSPEND" ]; then
    JPDA_SUSPEND="n"
  fi
  if [ -z "$JPDA_OPTS" ]; then
    JPDA_OPTS="-agentlib:jdwp=transport=$JPDA_TRANSPORT,address=$JPDA_ADDRESS,server=y,suspend=$JPDA_SUSPEND"
  fi
  PARABUILD_OPTS="$JPDA_OPTS $PARABUILD_OPTS"
  shift
fi

if [ "$1" = "debug" ] ; then
  if $os400; then
    echo "Debug command not available on OS400"
    exit 1
  else
    shift
    if [ "$1" = "-security" ] ; then
      if [ $have_tty -eq 1 ]; then
        echo "Using Security Manager"
      fi
      shift
      exec "$_RUNJDB" "$LOGGING_CONFIG" $LOGGING_MANAGER $JAVA_OPTS $PARABUILD_OPTS \
        -classpath "$CLASSPATH" \
        -sourcepath "$PARABUILD_HOME"/../../java \
        -Djava.security.manager \
        -Djava.security.policy=="$PARABUILD_BASE"/conf/catalina.policy \
        -Dcatalina.base="$PARABUILD_BASE" \
        -Dcatalina.home="$PARABUILD_HOME" \
        -Djava.io.tmpdir="$PARABUILD_TMPDIR" \
        org.apache.catalina.startup.Bootstrap "$@" start
    else
      exec "$_RUNJDB" "$LOGGING_CONFIG" $LOGGING_MANAGER $JAVA_OPTS $PARABUILD_OPTS \
        -classpath "$CLASSPATH" \
        -sourcepath "$PARABUILD_HOME"/../../java \
        -Dcatalina.base="$PARABUILD_BASE" \
        -Dcatalina.home="$PARABUILD_HOME" \
        -Djava.io.tmpdir="$PARABUILD_TMPDIR" \
        org.apache.catalina.startup.Bootstrap "$@" start
    fi
  fi

elif [ "$1" = "run" ]; then

  shift
  if [ "$1" = "-security" ] ; then
    if [ $have_tty -eq 1 ]; then
      echo "Using Security Manager"
    fi
    shift
    eval exec "\"$_RUNJAVA\"" "\"$LOGGING_CONFIG\"" $LOGGING_MANAGER $JAVA_OPTS $PARABUILD_OPTS \
      -classpath "\"$CLASSPATH\"" \
      -Djava.security.manager \
      -Djava.security.policy=="\"$PARABUILD_BASE/conf/catalina.policy\"" \
      -Dcatalina.base="\"$PARABUILD_BASE\"" \
      -Dcatalina.home="\"$PARABUILD_HOME\"" \
      -Djava.io.tmpdir="\"$PARABUILD_TMPDIR\"" \
      org.apache.catalina.startup.Bootstrap "$@" start
  else
    eval exec "\"$_RUNJAVA\"" "\"$LOGGING_CONFIG\"" $LOGGING_MANAGER $JAVA_OPTS $PARABUILD_OPTS \
      -classpath "\"$CLASSPATH\"" \
      -Dcatalina.base="\"$PARABUILD_BASE\"" \
      -Dcatalina.home="\"$PARABUILD_HOME\"" \
      -Djava.io.tmpdir="\"$PARABUILD_TMPDIR\"" \
      org.apache.catalina.startup.Bootstrap "$@" start
  fi

elif [ "$1" = "start" ] ; then

  if [ ! -z "$PARABUILD_PID" ]; then
    if [ -f "$PARABUILD_PID" ]; then
      if [ -s "$PARABUILD_PID" ]; then
        echo "Existing PID file found during start."
        if [ -r "$PARABUILD_PID" ]; then
          PID=`cat "$PARABUILD_PID"`
          ps -p $PID >/dev/null 2>&1
          if [ $? -eq 0 ] ; then
            echo "Tomcat appears to still be running with PID $PID. Start aborted."
            echo "If the following process is not a Tomcat process, remove the PID file and try again:"
            ps -f -p $PID
            exit 1
          else
            echo "Removing/clearing stale PID file."
            rm -f "$PARABUILD_PID" >/dev/null 2>&1
            if [ $? != 0 ]; then
              if [ -w "$PARABUILD_PID" ]; then
                cat /dev/null > "$PARABUILD_PID"
              else
                echo "Unable to remove or clear stale PID file. Start aborted."
                exit 1
              fi
            fi
          fi
        else
          echo "Unable to read PID file. Start aborted."
          exit 1
        fi
      else
        rm -f "$PARABUILD_PID" >/dev/null 2>&1
        if [ $? != 0 ]; then
          if [ ! -w "$PARABUILD_PID" ]; then
            echo "Unable to remove or write to empty PID file. Start aborted."
            exit 1
          fi
        fi
      fi
    fi
  fi

  shift
  touch "$PARABUILD_OUT"
  if [ "$1" = "-security" ] ; then
    if [ $have_tty -eq 1 ]; then
      echo "Using Security Manager"
    fi
    shift
    eval $_NOHUP "\"$_RUNJAVA\"" "\"$LOGGING_CONFIG\"" $LOGGING_MANAGER $JAVA_OPTS $PARABUILD_OPTS \
      -classpath "\"$CLASSPATH\"" \
      -Djava.security.manager \
      -Djava.security.policy=="\"$PARABUILD_BASE/conf/catalina.policy\"" \
      -Dcatalina.base="\"$PARABUILD_BASE\"" \
      -Dcatalina.home="\"$PARABUILD_HOME\"" \
      -Djava.io.tmpdir="\"$PARABUILD_TMPDIR\"" \
      org.apache.catalina.startup.Bootstrap "$@" start \
      >> "$PARABUILD_OUT" 2>&1 "&"

  else
    eval $_NOHUP "\"$_RUNJAVA\"" "\"$LOGGING_CONFIG\"" $LOGGING_MANAGER $JAVA_OPTS $PARABUILD_OPTS \
      -classpath "\"$CLASSPATH\"" \
      -Dcatalina.base="\"$PARABUILD_BASE\"" \
      -Dcatalina.home="\"$PARABUILD_HOME\"" \
      -Djava.io.tmpdir="\"$PARABUILD_TMPDIR\"" \
      org.apache.catalina.startup.Bootstrap "$@" start \
      >> "$PARABUILD_OUT" 2>&1 "&"

  fi

  if [ ! -z "$PARABUILD_PID" ]; then
    echo $! > "$PARABUILD_PID"
  fi

  echo "Tomcat started."

elif [ "$1" = "stop" ] ; then

  shift

  SLEEP=5
  if [ ! -z "$1" ]; then
    echo $1 | grep "[^0-9]" >/dev/null 2>&1
    if [ $? -gt 0 ]; then
      SLEEP=$1
      shift
    fi
  fi

  FORCE=0
  if [ "$1" = "-force" ]; then
    shift
    FORCE=1
  fi

  if [ ! -z "$PARABUILD_PID" ]; then
    if [ -f "$PARABUILD_PID" ]; then
      if [ -s "$PARABUILD_PID" ]; then
        kill -0 `cat "$PARABUILD_PID"` >/dev/null 2>&1
        if [ $? -gt 0 ]; then
          echo "PID file found but no matching process was found. Stop aborted."
          exit 1
        fi
      else
        echo "PID file is empty and has been ignored."
      fi
    else
      echo "\$PARABUILD_PID was set but the specified file does not exist. Is Tomcat running? Stop aborted."
      exit 1
    fi
  fi

  eval "\"$_RUNJAVA\"" $JAVA_OPTS \
    -classpath "\"$CLASSPATH\"" \
    -Dcatalina.base="\"$PARABUILD_BASE\"" \
    -Dcatalina.home="\"$PARABUILD_HOME\"" \
    -Djava.io.tmpdir="\"$PARABUILD_TMPDIR\"" \
    org.apache.catalina.startup.Bootstrap "$@" stop

  # stop failed. Shutdown port disabled? Try a normal kill.
  if [ $? != 0 ]; then
    if [ ! -z "$PARABUILD_PID" ]; then
      echo "The stop command failed. Attempting to signal the process to stop through OS signal."
      kill -15 `cat "$PARABUILD_PID"` >/dev/null 2>&1
    fi
  fi

  if [ ! -z "$PARABUILD_PID" ]; then
    if [ -f "$PARABUILD_PID" ]; then
      while [ $SLEEP -ge 0 ]; do
        kill -0 `cat "$PARABUILD_PID"` >/dev/null 2>&1
        if [ $? -gt 0 ]; then
          rm -f "$PARABUILD_PID" >/dev/null 2>&1
          if [ $? != 0 ]; then
            if [ -w "$PARABUILD_PID" ]; then
              cat /dev/null > "$PARABUILD_PID"
              # If Tomcat has stopped don't try and force a stop with an empty PID file
              FORCE=0
            else
              echo "The PID file could not be removed or cleared."
            fi
          fi
          echo "Tomcat stopped."
          break
        fi
        if [ $SLEEP -gt 0 ]; then
          sleep 1
        fi
        if [ $SLEEP -eq 0 ]; then
          echo "Tomcat did not stop in time."
          if [ $FORCE -eq 0 ]; then
            echo "PID file was not removed."
          fi
          echo "To aid diagnostics a thread dump has been written to standard out."
          kill -3 `cat "$PARABUILD_PID"`
        fi
        SLEEP=`expr $SLEEP - 1 `
      done
    fi
  fi

  KILL_SLEEP_INTERVAL=5
  if [ $FORCE -eq 1 ]; then
    if [ -z "$PARABUILD_PID" ]; then
      echo "Kill failed: \$PARABUILD_PID not set"
    else
      if [ -f "$PARABUILD_PID" ]; then
        PID=`cat "$PARABUILD_PID"`
        echo "Killing Tomcat with the PID: $PID"
        kill -9 $PID
        while [ $KILL_SLEEP_INTERVAL -ge 0 ]; do
            kill -0 `cat "$PARABUILD_PID"` >/dev/null 2>&1
            if [ $? -gt 0 ]; then
                rm -f "$PARABUILD_PID" >/dev/null 2>&1
                if [ $? != 0 ]; then
                    if [ -w "$PARABUILD_PID" ]; then
                        cat /dev/null > "$PARABUILD_PID"
                    else
                        echo "The PID file could not be removed."
                    fi
                fi
                echo "The Tomcat process has been killed."
                break
            fi
            if [ $KILL_SLEEP_INTERVAL -gt 0 ]; then
                sleep 1
            fi
            KILL_SLEEP_INTERVAL=`expr $KILL_SLEEP_INTERVAL - 1 `
        done
        if [ $KILL_SLEEP_INTERVAL -lt 0 ]; then
            echo "Tomcat has not been killed completely yet. The process might be waiting on some system call or might be UNINTERRUPTIBLE."
        fi
      fi
    fi
  fi

elif [ "$1" = "configtest" ] ; then

    eval "\"$_RUNJAVA\"" $LOGGING_MANAGER $JAVA_OPTS \
      -classpath "\"$CLASSPATH\"" \
      -Dcatalina.base="\"$PARABUILD_BASE\"" \
      -Dcatalina.home="\"$PARABUILD_HOME\"" \
      -Djava.io.tmpdir="\"$PARABUILD_TMPDIR\"" \
      org.apache.catalina.startup.Bootstrap configtest
    result=$?
    if [ $result -ne 0 ]; then
        echo "Configuration error detected!"
    fi
    exit $result

elif [ "$1" = "version" ] ; then

    "$_RUNJAVA"   \
      -classpath "$PARABUILD_HOME/lib/catalina.jar" \
      org.apache.catalina.util.ServerInfo

else

  echo "Usage: parabuild.sh ( commands ... )"
  echo "commands:"
  if $os400; then
    echo "  debug             Start Parabuild in a debugger (not available on OS400)"
    echo "  debug -security   Debug Parabuild with a security manager (not available on OS400)"
  else
    echo "  debug             Start Parabuild in a debugger"
    echo "  debug -security   Debug Parabuild with a security manager"
  fi
  echo "  jpda start        Start Parabuild under JPDA debugger"
  echo "  run               Start Parabuild in the current window"
  echo "  run -security     Start in the current window with security manager"
  echo "  start             Start Parabuild in a separate window"
  echo "  start -security   Start in a separate window with security manager"
  echo "  stop              Stop Parabuild, waiting up to 5 seconds for the process to end"
  echo "  stop n            Stop Parabuild, waiting up to n seconds for the process to end"
  echo "  stop -force       Stop Parabuild, wait up to 5 seconds and then use kill -KILL if still running"
  echo "  stop n -force     Stop Parabuild, wait up to n seconds and then use kill -KILL if still running"
  echo "  configtest        Run a basic syntax check on server.xml - check exit code for result"
  echo "  version           What version of tomcat are you running?"
  echo "Note: Waiting for the process to end and use of the -force option require that \$PARABUILD_PID is defined"
  exit 1

fi
