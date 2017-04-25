
#**************************************************************************
#
# $Header:   Y:/archives/dv/intersolv/Merant_Build/VM/Integration/Event_Triggers/VMMBI_om_PET.pl-arc   1.13   08 Mar 2005 15:25:54   rjoachim  $
#
# Copyright (c) 2003, MERANT.  All Rights Reserved.
#
# VMMBI_om_PET.pl -- om pre-event trigger
#
#
#  Changes:
#
#     07/18/03 Roger P. Joachim - Original code
#
#***************************************************************************

#***************************************************************************
#
# 08/20/03 This program is currently waiting on a change from catalyst.
#
# Setting the current directory here works for the event triggers but it
# does not work for the om processing.  om is still running off of the
# current directory set before om was called or its -c parameter.  For now
# we will have to use the -c parameter which means we can not resolve
# environment variables inbeded in the build directory.
#
#***************************************************************************

#use strict;     # Commented out as a temporary fix for Perl Version 5.8.6

#***************************************************************************
#***************************************************************************
#*
#*    S T A R T   O F   P A C K A G E S
#*
#*
#*    All packages must be located at the top of the program
#*
#***************************************************************************
#***************************************************************************

#***************************************************************************
#
# Class used to hold bldmake and om command line parameters
#
#***************************************************************************

package CMD_LINE_PARMS;
BEGIN
{
    # Class field deffinitions

    use fields qw( jobName
                   userID
                   jobDTG
                   buildMachine
                   publicJob
                   verboseOutput
                   loggingEnabled
                 );

    #***************************************************************************
    #
    # Constructor
    #
    #***************************************************************************

    sub new                                        # Constructor (can be any name, not just new)
    {
       my( $class )           = shift ;            # Get the class name or the object address which is the first parameter
       my( $cmdLineIn )       = shift if @_  ;

       my( %bldmakeParmHash ) = {};

       #***************************************************************************
       #
       # The "ref" function returns a TRUE value if EXPR ($class) is a symbolic
       # reference, and returns FALSE otherwise.  If the referenced object has been
       # blessed into a package, then that package name is returned instead.
       #
       # It is the second part of the above statement that is important.  If $class
       # is the address of an object then the class name is returned otherwise zero
       # is returned which allows the "||" operator to go on the second argument
       # which we assume is a string containing the class name.
       #
       #***************************************************************************

        my( $className ) = ref( $class ) || $class;  # Accept either an object or a class name

        my( $self ) = fields::new( $className );

        bless( $self, $className);

        #***************************************************************************
        #
        # Initialize variables
        #
        #***************************************************************************

        $self->{loggingEnabled} = 0;

        #***************************************************************************
        #
        # Parse the MB command line and extract the values we are interested in
        #
        #***************************************************************************

        %bldmakeParmHash = Openmake::PrePost::ParseParms( $cmdLineIn );

        $self->{jobName}       = $bldmakeParmHash{ "lj" };
        $self->{userID}        = $bldmakeParmHash{ "lo" };
        $self->{jobDTG}        = $bldmakeParmHash{ "ld" };
        $self->{buildMachine}  = $bldmakeParmHash{ "lm" };
        $self->{publicJob}     = $bldmakeParmHash{ "lp" };
        $self->{verboseOutput} = $bldmakeParmHash{ "ov" };

        #***************************************************************************
        #
        # Determine if logging is enabled
        #
        #***************************************************************************

        ENABLED_BLOCK:
        {
            last ENABLED_BLOCK if ( $self->{jobName}       eq "" );
            last ENABLED_BLOCK if ( $self->{userID}        eq "" );
            last ENABLED_BLOCK if ( $self->{jobDTG}        eq "" );
            last ENABLED_BLOCK if ( $self->{buildMachine}  eq "" );

            $self->{loggingEnabled} = 1;

        }          # End of ENABLED_BLOCK:

        return( $self  );                     # Return the freshly generated CMD_LINE_PARMS object

    }   ## end -- new

    #***************************************************************************
    #
    # Methods
    #
    #***************************************************************************

    #***************************************************************************
    #
    # toString --  Display the contents of the object
    #
    # Input Parameters:
    #
    #    None
    #
    # Output Parameters:
    #
    #    $cmdLineParmsOut = Command line parameters
    #
    #***************************************************************************

    sub toString
    {
        my( $class )   = shift;  # Gets the object address which is the first input parameter

        my( $cmdLineParmsOut ) = "";

        $cmdLineParmsOut = sprintf( "%-30.30s %-10.10s %-20.20s %-20.20s %-12.12s %-12.12s %-12.12s ",
                                    $class->{jobName},
                                    $class->{userID},
                                    $class->{jobDTG},
                                    $class->{buildMachine},
                                    $class->{publicJob},
                                    $class->{verboseOutput},
                                    $class->{loggingEnabled}
                                  );

        return( $cmdLineParmsOut . "\n" );

    }   ## end -- toString

}    ## End of package CMD_LINE_PARMS

#*****************************************************************************
#*****************************************************************************
#*
#*   E N D   O F   P A C K A G E S
#*
#*****************************************************************************
#*****************************************************************************

use Cwd;

#***************************************************************************
#
# Main program variable deffinitions
#
#***************************************************************************

our( $TRUE )                  = 1;                        # To store the TRUE boolean value
our( $FALSE )                 = 0;                        # To store the FALSE boolean value

our( $PGM_VERSION )           = '$Header:   Y:/archives/dv/intersolv/Merant_Build/VM/Integration/Event_Triggers/VMMBI_om_PET.pl-arc   1.13   08 Mar 2005 15:25:54   rjoachim  $';

our( $statusOut )             = 0;
our( $rval )                  = 0;
our( $OMBUILD_NUMBER )        = 0;
our( $BUILD_DIRECTORY )       = "";
our( $ENV_VAR_BUILD_DIR )     = 'VMMBI_BLDDIR';
our( $IS_W32 )                = $FALSE;
our( $IS_UNIX )               = $FALSE;
our( $CURRENT_OS )            = "";
our( $PROGRAM_DIR )           = "";
our( $CURRENT_WORKING_DIR )   = "";
our( $DEBUG )                 = $FALSE;
our( $DEBUG_OPTION )          = $FALSE;
our( $OUR_PROGRAM_NAME )      = "VMMBI_om_PET.pl";
our( $CMD_LINE_PARMS_OBJECT ) = 0;

#*****************************************************************************
#
# Character variable assignments
#
#*****************************************************************************

our( $DEFAULT_BUILD_DIR )               = "";          # OS dependent
our( $OUR_SLASH )                       = "";          # OS dependent
our( $OTHER_SLASH )                     = "";          # OS dependent
our( $OUR_SLASH_ESCAPED )               = "";          # OS dependent
our( $OTHER_SLASH_ESCAPED )             = "";          # OS dependent
our( $VMMBI_UTILITY_PGM_NAME )          = "";          # OS dependent

#*****************************************************************************
#
# Unchanged program variables
#
#*****************************************************************************

our( $DQ )                              = '"';

#*****************************************************************************
#
# Local variables for main
#
#*****************************************************************************

my( $msg )                     = "";

log_msg("\n");
log_msg("*********************************************************\n");
log_msg("*\n");
log_msg("* VMMBI_om_PET.pl: Starting\n");
log_msg("*\n");
log_msg("*********************************************************\n");
log_msg("\n");

#*****************************************************************************
#
# Determine OS on which the script is being executed
#
#*****************************************************************************

( $IS_W32, $IS_UNIX ) = get_os_type();

if ( $IS_W32 )
{
    $CURRENT_OS = "W32";
}
elsif( $IS_UNIX )
{
    $CURRENT_OS = "UNIX";
}
else
{
    $statusOut++
}

#*****************************************************************************
#
# Set operating system dependent values after the operating system is known
#
#*****************************************************************************

if ( $IS_W32 )
{
    our( $DEFAULT_BUILD_DIR )               = "C:\\Merant_Build";
    our( $OUR_SLASH )                       = '\\';
    our( $OTHER_SLASH )                     = '/';
    our( $OUR_SLASH_ESCAPED )               = '\\\\';
    our( $OTHER_SLASH_ESCAPED )             = '\/';
    our( $VMMBI_UTILITY_PGM_NAME )          = "VMMBI_Utility.exe";
}

if ( $IS_UNIX )
{
    our( $DEFAULT_BUILD_DIR )               = '$home/Merant_Build';
    our( $OUR_SLASH )                       = '/';
    our( $OTHER_SLASH )                     = '\\';
    our( $OUR_SLASH_ESCAPED )               = '\/';
    our( $OTHER_SLASH_ESCAPED )             = '\\\\';
    our( $VMMBI_UTILITY_PGM_NAME )          = "VMMBI_Utility";
}

#*****************************************************************************
#
# Parse out the bldmake command line
#
#*****************************************************************************

$CMD_LINE_PARMS_OBJECT = CMD_LINE_PARMS->new( $OMCOMMANDLINE );

if ( $CMD_LINE_PARMS_OBJECT->{verboseOutput} == 1 )
{
    $DEBUG = $TRUE;
}

if ( $DEBUG )
{
    $msg = $CMD_LINE_PARMS_OBJECT->toString();
    log_msg( "Job Name                       User ID    Job DTG              Build Machine        Public Job   Verbose      Logging Enabled\n" );
    log_msg( "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  ~~~~~~~~~  ~~~~~~~~~~~~~~~~~~~  ~~~~~~~~~~~~~~~~~~~  ~~~~~~~~~~~  ~~~~~~~~~~~  ~~~~~~~~~~~~~~~\n" );
    log_msg( "$msg \n" );
}

#*****************************************************************************
#
# Perform main processing
#
#*****************************************************************************

MAIN_PROCESSING_BLOCK:
{
    #*****************************************************************************
    #
    # Get the current working directory
    #
    #*****************************************************************************

    $CURRENT_WORKING_DIR = get_current_dir();

    #*****************************************************************************
    #
    # Get the directory we are executing this program from
    #
    #*****************************************************************************

    ( $rval, $PROGRAM_DIR ) = get_program_path();

    $statusOut += $rval;

    #***************************************************************************
    #
    # Decrypt and parse the build directory value
    #
    #***************************************************************************

    ( $rval, $BUILD_DIRECTORY ) = get_build_dir();

    $statusOut += $rval;

    #***************************************************************************
    #
    # Tell them who we are
    #
    #***************************************************************************

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* ChangeMan Builder, Version Manager Integration Pre-Processor\n");
    log_msg("*\n");
    log_msg("*    Date: " . print_date() . " \n" );
    log_msg("*\n");
    log_msg("*    ChangeMan Builder om Variables:\n");
    log_msg("*\n");
    log_msg("*       OMCOMMANDLINE                = $OMCOMMANDLINE\n");
    log_msg("*       OMERRORRC                    = $OMERRORRC\n");
    log_msg("*       OMPROJECT                    = $OMPROJECT\n");
    log_msg("*       OMVPATHNAME                  = $OMVPATHNAME\n");
    log_msg("*       OMVPATH                      = $OMVPATH\n");
    log_msg("*       OMEMBEDTYPE                  = $OMEMBEDTYPE\n");
    log_msg("*\n");
    log_msg("*    VMMBI_bldmake_PET Variables:\n");
    log_msg("*\n");
    log_msg("*       Program name                 = $OUR_PROGRAM_NAME\n");
    log_msg("*       Program version              = $PGM_VERSION\n");
    log_msg("*       Current OS                   = $CURRENT_OS\n");
    log_msg("*       Program directory            = $PROGRAM_DIR\n");
    log_msg("*       Current working directory    = $CURRENT_WORKING_DIR\n");
    log_msg("*       Build directory              = $BUILD_DIRECTORY\n");
    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");

    #***************************************************************************
    #
    # If specified set the build directory
    #
    #***************************************************************************

    if ( $BUILD_DIRECTORY eq "" )
    {
        log_error_msg("VMMBI0100: Error: The build directory is null\n");
        $statusOut++;
        last MAIN_PROCESSING_BLOCK;
    }

    #***************************************************************************
    #
    # Change to the build directory
    #
    #***************************************************************************

    $rval = run_chdir( $BUILD_DIRECTORY );

    $statusOut += $rval;

}          # End of MAIN_PROCESSING_BLOCK:

log_msg("\n");
log_msg("*********************************************************\n");
log_msg("*\n");
log_msg("* VMMBI_om_PET.pl: Ending\n");
log_msg("*\n");
log_msg("*    Build Directory = $BUILD_DIRECTORY\n");
log_msg("*\n");
log_msg("*    Status          = $statusOut\n");
log_msg("*\n");
log_msg("*********************************************************\n");
log_msg("\n");

#***************************************************************************
#
# All done
#
# Note that Openmake appends statements to the event triggers it runs thus
# we can not perform a "exit" or a "return" under normal completion.  We must
# code like the main program is just going to continue after all of the
# subroutine deffinitions, which it will.
#
#***************************************************************************

if ( $statusOut > 0 )
{
    exit( $statusOut );
}
else
{
    $statusOut;
}

#***************************************************************************
#***************************************************************************
#
# End of main program
#
#***************************************************************************
#***************************************************************************

#***************************************************************************
#***************************************************************************
#
# Subroutines
#
#***************************************************************************
#***************************************************************************

#***************************************************************************
#
# get_decrypted_str -- Make a call to the C-Program with encrypted string as
#                      as input and get the decrypted string
#
# Input Parameters:
#
#     $encrypStrIn = The string to be decrypted
#
# Output Parameters:
#
#     $statusOut   = Return code
#                    0 = successful
#                    1 = failure
#     $decrypStr   = The decrypted string
#
#***************************************************************************

sub get_decrypted_str
{
    my( $encrypStrIn )      = shift;                    # Value of the encrypted string from VMMBI ENV var

    my( $decrypStr )        = "";                       # Decrypted string that will be returned
    my( $statusOut)         = 0;

    my( $VMMBI_UtilityPgm ) = "";                       # Path and program to the decryption program
    my( $cmd )              = "";                       # The command line to the C application
    my( $mode )             = 2;                        # The mode in which the application will run (write to file or write to standard out)
                                                        # 2 = Write to standard out
                                                        # 1 = Write to a file
    my( $rval )             = 0;
    my( @lines )            = ();
    my( $i )                = 0;

    #************************************************************************
    #
    # Start of by validating the environment variable for encrypted string i.e.
    # VMMBI16. If it is proper one, send the input to C application and get the
    # decrypted string from the command buffer and store it in a variable.
    # Return the status and decrypted string to main program
    #
    #************************************************************************

    DECRYPT_BLOCK:
    {
        if ( $encrypStrIn eq "" )
        {
            log_error_msg( "VMMBI3400: get_decrypted_str: Error: Environment variable does not exist\n" );
            $statusOut++;
            last DECRYPT_BLOCK;
        }

        $VMMBI_UtilityPgm = $PROGRAM_DIR . $OUR_SLASH . $VMMBI_UTILITY_PGM_NAME;

        if ( ! -f $VMMBI_UtilityPgm )
        {
            log_error_msg( "VMMBI3440: get_decrypted_str: Error: The VMMBI utility program is not available. '$VMMBI_UtilityPgm'\n" );
            $statusOut++;
            last DECRYPT_BLOCK;
        }

        #************************************************************************
        #
        # We must put the double quotes around "$VMMBI_UtilityPgm" or its croak
        # time when we get a path with spaces in it.
        #
        #************************************************************************

        $cmd = $DQ . $VMMBI_UtilityPgm . $DQ . " $encrypStrIn $mode  2>&1 |";

        $rval = open(CMD, $cmd );
        if ( ! $rval )
        {
            log_error_msg( "VMMBI3500: get_decrypted_str: Error: Decrypting variable: status = $rval\n" );
            $statusOut++;
            last DECRYPT_BLOCK;
        }

        @lines     = <CMD>;            # Read standard out into an array
        $decrypStr = @lines[0];        # The first element in the array should be the decrypted string

        $i = index( $decrypStr, "\n" );
        if ( $i )
        {
            $decrypStr = substr( $decrypStr, 0, $i );      # Strip the trailing linefeed off of the string
        }

        close(CMD);

    }          # End of DECRYPT_BLOCK:

    return ( $statusOut, $decrypStr );

}   ## end -- get_decrypted_str

#***************************************************************************
#
# parse_VMMBI_Parm_Line -- Get an array of parameters on the specified
#                          environment variable line
#
# Input Parameters:
#
#    $VMMBI_ParmLineIn  = VMMBI parameter line stirng with length prefixs
#
# Output Parameters:
#
#    $statusOut        = Return code
#                        0 = Successful
#                        1 = Errors
#    @parmsOut         = Array of VMMBI parsed parameters
#
#***************************************************************************

sub parse_VMMBI_Parm_Line
{
    my( $VMMBI_ParmLineIn )  = shift;
    my( $statusOut )         = 0;
    my( @parmsOut )          = ();

    my( $start )             = 0;
    my( $error )             = 0;
    my( $len )               = 0;
    my( $stringLen )         = length( $VMMBI_ParmLineIn );
    my( $value )             = "";

    PARSE_BLOCK:
    for ( my $i = 0; $i < 999; $i++)
    {
        if ( $stringLen < 3 )
        {
            $error = $start + 1;
            log_error_msg( "VMMBI3600: parse_VMMBI_Parm_Line: Error: Length prefix missing or invalid at position = $error\n" );
            log_error_msg( "VMMBI3700: parse_VMMBI_Parm_Line: Error:    String = '$VMMBI_ParmLineIn'\n" );
            $statusOut++;
            last PARSE_BLOCK;
        }

        last PARSE_BLOCK if ( $start >= $stringLen );

        $len   = substr( $VMMBI_ParmLineIn, $start, 3 );

        if ( ! ( $len =~ /^\d\d\d/ ) )
        {
            $value = substr( $VMMBI_ParmLineIn, $start);
            $value =~ s/\s*$//;                  # Remove trailing white space

            if ( length( $value ) > 0 )
            {
                $error = $start + 1;
                log_error_msg( "VMMBI3800: parse_VMMBI_Parm_Line: Error: Length prefix missing or invalid at position = $error\n" );
                log_error_msg( "VMMBI3900: parse_VMMBI_Parm_Line: Error:    String = '$VMMBI_ParmLineIn'\n" );
                $statusOut++;
                last PARSE_BLOCK;
            }

            last PARSE_BLOCK;

        }

        $start = $start + 3;

        if ( $len eq '000' )
        {
            push @parmsOut, "";
            next PARSE_BLOCK;
        }

        if ( $start + $len > $stringLen )
        {
            $error = $start + 1;
            log_error_msg( "VMMBI4000: parse_VMMBI_Parm_Line: Error: Length prefix missing or invalid at position = $error\n" );
            log_error_msg( "VMMBI4100: parse_VMMBI_Parm_Line: Error:    String = '$VMMBI_ParmLineIn'\n" );
            $statusOut++;
            last PARSE_BLOCK;
        }

        $value = substr( $VMMBI_ParmLineIn, $start, $len );
        $start = $start + $len;

        push @parmsOut, $value;
    }        # End of PARSE_BLOCK

    return( $statusOut, @parmsOut );

}   ## end -- parse_VMMBI_Parm_Line

#***************************************************************************
#
# run_chdir() - Run the chdir command and adjust the return code
#
#
# Input Parameters:
#
#    $cmdIn  =  Command to be called with "do_cmd:
#
# Output Parameters:
#
#    $statusOut = Return status
#                 0 = Successful
#                 1 = Failed
#
#***************************************************************************

sub run_chdir
{
    my( $cmdIn )     = @_;              # Input  parameters

    my( $statusOut ) = 0;               # Output parameters

    my( $rval ) = 0;

    log_msg( "run_chdir: chdir $cmdIn \n" );

    $rval = chdir( $cmdIn );

    #***************************************************************************
    #
    # Reverse the return code $rval into $statusOut. This makes all return codes
    # consistant
    #
    #***************************************************************************

    if ( $rval )
    {
        $statusOut = 0;
    }
    else
    {
        $statusOut = 1;
    }

    log_msg( "run_chdir: status = $statusOut \n" );

    return $statusOut;                       # Return code

}   ## end -- run_chdir()

#***************************************************************************
#
# get_build_dir   --   Get the build directory from the VMMBI01 env var
#
# Input Parameters:
#
#     None
#
# Output Parameters:
#
#    $statusOut   = Return status
#                   0 = Successful
#                   1 = Failed
#    $buildDirOut = Build directory path
#
#***************************************************************************

sub get_build_dir
{
    my( $statusOut )           = 0;
    my( $buildDirOut )         = "";

    my( $BUILD_DIR_ENCRYPTED ) = "";
    my( $BUILD_DIR_DECRYPTED ) = "";
    my( $BUILD_DIRECTORY )     = "";
    my( $cmd )                 = "";
    my( @parms )               = ();

    GET_BUILD_DIR_BLOCK:
    {
        #***************************************************************************
        #
        # Get the encrypted build dir value
        #
        #***************************************************************************

        $BUILD_DIR_ENCRYPTED =  $ENV{ "VMMBI01" };

        if ( $BUILD_DIR_ENCRYPTED eq "" )
        {
            log_error_msg("VMMBI4200: get_build_dir: Error: VMMBI01 environment variable is not defined\n");
            $statusOut++;
            last GET_BUILD_DIR_BLOCK;
        }

        #***************************************************************************
        #
        # Decrypt build dir environment variable
        #
        #***************************************************************************

        ( $rval, $BUILD_DIR_DECRYPTED ) = get_decrypted_str( $BUILD_DIR_ENCRYPTED );

        if ( $rval )
        {
            log_error_msg("VMMBI4300: get_build_dir: Error: VMMBI01 environment variable decryption failed\n");
            $statusOut += $rval;
            last GET_BUILD_DIR_BLOCK;
        }

        #***************************************************************************
        #
        # Parse build dir environment variable
        #
        #***************************************************************************

        ( $rval, @parms )  = parse_VMMBI_Parm_Line( $BUILD_DIR_DECRYPTED, "BUILD_DIR" );

        if ( $rval )
        {
            log_error_msg("VMMBI4400: get_build_dir: Error: VMMBI01 environment variable parse failed\n");
            $statusOut += $rval;
            last GET_BUILD_DIR_BLOCK;
        }

        $BUILD_DIRECTORY = @parms[ 0 ];     # Get the build directory from the parms array

        #***************************************************************************
        #
        # Resolve any environment variables embeded in the build dir
        #
        #***************************************************************************

        ( $rval, $buildDirOut ) = expand_path( $BUILD_DIRECTORY );

        if ( $rval )
        {
            log_error_msg("VMMBI4500: get_build_dir: Error: Build directory expansion failed\n");
            $statusOut += $rval;
            last GET_BUILD_DIR_BLOCK;
        }

        #***************************************************************************
        #
        # We now have the final build directory path
        #
        # Make sure all of the slashes are going the right way
        #
        #***************************************************************************

        $buildDirOut = normalize_path( $buildDirOut );

        #***************************************************************************
        #
        # Make sure the build directory does not end with a slash
        #
        #***************************************************************************

        $cmd = '$buildDirOut =~ s!' . $OUR_SLASH_ESCAPED . '*$!!';      # Create Perl statement to be executed
        eval $cmd;                                                      # Execute the Perl statement - Remove trailing slashes

    }

    return( $statusOut, $buildDirOut );

}   ## end -- get_build_dir

#***************************************************************************
#
# expand_path() - Expand any environment variables imbeded in the specified
#                 path
#
# Input Parameters:
#
#    $pathIn           = Path to be expanded
#
# Output Parameters:
#
#    $statusOut        = Return code
#                        0 = Successful
#                        1 = Errors
#    $pathOut          = Expanded path
#
#***************************************************************************

sub expand_path
{
    my( $pathIn )     = shift;

    my( $statusOut )  = 0;
    my( $pathOut )    = "";


    if ( $IS_W32 )
    {
        ( $statusOut, $pathOut ) = expand_w32_path( $pathIn );
    }
    elsif ( $IS_UNIX )
    {
        ( $statusOut, $pathOut ) = expand_unix_path( $pathIn );
    }
    else
    {
        log_error_msg( "VMMBI4600: expand_path: Error: Unsupported OS.\n");
        $statusOut++;
    }

    return( $statusOut, $pathOut );

}   ## end -- expand_path

#***************************************************************************
#
# expand_w32_path() - Expand any w32 environment variables imbeded in the
#                     specified path
#
# Input Parameters:
#
#    $pathIn           = Path to be expanded
#
# Output Parameters:
#
#    $statusOut        = Return code
#                        0 = Successful
#                        1 = Errors
#    $pathOut          = Expanded path
#
#***************************************************************************

sub expand_w32_path
{
    my( $pathIn )        = shift;

    my( $statusOut )     = 0;
    my( $pathOut )       = "";

    my( @elements )      = ();
    my( $element )       = "";
    my( $len )           = 0;
    my( $env_var_name )  = "";
    my( $env_var_value ) = "";

    EXPAND_PATH_BLOCK:
    {
        last EXPAND_PATH_BLOCK if ( $pathIn eq "" );

        #***************************************************************************
        #
        # Split the path on percent signs
        #
        # Note that using the following "split" regular expression will create elements
        # for the data inbetween the percent signs and elements containing the percent
        # signs.  For example the string "%a%b%c%" will produce the following elements:
        #
        #    [0] =        <=== null
        #    [1] = %
        #    [2] = a
        #    [3] = %
        #    [4] = b
        #    [5] = %
        #    [6] = c
        #    [7] = %
        #
        #***************************************************************************

        @elements = split /(%)/, $pathIn;

        $len = scalar( @elements );

        #***************************************************************************
        #
        # Spin through the segments and expand each environment variable found.
        #
        #***************************************************************************

        ENV_VAR_BLOCK:
        for (my $i = 0; $i < $len; $i++)
        {
            $element = @elements[ $i ];

            next ENV_VAR_BLOCK if ( $element eq "" );

            #***************************************************************************
            #
            # If this is not a percent sign then this segment is normal text, so we add
            # it to the output path
            #
            #***************************************************************************

            if ( $element ne '%' )
            {
                $pathOut .= $element;
                next ENV_VAR_BLOCK;
            }

            if ( $i + 2 > $len )
            {
                log_error_msg( "VMMBI4700: expand_w32_path: Error: The environment variable percent signs are not paired (1)\n");
                $statusOut++;
                last EXPAND_PATH_BLOCK;
            }

            $i++;
            $env_var_name = @elements[ $i ];
            $i++;

            if ( @elements[ $i ] ne '%' )
            {
                log_error_msg( "VMMBI4800: expand_w32_path: Error: The environment variable percent signs are not paired (2)\n");
                $statusOut++;
                last EXPAND_PATH_BLOCK;
            }

            if ( $env_var_name eq "" )
            {
                log_error_msg( "VMMBI4900: expand_w32_path: Error: A null environment variable name was specified\n");
                $statusOut++;
                last EXPAND_PATH_BLOCK;
            }

            $env_var_value = $ENV{$env_var_name};              # Obtain PATH from the environment

            if ( $env_var_value eq "" )
            {
                log_error_msg( "VMMBI5000: expand_w32_path: Error: The specified environment variable does not exist: '$env_var_name'\n");
                $statusOut++;
                last EXPAND_PATH_BLOCK;
            }

            #***************************************************************************
            #
            # Add the expanded environment variable to the output path
            #
            #***************************************************************************

            $pathOut .= $env_var_value;

        }          # End of ENV_VAR_BLOCK:
    }          # End of EXPAND_PATH_BLOCK:

    return( $statusOut, $pathOut );

}   ## end -- expand_w32_path

#***************************************************************************
#
# expand_unix_path() - Expand any unix environment variables imbeded in the
#                      specified path
#
# Input Parameters:
#
#    $pathIn           = Path to be expanded
#
# Output Parameters:
#
#    $statusOut        = Return code
#                        0 = Successful
#                        1 = Errors
#    $pathOut          = Expanded path
#
#***************************************************************************

sub expand_unix_path
{
    my( $pathIn )        = shift;

    my( $statusOut )     = 0;
    my( $pathOut )       = "";

    my( @dirs )          = ();
    my( $dir )           = "";
    my( $len1 )          = 0;
    my( @elements )      = ();
    my( $element )       = "";
    my( $len2 )          = 0;
    my( $env_var_name )  = "";
    my( $env_var_value ) = "";
    my( $expandedDir )   = "";

    EXPAND_PATH_BLOCK:
    {
        last EXPAND_PATH_BLOCK if ( $pathIn eq "" );

        #***************************************************************************
        #
        # Split path on forward slashes
        #
        # Note that using the following "split" regular expression will create elements
        # for the data inbetween the slashes and elements containing the slashes.
        # For example the string "/a//b/" will produce the following elements:
        #
        #    [0] =        <=== null
        #    [1] = /
        #    [2] = a
        #    [3] = /
        #    [4] =        <=== null
        #    [5] = /
        #    [6] = b
        #    [7] = /
        #
        #***************************************************************************

        @dirs = split /(\/)/, $pathIn;

        $len1 = scalar( @dirs );

        I_LOOP:
        for (my $i = 0; $i < $len1; $i++ )
        {
            $dir = @dirs[ $i ];

            next I_LOOP if ( $dir eq "" );
            next I_LOOP if ( $dir eq '/' );

            $expandedDir   = "";
            @elements = split /(\$)/, $dir;
            $len2     = scalar( @elements );

            #***************************************************************************
            #
            # Split elements on dollar signs
            #
            # Note that using the following "split" regular expression will create elements
            # for the data inbetween the dollar signs and elements containing the dollar
            # signs. For example the string "x$abc$def" will produce the following elements:
            #
            #    [0] = x
            #    [1] = $
            #    [2] = abc
            #    [3] = $
            #    [4] = def
            #
            # This loop will increment $j when it finds an element which only contains a
            # dollar sign to get the variable name in the next element.
            #
            #***************************************************************************

            J_LOOP:
            for (my $j = 0; $j < $len2; $j++ )
            {
                $element = @elements[ $j ];

                next J_LOOP if ( $element eq "" );

                if ( $element ne '$' )         # If not a dollar sign then add the element and continue
                {
                    $expandedDir .= $element;
                    next J_LOOP;
                }

                if ( $j + 1 <= $len2 )
                {
                    $j++;
                    $env_var_name = @elements[ $j ];
                }
                else
                {
                    $env_var_name = "";
                }

                if ( $env_var_name eq "" )
                {
                    log_error_msg( "VMMBI5100: expand_unix_path: Error: The specified environment variable name is null\n");
                    $statusOut++;
                    last EXPAND_PATH_BLOCK;
                }

                $env_var_value = $ENV{$env_var_name};              # Obtain PATH from the environment

                if ( $env_var_value eq "" )
                {
                    log_error_msg( "VMMBI5200: expand_unix_path: Error: The specified environment variable does not exist: '$env_var_name'\n");
                    $statusOut++;
                    last EXPAND_PATH_BLOCK;
                }

                #***************************************************************************
                #
                # Add the environment variable value to the expanded directory
                #
                #***************************************************************************

                $expandedDir .= $env_var_value;

            }          # End of J_LOOP:

            #***************************************************************************
            #
            # Replace the input directory with the expanded directory
            #
            #***************************************************************************

            @dirs[ $i ] = $expandedDir;

        }          # End of I_LOOP:

        #***************************************************************************
        #
        # Re-join the path with the expanded directories
        #
        # Because the forward slashes are retained in the array they are placed back
        # in their correct locations.
        #
        #***************************************************************************

        $pathOut = join '', @dirs;

    }          # End of EXPAND_PATH_BLOCK:

    return( $statusOut, $pathOut );

}   ## end -- expand_unix_path

#***************************************************************************
#
# get_program_path -- Locate the path from which this program is being run
#
# Input Parameters:
#
#    None
#
# Output Parameters:
#
#    $statusOut         = Return status
#                         0 = Successful
#                         1 = Failed
#    $PathString        = Path in which the program was found
#
#***************************************************************************

sub get_program_path
{
    my( $statusOut )     = 0;
    my( $PathOut )       = "";

    my( $var )           = "";
    my( $Path )          = "";
    my( @Paths )         = ();
    my( $PathString )    = $ENV{'PATH'};

    if ( $IS_W32 )
    {
        #***************************************************************************
        #
        # Convert /'s to \'s in path
        #
        #***************************************************************************

        $PathString = normalize_path( $PathString );

        #***************************************************************************
        #
        # Move each directory of the PATH into an array element
        #
        #***************************************************************************

        @Paths = split( /\;/, $PathString);
    }

    if ( $IS_UNIX )
    {
        #***************************************************************************
        #
        # Convert \'s to /'s in path
        #
        #***************************************************************************

        $PathString = normalize_path( $PathString );

        #***************************************************************************
        #
        # Move each directory of the PATH into an array element
        #
        #***************************************************************************

        @Paths = split( /\:/, $PathString);
    }

    if ( $DEBUG )
    {
        log_msg("get_program_path: \$PathString = $PathString\n");
    }

    #***************************************************************************
    #
    # Look first in the current directory
    #
    #***************************************************************************

    SEARCH_BLOCK:
    {
        if ( -f "$CURRENT_WORKING_DIR$OUR_SLASH$OUR_PROGRAM_NAME"  )
        {
            $PathOut = $CURRENT_WORKING_DIR;
            last SEARCH_BLOCK;
        }

        #***************************************************************************
        #
        # Search for our program (VMMBI_bldmake_PET.pl) in the path
        #
        #***************************************************************************

        PATH_BLOCK:
        foreach $Path( @Paths )
        {
            next PATH_BLOCK if ( $Path eq "" );

            if ( -f "$Path$OUR_SLASH$OUR_PROGRAM_NAME"  )  # Is our program name in this sub-directory?
            {
                $PathOut = $Path;
                last SEARCH_BLOCK;
            }
        }          # End of PATH_BLOCK:
    }          # End of SEARCH_BLOCK:

    #***************************************************************************
    #
    # Checkto see if we found it
    #
    #***************************************************************************

    if ( $PathOut eq "" )
    {
        log_error_msg( "VMMBI6300: get_program_path: Error: Unable to get the program path for $OUR_PROGRAM_NAME\n" );

        $statusOut++;
    }

    return( $statusOut, $PathOut);

}   ## end -- get_program_path

#***************************************************************************
#
# get_current_dir   --   To return the current working directory
#
#
# Input Parameters:
#
#     None
#
# Output Parameters:
#
#     $currentWorkDirOut = The current working directory
#
#***************************************************************************

#***************************************************************************
#
# For seeking the current working directory
#
#***************************************************************************

use Cwd;
sub get_current_dir
{
    my( $currentWorkDirOut ) = "";

    my( $currentWorkDir )    = "";

    $currentWorkDir = cwd();

    $currentWorkDirOut = normalize_path( $currentWorkDir );

    return ( $currentWorkDirOut );

}   ## end -- get_current_dir

#***************************************************************************
#
# normalize_path  -- Adjust the slashes
#
#
# Input Parameters:
#
#     $pathIn  = The path to be normalized
#
# Output Parameters:
#
#     $pathOut = The normalized path
#
#***************************************************************************

sub normalize_path
{
    my( $pathIn )     = shift;

    my( $pathOut )    = $pathIn;

    my( $cmd )        = "";

    $cmd = '$pathOut =~ s!' . $OTHER_SLASH_ESCAPED . '!' . $OUR_SLASH_ESCAPED . '!g';  # Create Perl statement to be executed
    eval $cmd;                                                                         # Execute the Perl statement

    return ( $pathOut );

}   ## end -- normalize_path

#***************************************************************************
#
# get_os_type   --   Function to determine the Operating System type
#                     It will return the parameter for both Windows and UNIX
#                     operating system but one of them will carry the
#                     weightage of value 1 and the other will have 0 depending
#                     upon the O/S type
#
# Input Parameters:
#
#     None
#
# Output Parameters:
#
#     $isWindowsOS =  1, if Windows o/s, otherwise 0
#     $isUNIXOS    =  1, if UNIX o/s, otherwise 0
#
#***************************************************************************

sub get_os_type
{
    my( $isWindowsOS ) = 0;
    my( $isUNIXOS )    = 0;
    my( $OS )          = "";

    $OS = $^O;
    if($OS =~ /win/i)
    {
            $isWindowsOS = 1;
    }
    else
    {
            $isUNIXOS = 1;
    }
    return( $isWindowsOS, $isUNIXOS );

}   ## end -- get_os_type

#***************************************************************************
#
# print_date -- Output the current date and time
#
#
# Input Parameters:
#
#    None
#
# Output Parameters:
#
#    $dateOut = Date or null
#
#***************************************************************************

sub print_date
{
    my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdat)  = localtime(time);
    my ($today, $month, $temp_year)                           = '';
    my ($dateOut)                                             = '';

    $today = qw( Sunday Monday Tuesday Wednesday Thursday Friday Saturday )[ $wday ];
    $month = qw( January February March April May June July August September October November December )[ $mon ];

    $hour = sprintf("%2d", $hour);
    $hour =~ s/ /0/g;
    $min = sprintf("%2d", $min);
    $min =~ s/ /0/g;
    $sec = sprintf("%2d", $sec);
    $sec =~ s/ /0/g;

    $temp_year =~ s/^1##/##/g;

    $dateOut = "$today, $month $mday, 20$temp_year $hour:$min:$sec";

    return $dateOut;

}   ## end -- print_date()

#***************************************************************************
#
# log_error_msg() -
#
#
# Input Parameters:
#
#    $msgIn  =  Message to be loged
#
# Output Parameters:
#
#    None
#
#***************************************************************************

sub log_error_msg
{
    my( $msgIn ) = shift;

    print $OUR_PROGRAM_NAME . " " . $msgIn;

    return;

}   ## end -- log_error_msg

#***************************************************************************
#
# log_msg() -
#
#
# Input Parameters:
#
#    $msgIn  =  Message to be loged
#
# Output Parameters:
#
#    None
#
#***************************************************************************

sub log_msg
{
    my( $msgIn ) = shift;

    print $OUR_PROGRAM_NAME . " " . $msgIn;

    return;

}   ## end -- log_msg

no strict;

#***************************************************************************
#***************************************************************************
#
# End of VMMBI_om_PET.pl
#
#***************************************************************************
#***************************************************************************

