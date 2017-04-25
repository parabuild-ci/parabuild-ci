
#**************************************************************************
#
# $Header:   Y:/archives/dv/intersolv/Merant_Build/VM/Integration/Event_Triggers/VMMBI_bldmake_PET.pl-arc   1.37   08 Mar 2005 15:25:54   rjoachim  $
#
# Copyright (c) 2003, MERANT.  All Rights Reserved.
#
#
# VMMBI_bldmake_PET.pl -- Bldmake pre-event trigger for Version Manager to
#                         Merant Build integration
#
#
# 06/11/03 - Binoy Pandey
# 07/06/03 - Roger Joachim
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
# Class used to hold and manipulate archive information
#
#***************************************************************************

package ARCHIVE_INFO;
BEGIN
{
    # Class field deffinitions

    use fields qw( WorkFilePath
                   RelWorkFilePath
                   ArchivePath
                   Revision
                   DateModified
                   DateModifiedUTC
                   DateCheckedIn
                   DstUtcFactor
                 );

    #***************************************************************************
    #
    # Constructor
    #
    #***************************************************************************

    sub new                                       # Constructor (can be any name, not just new)
    {
       my( $class )               = shift;            # Get the class name or the object address which is the first parameter
       my( $VmProjNameIn )        = shift;
       my( $OurSlashEscapedIn )   = shift;
       my( $OtherSlashEscapedIn ) = shift;

       my( $WorkFilePath )        = "";
       my( $RelWorkFilePath)      = "";
       my( $start )               = 0;
       my( $VmProjNameLen )       = 0;
       my( $cmd )                 = "";

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

        $self->{WorkFilePath}     = shift if @_  ;
        $self->{ArchivePath}      = shift if @_  ;
        $self->{Revision}         = shift if @_  ;
        $self->{DateModified}     = shift if @_  ;
        $self->{DateModifiedUTC}  = shift if @_  ;
        $self->{DateCheckedIn}    = shift if @_  ;

        $self->{DstUtcFactor}     = 0;

        #***************************************************************************
        #
        # Calculate and save the relative work file path (Relative to the build dir)
        #
        # $VmProjNameIn can equal "/" or "/<project>" etc. When we calculate the
        # relative path we must make sure it begins with a forward slash.
        #
        #***************************************************************************

        REL_PATH_BLOCK:
        {
            $WorkFilePath = $self->{WorkFilePath};

            $start = index( $WorkFilePath, $VmProjNameIn );

            if ( $start != 0 )
            {
                print "ARCHIVE_INFO: new: Error: The project work file does not begin with the project name:\n";
                print "ARCHIVE_INFO: new: Error:    Work file name = $WorkFilePath\n";
                print "ARCHIVE_INFO: new: Error:    Project name   = $VmProjNameIn\n";
                $self = 0;
                last REL_PATH_BLOCK;
            }

            $VmProjNameLen = length( $VmProjNameIn );

            $RelWorkFilePath = substr( $WorkFilePath, $VmProjNameLen );   # Calculate relative work file path from the build dir

            #***************************************************************************
            #
            # Make sure the relative work file path begins with a forward slash
            #
            #***************************************************************************

            if ( ( length( $RelWorkFilePath )  != 0 ) && ( substr( $RelWorkFilePath, 0, 1 ) ne '/' ) )
            {
                $RelWorkFilePath = '/' . $RelWorkFilePath;
            }

            #***************************************************************************
            #
            # Normalize the relative file path
            #
            # OTHER_SLASH_ESCAPED  OUR_SLASH_ESCAPED
            #
            #***************************************************************************

            $cmd = '$RelWorkFilePath =~ s!' . $OtherSlashEscapedIn . '!' . $OurSlashEscapedIn . '!g';  # Create Perl statement to be executed
            eval $cmd;                                                                                 # Execute the Perl statement

            $self->{RelWorkFilePath} = $RelWorkFilePath;

        }            # End of REL_PATH_BLOCK:

        return( $self  );                     # Return the freshly generated ARCHIVE_INFO object

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
    #    $arcInfoOut = Archive information
    #
    #***************************************************************************

    sub toString
    {
        my( $class )   = shift;  # Gets the object address which is the first input parameter

        my( $arcInfoOut ) = "";

        $arcInfoOut = sprintf( "%-40.40s %-30.30s %-100.100s %-16.16s %-36.36s %-21.21s %-8.8s %-36.36s ",
                               $class->{WorkFilePath},
                               $class->{RelWorkFilePath},
                               $class->{ArchivePath},
                               $class->{Revision},
                               $class->{DateModified},
                               $class->{DateModifiedUTC},
                               $class->{DstUtcFactor},
                               $class->{DateCheckedIn}
                             );

        return( $arcInfoOut . "\n" ) ;

    }   ## end -- toString

    #***************************************************************************
    #
    # get_work_file_path() - Get the work file path
    #
    # Input Parameters:
    #
    #    None
    #
    # Output Parameters:
    #
    #    $WorkfilePathOut = This objects work file path
    #
    #***************************************************************************

    sub get_work_file_path
    {
        my( $class )           = shift;        # Gets the object address which is the first input parameter

        my( $WorkfilePathOut ) = "";

        $WorkfilePathOut = $class->{WorkFilePath};

        return( $WorkfilePathOut );

    }   ## end -- get_work_file_path()

    #***************************************************************************
    #
    # get_rel_work_file_path() - Get the work file path
    #
    # Input Parameters:
    #
    #    None
    #
    # Output Parameters:
    #
    #    $RelWorkfilePathOut = This objects work file path
    #
    #***************************************************************************

    sub get_rel_work_file_path
    {
        my( $class )           = shift;        # Gets the object address which is the first input parameter

        my( $RelWorkfilePathOut ) = "";

        $RelWorkfilePathOut = $class->{RelWorkFilePath};

        return( $RelWorkfilePathOut );

    }   ## end -- get_rel_work_file_path()

    #***************************************************************************
    #
    # get_archive_path() - Get the archive path
    #
    # Input Parameters:
    #
    #    None
    #
    # Output Parameters:
    #
    #    $ArchivePathOut = This objects archive path
    #
    #***************************************************************************

    sub get_archive_path
    {
        my( $class )           = shift;        # Gets the object address which is the first input parameter

        my( $ArchivePathOut ) = "";

        $ArchivePathOut = $class->{ArchivePath};

        return( $ArchivePathOut );

    }   ## end -- get_archive_path()

    #***************************************************************************
    #
    # get_last_modified_UTC() - Get last modified date in UTC
    #
    # Input Parameters:
    #
    #    None
    #
    # Output Parameters:
    #
    #    $DateModifiedUTCOut = UTC last modified date
    #
    #***************************************************************************

    sub get_last_modified_UTC
    {
        my( $class )              = shift;        # Gets the object address which is the first input parameter

        my( $DateModifiedUTCOut ) = "";

        $DateModifiedUTCOut = $class->{DateModifiedUTC};

        return( $DateModifiedUTCOut );

    }   ## end -- get_last_modified_UTC()

    #***************************************************************************
    #
    # get_DST_UTC_factor() - Get daylight saving times UTC factor
    #
    # Input Parameters:
    #
    #    None
    #
    # Output Parameters:
    #
    #    $DstUtcFactorOut = Daylight savings time UTC factor
    #
    #***************************************************************************

    sub get_DST_UTC_factor
    {
        my( $class )              = shift;        # Gets the object address which is the first input parameter

        my( $DstUtcFactorOut )    = "";

        $DstUtcFactorOut = $class->{DstUtcFactor};

        return( $DstUtcFactorOut );

    }   ## end -- get_DST_UTC_factor()

    #***************************************************************************
    #
    # get_revision() - Get revision number
    #
    # Input Parameters:
    #
    #    None
    #
    # Output Parameters:
    #
    #    $RevisionOut = This objects revision number
    #
    #***************************************************************************

    sub get_revision
    {
        my( $class )              = shift;        # Gets the object address which is the first input parameter

        my( $RevisionOut )        = "";

        $RevisionOut = $class->{Revision};

        return( $RevisionOut );

    }   ## end -- get_revision()

    #***************************************************************************
    #
    # set_DST_UTC_factor() - Set daylight saving times UTC factor
    #
    # Input Parameters:
    #
    #    $DstUtcFactorIn = Daylight savings time UTC factor
    #
    # Output Parameters:
    #
    #    None
    #
    #***************************************************************************

    sub set_DST_UTC_factor
    {
        my( $class )              = shift;        # Gets the object address which is the first input parameter
        my( $DstUtcFactorIn )     = shift;

        $class->{DstUtcFactor} = $DstUtcFactorIn;

        return;

    }   ## end -- set_DST_UTC_factor()

}    ## End of package ARCHIVE_INFO

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

#***************************************************************************
#
# Class used to hold a PDB path mapping
#
#***************************************************************************

package PDB_MAP;
BEGIN
{
    # Class field deffinitions

    use fields qw( FromString
                   FromStringLen
                   ToString
                   CaseSensitive
                 );

    #***************************************************************************
    #
    # Constructor
    #
    # Input Parameters:
    #
    #    $fromIn           = From string
    #    $toIn             = To string
    #    $CaseSensitiveIn  = Case sensitive compare flag
    #
    # Output Parameters:
    #
    #    $objectAddressOut = Address of object or zero if there is a input
    #                        parameter error
    #
    #***************************************************************************

    sub new                                        # Constructor (can be any name, not just new)
    {
       my( $class )             = shift;             # Get the class name or the object address which is the first parameter

       my( $fromIn )            = shift;
       my( $toIn     )          = shift;
       my( $CaseSensitiveIn )   = shift;

       my( $objectAddressOut )  = 0;

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

        VALIDATION_BLOCK:
        {
            #***************************************************************************
            #
            # Validate the input parameters
            #
            #***************************************************************************

            last VALIDATION_BLOCK if ( $fromIn eq "" );                                    # Null from value not allowed
            last VALIDATION_BLOCK if ( $CaseSensitiveIn != 0 && $CaseSensitiveIn != 1  );  # Must be zero or 1

            #***************************************************************************
            #
            # Save the input parameters
            #
            #***************************************************************************

            $self->{FromString}      = $fromIn;
            $self->{FromStringLen}   = length( $fromIn );
            $self->{ToString}        = $toIn;
            $self->{CaseSensitive}   = $CaseSensitiveIn;

            $objectAddressOut = $self;

        }          # End of VALIDATION_BLOCK:

        return( $objectAddressOut  );                     # Return the freshly generated PDB_MAP object

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
    #    $mapInfoOut = PDB map information
    #
    #***************************************************************************

    sub toString
    {
        my( $class )      = shift;  # Gets the object address which is the first input parameter

        my( $mapInfoOut ) = "";

        $mapInfoOut = sprintf( "%-14.14s %-14.14s %-1.1s",
                               $class->{FromString},
                               $class->{ToString},
                               $class->{CaseSensitive}
                             );

        return( $mapInfoOut . "\n" );

    }   ## end -- toString

}    ## End of package PDB_MAP

#*****************************************************************************
#*****************************************************************************
#*
#*   E N D   O F   P A C K A G E S
#*
#*****************************************************************************
#*****************************************************************************

#*****************************************************************************
#
# Initialize global variables
#
#*****************************************************************************

our( $TRUE )                            = 1;                        # to store the TRUE boolean value
our( $FALSE )                           = 0;                        # to store the FALSE boolean value

our( $VMMBI_VERSION )                   = "8.1.0";                  # VMMBI_bldmake_PET.pl version

our( $INFO_MSG )                        = 0;
our( $ERROR_MSG )                       = 1;
our( $WARNING_MSG )                     = 2;

our( $PGM_VERSION )                     = '$Header:   Y:/archives/dv/intersolv/Merant_Build/VM/Integration/Event_Triggers/VMMBI_bldmake_PET.pl-arc   1.37   08 Mar 2005 15:25:54   rjoachim  $';

our( $BLDMAKE_PET_LOGFILE )             = "VMMBI_bldmake_PET.log";  # Default.
our( $BLDMAKE_PET_LOGFILE_OPEN )        = $FALSE;                   # FALSE = $LOGFILE not open
                                                                    # TRUE  = $LOGFILE open
our( $BLDMAKE_PET_LOGFILE_APPEND_FLAG ) = "append";                 # "append" to append, null to not append
our( @BLDMAKE_PET_LOGFILE_MSG_QUEUE )   = ();                       # Message hold area until the $LOGFILE file is opened
our( @NO_OPTION_QUEUE )                 = ();                       #
our( $REDIRECT )                        = $FALSE;                   # Flag to indicating that we are redirecting STDOUT
our( $BUILD_DIRECTORY )                 = "";                       # Variable to store the build directory
our( $TZ_ENV_VAR )                      = "";                       # Value of the TZ environmant variable      
our( $OS_VERSION )                      = "";                       # Operating system version string 
our( $BUILD_DIRECTORY_FILE_SYSTEM )     = "";                       # Build directory file system name ( FAT, NTFS, FAT32, UFS )
our( $IS_W32 )                          = $FALSE;
our( $IS_UNIX )                         = $FALSE;
our( $VM_ID )                           = '$Header';
our( @LISTARCHIVEINFO )                 = ();                       # List to store the archive info after executing the PCLI command
our( $VMMBI_GETREVINFO_PCLI )           = "VMMBI_GetRevInfo.pcli";  # The PCLI file that will be used for getting revision info
our( $CURRENT_WORKING_DIR )             = "";
our( $PROGRAM_DIR )                     = "";
our( $COMMAND_LINE )                    = "";
our( $OUR_PROGRAM_NAME )                = "VMMBI_bldmake_PET.pl";
our( $INI_FILE_NAME )                   = "VMMBI_bldmake_PET_INI.pl";
our( $INI_FILE_PATH )                   = "";
our( $FOOTPRINT_INFO_FILE_NAME )        = "VMMBI_FOOTPRINT_INFO.pl";
our( @NULL_ARRAY )                      = ();
our( @ERROR_MSG_QUEUE )                 = ();
our( $PCLI_VERSION )                    = "";
our( $PCLI_BUILD_NUM )                  = "";
our( $BUILD_JOB_VM_VERSION )            = "";
our( $BUILD_JOB_VM_BUILD_NUM )          = "";
our( $CMD_LINE_PARMS_OBJECT )           = 0;
our( $VM_PDB_PATH )                     = "";
our( $ENV_VAR_LIST_LEN )                = 0;

#*****************************************************************************
#
# VMMBI_bldmake_PET_INI.pl ini file parameter initialization
#
#*****************************************************************************

our( @PDB_MAP_LIST )                    = ();                       # PDB map list array
our( $DST_ADJUSTMENT_OPTION )           = $TRUE;                    # Daylight savings time adjustment option

#*****************************************************************************
#
# Input parameters
#
#*****************************************************************************

our( $DELETE_TEMP )                     = $TRUE;                    # Delete temporary files created by this program
our( $NO_DELETE_TEMP_OPTION )           = $FALSE;
our( $DEBUG )                           = $FALSE;
our( $DEBUG_OPTION )                    = $FALSE;

#*****************************************************************************
#
#
#
#*****************************************************************************

our( @WORK_FILE_OLDER_LIST )                = ();
our( @WORK_FILE_OLDER_OR_NONEXISTANT_LIST ) = ();
our( @WORK_FILE_NEWER_LIST )                = ();
our( @WORK_FILE_NONEXISTANT_LIST )          = ();
our( @WORK_FILE_SAME_LIST )                 = ();
our( @WORK_FILE_DIFFERENT_LIST )            = ();

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
our( $VMMBI_DST_PGM_NAME )              = "";          # OS dependent

#*****************************************************************************
#
# Program constants
#
#*****************************************************************************

our( $CURRENT_OS )                      = "";
our( $FILENAME )                        = "";
our( $ARCHIVENAME )                     = "";
our( $REVISION )                        = "";
our( $DATEMODIFIED )                    = "";
our( $DATECHECKEDIN )                   = "";
our( $DQ )                              = '"';

#***************************************************************************
#
# Define the help text
#
#***************************************************************************

our( $USAGE ) =
"Usage:
   VMMBI_bldmake_PET.pl
      [-help]                            This information
      [-no_delete]                       Do not delete temporary program files
      [-debug]                           Run in debug mode (more messages)
   \n";

#*****************************************************************************
#
# Environment Variable Name List
#
# The following lists must all be kept in sync:
#
#    Environment Variable Name List
#    Encrypted   Environment Vraibles
#    Decrypted   Environment Vraibles
#    Program     Variable Name List
#    Parsed      Environment Vraibles
#
#*****************************************************************************

our( @ENV_VAR_LIST )   = qw (
                              VMMBI01
                              VMMBI02
                              VMMBI03
                              VMMBI04
                              VMMBI05
                              VMMBI06
                              VMMBI07
                              VMMBI08
                              VMMBI09
                              VMMBI10
                              VMMBI11
                              VMMBI12
                              VMMBI13
                              VMMBI14
                              VMMBI15
                              VMMBI16
                              VMMBI17
                              VMMBI18
                              VMMBI19
                              VMMBI20
                              VMMBI21
                              VMMBI22
                              VMMBI23
                              VMMBI24
                            );

#*****************************************************************************
#
# Encrypted Environment Vraibles
#
# The following lists must all be kept in sync:
#
#    Environment Variable Name List
#    Encrypted Environment Vraibles
#    Decrypted Environment Vraibles
#    Program Variable Name List
#    Parsed    Environment Vraibles
#
#*****************************************************************************

our( $VMMBI01_ENCRYPTED )          = "";   # Build directory
our( $VMMBI02_ENCRYPTED )          = "";   # Perform footprinting
our( $VMMBI03_ENCRYPTED )          = "";   # Build mode
our( $VMMBI04_ENCRYPTED )          = "";   # Source and target revision selection type
our( $VMMBI05_ENCRYPTED )          = "";   # Source revision selection version label, or promotion group
our( $VMMBI06_ENCRYPTED )          = "";   # VM Project database path
our( $VMMBI07_ENCRYPTED )          = "";   # VM Project name
our( $VMMBI08_ENCRYPTED )          = "";   # Include sub-projects when getting the files from the project
our( $VMMBI09_ENCRYPTED )          = "";   # Merant Build Project name
our( $VMMBI10_ENCRYPTED )          = "";   # Merant Build search path name
our( $VMMBI11_ENCRYPTED )          = "";   # Merant Build job name
our( $VMMBI12_ENCRYPTED )          = "";   # Target selection option
our( $VMMBI13_ENCRYPTED )          = "";   # bldmake parameters
our( $VMMBI14_ENCRYPTED )          = "";   # om parameters
our( $VMMBI15_ENCRYPTED )          = "";   # Enable project processing
our( $VMMBI16_ENCRYPTED )          = "";   # User ID, password, project name, search path name, build job name
our( $VMMBI17_ENCRYPTED )          = "";   # Build machine name
our( $VMMBI18_ENCRYPTED )          = "";   # Build machine operating system
our( $VMMBI19_ENCRYPTED )          = "";   # VM login source
our( $VMMBI20_ENCRYPTED )          = "";   # VM version and build number
our( $VMMBI21_ENCRYPTED )          = "";   # VM Project database NFS mapped path
our( $VMMBI22_ENCRYPTED )          = "";   # Future 1
our( $VMMBI23_ENCRYPTED )          = "";   # Future 2
our( $VMMBI24_ENCRYPTED )          = "";   # Future 3


#*****************************************************************************
#
# Decrypted Environment Vraibles
#
# The following lists must all be kept in sync:
#
#    Environment Variable Name List
#    Encrypted Environment Vraibles
#    Decrypted Environment Vraibles
#    Program Variable Name List
#    Parsed    Environment Vraibles
#
#*****************************************************************************

our( $VMMBI01_DECRYPTED )          = "";   # Build directory
our( $VMMBI02_DECRYPTED )          = "";   # Perform footprinting
our( $VMMBI03_DECRYPTED )          = "";   # Build mode
our( $VMMBI04_DECRYPTED )          = "";   # Source and target revision selection type
our( $VMMBI05_DECRYPTED )          = "";   # Source revision selection version label, or promotion group
our( $VMMBI06_DECRYPTED )          = "";   # VM Project database path
our( $VMMBI07_DECRYPTED )          = "";   # VM Project name
our( $VMMBI08_DECRYPTED )          = "";   # Include sub-projects when getting the files from the project
our( $VMMBI09_DECRYPTED )          = "";   # Merant Build Project name
our( $VMMBI10_DECRYPTED )          = "";   # Merant Build search path name
our( $VMMBI11_DECRYPTED )          = "";   # Merant Build job name
our( $VMMBI12_DECRYPTED )          = "";   # Target selection option
our( $VMMBI13_DECRYPTED )          = "";   # bldmake parameters
our( $VMMBI14_DECRYPTED )          = "";   # om parameters
our( $VMMBI15_DECRYPTED )          = "";   # Enable project processing
our( $VMMBI16_DECRYPTED )          = "";   # User ID password, project name, search path name, build job name
our( $VMMBI17_DECRYPTED )          = "";   # Build machine name
our( $VMMBI18_DECRYPTED )          = "";   # Build machine operating system
our( $VMMBI19_DECRYPTED )          = "";   # VM login source
our( $VMMBI20_DECRYPTED )          = "";   # VM version and build number
our( $VMMBI21_DECRYPTED )          = "";   # VM Project database NFS mapped path
our( $VMMBI22_DECRYPTED )          = "";   # Future 1
our( $VMMBI23_DECRYPTED )          = "";   # Future 2
our( $VMMBI24_DECRYPTED )          = "";   # Future 3

#*****************************************************************************
#
# Program Variable Name List
#
# The following lists must all be kept in sync:
#
#    Environment Variable Name List
#    Encrypted Environment Vraibles
#    Decrypted Environment Vraibles
#    Program Variable Name List
#    Parsed    Environment Vraibles
#
#*****************************************************************************

our( @PROGRAM_VAR_LIST ) = qw (
                                BUILD_DIRECTORY_IN
                                FOOTPRINTING_IN
                                BUILD_MODE_IN
                                SOURCE_SELECTION_TYPE_IN
                                SOURCE_VERSION_PROMO_IN
                                VM_PDB_IN
                                VM_PROJECT_NAME_IN
                                INCLUDE_SUBPROJECTS_IN
                                MB_PROJECT_NAME_01_IN
                                MB_SEARCH_PATH_NAME_01_IN
                                MB_BUILD_JOB_NAME_01_IN
                                TARGET_SELECTION_OPTION_IN
                                MB_BLDMAKE_PARMS_IN
                                MB_OM_PARMS
                                ENABLE_PROJECT_PROCESSING_IN
                                VM_USER_ID_DUMMY_IN
                                MB_BUILD_MACHINE_IN
                                MB_BUILD_MACHINE_OS_IN
                                VM_LOGIN_SOURCE_IN
                                VM_VERSION_AND_BUILD_NUM_IN
                                VM_NFS_MAPPED_PDB_IN
                                FUTURE_1_IN
                                FUTURE_2_IN
                                FUTURE_3_IN
                             );

#*****************************************************************************
#
# Parsed Environment Varibles
#
# The following lists must all be kept in sync:
#
#    Environment Variable Name List
#    Encrypted Environment Vraibles
#    Decrypted Environment Vraibles
#    Program Variable Name List
#    Parsed    Environment Vraibles
#
# This set of variables contains the environment variables after the length
# prefixs have been removed.  If the particular environment variable contains
# multiple parameters then it is only left as a dummy parameter to keep the
# lists in sync.  The parameter values are extracted and placed into seperate
# variables listed next.
#
#*****************************************************************************

our( $BUILD_DIRECTORY_IN )            = "";   # Build directory
our( $FOOTPRINTING_IN )               = "";   # Perform footprinting
our( $BUILD_MODE_IN )                 = "";   # Build mode
our( $SOURCE_SELECTION_TYPE_IN )      = "";   # Source and target revision selection type
our( $SOURCE_VERSION_PROMO_IN )       = "";   # Source revision selection version label, or promotion group
our( $VM_PDB_IN )                     = "";   # VM Project database path
our( $VM_PROJECT_NAME_IN )            = "";   # VM Project name
our( $INCLUDE_SUBPROJECTS_IN )        = "";   # Include sub-projects when getting the files from the project
our( $MB_PROJECT_NAME_01_IN )         = "";   # Merant Build Project name
our( $MB_SEARCH_PATH_NAME_01_IN )     = "";   # Merant Build search path name
our( $MB_BUILD_JOB_NAME_01_IN )       = "";   # Merant Build job name
our( $TARGET_SELECTION_OPTION_IN )    = "";   # Target selection option
our( $MB_BLDMAKE_PARMS_IN )           = "";   # bldmake parameters
our( $MB_OM_PARMS )                   = "";   # om parameters
our( $ENABLE_PROJECT_PROCESSING_IN )  = "";   # Enable project processing
our( $VM_USER_ID_DUMMY_IN )           = "";   # User ID password, project name, search path name, build job name
our( $MB_BUILD_MACHINE_IN )           = "";   # Build machine name
our( $MB_BUILD_MACHINE_OS_IN )        = "";   # Build machine operating system
our( $VM_LOGIN_SOURCE_IN )            = "";   # VM login source
our( $VM_VERSION_AND_BUILD_NUM_IN )   = "";   # VM version and build number
our( $VM_NFS_MAPPED_PDB_IN )          = "";   # VM Project database NFS mapped path
our( $FUTURE_1_IN )                   = "";   # Future 1
our( $FUTURE_2_IN )                   = "";   # Future 2
our( $FUTURE_3_IN )                   = "";   # Future 3

#*****************************************************************************
#
# Program Variable Name List
#
# The following lists must all be kept in sync:
#
#    Environment Variable Name List
#    Encrypted Environment Vraibles
#    Decrypted Environment Vraibles
#    Program Variable Name List
#    Parsed    Environment Vraibles
#
#*****************************************************************************

our( @PROGRAM_VAR_LIST_2 )            = ();

@PROGRAM_VAR_LIST_2 = @PROGRAM_VAR_LIST;

push( @PROGRAM_VAR_LIST_2, "VM_USER_ID" );
push( @PROGRAM_VAR_LIST_2, "VM_PASSWORD" );
push( @PROGRAM_VAR_LIST_2, "MB_PROJECT_NAME_02" );
push( @PROGRAM_VAR_LIST_2, "MB_SEARCH_PATH_NAME_02" );
push( @PROGRAM_VAR_LIST_2, "MB_BUILD_JOB_NAME_02" );

#*****************************************************************************
#
# Contents of multi-parameter lines
#
#*****************************************************************************

our( $VM_USER_ID )                 = "";
our( $VM_PASSWORD )                = "";
our( $MB_PROJECT_NAME_02 )         = "";
our( $MB_SEARCH_PATH_NAME_02 )     = "";
our( $MB_BUILD_JOB_NAME_02 )       = "";

#***************************************************************************
#
# Archive information lists
#
#***************************************************************************

our( @ARCHIVE_INFO_LIST )            = ();

#*****************************************************************************
#
# Initialize local variables
#
#*****************************************************************************

my( $projectProcessing )                = "no";
my( $includeSubprojects )               = "no";
my( $enableFootprinting )               = "no";
my( $msg )                              = "";
my( $var )                              = "";
my( @listUndefinedEnvVar )              = ();
my( $PDB_MAP_Object )                   = 0;
my( $statusOut )                        = 0;
my( $decryptStr )                       = "";                       # Variable to store the decrypted string
my( $rval )                             = 0;
my( $element )                          = "";
my( $errorMsg )                         = "";
my( $versionMsg )                       = "";
my( $cmd )                              = "";
my( $ARCHIVE_INFO_Object )              = 0;

#***************************************************************************
#***************************************************************************
#
# Initialization
#
#***************************************************************************
#***************************************************************************

MAIN_PROCESSING_BLOCK:
{
    #***************************************************************************
    #
    # Save command line for later echo.
    #
    #***************************************************************************

    $COMMAND_LINE = join ' ', @ARGV;

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* Performing initialization\n");
    log_msg("*\n");
    log_msg("*    Date:    ", print_date(), " \n" );
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
    # Perform Windows specific requires
    #
    #***************************************************************************** 

    if ( $IS_W32 )
    {
        $cmd = "require \"Win32.pm\";";                                       # Create Perl statement to be executed
        log_msg( "Performing require for Win32.pm: Eval command = '$cmd'\n" );
        eval $cmd;                                                            # Execute the Perl statement
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
        our( $VMMBI_DST_PGM_NAME )              = "VMMBI_DST.exe";
    }

    if ( $IS_UNIX )
    {
        our( $DEFAULT_BUILD_DIR )               = '$home/Merant_Build';
        our( $OUR_SLASH )                       = '/';
        our( $OTHER_SLASH )                     = '\\';
        our( $OUR_SLASH_ESCAPED )               = '\/';
        our( $OTHER_SLASH_ESCAPED )             = '\\\\';
        our( $VMMBI_UTILITY_PGM_NAME )          = "VMMBI_Utility";
        our( $VMMBI_DST_PGM_NAME )              = "VMMBI_DST";                     # Used only on w32
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
        log_msg( "\n" );
        $msg = $CMD_LINE_PARMS_OBJECT->toString();
        log_msg( "Job Name                       User ID    Job DTG              Build Machine        Public Job   Verbose      Logging Enabled\n" );
        log_msg( "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  ~~~~~~~~~  ~~~~~~~~~~~~~~~~~~~  ~~~~~~~~~~~~~~~~~~~  ~~~~~~~~~~~  ~~~~~~~~~~~  ~~~~~~~~~~~~~~~\n" );
        log_msg( "$msg \n" );
    }

    #*****************************************************************************
    #
    # Tell the log we are running
    #
    #*****************************************************************************

    sendMsgToMB_KBS_JobLog( $INFO_MSG,
                            "\nVMMBI0100: Starting VM bldmake integration processing.\n"
                          );

    #*****************************************************************************
    #
    # Check data structures
    #
    #*****************************************************************************

    if ( scalar( @ENV_VAR_LIST ) != scalar( @PROGRAM_VAR_LIST ) )
    {
        log_error_msg( "VMMBI0200: Error: The lengths of \@ENV_VAR_LIST and \@PROGRAM_VAR_LIST do not match\n" );

        last MAIN_PROCESSING_BLOCK;
    }

    #*****************************************************************************
    #
    # Get the current working directory and store it in the global variable
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

    #*****************************************************************************
    #
    # If an ini file exists then include it now
    #
    #*****************************************************************************

    $INI_FILE_PATH = $PROGRAM_DIR . $OUR_SLASH . $INI_FILE_NAME;

    log_msg( "\n" );
    log_msg("*********************************************************\n");
    log_msg( "*\n" );
    log_msg( "* Reading ini file:\n" );
    log_msg( "*\n" );
    log_msg( "*    ini file path = $INI_FILE_PATH\n" );
    log_msg( "*\n" );
    log_msg("*********************************************************\n");
    log_msg( "\n" );

    if ( -e $INI_FILE_PATH )
    {
        require( $INI_FILE_PATH );
    }
    else
    {
        log_msg("No INI file exists\n");
    }

    #***************************************************************************
    #
    # Parse the input parameters and set the options
    #
    #***************************************************************************

    log_msg( "\n" );
    log_msg("*********************************************************\n");
    log_msg( "*\n" );
    log_msg( "* Parsing input parameters:\n" );
    log_msg( "*\n" );
    log_msg("*********************************************************\n");
    log_msg( "\n" );

    PARM_PARSE_BLOCK:
    while ( @ARGV )
    {
        $_ = shift;

        if (/^-help/)
        {
            log_msg( "$USAGE" );
            $statusOut++;
            last PARM_PARSE_BLOCK;
        }

        elsif (/-no_delete/)
        {
            $DELETE_TEMP           = $FALSE;
            $NO_DELETE_TEMP_OPTION = $TRUE;
            next PARM_PARSE_BLOCK;
        }

        elsif (/-debug/)
        {
            $DEBUG                 = $TRUE;
            $DEBUG_OPTION          = $TRUE;
            next PARM_PARSE_BLOCK;
        }

        #***************************************************************************
        #
        # Catch any unrecognized options
        #
        #***************************************************************************

        log_error_msg( "VMMBI0300: Error: Unrecognized option '$_'\n\n" );
        log_msg( "$USAGE" );
        $statusOut++;
        last PARM_PARSE_BLOCK;

    }          # End of PARM_PARSE_BLOCK:

    #*****************************************************************************
    #
    # Terminate here if the status is not zero
    #
    #*****************************************************************************

    if ( $statusOut )
    {
        last MAIN_PROCESSING_BLOCK;
    }

    #*****************************************************************************
    #
    # Verify that all of the VMMBI environment variables have been set
    #
    #*****************************************************************************

    log_msg( "\n" );
    log_msg("*********************************************************\n");
    log_msg( "*\n" );
    log_msg( "* Verifying VMMBI environment variables:\n" );
    log_msg( "*\n" );
    log_msg("*********************************************************\n");
    log_msg( "\n" );

    ( $rval, @listUndefinedEnvVar) = verify_env_var_exists();

    if ( $rval )
    {
        $statusOut += $rval;

        log_error_msg( "VMMBI0400: Error: The following ChangeMan Builder integration VMMBI environment variables\n" );
        log_error_msg( "VMMBI0500:        are not defined.  Build jobs running this event trigger must be\n" );
        log_error_msg( "VMMBI0600:        created from the ChangeMan Version Manager to Builder integration.\n" );
        log_error_msg( "\n" );

        foreach $var ( @listUndefinedEnvVar )
        {
            log_msg( "          $var\n" );
        }

        last MAIN_PROCESSING_BLOCK;
    }

    #*****************************************************************************
    #
    # Get VMMBI environment variables
    #
    #*****************************************************************************

    $rval = get_VMMBI_env_vars();

    $statusOut += $rval;

    #***************************************************************************
    #
    # Validate the build directory associated with this build job. If the
    # the directory is not found, use the default directory i.e.
    # C:\Merant_Build, otherwise set the build directory to user defined
    #
    # Different levels of validations done in the calling function are:
    #
    # 1 - If the Env variable VMMBI03 (build mode) is "new" and the directory already exists
    #     Then return an error condition
    # 2 - If the Env variable VMMBI03 (build mode) is blank
    #     Then set the default directory to C:\Merant_Build and return an error condition
    # 3 - If the Env variable is "Clean" and the directory already exists
    #     Then the directory and all its contents are deleted and recreated
    #
    #***************************************************************************

    ( $rval, $BUILD_DIRECTORY ) = get_build_dir( $BUILD_DIRECTORY_IN );

    $statusOut += $rval;

    #***************************************************************************
    #
    # Change to the build directory
    #
    #***************************************************************************

    $rval = run_chdir( $BUILD_DIRECTORY );

    $statusOut += $rval;

    #***************************************************************************
    #
    # Get the build directory file system
    #
    #*************************************************************************** 

    ( $rval, $BUILD_DIRECTORY_FILE_SYSTEM ) = get_build_dir_file_system( $BUILD_DIRECTORY );

    $statusOut += $rval;

    #***************************************************************************
    #
    # Get the value of the TZ environment variable
    #
    # This is just to log the information to the user. An incorrectly assigned
    # TZ value can break things
    #
    #***************************************************************************

    $TZ_ENV_VAR = $ENV{ "TZ" };

    if ( $TZ_ENV_VAR eq "" )
    {
        $TZ_ENV_VAR = "Undefined";
    }

    #***************************************************************************
    #
    # Get the operating system
    #
    # This is just to log the information to the user.
    #
    #*************************************************************************** 

    $OS_VERSION = get_os_version();

    #***************************************************************************
    #
    # Redirect log output
    #
    # Redirect stdout and stderr to the log file.  Save stdout and stderr for
    # restoration later.
    #
    #***************************************************************************

    $BLDMAKE_PET_LOGFILE_OPEN = redirect_output( $BLDMAKE_PET_LOGFILE,             # Path and file name
                                                 'save_streams',                   # Save output streams for restore
                                                 $BLDMAKE_PET_LOGFILE_APPEND_FLAG  # Append to end of existing file
                                               );

    #***************************************************************************
    #
    # Map the PDB path
    #
    #***************************************************************************

    $VM_PDB_PATH = map_PDB_path( $VM_PDB_IN );

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
    log_msg("*    Date:    ", print_date(), " \n" );
    log_msg("*\n");
    log_msg("*    ChangeMan Builder bldmake Variables:\n");
    log_msg("*\n");
    log_msg("*       OMCOMMANDLINE                    = $OMCOMMANDLINE\n");
    log_msg("*       OMERRORRC                        = $OMERRORRC\n");
    log_msg("*       OMPROJECT                        = $OMPROJECT\n");
    log_msg("*       OMVPATHNAME                      = $OMVPATHNAME\n");
    log_msg("*       OMVPATH                          = $OMVPATH\n");
    log_msg("*       OMEMBEDTYPE                      = $OMEMBEDTYPE\n");
    log_msg("*\n");
    log_msg("*    VMMBI_bldmake_PET Variables:\n");
    log_msg("*\n");
    log_msg("*       Program name                     = $OUR_PROGRAM_NAME\n");
    log_msg("*       Program version                  = $PGM_VERSION\n");
    log_msg("*       Operating system                 = $CURRENT_OS\n");
    log_msg("*       Operating system version         = $OS_VERSION\n");
    log_msg("*       Command line                     = $COMMAND_LINE\n");
    log_msg("*       Program directory                = $PROGRAM_DIR\n");
    log_msg("*       Current working directory        = $CURRENT_WORKING_DIR\n");
    log_msg("*       Build directory                  = $BUILD_DIRECTORY\n");
    log_msg("*       Build directory file system      = $BUILD_DIRECTORY_FILE_SYSTEM\n");     
    log_msg("*       TZ setting                       = $TZ_ENV_VAR\n");      
    log_msg("*\n");

    log_env_vars();

    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* Performing parameter validation\n");
    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");

    #*****************************************************************************
    #
    # Write the parameters to the KB server build job log
    #
    #*****************************************************************************

    if ( $ENABLE_PROJECT_PROCESSING_IN ) { $projectProcessing  = "yes"; }
    if ( $INCLUDE_SUBPROJECTS_IN )       { $includeSubprojects = "yes"; }
    if ( $FOOTPRINTING_IN )              { $enableFootprinting = "yes"; }

    $msg  = "\n";
    $msg .= "VMMBI0700: ChangeMan Version Manager to ChangeMan Builder integration parameters:\n";
    $msg .= "\n";
    $msg .= "           Build directory                        = $BUILD_DIRECTORY\n";
    $msg .= "\n";
    $msg .= "           Project processing enabled             = $projectProcessing\n";
    $msg .= "           ChangeMan Version Manager PDB path     = $VM_PDB_PATH\n";
    $msg .= "           ChangeMan Version Manager project      = $VM_PROJECT_NAME_IN\n";
    $msg .= "           Include sub-projects                   = $includeSubprojects\n";
    $msg .= "\n";
    $msg .= "           Build mode                             = $BUILD_MODE_IN\n";
    $msg .= "           Source selection type                  = $SOURCE_SELECTION_TYPE_IN\n";
    $msg .= "           Version label or promo group           = $SOURCE_VERSION_PROMO_IN\n";
    $msg .= "           Perform footprinting                   = $enableFootprinting\n";
    $msg .= "\n";
    $msg .= "           VMMBI log                              = $BUILD_DIRECTORY$OUR_SLASH$BLDMAKE_PET_LOGFILE\n";
    $msg .= "           VMMBI program directory                = $PROGRAM_DIR \n";
    $msg .= "\n";

    sendMsgToMB_KBS_JobLog( $INFO_MSG,
                            $msg
                          );

    #***************************************************************************
    #
    # Validate the user for this build job and if it is a valid user then
    # allow to execute the build job, otherwise display a error message and exit
    #
    #***************************************************************************

    $rval = validate_user();

    $statusOut += $rval;

    #***************************************************************************
    #
    # Verify project name and search path name between environment variables and
    # bldmake pre-event trigger perl variables
    #
    #***************************************************************************

    if ( lc( $OMPROJECT )   ne lc( $MB_PROJECT_NAME_01_IN ) )
    {
        log_error_msg( "\n" );
        log_error_msg( "VMMBI0800: Error: The environment variable and ChangeMan Builder project names do not match\n" );
        log_error_msg( "VMMBI0900:        $OMPROJECT | $MB_PROJECT_NAME_01_IN\n" );
        $statusOut++;
    }

    if ( lc( $OMVPATHNAME ) ne lc( $MB_SEARCH_PATH_NAME_01_IN ) )
    {
        log_error_msg( "\n" );
        log_error_msg( "VMMBI1000: Error: The environment variable and ChangeMan Builder search path names do not match\n" );
        log_error_msg( "VMMBI1100:        $OMVPATHNAME | $MB_SEARCH_PATH_NAME_01_IN\n" );
        $statusOut++;
    }

    #***************************************************************************
    #
    # Verify that the build job OS and our OS match up
    #
    # 07/31/03 - Roger P. Joachim - SCR 13230
    #
    #***************************************************************************

    if ( lc( $CURRENT_OS ) ne lc( $MB_BUILD_MACHINE_OS_IN ) )
    {
        log_error_msg( "\n" );
        log_error_msg( "VMMBI1200: Error: The current operating system and the ChangeMan Builder job operatiog system do not match\n" );
        log_error_msg( "VMMBI1300: Error:    Current OS               = $CURRENT_OS\n" );
        log_error_msg( "VMMBI1400: Error:    ChangeMan Builder job OS = $MB_BUILD_MACHINE_OS_IN\n" );
        $statusOut++;
    }

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* Parameter validation completed\n");
    log_msg("*\n");
    log_msg("*    Status = $statusOut\n");
    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");

    #***************************************************************************
    #
    # Check to see if we should continue
    #
    #***************************************************************************

    if ( $statusOut )
    {
        log_msg("\n");
        log_msg("*********************************************************\n");
        log_msg("*\n");
        log_msg("* Build is being terminated due to error conditions\n");
        log_msg("*\n");
        log_msg("*    Date:    ", print_date(), " \n" );
        log_msg("*\n");
        log_msg("*    Status = $statusOut\n");
        log_msg("*\n");
        log_msg("*********************************************************\n");
        log_msg("\n");

        last MAIN_PROCESSING_BLOCK;
    }

    #***************************************************************************
    #***************************************************************************
    #
    # Main processing
    #
    #***************************************************************************
    #***************************************************************************

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* Main processing started\n");
    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");

    #***************************************************************************
    #
    # Perform processing based on the build mode (Targets, Incremental, Exact,
    # Clean, New, and Rebuild
    #
    #***************************************************************************

    BUILD_MODE_BLOCK:
    {
        #***************************************************************************
        #
        # Targets only build
        #
        #***************************************************************************

        if ( $BUILD_MODE_IN eq "Targets" )
        {
            $rval = Targets_Only_Build();
            $statusOut += $rval;
            last BUILD_MODE_BLOCK;
        }

        #***************************************************************************
        #
        # Incremental build
        #
        #***************************************************************************

        if ( $BUILD_MODE_IN eq "Incremental" )
        {
            $rval = Incremental_Build();
            $statusOut += $rval;
            last BUILD_MODE_BLOCK;
        }

        #***************************************************************************
        #
        # Exact build
        #
        #***************************************************************************

        if ( $BUILD_MODE_IN eq "Exact" )
        {
            $rval = Exact_Build();
            $statusOut += $rval;
            last BUILD_MODE_BLOCK;
        }

        #***************************************************************************
        #
        # Clean build
        #
        #***************************************************************************

        if ( $BUILD_MODE_IN eq "Clean" )
        {
            $rval = Clean_Build();
            $statusOut += $rval;
            last BUILD_MODE_BLOCK;
        }

        #***************************************************************************
        #
        # New build
        #
        #***************************************************************************

        if ( $BUILD_MODE_IN eq "New" )
        {
            $rval = New_Build();
            $statusOut += $rval;
            last BUILD_MODE_BLOCK;
        }

        #***************************************************************************
        #
        # Re-build
        #
        #***************************************************************************

        if ( $BUILD_MODE_IN eq "Rebuild" )
        {
            $rval = Rebuild();
            $statusOut += $rval;
            last BUILD_MODE_BLOCK;
        }

        #***************************************************************************
        #
        # Unknown build mode
        #
        #***************************************************************************

        $statusOut++;
        log_error_msg( "VMMBI1500: Error: Unknown build mode was specified: Build mode = $BUILD_MODE_IN\n");
        log_error_msg( "VMMBI1600: Error:    Supported build modes are: Incremental, Exact, Clean, New, and Rebuild\n");

    }       # End of BUILD_MODE_BLOCK

    #***************************************************************************
    #
    # Only display in debug mode
    #
    #***************************************************************************

    if ( $DEBUG )
    {
        #***************************************************************************
        #
        # Display PDB map array
        #
        #***************************************************************************

        log_msg( "\n");
        log_msg( "*********************************************************\n");
        log_msg( "*\n");
        log_msg( "* PDB map array \n");
        log_msg( "*\n");
        log_msg( "*********************************************************\n");
        log_msg( "\n");

        if ( scalar( @PDB_MAP_LIST ) > 0 )
        {
            log_msg( "From String    To String      Case Sensitive\n");
            log_msg( "~~~~~~~~~~~~~  ~~~~~~~~~~~~~  ~~~~~~~~~~~~~~\n");
        }

        foreach $PDB_MAP_Object( @PDB_MAP_LIST )
        {
            $msg = $PDB_MAP_Object->toString();
            log_msg( $msg );
        }

        #***************************************************************************
        #
        # Display Archive information array
        #
        #***************************************************************************

        log_msg( "\n");
        log_msg( "*********************************************************\n");
        log_msg( "*\n");
        log_msg( "* Archive information array \n");
        log_msg( "*\n");
        log_msg( "*********************************************************\n");
        log_msg( "\n");

        if ( scalar( @ARCHIVE_INFO_LIST ) > 0 )
        {
            log_msg( "Work File Path                           Relative Work File Path        Archive Path                                                                                         Revision Number  Date Modified                        Date Modified (UTC)   DST Adj  Date Checked In\n");
            log_msg( "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  ~~~~~~~~~~~~~~~  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  ~~~~~~~~~~~~~~~~~~~~  ~~~~~~~  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        }

        foreach $ARCHIVE_INFO_Object( @ARCHIVE_INFO_LIST )
        {
            $msg = $ARCHIVE_INFO_Object->toString();
            log_msg( $msg );
        }

        #***************************************************************************
        #
        # Display work file lists
        #
        #***************************************************************************

        my( $title1 ) = "Relative Work File Path                                                          Work File Path\n";
        my( $title2 ) = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";

        log_msg( "\n");
        log_msg( "*********************************************************\n");
        log_msg( "*\n");
        log_msg( "* Work file older list\n");
        log_msg( "*\n");
        log_msg( "* Output is:\n");
        log_msg( "*\n");
        log_msg( "*    <rel_work_file_path> | <work_file_path>\n");
        log_msg( "*\n");
        log_msg( "*********************************************************\n");
        log_msg( "\n");

        if ( scalar( @WORK_FILE_OLDER_LIST ) > 0 )
        {
            log_msg( $title1 );
            log_msg( $title2 );
        }

        foreach $ARCHIVE_INFO_Object( @WORK_FILE_OLDER_LIST )
        {
            my( $WorkFilePath )    = $BUILD_DIRECTORY.$ARCHIVE_INFO_Object->get_work_file_path();
            my( $RelWorkFilePath ) = $BUILD_DIRECTORY.$ARCHIVE_INFO_Object->get_rel_work_file_path();

            $msg = sprintf( "%-80.80s %-80.80s \n",
                                   $RelWorkFilePath,
                                   $WorkFilePath
                                 );

            log_msg( $msg );
        }

        log_msg( "\n");
        log_msg( "*********************************************************\n");
        log_msg( "*\n");
        log_msg( "* Work file older or does not exist list\n");
        log_msg( "*\n");
        log_msg( "*********************************************************\n");
        log_msg( "\n");

        if ( scalar( @WORK_FILE_OLDER_OR_NONEXISTANT_LIST ) > 0 )
        {
            log_msg( $title1 );
            log_msg( $title2 );
        }

        foreach $ARCHIVE_INFO_Object( @WORK_FILE_OLDER_OR_NONEXISTANT_LIST )
        {
            $msg = sprintf( "%-80.80s %-80.80s \n",
                            $BUILD_DIRECTORY . $ARCHIVE_INFO_Object->get_rel_work_file_path(),
                            $BUILD_DIRECTORY . $ARCHIVE_INFO_Object->get_work_file_path()
                          );

            log_msg( $msg );
        }

        log_msg( "\n");
        log_msg( "*********************************************************\n");
        log_msg( "*\n");
        log_msg( "* Work file newer list\n");
        log_msg( "*\n");
        log_msg( "*********************************************************\n");
        log_msg( "\n");

        if ( scalar( @WORK_FILE_NEWER_LIST ) > 0 )
        {
            log_msg( $title1 );
            log_msg( $title2 );
        }

        foreach $ARCHIVE_INFO_Object( @WORK_FILE_NEWER_LIST )
        {
            $msg = sprintf( "%-80.80s %-80.80s \n",
                            $BUILD_DIRECTORY . $ARCHIVE_INFO_Object->get_rel_work_file_path(),
                            $BUILD_DIRECTORY . $ARCHIVE_INFO_Object->get_work_file_path()
                          );

            log_msg( $msg );
        }

        log_msg( "\n");
        log_msg( "*********************************************************\n");
        log_msg( "*\n");
        log_msg( "* Work file doesnt exist list\n");
        log_msg( "*\n");
        log_msg( "*********************************************************\n");
        log_msg( "\n");

        if ( scalar( @WORK_FILE_NONEXISTANT_LIST ) > 0 )
        {
            log_msg( $title1 );
            log_msg( $title2 );
        }

        foreach $ARCHIVE_INFO_Object( @WORK_FILE_NONEXISTANT_LIST )
        {
            $msg = sprintf( "%-80.80s %-80.80s \n",
                            $BUILD_DIRECTORY . $ARCHIVE_INFO_Object->get_rel_work_file_path(),
                            $BUILD_DIRECTORY . $ARCHIVE_INFO_Object->get_work_file_path()
                          );

            log_msg( $msg );
        }

        log_msg( "\n");
        log_msg( "*********************************************************\n");
        log_msg( "*\n");
        log_msg( "* Work file same list\n");
        log_msg( "*\n");
        log_msg( "*********************************************************\n");
        log_msg( "\n");

        if ( scalar( @WORK_FILE_SAME_LIST ) > 0 )
        {
            log_msg( $title1 );
            log_msg( $title2 );
        }

        foreach $ARCHIVE_INFO_Object( @WORK_FILE_SAME_LIST )
        {
            $msg = sprintf( "%-80.80s %-80.80s \n",
                            $BUILD_DIRECTORY . $ARCHIVE_INFO_Object->get_rel_work_file_path(),
                            $BUILD_DIRECTORY . $ARCHIVE_INFO_Object->get_work_file_path()
                          );

            log_msg( $msg );
        }

        log_msg( "\n");
        log_msg( "*********************************************************\n");
        log_msg( "*\n");
        log_msg( "* Work file different list\n");
        log_msg( "*\n");
        log_msg( "*********************************************************\n");
        log_msg( "\n");

        if ( scalar( @WORK_FILE_DIFFERENT_LIST ) > 0 )
        {
            log_msg( $title1 );
            log_msg( $title2 );
        }

        foreach $ARCHIVE_INFO_Object( @WORK_FILE_DIFFERENT_LIST )
        {
            $msg = sprintf( "%-80.80s %-80.80s \n",
                            $BUILD_DIRECTORY . $ARCHIVE_INFO_Object->get_rel_work_file_path(),
                            $BUILD_DIRECTORY . $ARCHIVE_INFO_Object->get_work_file_path()
                          );

            log_msg( $msg );
        }
    }
}          # End of MAIN_PROCESSING_BLOCK:

#***************************************************************************
#
# All done
#
#***************************************************************************

log_msg( "\n");
log_msg( "*********************************************************\n");
log_msg( "*\n");
log_msg( "* End of processing\n");
log_msg( "*\n");
log_msg( "*    Date and Time = ", print_date(), " \n" );
log_msg( "*\n");
log_msg( "*    Integration version information:\n");
log_msg( "*\n");
log_msg( "*        VMMBI_bldmake_PET.pl Version = $VMMBI_VERSION\n");
log_msg( "*        Build Job VM Version         = $BUILD_JOB_VM_VERSION\n");
log_msg( "*        Build Job VM Build Number    = $BUILD_JOB_VM_BUILD_NUM\n");
log_msg( "*        VM PCLI Version              = $PCLI_VERSION\n");
log_msg( "*        VM PCLI Build Number         = $PCLI_BUILD_NUM\n");
log_msg( "*\n");
log_msg( "*    Status = $statusOut\n");
log_msg( "*\n");
log_msg( "*********************************************************\n");
log_msg( "\n");

if ( $DEBUG )
{
    #*****************************************************************************
    #
    # Generate version information message for debug
    #
    #*****************************************************************************

    $versionMsg  = "\n";
    $versionMsg .= "VMMBI9900: Integration version information:\n";
    $versionMsg .= "\n";
    $versionMsg .= "           VMMBI_bldmake_PET.pl Version = $VMMBI_VERSION\n";
    $versionMsg .= "           Build Job VM Version         = $BUILD_JOB_VM_VERSION\n";
    $versionMsg .= "           Build Job VM Build Number    = $BUILD_JOB_VM_BUILD_NUM\n";
    $versionMsg .= "           VM PCLI Version              = $PCLI_VERSION\n";
    $versionMsg .= "           VM PCLI Build Number         = $PCLI_BUILD_NUM\n";
}

if ( $statusOut > 0 )
{
    #***************************************************************************
    #
    # Generate error completion message
    #
    #***************************************************************************

    $msg  = "VMMBI9600: Error: The bldmake VM integration processing completed with errors: RC = $statusOut\n";
    $msg .= $versionMsg;
    $msg .= "\n";
    $msg .= "VMMBI9700: Error Message Summary:\n";
    $msg .= "\n";

    foreach $errorMsg ( @ERROR_MSG_QUEUE )
    {
        $msg .= "               $errorMsg";
    }

    $msg .= "\n";

    sendMsgToMB_KBS_JobLog( $ERROR_MSG,
                            $msg
                          );

    #***************************************************************************
    #
    # Add error summary to message
    #
    #***************************************************************************

    log_msg( "Error Message Summary:\n" );
    log_msg( "\n" );

    foreach $errorMsg ( @ERROR_MSG_QUEUE )
    {
        log_msg( "    $errorMsg" );
    }

    log_msg( "\n" );

}
else
{
    #***************************************************************************
    #
    # Generate normal completion message
    #
    #***************************************************************************

    $msg  = "VMMBI9800: The bldmake VM integration processing completed without errors.\n";
    $msg .= $versionMsg;
    $msg .= "\n";

    sendMsgToMB_KBS_JobLog( $INFO_MSG,
                            $msg
                          );

}

#***************************************************************************
#
# Return stdout and stderr to normal
#
#***************************************************************************

reset_output();

#***************************************************************************
#
# If we completed with errors then use exit to terminate the build process
# Other wise terminate normally and allow the build to continue
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
# Targets_Only_Build()  --
#
#
# Input Parameters:
#
#     None
#
# Output Parameters:
#
#     $statusOut = Return code
#                  0 = successful
#                  1 = failure
#
#***************************************************************************

sub Targets_Only_Build
{
    my( $statusOut ) = 0;

    $statusOut = get_target_files();

    return ( $statusOut );

}   ## end -- Targets_Only_Build

#***************************************************************************
#
# Incremental_Build()   --
#
#
# Input Parameters:
#
#     None
#
# Output Parameters:
#
#     $statusOut = Return code
#                  0 = successful
#                  1 = failure
#
#***************************************************************************

sub Incremental_Build
{
    my( $statusOut ) = 0;

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* Incremental_Build: Starting\n");
    log_msg("*\n");
    log_msg("*    Date:    ", print_date(), " \n" );
    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");

    $statusOut = perform_incremental();

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* Incremental_Build: Ending\n");
    log_msg("*\n");
    log_msg("*    Date:    ", print_date(), " \n" );
    log_msg("*\n");
    log_msg("*    Status = $statusOut\n");
    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");

    return ( $statusOut );

}   ## end -- Incremental_Build

#***************************************************************************
#
# Exact_Build()   --
#
#
# Input Parameters:
#
#     None
#
# Output Parameters:
#
#     $statusOut = Return code
#                  0 = successful
#                  1 = failure
#
#***************************************************************************

sub Exact_Build
{
    my( $statusOut )   = 0;

    my( $rval )        = 0;
    my( $joinedLists ) = 0;

    EXACT_BUILD_BLOCK:
    {
        log_msg("\n");
        log_msg("*********************************************************\n");
        log_msg("*\n");
        log_msg("* Exact_Build: Starting\n");
        log_msg("*\n");
        log_msg("*    Date:    ", print_date(), " \n" );
        log_msg("*\n");
        log_msg("*********************************************************\n");
        log_msg("\n");

        #***************************************************************************
        #
        # Get archive information
        #
        # We get an array of ARCHIVE_INFO objects
        #
        #***************************************************************************

        ( $rval, @ARCHIVE_INFO_LIST ) = get_archive_info_list();

        if ( $rval )
        {
            log_error_msg("VMMBI1700: Exact_Build: Error: Get archive information list failed\n");
            $statusOut += $rval;
            last EXACT_BUILD_BLOCK;
        }

        #***************************************************************************
        #
        # Compare the archive last modified time to the work file last modified time
        # and create lists based on result of comparisons.
        #
        #    @WORK_FILE_OLDER_LIST
        #    @WORK_FILE_OLDER_OR_NONEXISTANT_LIST
        #    @WORK_FILE_NEWER_LIST
        #    @WORK_FILE_NONEXISTANT_LIST
        #    @WORK_FILE_SAME_LIST
        #    @WORK_FILE_DIFFERENT_LIST
        #
        #***************************************************************************

        $rval = compare_file_times( \@ARCHIVE_INFO_LIST      # This parameter is a pointer to the array
                                  );

        if ( $rval )
        {
            log_error_msg("VMMBI1800: Exact_Build: Error: Compare file times failed\n");
            $statusOut += $rval;
            last EXACT_BUILD_BLOCK;
        }

        #***************************************************************************
        #
        # Get the project files
        #
        #***************************************************************************

        $rval = get_exact_project_files( \@WORK_FILE_DIFFERENT_LIST
                                       );

        if ( $rval )
        {
            log_error_msg("VMMBI1900: Exact_Build: Error: Get project files failed\n");
            $statusOut += $rval;
            last EXACT_BUILD_BLOCK;
        }

        #***************************************************************************
        #
        # Check to see that we got what we expected
        #
        #***************************************************************************

        log_msg("\n");
        log_msg("*********************************************************\n");
        log_msg("*\n");
        log_msg("* Exact_Build: Verifying revision retrieval\n");
        log_msg("*\n");
        log_msg("*********************************************************\n");
        log_msg("\n");

        $rval = verify_gets( \@WORK_FILE_DIFFERENT_LIST
                           );

        if ( $rval )
        {
            log_error_msg("VMMBI2000: Exact_Build: Error: Get verify failed\n");
            $statusOut += $rval;
            last EXACT_BUILD_BLOCK;
        }
        else
        {
            log_msg("Exact_Build: Verify successful\n");
        }

        #***************************************************************************
        #
        # Join the ARCHIVE_INFO object arrays to get the files which will be
        # footprinted.
        #
        # If we are in debug mode then join_arc_info_arrays() will also sort the
        # resulting array and check for duplicates.
        #
        #***************************************************************************

        ( $rval, $joinedLists ) = join_arc_info_arrays( \@WORK_FILE_DIFFERENT_LIST,
                                                        \@WORK_FILE_SAME_LIST
                                                      );

        if ( $rval )
        {
            $statusOut += $rval;
            last EXACT_BUILD_BLOCK;
        }

        #***************************************************************************
        #
        # Generate footprint information file
        #
        #***************************************************************************

        if ( $FOOTPRINTING_IN )
        {
            $rval = gen_footprint_info_file( $joinedLists );

            $statusOut += $rval;
        }

    }          # End of EXACT_BUILD_BLOCK:

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* Exact_Build: Ending\n");
    log_msg("*\n");
    log_msg("*    Date:    ", print_date(), " \n" );
    log_msg("*\n");
    log_msg("*    Status = $statusOut\n");
    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");

    return ( $statusOut );

}   ## end -- Exact_Build

#***************************************************************************
#
# Clean_Build()  --
#
#
# Input Parameters:
#
#     None
#
# Output Parameters:
#
#     $statusOut = Return code
#                  0 = successful
#                  1 = failure
#
#***************************************************************************

sub Clean_Build
{
    my( $statusOut )   = 0;

    my( $rval )        = 0;
    my( $joinedLists ) = 0;

    CLEAN_BUILD_BLOCK:
    {
        log_msg("\n");
        log_msg("*********************************************************\n");
        log_msg("*\n");
        log_msg("* Clean_Build: Starting\n");
        log_msg("*\n");
        log_msg("*    Date:    ", print_date(), " \n" );
        log_msg("*\n");
        log_msg("*********************************************************\n");
        log_msg("\n");

        #***************************************************************************
        #
        # Get archive information
        #
        # We get an array of ARCHIVE_INFO objects
        #
        #***************************************************************************

        ( $rval, @ARCHIVE_INFO_LIST ) = get_archive_info_list();

        if ( $rval )
        {
            log_error_msg("VMMBI2100: Clean_Build: Error: Get archive information list failed\n");
            $statusOut += $rval;
            last CLEAN_BUILD_BLOCK;
        }

        #***************************************************************************
        #
        # There is no need to perform a comparison of the archive last modified time
        # to the work file last modified time.  This is becuase we know we are starting
        # with a clean directory, there are no work files.
        #
        # But we will go ahead and add all of the elements to the work file
        # non-existant list for consistancey and display.
        #
        #***************************************************************************

        ( $rval, $joinedLists ) = join_arc_info_arrays( \@ARCHIVE_INFO_LIST,
                                                        \@NULL_ARRAY
                                                      );

        @WORK_FILE_NONEXISTANT_LIST = @$joinedLists;

        if ( $rval )
        {
            $statusOut += $rval;
            last CLEAN_BUILD_BLOCK;
        }

        #***************************************************************************
        #
        # Get the project files
        #
        #***************************************************************************

        $rval = get_project_files();

        if ( $rval )
        {
            log_error_msg("VMMBI2200: Clean_Build: Error: Get project files failed\n");
            $statusOut += $rval;
            last CLEAN_BUILD_BLOCK;
        }

        #***************************************************************************
        #
        # Check to see that we got what we expected
        #
        #***************************************************************************

        log_msg("\n");
        log_msg("*********************************************************\n");
        log_msg("*\n");
        log_msg("* Clean_Build: Verifying revision retrieval\n");
        log_msg("*\n");
        log_msg("*********************************************************\n");
        log_msg("\n");

        $rval = verify_gets( \@ARCHIVE_INFO_LIST
                           );

        if ( $rval )
        {
            log_error_msg("VMMBI2300: Clean_Build: Error: Get verify failed\n");
            $statusOut += $rval;
            last CLEAN_BUILD_BLOCK;
        }
        else
        {
            log_msg("Clean_Build: Verify successful\n");
        }

        #***************************************************************************
        #
        # Join the ARCHIVE_INFO object arrays to get the files which will be
        # footprinted.
        #
        # If we are in debug mode then join_arc_info_arrays() will also sort the
        # resulting array and check for duplicates.
        #
        #***************************************************************************

        ( $rval, $joinedLists ) = join_arc_info_arrays( \@ARCHIVE_INFO_LIST,
                                                        \@NULL_ARRAY
                                                      );

        if ( $rval )
        {
            $statusOut += $rval;
            last EXACT_BUILD_BLOCK;
        }

        #***************************************************************************
        #
        # Generate footprint information file
        #
        #***************************************************************************

        if ( $FOOTPRINTING_IN )
        {
            $rval = gen_footprint_info_file( $joinedLists );

            $statusOut += $rval;
        }

    }          # End of CLEAN_BUILD_BLOCK:

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* Clean_Build: Ending\n");
    log_msg("*\n");
    log_msg("*    Date:    ", print_date(), " \n" );
    log_msg("*\n");
    log_msg("*    Status = $statusOut\n");
    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");

    return ( $statusOut );

}   ## end -- Clean_Build

#***************************************************************************
#
# New_Build()  --
#
#
# Input Parameters:
#
#     None
#
# Output Parameters:
#
#     $statusOut = Return code
#                  0 = successful
#                  1 = failure
#
#***************************************************************************

sub New_Build
{
    my( $statusOut )   = 0;

    my( $rval )        = 0;
    my( $joinedLists ) = 0;

    NEW_BUILD_BLOCK:
    {
        log_msg("\n");
        log_msg("*********************************************************\n");
        log_msg("*\n");
        log_msg("* New_Build: Starting\n");
        log_msg("*\n");
        log_msg("*    Date:    ", print_date(), " \n" );
        log_msg("*\n");
        log_msg("*********************************************************\n");
        log_msg("\n");

        #***************************************************************************
        #
        # Get archive information
        #
        # We get an array of ARCHIVE_INFO objects
        #
        #***************************************************************************

        ( $rval, @ARCHIVE_INFO_LIST ) = get_archive_info_list();

        if ( $rval )
        {
            log_error_msg("VMMBI2400: New_Build: Error: Get archive information list failed\n");
            $statusOut += $rval;
            last NEW_BUILD_BLOCK;
        }

        #***************************************************************************
        #
        # There is no need to perform a comparison of the archive last modified time
        # to the work file last modified time.  This is becuase we know we are starting
        # with a clean directory, there are not work files.
        #
        #***************************************************************************

        #***************************************************************************
        #
        # Get the project files
        #
        #***************************************************************************

        $rval = get_project_files();

        if ( $rval )
        {
            log_error_msg("VMMBI2500: New_Build: Error: Get project files failed\n");
            $statusOut += $rval;
            last NEW_BUILD_BLOCK;
        }

        #***************************************************************************
        #
        # Check to see that we got what we expected
        #
        #***************************************************************************

        log_msg("\n");
        log_msg("*********************************************************\n");
        log_msg("*\n");
        log_msg("* New_Build: Verifying revision retrieval\n");
        log_msg("*\n");
        log_msg("*********************************************************\n");
        log_msg("\n");

        $rval = verify_gets( \@ARCHIVE_INFO_LIST
                           );

        if ( $rval )
        {
            log_error_msg("VMMBI2600: New_Build: Error: Get verify failed\n");
            $statusOut += $rval;
            last NEW_BUILD_BLOCK;
        }
        else
        {
            log_msg("New_Build: Verify successful\n");
        }

        #***************************************************************************
        #
        # Join the ARCHIVE_INFO object arrays to get the files which will be
        # footprinted.
        #
        # If we are in debug mode then join_arc_info_arrays() will also sort the
        # resulting array and check for duplicates.
        #
        #***************************************************************************

        ( $rval, $joinedLists ) = join_arc_info_arrays( \@ARCHIVE_INFO_LIST,
                                                        \@NULL_ARRAY
                                                      );

        if ( $rval )
        {
            $statusOut += $rval;
            last EXACT_BUILD_BLOCK;
        }

        #***************************************************************************
        #
        # Generate footprint information file
        #
        #***************************************************************************

        if ( $FOOTPRINTING_IN )
        {
            $rval = gen_footprint_info_file( $joinedLists );

            $statusOut += $rval;
        }

    }          # End of NEW_BUILD_BLOCK:

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* New_Build: Ending\n");
    log_msg("*\n");
    log_msg("*    Date:    ", print_date(), " \n" );
    log_msg("*\n");
    log_msg("*    Status = $statusOut\n");
    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");

    return ( $statusOut );

}   ## end -- New_Build

#***************************************************************************
#
# Rebuild()   --  Perform an incremental build after all intermediate and
#                  final targets have been deleted
#
#
# Input Parameters:
#
#     None
#
# Output Parameters:
#
#     $statusOut = Return code
#                  0 = successful
#                  1 = failure
#
# Notes:
#
#     (1) The processing for the "Rebuild" build mode is identical to the
#         "Incremental" build mode.  The difference lies in the fact that
#         the VM integration also added the "clean" parameter to the OM
#         command line.
#
#***************************************************************************

sub Rebuild
{
    my( $statusOut ) = 0;

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* Rebuild: Starting\n");
    log_msg("*\n");
    log_msg("*    Date:    ", print_date(), " \n" );
    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");

    $statusOut = perform_incremental();

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* Rebuild: Ending\n");
    log_msg("*\n");
    log_msg("*    Date:    ", print_date(), " \n" );
    log_msg("*\n");
    log_msg("*    Status = $statusOut\n");
    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");

    return ( $statusOut );

}   ## end -- Rebuild

#***************************************************************************
#
# perform_incremental()   -- Perform a incremental build
#
#
# Input Parameters:
#
#     None
#
# Output Parameters:
#
#     $statusOut = Return code
#                  0 = successful
#                  1 = failure
#
#***************************************************************************

sub perform_incremental
{
    my( $statusOut )   = 0;

    my( $rval )        = 0;
    my( $joinedLists ) = 0;

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* perform_incremental: Starting\n");
    log_msg("*\n");
    log_msg("* Note that the pcli message:\n");
    log_msg("*\n");
    log_msg("*    Warning: /<project>/<file_name>: Could not check out the file because it does not contain the specified revision.\n");
    log_msg("*\n");
    log_msg("* Is an indication that the -u parameter is being used and that\n");
    log_msg("* the target file is up-to-date. The verify step will determine\n");
    log_msg("* whether there is a problem or not.\n");
    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");


    INCREMENTAL_BLOCK:
    {

        #***************************************************************************
        #
        # Get archive information
        #
        # We get an array of ARCHIVE_INFO objects
        #
        #***************************************************************************

        ( $rval, @ARCHIVE_INFO_LIST ) = get_archive_info_list();

        if ( $rval )
        {
            log_error_msg("VMMBI2700: perform_incremental: Error: Get archive information list failed\n");
            $statusOut += $rval;
            last INCREMENTAL_BLOCK;
        }

        #***************************************************************************
        #
        # Compare the archive last modified time to the work file last modified time
        # and create lists based on result of comparisons.
        #
        #    @WORK_FILE_OLDER_LIST
        #    @WORK_FILE_OLDER_OR_NONEXISTANT_LIST
        #    @WORK_FILE_NEWER_LIST
        #    @WORK_FILE_NONEXISTANT_LIST
        #    @WORK_FILE_SAME_LIST
        #    @WORK_FILE_DIFFERENT_LIST
        #
        #***************************************************************************

        $rval = compare_file_times( \@ARCHIVE_INFO_LIST      # This parameter is a pointer to the array
                                  );

        if ( $rval )
        {
            log_error_msg("VMMBI2800: perform_incremental: Error: Compare file times failed\n");
            $statusOut += $rval;
            last INCREMENTAL_BLOCK;
        }

        #***************************************************************************
        #
        # Get the project files
        #
        #***************************************************************************

        $rval = get_project_files();

        if ( $rval )
        {
            log_error_msg("VMMBI2900: perform_incremental: Error: Get project files failed\n");
            $statusOut += $rval;
            last INCREMENTAL_BLOCK;
        }

        #***************************************************************************
        #
        # Check to see that we got what we expected
        #
        #***************************************************************************

        log_msg("\n");
        log_msg("*********************************************************\n");
        log_msg("*\n");
        log_msg("* perform_incremental: Verifying revision retrieval\n");
        log_msg("*\n");
        log_msg("*********************************************************\n");
        log_msg("\n");

        $rval = verify_gets( \@WORK_FILE_OLDER_OR_NONEXISTANT_LIST
                           );

        if ( $rval )
        {
            log_error_msg("VMMBI3000: perform_incremental: Error: Get verify failed\n");
            $statusOut += $rval;
            last INCREMENTAL_BLOCK;
        }
        else
        {
            log_msg("perform_incremental: Verify successful\n");
        }

        #***************************************************************************
        #
        # Join the ARCHIVE_INFO object arrays to get the files which will be
        # footprinted.
        #
        # If we are in debug mode then join_arc_info_arrays() will also sort the
        # resulting array and check for duplicates.
        #
        #***************************************************************************

        ( $rval, $joinedLists ) = join_arc_info_arrays( \@WORK_FILE_OLDER_OR_NONEXISTANT_LIST,
                                                        \@WORK_FILE_SAME_LIST
                                                      );

        if ( $rval )
        {
            $statusOut += $rval;
            last EXACT_BUILD_BLOCK;
        }

        #***************************************************************************
        #
        # Generate footprint information file
        #
        #***************************************************************************

        if ( $FOOTPRINTING_IN )
        {
            $rval = gen_footprint_info_file( $joinedLists );

            $statusOut += $rval;
        }

    }          # End of INCREMENTAL_BLOCK:

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* perform_incremental: Ending\n");
    log_msg("*\n");
    log_msg("*    Status = $statusOut\n");
    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");

    return ( $statusOut );

}   ## end -- perform_incremental

#***************************************************************************
#
# join_arc_info_arrays  -- Join two arrays together and check for duplicates
#
#
# Input Parameters:
#
#    $ArrayOneIn    = Pointer to first  array of ARCHIVE_INFO objects
#    $ArrayTwoIn    = Pointer to second array of ARCHIVE_INFO objects
#
# Output Parameters:
#
#    $statusOut     = Return status
#                     0 = Successful
#                     1 = Failed
#
#    @fullArrayOut  = Combined and sorted array
#
#***************************************************************************

sub join_arc_info_arrays
{
    my( $ArrayOneIn )              = shift;    # Pointer to an array
    my( $ArrayTwoIn )              = shift;    # Pointer to an array

    my( $statusOut )               = 0;
    my( @fullArrayOut )            = ();

    my( @arrayOne )                = @$ArrayOneIn;
    my( @arrayTwo )                = @$ArrayTwoIn;
    my( $element )                 = "";
    my( @fullArraySorted )         = ();
    my( $previous )                = "";
    my( $workFilePath )            = "";
    my( $arcPath )                 = "";
    my( $arcRev )                  = "";
    my( $msg )                     = "";

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* join_arc_info_arrays: Starting\n");
    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");

    #***************************************************************************
    #
    # Combine specified arrays
    #
    # One or both can be null
    #
    #***************************************************************************

    foreach $element ( @arrayOne )
    {
        push @fullArrayOut, $element;
    }

    foreach $element ( @arrayTwo )
    {
        push @fullArrayOut, $element;
    }

    #***************************************************************************
    #
    # Perform debug processing
    #
    #***************************************************************************

    if ( $DEBUG )
    {
        #***************************************************************************
        #
        # Sort the array by work file path
        #
        #***************************************************************************

        @fullArraySorted = sort( sort_by_WorkFilePath @fullArrayOut );

        #***************************************************************************
        #
        # Check the array
        #
        #***************************************************************************

        foreach $element ( @fullArraySorted )
        {
            $workFilePath = $element->get_rel_work_file_path();

            if ( $previous eq $workFilePath )
            {
                log_error_msg("VMMBI3100: join_arc_info_arrays: Error: Duplicate elements in full array list\n");
                $statusOut++;
            }

            $previous = $workFilePath;
        }

        @fullArrayOut = @fullArraySorted;
    }

    #***************************************************************************
    #
    # Display archive information in KB server build job log if in verbose mode
    #
    #***************************************************************************

    if ( $CMD_LINE_PARMS_OBJECT->{verboseOutput} == 1 )
    {
        #***************************************************************************
        #
        # Sort the array by archive path and file name
        #
        #***************************************************************************

        foreach $element ( @fullArrayOut )
        {
            my( $s ) = $element->get_archive_path();
        }

        @fullArraySorted = sort( sort_by_ArchivePath @fullArrayOut );

        $msg  = "\n";
        $msg .= "VMMBI3200: ChangeMan Version Manager revision numbers and archive files used in this build:\n";
        $msg .= "\n";

        foreach $element ( @fullArraySorted )
        {
            $arcPath = $element->get_archive_path();
            $arcRev  = substr_right( $element->get_revision(), 30 );

            $arcRev  = sprintf( "%-30.30s", $arcRev, );

            $msg    .= "           " . $arcRev . " " . $arcPath . "\n";
        }

        $msg .= "\n";
        $msg .= "\n";

        sendMsgToMB_KBS_JobLog( $INFO_MSG,
                                $msg
                              );
    }

    #***************************************************************************
    #
    # All done
    #
    #***************************************************************************

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* join_arc_info_arrays: Ending\n");
    log_msg("*\n");
    log_msg("*    Status = $statusOut\n");
    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");

    return ( $statusOut, \@fullArrayOut );

}   ## end -- join_arc_info_arrays

#***************************************************************************
#
# gen_footprint_info_file  -- Generate the footprint information file
#
#
# Input Parameters:
#
#    $arcInfoArrayIn    = Pointer to an array of ARCHIVE_INFO objects
#
# Output Parameters:
#
#    $statusOut = Return status
#                 0 = Successful
#                 1 = Failed
#
#***************************************************************************

sub gen_footprint_info_file
{
    my( $arcInfoArrayIn )          = shift;    # Pointer to an array

    my( $statusOut )               = 0;

    my( @arcInfoArray )            = @$arcInfoArrayIn;
    my( $line )                    = "";
    my( $arcInfo )                 = "";
    my( $SQ )                      = "'";
    my( $arcPath )                 = "";
    my( $arcRev )                  = "";
    my( $hashKey )                 = "";
    my( $VmProjNameLen )           = 0;
    my( $AL )                      = 101;    # Archive path display field length    (101)
    my( $RL )                      = 17;     # Revision number display field length (17)

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* gen_footprint_info_file: Starting\n");
    log_msg("*\n");
    log_msg("*    \$FOOTPRINT_INFO_FILE_NAME = $FOOTPRINT_INFO_FILE_NAME\n");
    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");

    FOOTPRINT_BLOCK:
    {
        #***************************************************************************
        #
        # If the file exists then delete it
        #
        #***************************************************************************

        if ( -e $FOOTPRINT_INFO_FILE_NAME )
        {
            unlink( $FOOTPRINT_INFO_FILE_NAME );
        }

        #***************************************************************************
        #
        # Open the footprint info file for output
        #
        #***************************************************************************

        $rval = open(FP_INFO_FILE, ">$FOOTPRINT_INFO_FILE_NAME" );

        if ( ! $rval )                                     # 1 is good otherwise bad
        {
            log_error_msg("VMMBI3300: gen_footprint_info_file: Error: Unable to create the footprint information file\n");
            $statusOut++;
            last FOOTPRINT_BLOCK;
        }

        #***************************************************************************
        #
        # Write out each line of the footprint info file
        #
        #***************************************************************************

        $VmProjNameLen = length( $VM_PROJECT_NAME_IN );

        foreach $element ( @arcInfoArray )
        {
            #***************************************************************************
            #
            # Get the archive path and revision number and right justify them into the
            # field length.  If the field fits then a leading blank is added if it does
            # not fit then a leading asterix is added.
            #
            #***************************************************************************

            $arcPath = substr_right( $element->get_archive_path(), $AL );
            $arcRev  = substr_right( $element->get_revision(),     $RL );

            #***************************************************************************
            #
            # Create the archive path and revision number information string
            #
            # This string will be added to the hash table and used for footprinting in
            # the om version information event trigger (VMMBI_om_VIT.pl).
            #
            #***************************************************************************

            $arcInfo = sprintf( "%-$AL.$AL" . "s %-$RL.$RL" . "s ", $arcPath, $arcRev, );

            #***************************************************************************
            #
            # Create the perl statement to add the key/value pair to the hash table
            #
            #***************************************************************************

            $hashKey = $element->get_rel_work_file_path();

            #***************************************************************************
            #
            # We are converting to all forward slashes because we treat this as a
            # Version Manager project path, which is done with all forward slashes. The
            # We do not want any slash mismatches between the hash key created her and
            # the one used in VMMBI_om_VIT.pl
            #
            #***************************************************************************

            $hashKey =~ s/\\/\//g;                     # Convert all back slashes to forward slashes

            $line = '$FILE_INFO_HASH{ ' . $SQ . $hashKey . $SQ . ' } = ' . $SQ . $arcInfo . $SQ . ';' . "\n";

            print FP_INFO_FILE $line;
        }

        print FP_INFO_FILE '$rval = 1;' . "   # Have to make the perl require statement happy\n";

        close(FP_INFO_FILE);

    }          # End of FOOTPRINT_BLOCK:

    #***************************************************************************
    #
    # All done
    #
    #***************************************************************************

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* gen_footprint_info_file: Ending\n");
    log_msg("*\n");
    log_msg("*    Status = $statusOut\n");
    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");

    return ( $statusOut );

}   ## end -- gen_footprint_info_file

#***************************************************************************
#
# substr_right() - Perform sub-string from right side of the string
#                  If the input string is longer then the specified length,
#                  then the string will be truncated from the left side by
#                  the length + 1 and an asterix will be appended to the
#                  begining of the string.  If the string is not longer then
#                  length + 1 then a space will be appended to the begining of
#                  the string then the output string is set to the input
#                  string.
#
#
# Input Parameters:
#
#     $stringIn   = String to be substr'ed
#     $lenIn      = Length
#
# Output Parameters:
#
#     $stringOut = Sub-string
#
#***************************************************************************

sub substr_right
{
    my( $stringIn )  = shift;
    my( $lenIn )     = shift;

    my( $stringOut ) = "";

    my( $len )       = 0;
    my( $start )     = 0;

    $len = length( $stringIn ) + 1;

    if ( $len > $lenIn )
    {
        $start   = $len - $lenIn;

        $stringOut = "*" . substr( $stringIn, $start );
    }
    else
    {
        $stringOut = $stringIn;
    }

    return ( $stringOut );

}   ## end -- substr_right

#***************************************************************************
#
# verify_env_var_exists   -- Verify whether all the environment variables are SET
#                            if not set then return -1 and a list of environment
#                            variables that are not SET
#
# Input Parameters:
#
#    None
#
# Output Parameters:
#
#    $statusOut            = Return code
#                            0 = Successful
#                            1 = Failed
#    @undefListOfEnvVarOut = List of undefined environment variables
#
#***************************************************************************

sub verify_env_var_exists
{
    my( @undefListOfEnvVarOut ) = ();       # for storing and returning list of undefined env vars
    my( $statusOut )            = 0;        # 0, if successful, otherwise 1

    my( $tempValue )            = "";       # for storing the environment variables
    my( $env_var_name )         = "";
    my( $rval )                 = 0;

    ENV_VAR_BLOCK:
    foreach $env_var_name ( @ENV_VAR_LIST )
    {
        next ENV_VAR_BLOCK if ( $env_var_name eq 'VMMBI01' );

        $tempValue = $ENV{ $env_var_name };

        if ( $tempValue eq "" )
        {
            next ENV_VAR_BLOCK if ( $env_var_name gt 'VMMBI18' );

            push( @undefListOfEnvVarOut, $env_var_name );
            $statusOut++;

            next ENV_VAR_BLOCK;
        }

        if ( $env_var_name eq 'VMMBI18' )
        {
            $ENV_VAR_LIST_LEN = 18;

            next ENV_VAR_BLOCK;
        }

        if ( $env_var_name eq 'VMMBI24' )
        {
            $ENV_VAR_LIST_LEN = 24;

            next ENV_VAR_BLOCK;
        }
    }          # End of ENV_VAR_BLOCK:

    return ( $statusOut, @undefListOfEnvVarOut );

}   ## end -- verify_env_var_exists

#***************************************************************************
#
# get_VMMBI_env_vars -- Decrypt and parse the VMMBI environment variables
#
#
# Input Parameters:
#
#    None
#
# Output Parameters:
#
#    $statusOut = Return status
#                 0 = Successful
#                 1 = Failed
#
#***************************************************************************

sub get_VMMBI_env_vars
{
    my( $statusOut )           = 0;

    my( $var )                 = "";
    my( $cmd )                 = "";
    my( $var_name )            = "";
    my( $ds )                  = '$';
    my( $rval )                = 0;

    GET_ENV_VAR_BLOCK:
    {
        log_msg("\n");
        log_msg("*********************************************************\n");
        log_msg("*\n");
        log_msg("* get_VMMBI_env_vars: Starting\n");
        log_msg("*\n");
        log_msg("*********************************************************\n");
        log_msg("\n");

        for ( my $i = 0; $i < $ENV_VAR_LIST_LEN; $i++ )
        {
            $var = @ENV_VAR_LIST[ $i ];
            $var_name  = "$var" . "_ENCRYPTED";
            $cmd = "$ds$var_name = $ds" . "ENV{$var}";  # Create Perl statement to be executed
            eval $cmd;                                  # Execute the Perl statement
        }

        #***************************************************************************
        #
        # Decrypt the environment variables
        #
        #***************************************************************************

        log_msg("\n");
        log_msg("*********************************************************\n");
        log_msg("*\n");
        log_msg("* get_VMMBI_env_vars: Decrypting environment variables\n");
        log_msg("*\n");
        log_msg("*    Status = $statusOut\n");
        log_msg("*\n");
        log_msg("*********************************************************\n");
        log_msg("\n");

        $rval = decrypt_env_variables();

        $statusOut += $rval;

        #***************************************************************************
        #
        # Process the above environment variables. If any of the vairables are
        # undefined, then the return status will be 1 and the reason will be printed
        # out to the user for taking appropriate action. This will also exit the
        # program.
        #
        #***************************************************************************

        log_msg("\n");
        log_msg("*********************************************************\n");
        log_msg("*\n");
        log_msg("* get_VMMBI_env_vars: Parsing environment variables and\n");
        log_msg("*                 converting them to program variables\n");
        log_msg("*\n");
        log_msg("*    Status = $statusOut\n");
        log_msg("*\n");
        log_msg("*********************************************************\n");
        log_msg("\n");

        $rval = process_env_var();

        $statusOut += $rval;

    }        # End of GET_ENV_VAR_BLOCK:

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* get_VMMBI_env_vars: Ending\n");
    log_msg("*\n");
    log_msg("*    Status = $statusOut\n");
    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");

    return( $statusOut );

}   ## end -- get_VMMBI_env_vars()

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
# display_usage   --   To display the usage of the program. If no command
#                      line arguments passed, then this function will be
#                      called to help the user understand the usage of
#                      this program
#
# Input Parameters:
#
#   None
#
# Output Parameters:
#
#   None
#
#***************************************************************************

sub display_usage
{
    log_msg( "\n PERL <PROGRAM NAME> <OPTIONS>\n");
    log_msg( "where <OPTIONS> = -newDir for building in a new directory\n");
    log_msg( "                = -oldDir for building in a already existing directory\n");

    return;

}   ## end -- display_usage

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
# decrypt_env_variables   --  Decrypt environment variables
#
# Input Parameters:
#
#    None
#
# Output Parameters:
#
#    $statusOut       = Status indicating whether it was processed/not
#                       0 = indicates all variables were processed
#                       1 = indicates failed to process it
#
#***************************************************************************

sub decrypt_env_variables
{
    my( $statusOut )         = 0;

    my( $VarName )           = "";
    my( $DecryptedValue )    = "";
    my( $EncryptedVarName )  = "";
    my( $DecryptedVarName )  = "";
    my( $rval )              = 0;

    #************************************************************
    #
    # Process the encrypted environment variable $VMMBInn_ENCRYPTED
    # and then store it into the decrypted variable $VMMBInn_DECRYPTED
    #
    #*************************************************************

    for ( my $i = 0; $i < $ENV_VAR_LIST_LEN; $i++ )
    {
        $VarName = @ENV_VAR_LIST[ $i ];
        $EncryptedVarName  = "$VarName" . "_ENCRYPTED";
        $DecryptedVarName  = "$VarName" . "_DECRYPTED";

        #no strict "refs";     # Commented out as a temporary fix for Perl Version 5.8.6

        ( $rval, $DecryptedValue ) = get_decrypted_str( $$EncryptedVarName );

        $$DecryptedVarName = $DecryptedValue;

        #use strict "refs";     # Commented out as a temporary fix for Perl Version 5.8.6

        $statusOut += $rval;

    }

    return( $statusOut );

}   ## end -- decrypt_env_variables

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
# process_env_var   -- To process the environment variables and store it into
#                      program parameters for further usage
#
# Input Parameters:
#
#    None
#
# Output Parameters:
#
#    $statusOut = Return code
#                 0 = successful
#                 1 = failure
#
#***************************************************************************

sub process_env_var
{
    my( $statusOut )         = 0;

    my( $VarName )           = "";
    my( $ConvertedValue )    = "";
    my( $DecryptedVarName )  = "";
    my( $DecryptedVarValue ) = "";
    my( $ProgramVarName )    = "";
    my( $rval )              = 0;
    my( @parms )             = ();


    #************************************************************
    #
    # Spin through the decrypted environment variables and parse
    # them to remove the length prefixs (verify the length of the
    # variables) and to extract those env variable lines which
    # have multiple parameters.
    #
    #*************************************************************

    PROCESS_LOOP:
    for ( my $i = 0; $i < $ENV_VAR_LIST_LEN; $i++ )
    {
        $VarName           = @ENV_VAR_LIST[ $i ];
        $DecryptedVarName  = "$VarName" . "_DECRYPTED";

        $VarName           = @PROGRAM_VAR_LIST[ $i ];
        $ProgramVarName    = $VarName;

        #no strict "refs";     # Commented out as a temporary fix for Perl Version 5.8.6

        $DecryptedVarValue = $$DecryptedVarName;

        #use strict "refs";     # Commented out as a temporary fix for Perl Version 5.8.6

        ( $rval, @parms )  = parse_VMMBI_Parm_Line( $DecryptedVarValue, $DecryptedVarName );
        $statusOut        += $rval;

        if ( $DecryptedVarName eq "VMMBI16_DECRYPTED" )
        {
            $VM_USER_ID             = @parms[ 0 ];
            $VM_PASSWORD            = @parms[ 1 ];
            $MB_PROJECT_NAME_02     = @parms[ 2 ];
            $MB_SEARCH_PATH_NAME_02 = @parms[ 3 ];
            $MB_BUILD_JOB_NAME_02   = @parms[ 4 ];

            next PROCESS_LOOP;
        }

        if ( $DecryptedVarName eq "VMMBI20_DECRYPTED" )
        {
            $BUILD_JOB_VM_VERSION    = @parms[ 0 ];
            $BUILD_JOB_VM_BUILD_NUM  = @parms[ 1 ];

            next PROCESS_LOOP;
        }

        #no strict "refs";     # Commented out as a temporary fix for Perl Version 5.8.6

        $$ProgramVarName = @parms[ 0 ];

        #use strict "refs";     # Commented out as a temporary fix for Perl Version 5.8.6

    }

    #************************************************************
    #
    # Make sure the project path is all forward slashes
    #
    #*************************************************************

    $VM_PROJECT_NAME_IN =~ s!\\!/!g;

    #************************************************************
    #
    # Check and convert yes/no variables to $TRUE/$FALSE values
    #
    #*************************************************************

    ( $rval, $ConvertedValue )    = convert_yes_no( $FOOTPRINTING_IN, "footprinting" );
    $FOOTPRINTING_IN              = $ConvertedValue;
    $statusOut                   += $rval;

    ( $rval, $ConvertedValue )    = convert_yes_no( $INCLUDE_SUBPROJECTS_IN, "include sub-projects" );
    $INCLUDE_SUBPROJECTS_IN       = $ConvertedValue;
    $statusOut                   += $rval;

    ( $rval, $ConvertedValue )    = convert_yes_no( $ENABLE_PROJECT_PROCESSING_IN, "enable project processing" );
    $ENABLE_PROJECT_PROCESSING_IN = $ConvertedValue;
    $statusOut                   += $rval;

    #************************************************************
    #
    # If project processing is not set then we change the build
    # mode to "Targets" so that all we do is get the target files
    # stored in Version Manager
    #
    #*************************************************************

    if ( ! $ENABLE_PROJECT_PROCESSING_IN )
    {
        $BUILD_MODE_IN = "Targets";
    }

    return ( $statusOut );

}   ## end -- process_env_var

#***************************************************************************
#
# log_env_vars -- Write encrypted and decrypted and parsed env vars to the log
#
#
# Input Parameters:
#
#    None
#
# Output Parameters:
#
#    None
#
#***************************************************************************

sub log_env_vars
{
    my( $EnvVarName )                       = "";
    my( $EnvVarName2 )                      = "";
    my( $PgmVarName )                       = "";
    my( $EncryptedVarName )                 = "";
    my( $EncryptedVarName2 )                = "";
    my( $DecryptedVarName )                 = "";
    my( $DecryptedVarName2 )                = "";
    my( $ProgramVarName )                   = "";
    my( $ProgramVarName2 )                  = "";

    LOG_ENV_VARS_BLOCK:
    for ( my $i = 0; $i < $ENV_VAR_LIST_LEN; $i++ )
    {
        $EnvVarName        = @ENV_VAR_LIST[ $i ];
        $EncryptedVarName  = "$EnvVarName" . "_ENCRYPTED";
        $EncryptedVarName2 = sprintf( "%-28.28s ", "Encrypted");

        $EnvVarName        = @ENV_VAR_LIST[ $i ];
        $DecryptedVarName  = "$EnvVarName" . "_DECRYPTED";
        $DecryptedVarName2 = sprintf( "%-28.28s ", "Decrypted");

        $PgmVarName        = @PROGRAM_VAR_LIST[ $i ];
        $ProgramVarName    = $PgmVarName;
        $ProgramVarName2   = sprintf( "%-28.28s ", "Parsed");


        if ( $PgmVarName eq "VM_USER_ID_DUMMY_IN" )
        {
            $EnvVarName2       = sprintf( "%-34.34s %-100.100s", "$EnvVarName ===>", $PgmVarName );

            #no strict "refs";     # Commented out as a temporary fix for Perl Version 5.8.6

            log_msg("*\n");
            log_msg("*       $EnvVarName2\n");
            log_msg("*          $EncryptedVarName2" . " = $$EncryptedVarName\n");
            log_msg("*          $DecryptedVarName2" . " = XXXXXXXX\n");
            log_msg("*          Parsed:\n");

            #use strict "refs";     # Commented out as a temporary fix for Perl Version 5.8.6

            log_msg("*             VM PDB Path                = $VM_PDB_PATH\n");
            log_msg("*             VM User ID                 = $VM_USER_ID\n");
            log_msg("*             VM Password                = XXXXXXXX\n");
            log_msg("*             MB Project Name 02         = $MB_PROJECT_NAME_02\n");
            log_msg("*             MB Serach Path Name 02     = $MB_SEARCH_PATH_NAME_02\n");
            log_msg("*             MB Build Job Name 02       = $MB_BUILD_JOB_NAME_02\n");

            next LOG_ENV_VARS_BLOCK;

        }

        if ( $PgmVarName eq "VM_VERSION_AND_BUILD_NUM_IN" )
        {
            $EnvVarName2       = sprintf( "%-34.34s %-100.100s", "$EnvVarName ===>", $PgmVarName );

            #no strict "refs";     # Commented out as a temporary fix for Perl Version 5.8.6

            log_msg("*\n");
            log_msg("*       $EnvVarName2\n");
            log_msg("*          $EncryptedVarName2" . " = $$EncryptedVarName\n");
            log_msg("*          $DecryptedVarName2" . " = $$DecryptedVarName\n");
            log_msg("*          Parsed:\n");

            #use strict "refs";     # Commented out as a temporary fix for Perl Version 5.8.6

            log_msg("*             Build Job VM Version       = $BUILD_JOB_VM_VERSION\n");
            log_msg("*             Build Job VM Build Num     = $BUILD_JOB_VM_BUILD_NUM\n");

            next LOG_ENV_VARS_BLOCK;
        }

        $EnvVarName2       = sprintf( "%-34.34s %-60.60s", "$EnvVarName ===>", $PgmVarName);

        #no strict "refs";     # Commented out as a temporary fix for Perl Version 5.8.6

        log_msg("*\n");
        log_msg("*       $EnvVarName2\n");
        log_msg("*          $EncryptedVarName2" . " = $$EncryptedVarName\n");
        log_msg("*          $DecryptedVarName2" . " = $$DecryptedVarName\n");
        log_msg("*          $ProgramVarName2"   . " = $$ProgramVarName\n");

        #use strict "refs";     # Commented out as a temporary fix for Perl Version 5.8.6

    }          # End of LOG_ENV_VARS_BLOCK:

    return;

}   ## end -- log_env_vars()

#***************************************************************************
#
# convert_yes_no --
#
# Input Parameters:
#
#    ValueIn       = yes/no value to be converted
#    ValueTitleIn  = Title of value used for error message
#
# Output Parameters:
#
#    $statusOut = Return code
#                 0 = successful
#                 1 = failure
#    $ValueOut  = Converted value
#                 yes = $TRUE
#                 no  = $FALSE
#
#***************************************************************************

sub convert_yes_no
{
    my( $ValueIn )      = shift;
    my( $ValueTitleIn ) = shift;

    my( $statusOut )    = 0;
    my( $ValueOut )     = $ValueIn;


    if ( ( $ValueIn eq "yes" ) or ( $ValueIn eq "YES" ) )
    {
        $ValueOut = $TRUE;
    }
    elsif ( ( $ValueIn eq "no" ) or ( $ValueIn eq "NO" ) )
    {
        $ValueOut = $FALSE;
    }
    else
    {
        log_error_msg( "VMMBI3540: convert_yes_no: Error: Invalid $ValueTitleIn value, expected 'yes/no', actual is: $ValueIn\n" );
        $statusOut++;
    }

    return ( $statusOut, $ValueOut );

}   ## end -- convert_yes_no

#***************************************************************************
#
# update_DST_adjustments -- Update the daylight saving time adjustment
#                           factor for all files in the archive information
#                           list
#
# Description:
#
#    The update_DST_adjustments() subroutine will update each archive info
#    object with the calculated daylight saving time adjustiment. We only
#    need the adjust when the build directory is located on a Windows NTFS
#    disk partition.
#
# Input Parameters:
#
#     $InfoListIn  = Array of archive info list objects
#
# Output Parameters:
#
#     $statusOut   = Return code
#                    0 = successful
#                    1 = failure
#
#***************************************************************************

sub update_DST_adjustments
{
    my( $InfoListIn )           = shift;

    my( $statusOut)             = 0;

    my( @InfoList )             = @$InfoListIn;

    my( $VMMBI_DST_Pgm )        = "";                       # Path and program to the daylight saving time program
    my( $cmd )                  = "";                       # The command line to the C application
    my( $DST_FileName )         = "";
    my( $DST_CmdParms )         = "";

    my( $iInfoListLen )         = 0;
    my( $DST_DateModifiedUTC )  = 0;
    my( $DST_AdjustFactor )     = 0;
    my( $InfoListCount )        = 0;
    my( $ArchiveLastModTime )   = 0;

    my( @words )                = ();
    my( @lines )                = ();
    my( $line )                 = "";

    #************************************************************************
    #
    # Get the drive, path and name of the VMMBI_DST.exe input file and then
    # delete any existing one before creating it
    #
    #************************************************************************

    $DST_FileName = $BUILD_DIRECTORY.$OUR_SLASH."VMMBI_DST_IN.txt";

    unlink( $DST_FileName );

    log_msg( "\n" );
    log_msg( "*********************************************************\n" );
    log_msg( "*\n" );
    log_msg( "* update_DST_adjustments: Starting\n" );
    log_msg( "*\n" );
    log_msg( "*    W32 flag              = $IS_W32\n" );
    log_msg( "*    Current OS            = $CURRENT_OS\n" );
    log_msg( "*    Build dir file system = $BUILD_DIRECTORY_FILE_SYSTEM\n" );    
    log_msg( "*\n" );
    log_msg( "*********************************************************\n" );
    log_msg( "\n");

    DST_BLOCK:
    {
        #************************************************************************
        #
        # We only need to perform the DST adjustment when the build directory is
        # located on a Windows NTFS disk partition.  If it is not then we skip 
        # this process which leaves the DST adjustment factor set to zero.
        #
        #************************************************************************

        last DST_BLOCK if ( ! $IS_W32 );                                  # We dont perform the DST adjustment on UNIX
        last DST_BLOCK if ( $BUILD_DIRECTORY_FILE_SYSTEM ne "NTFS" );     # We only perform the DST adjustment for Windows NTFS

        #************************************************************************
        #
        # Get the archive information list length and make sure its valid
        #
        #************************************************************************

        $iInfoListLen = scalar( @InfoList );

        if ( $iInfoListLen <= 0 )
        {
            log_error_msg( "VMMBI3600: update_DST_adjustments: ERROR: The daylight saving time adjustment program returned no output\n" );
            $statusOut++;
            # SCR 14827 - replaced PROCESSING_BLOCK with DST_BLOCK
            last DST_BLOCK;
        }

        #************************************************************************
        #
        # Open the VMMBI DST program input file for output
        #
        #************************************************************************

        open(DST_FILE, "> $DST_FileName");

        #************************************************************************
        #
        # Loop through the archive info list and write a input record for each
        # object.  The record will contain the UTC last modified time
        #
        #************************************************************************

        INPUT_DATA_LOOP:
        for ( my $i = 0; $i < $iInfoListLen; $i++ )
        {
            $element = @InfoList[ $i ];

            $ArchiveLastModTime = $element->get_last_modified_UTC();

            print DST_FILE "$ArchiveLastModTime\n";

        }          # End of INPUT_DATA_LOOP:

        close(DST_FILE);

        last DST_BLOCK if ( $statusOut );

        #************************************************************************
        #
        # Get the VMMBI DST program path and make sure it exists
        #
        #************************************************************************

        $VMMBI_DST_Pgm = $PROGRAM_DIR . $OUR_SLASH . $VMMBI_DST_PGM_NAME;

        if ( ! -f $VMMBI_DST_Pgm )
        {
            log_error_msg( "VMMBI3620: update_DST_adjustments: Error: The VMMBI DST program is not available. '$VMMBI_DST_Pgm'\n" );
            $statusOut++;
            last DST_BLOCK;
        }

        #************************************************************************
        #
        # We must put the double quotes around "$VMMBI_DST_Pgm" or its croak
        # time when we get a path with spaces in it.
        #
        #************************************************************************

        if ( $DEBUG )
        {
            $DST_CmdParms = " -v";
        }

        # SCR14423 - Added double quotes around DST file name
        # SCR14424 - Changed to use call_cmd instead of open so that we can get the correct return code

        $cmd = $DQ . $VMMBI_DST_Pgm . $DQ . $DST_CmdParms . " -f " . $DQ . $DST_FileName . $DQ;

        ( $rval, @lines ) = call_cmd( $cmd,               # Command string
                                      "",                 # No string to strip from the command
                                      $DEBUG              # Log standard output if in debug mode
                                    );

        if ( $rval )
        {
            log_error_msg( "VMMBI3630: update_DST_adjustments: Error: Getting DST adjustments: status = $rval\n" );
            $statusOut++;
            last DST_BLOCK;
        }

        #************************************************************************
        #
        # Spin through the output data and update the archive info list
        # objects with the DST adjustment factor
        #
        #************************************************************************

        if ( $DEBUG )
        {
            log_msg( "\n");
            log_msg( "Daylight Saving Time Adjustment Program Output:\n");
            log_msg( "\n");
        }

        OUTPUT_DATA_LOOP:
        for ( my $i = 0; $i < scalar(@lines); $i++ )
        {
            $line  = @lines[ $i ];

            #***************************************************************************
            #
            # Parse out the information from the VMMBI DST program output
            #
            # The DST adjustment factor is returned in minutes, positive or negative
            #
            #***************************************************************************

            @words = split( " ", $line );

            if ( @words[0] eq "VMMBI_DST_DATA" )
            {
                $DST_DateModifiedUTC   = @words[1];
                $DST_AdjustFactor      = @words[2];                 # Returned in minutes

                $DST_AdjustFactor      = $DST_AdjustFactor * 60;    # Convert to seconds for UTC

                $element = @InfoList[ $InfoListCount ];

                $InfoListCount++;

                #***************************************************************************
                #
                # Get the last modified UTC time passed to VMMBI_DST.exe and returned from
                # VMMBI_DST.exe.  Make sure they match up.
                #
                #***************************************************************************

                $ArchiveLastModTime = $element->get_last_modified_UTC();

                if ( $ArchiveLastModTime != $DST_DateModifiedUTC )
                {
                    log_error_msg( "VMMBI3640: update_DST_adjustments: Error: The file last modified time and " .
                                   "DST adjustment factor program last modified time do not match: " .
                                   "File time = $ArchiveLastModTime, DST pgm time = $DST_DateModifiedUTC\n" );
                    $statusOut++;
                    last DST_BLOCK;
                }

                #***************************************************************************
                #
                # Update the daylight saving time adjust vactor returned from VMMBI_DST.exe
                # in the associated archive information object
                #
                #***************************************************************************

                $element->set_DST_UTC_factor( $DST_AdjustFactor );
            }
        }          # End of OUTPUT_DATA_LOOP:
    }          # End of DST_BLOCK:

    log_msg( "\n" );
    log_msg( "*********************************************************\n" );
    log_msg( "*\n" );
    log_msg( "* update_DST_adjustments: Ending\n" );
    log_msg( "*\n" );
    log_msg( "*    Status = $statusOut\n" );
    log_msg( "*\n" );
    log_msg( "*********************************************************\n" );
    log_msg( "\n");

    return ( $statusOut );

}   ## end -- update_DST_adjustments

#***************************************************************************
#
# get_archive_info_list -- Get the archive information list
#
# Input Parameters:
#
#    None
#
# Output Parameters:
#
#    $statusOut           = Return code
#                           0 = successful
#                           1 = failure
#    @ArchiveInfoListOut  = Converted value
#
#***************************************************************************

sub get_archive_info_list
{
    my( $statusOut )           = 0;
    my( @ArchiveInfoListOut )  = ();

    my( $GetRevInfoPath )      = "";
    my( $PcliCmd )             = "";
    my( $rval )                = 0;
    my( @ListArchiveInfo )     = ();
    my( $iLoopLen )            = 0;
    my( $len )                 = 0;
    my( $line )                = "";
    my( @SplitDTG )            = ();
    my( $WorkFileName )        = "";
    my( $ArchiveName )         = "";
    my( $Revision )            = "";
    my( $DateModified )        = "";
    my( $DateCheckedIn )       = "";
    my( $UTC_DateModified )    = 0;
    my( $ArchiveInfoObject )   = "";
    my( $UserIdPassword )      = "";
    my( $PDB_Path )            = "";
    my( $VersionPromo )        = "";
    my( $SubProjects )         = "";
    my( $ProjectName )         = "";
    my( $NoWarnings )          = "";
    my( $DataLineID )          = "";
    my( $versionString )       = "";
    my( $buildNumString )      = "";
    my( $DisplayCmd )          = "";
    my( $count )               = "";
    my( @words )               = ();
    my( $pcliBannerHit )       = $FALSE;

    #***************************************************************************
    #
    # Set the database and PCLI file PATH. Also set the command lines
    #
    # All parameter values must be inclosed in double quotes in case they might
    # contain white space.
    #
    #***************************************************************************

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* get_archive_info_list: Starting\n");
    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");

    $PDB_Path       = " PR=$DQ$VM_PDB_PATH$DQ";
    $ProjectName    = " $DQ$VM_PROJECT_NAME_IN$DQ";
    $GetRevInfoPath = " -s$DQ$PROGRAM_DIR$OUR_SLASH$VMMBI_GETREVINFO_PCLI$DQ";
    $NoWarnings     = " NoWarn=True";

    if ( $VM_USER_ID ne "" )
    {
        $UserIdPassword = " ID=$DQ$VM_USER_ID$DQ";

        if ( $VM_PASSWORD ne "" )
        {
            $UserIdPassword = " ID=$DQ$VM_USER_ID:$VM_PASSWORD$DQ";
        }
    }

    #***************************************************************************
    #
    # Set version label or promotion group or defalut revision
    #
    # If neither the Label or Group parameters are specified then GetRevInfo
    # will process with the default revision (normally the tip) but it could be
    # a default version label.
    #
    #***************************************************************************

    if ( $SOURCE_SELECTION_TYPE_IN eq "label" )
    {
        $VersionPromo = " Label=$DQ$SOURCE_VERSION_PROMO_IN$DQ";
    }
    elsif ( $SOURCE_SELECTION_TYPE_IN eq "group" )
    {
        $VersionPromo = " Group=$DQ$SOURCE_VERSION_PROMO_IN$DQ";
    }

    if ( $INCLUDE_SUBPROJECTS_IN )
    {
        $SubProjects = " Recursive=True";
    }
    else
    {
        $SubProjects = " Recursive=False";
    }

    #***************************************************************************
    #
    # Assemble the command
    #
    #***************************************************************************

    $PcliCmd = "pcli run -xl "   .
               $GetRevInfoPath   .
               $PDB_Path         .
               $UserIdPassword   .
               $VersionPromo     .
               $NoWarnings       .
               $SubProjects      .
               $ProjectName      .
               "";

    PROCESSING_BLOCK:
    {
        last PROCESSING_BLOCK if ( $statusOut );

        #***************************************************************************
        #
        #  Suppress the user ID and password and print the command
        #
        #***************************************************************************

        $DisplayCmd = strip( $UserIdPassword, $PcliCmd, " XXXXXXXX" );

        log_msg( "get_archive_info_list: $DisplayCmd \n" );

        #***************************************************************************
        #
        # Execute a PCLI command to get the list of archive files for a particular project
        #
        # We can not check the return code from the actual command because pcli can
        # produce a non-zero return command from a wild card operation.  The is the
        # old VM wild card issue where you only get the return code of the last
        # file operated on.  If that one happened to not be a match then you would
        # not get a zero return code.
        #
        #***************************************************************************

        $rval = open(CMD, "$PcliCmd 2>&1 |" );

        if ( $rval )
        {
            @ListArchiveInfo = <CMD>;

            close(CMD);
        }
        else
        {
            log_error_msg( "VMMBI3640: get_archive_info_list: ERROR: The pcli command could not be executed.\n" );
            $statusOut++;
            last PROCESSING_BLOCK;
        }

        #***************************************************************************
        #
        # Get length of the output array, make sure its not zero and then print the
        # contents
        #
        #***************************************************************************

        $iLoopLen = scalar( @ListArchiveInfo );

        if ( $iLoopLen <= 0 )
        {
            log_error_msg( "VMMBI3680: get_archive_info_list: ERROR: The pcli command returned no output\n" );
            $statusOut++;
            last PROCESSING_BLOCK;
        }

        if ( $DEBUG )
        {
            log_msg("\n");
            log_msg("Archive Information List Program Output:\n");
            log_msg("\n");
        }

        for ( my $i = 0; $i < $iLoopLen; $i++ )
        {
            $line  = @ListArchiveInfo[ $i ];
            $count = sprintf "%02d", $i;

            if ( $DEBUG )
            {
                log_msg("   [ $count ] = $line");
            }
        }

        #***************************************************************************
        #
        #  Spin through the archive information and create an array of objects from it
        #
        #***************************************************************************

        if ( $DEBUG )
        {
            log_msg("\n");
            log_msg("Archive Information List Processing:\n");
            log_msg("\n");
        }

        I_LOOP:
        for ( my $i = 0; $i < $iLoopLen; $i++ )
        {
            $line  = @ListArchiveInfo[ $i ];
            $count = sprintf "%02d", $i;

            #***************************************************************************
            #
            # Parse out the information from the archive information pcli output line
            #
            #***************************************************************************

            @SplitDTG = split( "\"", $line );

            $DataLineID     = @SplitDTG[1];        # VMMBI_GetRevInfo.pl data line ID
            $WorkFileName   = @SplitDTG[3];        # Work file path and name relative to the specified project
            $ArchiveName    = @SplitDTG[5];        # Archive path and file name
            $Revision       = @SplitDTG[7];        # Revision number
            $DateModified   = @SplitDTG[9];        # Last modified date
            $DateCheckedIn  = @SplitDTG[11];       # Check in date

            #***************************************************************************
            #
            # Filter out non data lines
            #
            #***************************************************************************

            if ( $DataLineID ne "VMMBI_GetRevInfo_ID" )
            {
                @words = split( " ", $line );

                #***************************************************************************
                #
                #  Check for pcli command failure
                #
                #     0        1         2
                #     16:09:36 Sep.15.03 [Error]
                #
                #***************************************************************************

                if ( lc( @words[2] ) eq "[error]" )
                {
                    log_error_msg( "VMMBI3700: get_archive_info_list: Error: Archive information pcli command failed:\n" );

                    for ( my $j = 0; $j < $iLoopLen; $j++ )
                    {
                        log_error_msg( "VMMBI3800: get_archive_info_list: Error:    [ $j ] = " . @ListArchiveInfo[ $j ] );
                    }

                    $statusOut++;
                    last I_LOOP;
                }

                #***************************************************************************
                #
                # Check for pcli version number and build number
                #
                #     0      1         2       3       4        5        6      7    8
                #     Serena ChangeMan Version Manager (PCLI)   v8.1.0.0 (Build 734) for Windows NT/80x86
                #
                #     0      1         2       3       4        5      6    7    8
                #     Merant Version   Manager (PCLI)  v8.0.0.0 (Build 374) for Windows/80x86
                #     PVCS   Version   Manager (PCLI)  v7.5.1.2 (Build 002) for Windows NT/80x86
                #
                #***************************************************************************


                VERSION_BLOCK:
                {
                    if ( lc( @words[2] ) eq "version" && lc( @words[3] ) eq "manager" )
                    {
                        $pcliBannerHit  = $TRUE;
                        $versionString  = lc( @words[5] );
                        $buildNumString = lc( @words[7] );
                        last VERSION_BLOCK; 
                    }

                    if ( lc( @words[1] ) eq "version" && lc( @words[2] ) eq "manager" )
                    {
                        $pcliBannerHit  = $TRUE;
                        $versionString  = lc( @words[4] );
                        $buildNumString = lc( @words[6] );
                        last VERSION_BLOCK; 
                    }
                }            # End of VERSION_BLOCK:

                if ( $pcliBannerHit )
                {
                    $PCLI_VERSION   = $versionString;
                    $PCLI_BUILD_NUM = $buildNumString;

                    if ( substr( $versionString, 0, 1 ) eq "v" )
                    {
                        $PCLI_VERSION = substr( $versionString, 1 );
                    }

                    $len = length( $buildNumString );

                    if ( substr( $buildNumString, $len - 1, 1 ) eq ")" )
                    {
                        $PCLI_BUILD_NUM = substr( $buildNumString, 0, $len - 1 );
                    }
                }

                next I_LOOP;
            }

            #***************************************************************************
            #
            # Get the UTC format of date modified, use it and the other archive information
            # to create a archive info object which we will add the the archive info array.
            #
            #***************************************************************************

            $UTC_DateModified = get_utc_from_string( $DateModified );

            if ( $DEBUG )
            {
                log_msg( "   [ $count ] Last Modified Date = $DateModified ===> $UTC_DateModified\n" );
            }

            $ArchiveInfoObject = ARCHIVE_INFO->new( $VM_PROJECT_NAME_IN,
                                                    $OUR_SLASH_ESCAPED,
                                                    $OTHER_SLASH_ESCAPED,
                                                    $WorkFileName,
                                                    $ArchiveName,
                                                    $Revision,
                                                    $DateModified,
                                                    $UTC_DateModified,
                                                    $DateCheckedIn
                                                  );

            #***************************************************************************
            #
            # A zero value for the object means that the constructor encountered an
            # error. We will not add the object but we will bump the error count.
            #
            #***************************************************************************

            if ( $ArchiveInfoObject == 0 )
            {
                $statusOut++;
                next I_LOOP;
            }

            #***************************************************************************
            #
            # Add the object to the base archive information array, we create other arrays
            # from the base array
            #
            #***************************************************************************

            push( @ArchiveInfoListOut, $ArchiveInfoObject );

        }          # End of I_LOOP:
    }          # End of PROCESSING_BLOCK:

    #***************************************************************************
    #
    # Make sure that we at least got the banner line from the pcli command
    #
    #***************************************************************************

    if ( ! $pcliBannerHit )
    {
        log_error_msg( "VMMBI3840: get_archive_info_list: Error: The pcli command banner line was not found.\n" );
        $statusOut++;
    }

    #***************************************************************************
    #
    # If this is W32 get daylight saving time adjustment factors for all files
    # in the archive information list
    #
    # The update_DST_adjustments() subroutine will update each archive info object
    # with the calculated daylight saving time adjustiment
    #
    #***************************************************************************

    if ( $IS_W32 )
    {
        $rval = update_DST_adjustments( \@ArchiveInfoListOut
                                      );
        $statusOut += $rval

    }

    log_msg( "\n" );
    log_msg( "*********************************************************\n" );
    log_msg( "*\n" );
    log_msg( "* get_archive_info_list: Ending\n" );
    log_msg( "*\n" );
    log_msg( "*    VM PCLI Version      = $PCLI_VERSION\n" );
    log_msg( "*    VM PCLI Build Number = $PCLI_BUILD_NUM\n" );
    log_msg( "*\n" );
    log_msg( "*    Status = $statusOut\n" );
    log_msg( "*\n" );
    log_msg( "*********************************************************\n" );
    log_msg( "\n");

    return ( $statusOut, @ArchiveInfoListOut );

}   ## end -- get_archive_info_list

#***************************************************************************
#
# get_build_dir -- Get the build directory from the evironment variable,
#                  store it in a string, and return it to the caller
#                  If the directory does not exist, create a new one and
#                  return it to the user. This directory will be used as
#                  default directoy for building the project
#
# Input Parameters:
#
#    $buildDirIn   = Build directory from env var
#
# Output Parameters:
#
#    $statusOut    = Return code
#                    0 = successful
#                    1 = failure
#    $buildDirOut  = The build directory retrieved from
#                    environment variable VMMBI01
#
#***************************************************************************

sub get_build_dir
{
    my( $buildDirIn )               = shift;

    my( $statusOut )                = 0;         # The return status of the sub routine
    my( $buildDirOut )              = "";        # The build directory to be created

    my( $BuildDirExists )           = $FALSE;    # Set to build directory not existing
    my( $rval )                     = 0;
    my( $InvalidDir )               = 0;
    my( @words )                    = ();
    my( $cmd )                      = "";
    my( $TempBuildDir )             = "";
    my( $word0 )                    = "";
    my( $word1 )                    = "";
    my( $word2 )                    = "";
    my( $word3 )                    = "";
    my( $word4 )                    = "";
    my( $word5 )                    = "";

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* get_build_dir: Starting\n");
    log_msg("*\n");
    log_msg("*    Build directory from env var = $buildDirIn\n");    
    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");

    log_msg("get_build_dir: Selecting and initializing the build directory\n");

    BUILD_DIR_BLOCK:
    {
        #***************************************************************************
        #
        # Resolve any environment variables embeded in the build dir
        #
        #***************************************************************************

        ( $rval, $buildDirOut ) = expand_path( $buildDirIn );

        if ( $rval )
        {
            $statusOut++;
            $buildDirOut = $DEFAULT_BUILD_DIR;
            last BUILD_DIR_BLOCK;
        }

        #***************************************************************************
        #
        # Decide whether we are going to use the default build dir or not
        #
        #***************************************************************************

        if ( $buildDirOut eq "" )
        {
            log_error_msg( "VMMBI3900: get_build_dir: Error: The build directory was not specified\n" );
            log_error_msg( "VMMBI4000: get_build_dir: Error: The default build directory will be used to create the log." );
            $statusOut++;
            $buildDirOut = $DEFAULT_BUILD_DIR;
            last BUILD_DIR_BLOCK;
        }
        else
        {
            $buildDirOut = $buildDirIn;
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

        #***************************************************************************
        #
        # Test to see if the build directroy exists
        #
        #***************************************************************************

        if ( -e $buildDirOut )
        {
            $BuildDirExists = $TRUE;
        }

        #***************************************************************************
        #
        # If build mode is new and the directory is already available then display
        # appropriate message and exit the program
        #
        #***************************************************************************

        if ( $BuildDirExists && ( lc( $BUILD_MODE_IN ) eq "new" ) )
        {
            log_error_msg( "VMMBI4100: get_build_dir: Error: A a new build was requested but the build directory already exists\n" );

            $statusOut++;
            last BUILD_DIR_BLOCK;
        }

        #***************************************************************************
        #
        # As best we can verify that we are not executing in a system directory
        #
        #***************************************************************************

        $TempBuildDir =  $buildDirOut;
        $TempBuildDir =~ s!\\!/!g;

        @words = split( "/", $TempBuildDir );
        $word0 = lc( @words[0] );
        $word1 = lc( @words[1] );
        $word2 = lc( @words[2] );
        $word3 = lc( @words[3] );
        $word4 = lc( @words[4] );
        $word5 = lc( @words[5] );


        CHECK_BUILD_DIR_BLOCK:
        {
            #***************************************************************************
            #
            # Check windows build dir
            #
            #***************************************************************************

            if ( $IS_W32 )
            {
                #***************************************************************************
                #
                # Check for drive letter build dir path
                #
                #***************************************************************************

                if ( substr( $word0, 1, 1 ) eq ':'  )
                {
                    if ( $word1 eq ""  )
                    {
                        $InvalidDir = 1;
                        last CHECK_BUILD_DIR_BLOCK;
                    }

                    if ( $word1 eq "windows" )
                    {
                        $InvalidDir = 2;
                        last CHECK_BUILD_DIR_BLOCK;
                    }

                    if ( $word1 eq "win32" )
                    {
                        $InvalidDir = 3;
                        last CHECK_BUILD_DIR_BLOCK;
                    }

                    if ( $word1 eq "winnt" )
                    {
                        $InvalidDir = 4;
                        last CHECK_BUILD_DIR_BLOCK;
                    }

                    if ( $word1 eq "program files" )
                    {
                        $InvalidDir = 5;
                        last CHECK_BUILD_DIR_BLOCK;
                    }

                    if ( $word2 eq "system32" )
                    {
                        $InvalidDir = 6;
                        last CHECK_BUILD_DIR_BLOCK;
                    }
                }

                #***************************************************************************
                #
                # Check for UNC build dir path
                #
                #***************************************************************************

                elsif ( $word0 eq "" && $word1 eq "" && $word2 ne "" && $word3 ne ""  )   # //<server>/<vol>
                {
                    if ( $word4 eq "" )
                    {
                        $InvalidDir = 20;
                        last CHECK_BUILD_DIR_BLOCK;
                    }

                    if ( $word4 eq "windows" )
                    {
                        $InvalidDir = 21;
                        last CHECK_BUILD_DIR_BLOCK;
                    }

                    if ( $word4 eq "win32" )
                    {
                        $InvalidDir = 22;
                        last CHECK_BUILD_DIR_BLOCK;
                    }

                    if ( $word4 eq "winnt" )
                    {
                        $InvalidDir = 23;
                        last CHECK_BUILD_DIR_BLOCK;
                    }

                    if ( $word4 eq "system32" )
                    {
                        $InvalidDir = 24;
                        last CHECK_BUILD_DIR_BLOCK;
                    }

                    if ( $word4 eq "program files" )
                    {
                        $InvalidDir = 25;
                        last CHECK_BUILD_DIR_BLOCK;
                    }

                    if ( $word5 eq "windows" )
                    {
                        $InvalidDir = 26;
                        last CHECK_BUILD_DIR_BLOCK;
                    }

                    if ( $word5 eq "win32" )
                    {
                        $InvalidDir = 27;
                        last CHECK_BUILD_DIR_BLOCK;
                    }

                    if ( $word5 eq "winnt" )
                    {
                        $InvalidDir = 28;
                        last CHECK_BUILD_DIR_BLOCK;
                    }

                    if ( $word5 eq "system32" )
                    {
                        $InvalidDir = 29;
                        last CHECK_BUILD_DIR_BLOCK;
                    }

                    if ( $word5 eq "program files" )
                    {
                        $InvalidDir = 30;
                        last CHECK_BUILD_DIR_BLOCK;
                    }
                }

                #***************************************************************************
                #
                # Path is not drive letter or UNC
                #
                #***************************************************************************

                else
                {
                    $InvalidDir = 40;
                    last CHECK_BUILD_DIR_BLOCK;

                }
            }

            #***************************************************************************
            #
            # Check UNIX build dir
            #
            #***************************************************************************

            if ( $IS_UNIX )
            {
                if ( $word0 ne "" )                      # Doesnt start with slash
                {
                    $InvalidDir = 60;
                    last CHECK_BUILD_DIR_BLOCK;
                }

                if ( $word1 eq "" )                      # Slash only
                {
                    $InvalidDir = 61;
                    last CHECK_BUILD_DIR_BLOCK;
                }

                if ( substr( $word1, 0, 4 ) eq "home" && $word2 eq "" )  # Specified a home directory
                {
                    $InvalidDir = 62;
                    last CHECK_BUILD_DIR_BLOCK;
                }

                if ( $word1 eq "bin"  )
                {
                    $InvalidDir = 63;
                    last CHECK_BUILD_DIR_BLOCK;
                }

                if ( $word1 eq "dev"  )
                {
                    $InvalidDir = 64;
                    last CHECK_BUILD_DIR_BLOCK;
                }

                if ( $word1 eq "etc"  )
                {
                    $InvalidDir = 65;
                    last CHECK_BUILD_DIR_BLOCK;
                }

                if ( $word1 eq "lib"  )
                {
                    $InvalidDir = 66;
                    last CHECK_BUILD_DIR_BLOCK;
                }

                if ( $word1 eq "mnt"  )
                {
                    $InvalidDir = 67;
                    last CHECK_BUILD_DIR_BLOCK;
                }

                if ( $word1 eq "opt"  )
                {
                    $InvalidDir = 68;
                    last CHECK_BUILD_DIR_BLOCK;
                }

                if ( $word1 eq "sbin"  )
                {
                    $InvalidDir = 69;
                    last CHECK_BUILD_DIR_BLOCK;
                }

                if ( $word1 eq "usr"  )
                {
                    $InvalidDir = 70;
                    last CHECK_BUILD_DIR_BLOCK;
                }

                if ( $word1 eq "var"  )
                {
                    $InvalidDir = 71;
                    last CHECK_BUILD_DIR_BLOCK;
                }

                if ( $word1 eq "vol"  )
                {
                    $InvalidDir = 72;
                    last CHECK_BUILD_DIR_BLOCK;
                }
            }
        }          # End of CHECK_BUILD_DIR_BLOCK:

        #***************************************************************************
        #
        # If we the build dir was rejected then send an error message and quit
        #
        #***************************************************************************

        if ( $InvalidDir > 0 )
        {
            log_error_msg( "VMMBI4200: get_build_dir: Error: Use of specified build directory is not allowed: Code = $InvalidDir\n" );
            $statusOut++;
            last BUILD_DIR_BLOCK;
        }

        #***************************************************************************
        #
        # If build mode is clean and the directory is already available then delete
        # the directory and re-create it.
        #
        #***************************************************************************

        if ( $BuildDirExists && ( lc( $BUILD_MODE_IN ) eq "clean" ) )
        {
            #***************************************************************************
            #
            # Delete the old build directory if it exists
            #
            #***************************************************************************

            if ( -d $buildDirOut )
            {
                $rval = run_rmtree( $buildDirOut );

                if ( $rval )
                {
                    log_error_msg( "VMMBI4300: get_build_dir: Error: Build directory deletion failed.\n" );
                    $statusOut += $rval;
                    last BUILD_DIR_BLOCK;
                }
            }

            #***************************************************************************
            #
            # Create the new build directory
            #
            # make_path() checks to make sure the directory was created
            #
            #***************************************************************************

            $rval = make_path( $buildDirOut );

            if ( $rval )
            {
                log_error_msg( "VMMBI4400: get_build_dir: Error: Build directory creation failed.\n" );
                $statusOut += $rval;
                last BUILD_DIR_BLOCK;
            }

            log_msg( "get_build_dir: Build directory has been deleted and recreated\n" );
        }
    }          # End of BUILD_DIR_BLOCK:

    #***************************************************************************
    #
    # If build directory is not found then create a new directory with requested
    # name or default name
    #
    #***************************************************************************

    if ( ! $BuildDirExists )
    {
        $rval = make_path( $buildDirOut );

        if ( $rval )
        {
            log_error_msg( "VMMBI4500: get_build_dir: Error: Build directory creation failed.\n" );
            $statusOut += $rval;
        }

        log_msg( "get_build_dir: The build directory is set to $buildDirOut\n" );
    }

    #***************************************************************************
    #
    # All done
    #
    #***************************************************************************

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* get_build_dir: Ending\n");
    log_msg("*\n");
    log_msg("*    Build Mode      = $BUILD_MODE_IN\n");
    log_msg("*    Build Directory = $buildDirOut\n");
    log_msg("*    Status          = $statusOut\n");
    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");

    return ( $statusOut, $buildDirOut );

}   ## end -- get_build_dir

#***************************************************************************
#
# get_build_dir_file_system -- Return the file system of the specified build
#                              directory.
#
# Input Parameters:
#
#    $buildDirIn             = Build directory
#
# Output Parameters:
#
#    $statusOut              = Return code
#                              0 = successful
#                              1 = failure
#    $buildDirFileSystemOut  = Build directory file system (FAT, FAT32, NTFS, 
#                              UFS, or null on error)
#
#***************************************************************************

sub get_build_dir_file_system
{
    my( $buildDirIn )               = shift;

    my( $statusOut )                = 0;         # The return status of the sub routine
    my( $buildDirFileSystemOut )    = "";        # The build directory file system

    my( $currentDir )               = "";        

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* get_build_dir_file_system: Starting\n");
    log_msg("*\n");
    log_msg("*    Build directory = $buildDirIn\n");
    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");

    BUILD_DIR_FS_BLOCK:
    {
        #***************************************************************************
        #
        # The following code will only work when the build directory is the current
        # directory
        #
        #***************************************************************************

        $currentDir = get_current_dir();

        if ( $currentDir ne $buildDirIn )
        {
            log_error_msg( "VMMBI4540: get_build_dir_file_system: Error: The current directory is not the build directory: $buildDirIn\n" );
            $statusOut++;
            last BUILD_DIR_FS_BLOCK;

        }

        if ( $IS_UNIX )
        {
            $buildDirFileSystemOut = "UFS";
            last BUILD_DIR_FS_BLOCK;
        }

        $buildDirFileSystemOut = Win32::FsType();                    # Get the windows file system type of the current directory
    }

    #***************************************************************************
    #
    # All done
    #
    #***************************************************************************

    log_msg("\n");
    log_msg("*********************************************************\n");
    log_msg("*\n");
    log_msg("* get_build_dir_file_system: Ending\n");
    log_msg("*\n");
    log_msg("*    Build dir file system = $buildDirFileSystemOut\n");
    log_msg("*    Status                = $statusOut\n");
    log_msg("*\n");
    log_msg("*********************************************************\n");
    log_msg("\n");

    return ( $statusOut, $buildDirFileSystemOut );

}   ## end -- get_build_dir_file_system

#***************************************************************************
#
# get_os_version -- Get the operating system version string
#
# Input Parameters:
#
#    None
#
# Output Parameters:
#
#    $osVersionStringOut  = Operating system version string
# 
# Notes:
#
#    (1) Win32::GetOSVersion() output
#
#        Variable  Description                   XP Example      W2K Example
#        ~~~~~~~~  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~  ~~~~~~~~~~~~~~  ~~~~~~~~~~~~~
#        $string   Arbitrary descriptive string  Service Pack 1  Service Pack 4
#        $major    Major version number          5               5
#        $minor    Minor version number          1               0
#        $build    Build number                  2600            2195
#        $id       Operating system ID           2               2
#                  0 = Win32's
#                  1 = Windows 95
#                  2 = Windows NT
#
#***************************************************************************

sub get_os_version
{
    my( $osVersionStringOut ) = ""; 

    my( $win32String )        = "";        
    my( $win32Major )         = "";        
    my( $win32Minor )         = "";        
    my( $win32Build )         = "";        
    my( $win32ID )            = "";        
    my( $windowsID )          = "";        

    OS_VERSION_BLOCK:
    {
        if ( $IS_UNIX )
        {
            $osVersionStringOut = "Unix";
            last OS_VERSION_BLOCK;
        }

        ( $win32String, $win32Major, $win32Minor, $win32Build, $win32ID ) = Win32::GetOSVersion();

        if ( $win32ID == 1 ) 
        { 
            $windowsID = " 95"; 
        }

        if ( $win32ID == 2 ) 
        { 
            $windowsID = " NT";

            if ( $win32Major == 5)
            {
                if ( $win32Minor == 0 )
                {
                    $windowsID = " 2000";
                }
                if ( $win32Minor == 1 )
                {
                    $windowsID = " XP";
                }
            }
        }

        $osVersionStringOut = "Windows$windowsID $win32Major.$win32Minor.$win32Build ($win32String)";
    }

    return ( $osVersionStringOut );

}   ## end -- get_os_version

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
# mapPDB() - Generat a PDB map object and add it to the PDB map array
#
# Input Parameters:
#
#    $fromIn           = String to be compared
#    $toIn             = String to be used to replace a thi
#    $CaseSensitiveIn  = Case sensitive comparision flag
#                        Perform case sensitive comparision     = cs, yes, 1
#                        Dont perform case sensitve comparision = anything else
#
# Output Parameters:
#
#    None
#
#***************************************************************************

sub mapPDB
{
    my( $fromIn )          = shift if @_  ;
    my( $toIn )            = shift if @_  ;
    my( $CaseSensitiveIn ) = shift if @_  ;

    my( $pdbMapObject )    = 0;
    my( $CaseSensitive )   = $FALSE;

    #***************************************************************************
    #
    # Decide whether this is a case sensitive comparision
    #
    #***************************************************************************

    if ( lc( $CaseSensitiveIn ) eq 'cs' )
    {
        $CaseSensitive = $TRUE;
    }
    elsif ( lc( $CaseSensitiveIn ) eq 'yes' )
    {
        $CaseSensitive = $TRUE;
    }
    elsif ( $CaseSensitiveIn == 1 )
    {
        $CaseSensitive = $TRUE;
    }

    $pdbMapObject = PDB_MAP->new( $fromIn,
                                  $toIn,
                                  $CaseSensitive,
                                );

    #***************************************************************************
    #
    # Add the object to the PDB map array
    #
    #
    # A zero value for the object means that the constructor encountered an
    # error. We will not add the object to the list.
    #
    #***************************************************************************

    if ( $pdbMapObject != 0 )
    {
        push( @PDB_MAP_LIST, $pdbMapObject );
    }

    return;

}   ## end -- mapPDB

#***************************************************************************
#
# map_PDB_path() - Map the specified path based on the PDB map array which
#                  came from the VMMBI_bldmake_PET_INI.pl file.
#
# Description:
#
#    This method loops through each entry in the PDB map array compairing the
#    from string to the begining of the specified input path.  When it gets
#    a hit it replaces the from string at the begining of the input path with
#    with the to string.  If the PDB map array specifies that the comparison
#    is to be make case sensitive then the input path and the from string are
#    compared as is.  If the comparison is not set to case sensitive then
#    both the input path and each from string are lowercased for the comparison.
#
# Input Parameters:
#
#    $pathIn           = Path to be mapped
#
# Output Parameters:
#
#    $pathOut          = The mapped path
#
#***************************************************************************

sub map_PDB_path
{
    my( $pathIn )          = shift;

    my( $pathOut )         = $pathIn;

    my( $PDB_MAP_Object )  = 0;
    my( $fromLen )         = 0;
    my( $fromString )      = "";
    my( $toString )        = "";
    my( $compPath )        = "";
    my( $compString )      = "";
    my( $caseSensitive )   = $TRUE;

    FOREACH_LOOP:
    foreach $PDB_MAP_Object( @PDB_MAP_LIST )
    {
        #***************************************************************************
        #
        # Get the from string to compare and the case sensitive comparison flag
        #
        #***************************************************************************

        $fromString    = $PDB_MAP_Object->{FromString};
        $fromLen       = $PDB_MAP_Object->{FromStringLen};
        $caseSensitive = $PDB_MAP_Object->{CaseSensitive};

        #***************************************************************************
        #
        # Adjust comparison strimgs based on case sensitivity flag
        #
        #***************************************************************************

        if ( $caseSensitive )
        {
            $compString = $fromString;
            $compPath   = substr( $pathIn, 0, $fromLen );
        }
        else
        {
            $compString = lc( $fromString );
            $compPath   = substr( $pathIn, 0, $fromLen );
            $compPath   = lc( $compPath );
        }

        #***************************************************************************
        #
        # Perform the comparison
        #
        #***************************************************************************

        if ( $compPath eq $compString )
        {
            $toString = $PDB_MAP_Object->{ToString};

            $pathOut  = $toString . substr( $pathIn, $fromLen );

            last FOREACH_LOOP;
        }
    }

    return( $pathOut );

}   ## end -- map_PDB_path

#***************************************************************************
#
# parse_VMMBI_Parm_Line -- Get an array of parameters on the specified
#                          environment variable line
#
# Input Parameters:
#
#    $VMMBI_ParmLineIn  = VMMBI parameter line stirng with length prefix
#    $VMMBI_VarNameIn   = VMMBI variable name
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
    my( $VMMBI_VarNameIn )   = shift;

    my( $statusOut )         = 0;
    my( @parmsOut )          = ();

    my( $start )             = 0;
    my( $errorIndex )        = 0;
    my( $iValueLen )         = 0;                  # Value length as an integer
    my( $sValueLen )         = "";                 # Value length as a string
    my( $stringLen )         = length( $VMMBI_ParmLineIn );
    my( $value )             = "";
    my( $errorHit )          = $FALSE;

    I_LOOP:
    for ( my $i = 0; $i < 999; $i++)
    {
        PARSE_BLOCK:
        {
            #*********************************************************************************
            #
            # Check to see if the string length is long enough to have the first length prefix
            #
            # Remember that the string length is the full length of the input string and is
            # not changed.
            #
            #**********************************************************************************

            if ( $stringLen < 3 )
            {
                $errorIndex = $start + 1;
                $errorHit   = $TRUE;
                last PARSE_BLOCK;
            }

            #*********************************************************************************
            #
            # Kick out of the loop normally if we have reached the end of the input string
            #
            #**********************************************************************************

            last I_LOOP if ( $start >= $stringLen );

            #*********************************************************************************
            #
            # Get the next length prefix
            #
            # We dont have to test to see if we have at least 3 characters left because the
            # next test will catch that.
            #
            #**********************************************************************************

            $sValueLen = substr( $VMMBI_ParmLineIn, $start, 3 );

            #*********************************************************************************
            #
            # If the next segment does not start with 3 decimal digits then see if it is just
            # trailing spaces.  If not then we have an error.
            #
            #**********************************************************************************

            if ( ! ( $sValueLen =~ /^\d\d\d/ ) )
            {
                $value = substr( $VMMBI_ParmLineIn, $start);
                $value =~ s/\s*$//;                  # Remove trailing white space

                if ( length( $value ) > 0 )
                {
                    $errorIndex = $start + 1;
                    $errorHit   = $TRUE;
                    last PARSE_BLOCK;
                }

                last I_LOOP;
            }

            $iValueLen = $sValueLen + 0;

            #*********************************************************************************
            #
            # Bump past the length prefix and check for a zero length value
            #
            #**********************************************************************************

            $start = $start + 3;

            if ( $sValueLen eq '000' )
            {
                $iValueLen = 0;
                push @parmsOut, "";
                next I_LOOP;
            }

            #*********************************************************************************
            #
            # Check to see if the length prefix exceeds the remaining length of the string
            #
            #**********************************************************************************

            if ( $start + $iValueLen > $stringLen )
            {
                $errorIndex = $start + 1;
                $errorHit   = $TRUE;
                last PARSE_BLOCK;
            }
        }          # End of PARSE_BLOCK:

        #*********************************************************************************
        #
        # If we have an error then process it otherwise get the value and add it to the
        # output array.
        #
        #**********************************************************************************

        if ( $errorHit )
        {
            log_error_msg( "VMMBI5700: parse_VMMBI_Parm_Line: Error: Length prefix missing or invalid at position = $errorIndex\n" );
            log_error_msg( "VMMBI5800: parse_VMMBI_Parm_Line: Error:    Variable Name = '$VMMBI_VarNameIn'\n" );
            log_error_msg( "VMMBI5900: parse_VMMBI_Parm_Line: Error:    String        = '$VMMBI_ParmLineIn'\n" );

            $statusOut++;
            last I_LOOP;
        }
        else
        {
            $value = substr( $VMMBI_ParmLineIn, $start, $iValueLen );
            $start = $start + $iValueLen;

            push @parmsOut, $value;

            next I_LOOP;
        }

    }        # End of I_LOOP

    return( $statusOut, @parmsOut );

}   ## end -- parse_VMMBI_Parm_Line

#*********************************************************************************
#
# validate_user --      Validates the user,password, project name, search path
#                       and build job names into output parameters and returns
#                       the status, i.e. 0, if successful, else -1
#
# Input Parameters:
#
#     None
#
# Output Parameters:
#
#    $statusOut        = Return code
#                        0 = Successful
#                        1 = Errors
#
#**********************************************************************************

sub validate_user
{
    my( $statusOut ) = 0;

    if ( lc( $MB_PROJECT_NAME_02 )     eq  lc( $MB_PROJECT_NAME_01_IN ) &&
         lc( $MB_SEARCH_PATH_NAME_02 ) eq  lc( $MB_SEARCH_PATH_NAME_01_IN ) &&
         lc( $MB_BUILD_JOB_NAME_02 )   eq  lc( $MB_BUILD_JOB_NAME_01_IN ) )
    {
         $statusOut = 0;
    }
    else
    {
        $statusOut++;
        log_error_msg( "VMMBI5980: validate_user: Error: The specified user ID is not authorized to execute this build job\n");
    }

    return( $statusOut );

}   ## end -- validate_user

#*********************************************************************************
#
# compare_file_times --  Compares the workfiles and files in archive for last modification
#                        in terms of UTC and if there are any unmatched files, the function
#                        lists those files.
#
# Input Parameters:
#
#    $ListIn    = Pointer to an array of ARCHIVE_INFO objects
#
# Output Parameters:
#
#    $statusOut = Return code
#                 0 = Successful
#                 1 = Errors
#
# Updates the following arrays:
#
#    @WORK_FILE_OLDER_LIST
#    @WORK_FILE_OLDER_OR_NONEXISTANT_LIST
#    @WORK_FILE_NEWER_LIST
#    @WORK_FILE_NONEXISTANT_LIST
#    @WORK_FILE_SAME_LIST
#    @WORK_FILE_DIFFERENT_LIST
#
#**********************************************************************************

sub compare_file_times
{
    my( $ListIn )                      = shift;    # Pointer to an array

    my( $statusOut )                   = 0;

    my( @List )                        = @$ListIn;
    my( @fileStatistics )              = ();
    my( $WorkFileLastModTime )         = 0;
    my( $WorkFileLastModTimeAdjusted ) = 0;
    my( $element )                     = "";
    my( $WorkFileName )                = "";
    my( $ArchiveLastModTime )          = "";
    my( $WorkFilePath )                = "";
    my( $DST_UTC_AdjustFactor )        = "";

    if ( $DEBUG )
    {
        log_msg("\n");
        log_msg("*********************************************************\n");
        log_msg("*\n");
        log_msg("* compare_file_times: Starting\n");
        log_msg("*\n");
        log_msg("*********************************************************\n");
        log_msg("\n");
    }

    I_LOOP:
    for ( my $i = 0; $i < scalar( @List ); $i++ )
    {
        $element = @List[ $i ];

        $WorkFileName         = $element->get_rel_work_file_path();
        $ArchiveLastModTime   = $element->get_last_modified_UTC();

        $WorkFilePath = $BUILD_DIRECTORY.$WorkFileName;

        $WorkFileLastModTime = 0;

        #**********************************************************************************
        #
        # If the work file exists get its stats
        #
        #**********************************************************************************

        if ( -e $WorkFilePath )                                      # If the file does not exist don't try to get stats
        {
            open( FILEHANDLE, $WorkFilePath );

            if ( FILEHANDLE )
            {
                @fileStatistics = stat( FILEHANDLE );

                close( FILEHANDLE );

                $WorkFileLastModTime = @fileStatistics[ 9 ];
            }
        }
        else
        {
            #**********************************************************************************
            #
            # If the work file does not exist then add the object to the work file doesnt exist
            # list and the work file different list
            #
            #**********************************************************************************

            push( @WORK_FILE_NONEXISTANT_LIST,           $element );
            push( @WORK_FILE_OLDER_OR_NONEXISTANT_LIST,  $element );
            push( @WORK_FILE_DIFFERENT_LIST,             $element );
            next I_LOOP;
        }

        #**********************************************************************************
        #
        # Adjust the last mod time by the daylight saving time adjustment if w32
        #
        #**********************************************************************************

        $DST_UTC_AdjustFactor = $element->get_DST_UTC_factor();

        if ( $IS_W32 && $DST_ADJUSTMENT_OPTION && $DST_UTC_AdjustFactor != 0 )
        {
            $WorkFileLastModTimeAdjusted = $WorkFileLastModTime + $DST_UTC_AdjustFactor;

            if ( $DEBUG )
            {
                log_msg("compare_file_times: DST_ADJUSTMENT_OPTION: Work file time adjusted: Adjustment factor = $DST_UTC_AdjustFactor: $WorkFilePath\n");
            }
        }
        else
        {
            $WorkFileLastModTimeAdjusted = $WorkFileLastModTime;            # Set value if we are not adjusting
        }

        #**********************************************************************************
        #
        # Compare the work file UTC time to the archive UTC time
        #
        #**********************************************************************************

        if ( $WorkFileLastModTimeAdjusted == $ArchiveLastModTime )
        {
            push( @WORK_FILE_SAME_LIST, $element );
            next I_LOOP;
        }

        if (  $WorkFileLastModTimeAdjusted > $ArchiveLastModTime )
        {
            push( @WORK_FILE_NEWER_LIST,        $element );
            push( @WORK_FILE_DIFFERENT_LIST,    $element );
            next I_LOOP;
        }

        if (  $WorkFileLastModTimeAdjusted < $ArchiveLastModTime )
        {
            push( @WORK_FILE_OLDER_LIST,                 $element );
            push( @WORK_FILE_OLDER_OR_NONEXISTANT_LIST,  $element );
            push( @WORK_FILE_DIFFERENT_LIST,             $element );
            next I_LOOP;
        }
    }        # End of I_LOOP

    if ( $DEBUG )
    {
        log_msg("\n");
        log_msg("*********************************************************\n");
        log_msg("*\n");
        log_msg("* compare_file_times: Ending\n");
        log_msg("*\n");
        log_msg("*    Return code = $statusOut\n");
        log_msg("*\n");
        log_msg("*********************************************************\n");
        log_msg("\n");
    }

    return( $statusOut );

}   ## end -- compare_file_times

#*********************************************************************************
#
# verify_gets --  Verify that the get completed by checking the last modified time
#
# Input Parameters:
#
#    $ListIn    = Pointer to an array of ARCHIVE_INFO objects
#
# Output Parameters:
#
#    $statusOut = Return code
#                 0 = Successful
#                 1 = Errors
#
#**********************************************************************************

sub verify_gets
{
    my( $ListIn )                      = shift;    # Pointer to an array

    my( $statusOut )                   = 0;

    my( @List )                        = @$ListIn;
    my( @fileStatistics )              = ();
    my( $WorkFileLastModTime )         = 0;
    my( $WorkFileLastModTimeAdjusted ) = 0;
    my( $DST_UTC_AdjustFactor )        = 0;
    my( $element )                     = "";
    my( $RelWorkFile )                 = "";
    my( $ArchiveLastModTime )          = "";
    my( $WorkFilePath )                = "";

    if ( $DEBUG )
    {
        log_msg("\n");
        log_msg("*********************************************************\n");
        log_msg("*\n");
        log_msg("* verify_gets: Starting\n");
        log_msg("*\n");
        log_msg("*********************************************************\n");
        log_msg("\n");
    }

    I_LOOP:
    for ( my $i = 0; $i < scalar( @List ); $i++ )
    {
        $element      = @List[ $i ];
        $RelWorkFile  = $element->get_rel_work_file_path();
        $WorkFilePath = $BUILD_DIRECTORY . $RelWorkFile;

        $WorkFileLastModTime = 0;

        #**********************************************************************************
        #
        # If the work file exists get its stats
        #
        #**********************************************************************************

        if ( -e $WorkFilePath )                                      # If the file does not exist don't try to get stats
        {
            open( FILEHANDLE, $WorkFilePath );

            if ( FILEHANDLE )
            {
                @fileStatistics = stat( FILEHANDLE );

                close( FILEHANDLE );

                $WorkFileLastModTime = @fileStatistics[ 9 ];
            }
        }
        else
        {
            log_error_msg( "VMMBI6000: verify_gets: Error: The get for: $WorkFilePath failed, the work file does not exist \n");
            $statusOut++;
            next I_LOOP;
        }

        #**********************************************************************************
        #
        # Check for daylight savings time adjustment
        #
        #**********************************************************************************

        $ArchiveLastModTime   = $element->get_last_modified_UTC();
        $DST_UTC_AdjustFactor = $element->get_DST_UTC_factor();

        DST_ADJUSTMENT_BLOCK:
        {
            $WorkFileLastModTimeAdjusted = $WorkFileLastModTime;                           # Set unadjusted default value

            last DST_ADJUSTMENT_BLOCK if ( $DST_UTC_AdjustFactor == 0 );
            last DST_ADJUSTMENT_BLOCK if ( ! $IS_W32 );
            last DST_ADJUSTMENT_BLOCK if ( $BUILD_DIRECTORY_FILE_SYSTEM ne "NTFS" );
            last DST_ADJUSTMENT_BLOCK if ( ! $DST_ADJUSTMENT_OPTION );

            $WorkFileLastModTimeAdjusted = $WorkFileLastModTime + $DST_UTC_AdjustFactor;

            if ( $DEBUG )
            {
                log_msg("verify_gets: DST_ADJUSTMENT_OPTION: Work file time adjusted: Work file UTC = $WorkFileLastModTime: Adjustment factor = $DST_UTC_AdjustFactor: Adjusted work file UTC = $WorkFileLastModTimeAdjusted: $WorkFilePath\n");
            }
        }

        #**********************************************************************************
        #
        # Compare the adjusted work file UTC time to the archive UTC time
        #
        #**********************************************************************************

        next I_LOOP if ( $WorkFileLastModTimeAdjusted == $ArchiveLastModTime );

        log_error_msg( "VMMBI6200: verify_gets: Error: The get for: '$WorkFilePath failed', the work file time does not match:\n");
        log_error_msg( "VMMBI6220: verify_gets: Error:    Work file time    = $WorkFileLastModTimeAdjusted\n");
        log_error_msg( "VMMBI6240: verify_gets: Error:    Archive file time = $ArchiveLastModTime\n");

        $statusOut++;
        next I_LOOP;

    }        # End of I_LOOP

    #**********************************************************************************
    #
    # All done
    #
    #**********************************************************************************

    if ( $DEBUG )
    {
        log_msg("\n");
        log_msg("*********************************************************\n");
        log_msg("*\n");
        log_msg("* verify_gets: Ending\n");
        log_msg("*\n");
        log_msg("*    Return code = $statusOut\n");
        log_msg("*\n");
        log_msg("*********************************************************\n");
        log_msg("\n");
    }

    return( $statusOut );

}   ## end -- verify_gets

#*********************************************************************************
#
# get_target_files --  Get target files in root of specified project only
#
#
#
# Input Parameters:
#
#     None
#
# Output Parameters:
#
#    $statusOut        = Return code
#                        0 = Successful
#                        1 = Errors
#
#**********************************************************************************

sub get_target_files
{
    my( $statusOut )      = 0;

    $statusOut = pcli_get( "/*.tgt",              # File specification (Get target files only)
                           $FALSE,                # Do not do sub-projects
                           $TRUE                  # Get newer files only
                         );

    return ( $statusOut );

}   ## end -- get_target_files

#*********************************************************************************
#
# get_project_files --  Get project files
#
#
#
# Input Parameters:
#
#     None
#
# Output Parameters:
#
#    $statusOut        = Return code
#                        0 = Successful
#                        1 = Errors
#
#**********************************************************************************

sub get_project_files
{
    my( $statusOut )      = 0;

    $statusOut = pcli_get( "",                        # File specification (Get all files)
                           $INCLUDE_SUBPROJECTS_IN,   # Set sub-project processing
                           $TRUE                      # Get newer files only
                         );

    return ( $statusOut );

}   ## end -- get_project_files

#*********************************************************************************
#
# pcli_get --  Use pcli to get files
#
#
# Input Parameters:
#
#    $FileSpecIn             = File spec to be appended to $VM_PROJECT_NAME_IN
#    $IncludeSubprojectsIn   = Flag to include sub-projects
#                              $FALSE = do not include sub-projects
#                              $TRUE  = Include sub-projects
#    $GetNewerOnlyIn         = Flag to get only revisions that are newer then the work file
#                              $FALSE = Get all files
#                              $TRUE  = Get newer files only
#
# Output Parameters:
#
#    $statusOut        = Return code
#                        0 = Successful
#                        1 = Errors
#
#**********************************************************************************

sub pcli_get
{
    my( $FileSpecIn )            = shift;
    my( $IncludeSubprojectsIn )  = shift;
    my( $GetNewerOnlyIn )        = shift;

    my( $statusOut )             = 0;

    my( $PcliCmd )               = "";
    my( @CmdResults )            = ();
    my( $PDB_Path )              = "";
    my( $UserIdPassword )        = "";
    my( $VersionPromo )          = "";
    my( $SubProjects )           = "";
    my( $ProjectName )           = "";
    my( $WorkPath )              = "";
    my( $Response )              = "";
    my( $GetNewerOption )        = "";

    #***************************************************************************
    #
    # Set the database and PCLI file PATH. Also set the command line
    #
    # All parameter values must be inclosed in double quotes in case they might
    # contain white space.
    #
    #***************************************************************************

    $PDB_Path    = " -pr$DQ$VM_PDB_PATH$DQ";
    $WorkPath    = " -o -a$DQ$BUILD_DIRECTORY$DQ -bp$DQ$VM_PROJECT_NAME_IN$DQ";

    #***************************************************************************
    #
    # Set the newer revisions only option
    #
    #***************************************************************************

    if ( $GetNewerOnlyIn )
    {
        $GetNewerOption = " -u"
    }

    #***************************************************************************
    #
    # Set the user ID and password
    #
    #***************************************************************************

    if ( $VM_USER_ID ne "" )
    {
        $UserIdPassword = " -id$DQ$VM_USER_ID$DQ";

        if ( $VM_PASSWORD ne "" )
        {
            $UserIdPassword = " -id$DQ$VM_USER_ID:$VM_PASSWORD$DQ";
        }
    }

    #***************************************************************************
    #
    # Set version label or promotion group or defalut revision
    #
    # If neither the Label or Group parameters are specified then GetRevInfo
    # will process with the default revision (normally the tip) but it could be
    # a default version label.
    #
    #***************************************************************************

    if ( $SOURCE_SELECTION_TYPE_IN eq "label" )
    {
        $VersionPromo = " -v$DQ$SOURCE_VERSION_PROMO_IN$DQ";
    }
    elsif ( $SOURCE_SELECTION_TYPE_IN eq "group" )
    {
        $VersionPromo = " -g$DQ$SOURCE_VERSION_PROMO_IN$DQ";
    }
    else
    {
        $VersionPromo = "";
    }

    if ( $IncludeSubprojectsIn )
    {
        $SubProjects = " -z";
    }

    #***************************************************************************
    #
    # Generate the project name specification
    #
    # Note that the file spec could be null which means all files or it could be
    # a wild card, for example, "/*.tgt"
    #
    #***************************************************************************

    $ProjectName = " $DQ" . $VM_PROJECT_NAME_IN . $FileSpecIn . "$DQ";

    #***************************************************************************
    #
    # Assemble the command
    #
    # We are running this as a pcli command, not as a pcli script thus the pcli
    # command is not stored in a file before running it.
    #
    #***************************************************************************

    $PcliCmd = "pcli run -y -ns -xl Get" .
               $PDB_Path                 .
               $UserIdPassword           .
               $VersionPromo             .
               $SubProjects              .
               $Response                 .
               $GetNewerOption           .
               $WorkPath                 .
               $ProjectName              .
               "";

    call_cmd( $PcliCmd,           # Command string
              $UserIdPassword,    # Strip this string from the command string before it is displayed to the log
              $FALSE              # Do not log standard output
            );

    return ( $statusOut );

}   ## end -- pcli_get

#*********************************************************************************
#
# get_exact_project_files --  Get exact list of specified project files
#
#
#
# Input Parameters:
#
#     $ListIn          = Pointer to array of archive info objects.
#
# Output Parameters:
#
#    $statusOut        = Return code
#                        0 = Successful
#                        1 = Errors
#
#**********************************************************************************

sub get_exact_project_files
{
    my( $ListIn )            = shift;

    my( $statusOut )         = 0;

    my( @List )              = @$ListIn;
    my( $rval )              = 0;
    my( $ScriptName )        = "";
    my( $UserIdPassword )    = "";
    my( $VersionPromo )      = "";
    my( $PDB_Path )          = "";
    my( $WorkPath )          = "";
    my( $cmd )               = "";
    my( $ProjectName )       = "";
    my( $WorkFilePath )      = "";

    GET_EXACT_BLOCK:
    {
        last GET_EXACT_BLOCK if ( scalar( @List ) <= 0 );

        #*********************************************************************************
        #
        # Setup the pcli script path and name and make sure the file does not exist
        #
        #**********************************************************************************

        $ScriptName = $BUILD_DIRECTORY.$OUR_SLASH."VMMBI_bldmake_EXACT_GET.pcli";

        unlink( $ScriptName );

        open(PCLI_FILE, "> $ScriptName");

        #*********************************************************************************
        #
        # Generate the project database path specification
        #
        #**********************************************************************************

        $PDB_Path       = " -pr$DQ$VM_PDB_PATH$DQ";

        #***************************************************************************
        #
        # Generate the work path specification
        #
        #***************************************************************************

        $WorkPath       = " -o -a$DQ$BUILD_DIRECTORY$DQ -bp$DQ$VM_PROJECT_NAME_IN$DQ";

        #***************************************************************************
        #
        # Generate the user ID and password specification
        #
        # Set the command sequence so that the user ID and password can be substituted.
        # We do this because the pcli statements are written to a file and we don't
        # want to write the user ID and password to the file where it can be viewed.
        # We supply the user ID and password when the pcli script is executed.
        #
        #***************************************************************************

        $UserIdPassword = " -id" . $DQ . '$1' . $DQ;

        #***************************************************************************
        #
        # Generate the version label or promotion group or defalut revision specification
        #
        # If neither the Label or Group parameters are specified then by default
        # the tip revision will be retrieved.  Thus for a default revison specification
        # $VersionPromo is set to null.
        #
        #***************************************************************************

        if ( $SOURCE_SELECTION_TYPE_IN eq "label" )
        {
            $VersionPromo = " -v$DQ$SOURCE_VERSION_PROMO_IN$DQ";
        }
        elsif ( $SOURCE_SELECTION_TYPE_IN eq "group" )
        {
            $VersionPromo = " -g$DQ$SOURCE_VERSION_PROMO_IN$DQ";
        }
        else
        {
            $VersionPromo = "";
        }

        #***************************************************************************
        #
        # Loop through the list of archive info objects and build a pcli get statement
        # for each assocaited project file
        #
        #***************************************************************************

        foreach $element( @List )
        {
            #***************************************************************************
            #
            # Generate the project name specification
            #
            #***************************************************************************

            $WorkFilePath = $element->get_work_file_path();      # Relative to the start of the build directroy
            $ProjectName  = " $DQ" . $WorkFilePath . "$DQ";

            #***************************************************************************
            #
            # Assemble the pcli statement
            #
            # This is being assembled into a pcli script
            #
            #***************************************************************************

            $cmd = "run -y Get"       .
                   $PDB_Path          .
                   $UserIdPassword    .
                   $VersionPromo      .
                   $WorkPath          .
                   $ProjectName       .
                   "";

            #*********************************************************************************
            #
            # Add the statement to the script
            #
            #**********************************************************************************

            print PCLI_FILE "$cmd\n";
        }

        #*********************************************************************************
        #
        # Run the pcli script
        #
        #**********************************************************************************

        close(PCLI_FILE);

        last GET_EXACT_BLOCK if ( $statusOut );

        $rval = run_pcli_script( $ScriptName );
        $statusOut += $rval;

        if ( $DELETE_TEMP )
        {
            unlink( $ScriptName );
        }
    }            # End of GET_EXACT_BLOCK:

    return ( $statusOut );

}   ## end -- get_exact_project_files

#*********************************************************************************
#
# run_pcli_script -- Run a pcli script
#
# Input Parameters:
#
#    $scriptIn            = Name of the PCLI script to be run
#
# Output Parameters:
#
#    $statusOut           = Return code
#                           0 = successful
#                           1 = failure
#
#*********************************************************************************

sub run_pcli_script
{
    my( $scriptIn )            = shift;

    my( $statusOut )           = 0;

    my( $PcliCmd )             = "";
    my( $rval )                = 0;
    my( $UserIdPassword )      = "";
    my( $ScriptPath )          = "";
    my( @lines )               = ();

    #***************************************************************************
    #
    # Set the database and PCLI file PATH. Also set the command lines
    #
    # All parameter values must be inclosed in double quotes in case they might
    # contain white space.
    #
    #***************************************************************************

    $ScriptPath     = " -s$DQ$scriptIn$DQ";

    if ( $VM_USER_ID ne "" )
    {
        $UserIdPassword = " $DQ$VM_USER_ID$DQ";

        if ( $VM_PASSWORD ne "" )
        {
            $UserIdPassword = " $DQ$VM_USER_ID:$VM_PASSWORD$DQ";
        }
    }

    #***************************************************************************
    #
    # Assemble the command
    #
    # We pass the user ID and password as a parameter so it does not have to be
    # stored in the pcli script.  The pcli command must use the following
    # sequence in the script in order to make use of the parameter passed in:
    #
    #    ' -id' . $DQ . '$1' . $DQ
    #
    #***************************************************************************

    $PcliCmd = "pcli run -xl "   .
               $ScriptPath       .
               $UserIdPassword   .
               "";

    #***************************************************************************
    #
    # Execute the PCLI command
    #
    #***************************************************************************

    ( $rval, @lines ) = call_cmd( $PcliCmd,           # Command string
                                  $UserIdPassword,    # Strip this string from the command string before it is displayed to the log
                                  $FALSE              # Do not log standard output
                                );

    $statusOut += $rval;

    return( $statusOut );

}   ## end -- run_pcli_script

#***************************************************************************
#
# sendMsgToMB_KBS_JobLog -- Send a message to the Merant Build KB server job
#                           log
#
#
# Input Parameters:
#
#    $MsgTypeIn   = Message type ( $INFO_MSG, $ERROR_MSG, $WARNING_MSG )
#    $MsgIn       = Message to be sent
#
# Output Parameters:
#
#    None
#
#***************************************************************************

use Openmake::Log;

sub sendMsgToMB_KBS_JobLog
{
    my( $MsgTypeIn )        = shift;
    my( $MsgIn )            = shift;

    my ( $DateTime )        = "";
    my ( $Event )           = "";

    if ( $CMD_LINE_PARMS_OBJECT->{loggingEnabled} )
    {
        my ( $sec, $min, $hour, $mday, $mon, $year, $wday, $yday, $isdist ) = localtime(time);

        $DateTime = sprintf "%02d/%02d/%02d %02d:%02d:%02d",$mon,$mday,$year-100,$hour,$min,$sec;

        if ( $MsgTypeIn == $ERROR_MSG )
        {
            $Event = "ERROR";
        }
        else
        {
            $Event = "Running"
        }

        Openmake::Log::SendSoapMessage(
                                        $MsgTypeIn,                               # Event Type
                                        $CMD_LINE_PARMS_OBJECT->{jobName},        # Job Name
                                        $CMD_LINE_PARMS_OBJECT->{jobDTG},         # Job Date and Time
                                        $CMD_LINE_PARMS_OBJECT->{buildMachine},   # Machine Name
                                        $CMD_LINE_PARMS_OBJECT->{userID},         # User Id
                                        $CMD_LINE_PARMS_OBJECT->{publicJob},      # Public Build Job
                                        $Event,                                   # Event
                                        "VMMBI_bldmake_PET",                      # Detail Group
                                        $DateTime,                                # Date and Time
                                        "VMMBI_bldmake_PET",                      # Source
                                        "",                                       # Short Message
                                        $MsgIn                                    # Message
                                      );
    }

    return;

}   ## end -- sendMsgToMB_KBS_JobLog()

#*******************************************************************************
#
# get_utc_from_string --  extracts the parameters from the string representation
#                         of DATE/TIME format and inputs it to the function
#                         "timelocal" available in the package Time::Local
#
# Input Parameters:
#
#     $strTimeIn   = The DATE/TIME in string format
#                    eg. "Mon May 18 16:37:40 GMT-07:00 1998"
#
# Output Parameters:
#
#     $utcTimeOut  = DATE/TIME in UTC format
#                    eg. 898213060 for above input
#
#*******************************************************************************

#*******************************************************************************
#
#         Using This Package for converting date/time to UTC
#
#*******************************************************************************

use Time::Local;

sub get_utc_from_string
{
    my( $strTimeIn )           = shift;   # The incoming date/time in string format
    my( $utcTimeOut )          = 0;       # The final UTC converted date/time that will be returned

    my( @arrSplittedDateTime ) = ();
    my( @arrSplittedTime )     = ();
    my( @MONTHS )              = ();
    my( $MonthNum )            = 99;
    my( $Seconds )             = 0;
    my( $Minutes )             = 0;
    my( $Hours )               = 0;
    my( $MonthDay )            = 0;
    my( $Year )                = 0;

    UTC_FROM_STRING_BLOCK:
    {
        #***********************************************************************
        #
        # If the input string is zero then we return zeros
        #
        #***********************************************************************

        if ( $strTimeIn eq "" )
        {
            if ( $DEBUG )
            {
                log_msg("get_utc_from_string: Warning: The input string is null;\n");
            }

            $utcTimeOut = 0;
            last UTC_FROM_STRING_BLOCK;
        }

        #***********************************************************************
        #
        # List for comparision of months
        #
        #***********************************************************************

        @MONTHS = qw( Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec );

        #***********************************************************************
        #
        # Split the string with SPACE as delimiter and then store it in an array
        # EXAMPLE: "Mon May 18 16:37:40 GMT-07:00 1998"
        # The above example will extract:
        #
        # Week day      = "Mon"         (This will be ignored)
        # Month         = "May"
        # Month day     = "18"
        # Modified Time = "16:37:40"
        # GMT Time      = "GMT-07:00"   (This will be ignored)
        # Year          = "1998"
        #
        #************************************************************************

        @arrSplittedDateTime = split( " ", $strTimeIn );

        $MonthDay   =   @arrSplittedDateTime[ 2 ];
        $Year       =   @arrSplittedDateTime[ 5 ];

        #************************************************************************
        #
        # Again split the extracted time with ":" as delimiter and then store
        # it in an array
        # EXAMPLE: "16:37:40"
        # The above example will extract:
        #
        # 16    =   Hours
        # 37    =   Minutes
        # 40    =   Seconds
        #
        #************************************************************************

        @arrSplittedTime = split(":",@arrSplittedDateTime[3]);

        $Seconds    =   @arrSplittedTime[ 2 ];
        $Minutes    =   @arrSplittedTime[ 1 ];
        $Hours      =   @arrSplittedTime[ 0 ];

        #************************************************************************
        #
        # Since the function "timelocal expects the month to be represented as
        # numeric value, hence iterate throught the MONTHS list, compare with the
        # one available in DATE/TIME array and preferably set the month in
        # numeric format.
        #
        # Note that the month value must be in the range 0 - 11.
        #
        #************************************************************************

        I_LOOP:
        for ( my $i = 0; $ i < scalar( @MONTHS ); $i++ )
        {
            if ( lc( @arrSplittedDateTime[ 1 ] ) eq lc( @MONTHS[ $i ] ) )
            {
                $MonthNum = $i;

                last I_LOOP;
            }
        }            # End of I_LOOP

        #************************************************************************
        #
        # After extracting all the paramaters from the string, use it as input parameter
        # to the function "timelocal" in the sequence as it expects
        # i.e. Seconds, Minutes, Hours, MonthDay, MONTH, Year
        #
        #************************************************************************

        $utcTimeOut = timelocal( $Seconds, $Minutes, $Hours, $MonthDay, $MonthNum, $Year );

    }          # End of UTC_FROM_STRING_BLOCK:

    #************************************************************************
    #
    # Return the time in UTC format
    #
    #************************************************************************

    return( $utcTimeOut );

}   ## end -- get_utc_from_string

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
# call_cmd -- Run a command with "do_cmd"
#
#
# Input Parameters:
#
#    $cmdIn        =  Command to be called with "do_cmd:
#    $supressIn    =  String to suppress when $cmdIn is printed
#    $logStdOutIn  =  Log command standard output flag ($TRUE or $FALSE)
#
# Output Parameters:
#
#    $statusOut    = Return status
#                    0 = Successful
#                    1 = Failed
#    @linesOut     = Standard out from the command
#
#***************************************************************************

sub call_cmd
{
    my( $cmdIn )       = shift;              # Input parameters
    my( $supressIn )   = shift;              # Input parameters
    my( $logStdOutIn ) = shift;              # Input parameters

    my( $statusOut )   = 0;                  # Output parameters
    my( @linesOut )    = ();                 # Output parameters

    my( $rval )        = 0;
    my( $i )           = 0;
    my( $count )       = 0;
    my( $line )        = "";
    my( $DisplayCmd )  = "";

    #***************************************************************************
    #
    #  Suppress string from printed command
    #
    #***************************************************************************

    if ( $supressIn ne "" )
    {
        $DisplayCmd = strip( $supressIn, $cmdIn, " XXXXXXXX" );
    }
    else
    {
        $DisplayCmd = $cmdIn;
    }

    log_msg( "call_cmd:  $DisplayCmd \n" );

    #***************************************************************************
    #
    #  Run the command
    #
    #***************************************************************************

    ( $rval, @linesOut ) = do_cmd( $cmdIn );

    $statusOut += $rval;

    log_msg( "call_cmd:  status = $rval \n" );

    #***************************************************************************
    #
    #  Send the output to STDOUT
    #
    #***************************************************************************

    if ( $logStdOutIn )
    {
        for ( my $i = 0; $i < scalar(@linesOut); $i++ )
        {
            $line  = @linesOut[ $i ];
            $count = sprintf "%02d", $i;

            log_msg( "   [ $count ] = $line");
        }
    }

    return ( $statusOut, @linesOut );

}   ## end -- call_cmd()

#***************************************************************************
#
#  do_cmd
#
#
# Input Parameters:
#
#    $cmdIn        =  Command to be called with "do_cmd:
#
# Output Parameters:
#
#    $rtn_val      = Return status
#                    0 = Successful
#                    1 = Failed
#
#***************************************************************************

sub do_cmd
{
    my( $cmdIn )                   = shift;

    my( @linesOut )                = ();
    my( $statusOut )               = 0;

    my( $rval )                    = 0;
    my( $status )                  = 0;
    my( $cmd_name )                = "";
    my( $line )                    = "";

    #***************************************************************************
    #
    #  Issue the command
    #
    #***************************************************************************

    $status = open(CMD, "$cmdIn 2>&1 |" );

    if ( $status )
    {
        @linesOut = <CMD>;

        close(CMD);

        #***************************************************************************
        #
        #  Get return value of shell command.
        #
        #***************************************************************************

        $rval      = $?;
        $statusOut = $rval / 256;

        if ($statusOut)
        {
            log_error_msg( "VMMBI6500: do_cmd: ERROR: The command failed: Return Value = $rval, Return Code = $statusOut\n" );
        }
    }

    #***************************************************************************
    #
    #  The open failed
    #
    #***************************************************************************

    else
    {
        log_error_msg( "VMMBI6520: do_cmd: ERROR: The command could not be executed.\n" );

        $statusOut = 1;
    }

    return( $statusOut, @linesOut );

}   ## end -- do_cmd()

#***************************************************************************
#
# run_chdir -- Run the chdir command and adjust the return code
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
# run_mkdir -- Run the mkdir command and adjust the return code
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

sub run_mkdir
{
    my( $cmdIn )     = @_;              # Input  parameters

    my( $statusOut ) = 0;               # Output parameters

    my( $rval ) = 0;

    log_msg( "run_mkdir: mkdir $cmdIn, 0775 \n" );

    $rval = mkdir( $cmdIn, 0775 );

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
        log_error_msg( "VMMBI6600: run_mkdir: Error: Failed to make directory: $cmdIn\n" );
    }

    log_msg( "run_mkdir: status = $statusOut \n" );

    return $statusOut;                       # Return code

}   ## end -- run_mkdir()

#***************************************************************************
#
# make_path -- Make a path.  Create only those elements which dont exist.
#              Creates a absolute or relative path. Excepts both W32 and
#              UNIX formats.
#
#
# Input Parameters:
#
#    $pathIn   =  Path to create
#    $flushIn  =  Flush the final directory if it exists
#
# Output Parameters:
#
#    $statusOut = Return status
#                 0 = Successful
#                 1 = Failed
#
#***************************************************************************

sub make_path
{
    my( $pathIn, $flushIn )     = @_;              # Input  parameters

    my( $statusOut )            = 0;               # Output parameters

    my( $rval )                 = 0;
    my( $iStart )               = 0;
    my( $flush )                = $FALSE;
    my( @elements )             = ();
    my( $path )                 = "";
    my( $makePath )             = "";
    my( $elementCount )         = "";
    my( $element )              = "";
    my( $cmd )                  = "";

    if ( $DEBUG )
    {
        log_msg("\n");
        log_msg("*********************************************************\n");
        log_msg("*\n");
        log_msg("* make_path: Starting\n");
        log_msg("*\n");
        log_msg("*    Path in    = $pathIn\n");
        log_msg("*    Flush flag = $flushIn\n");
        log_msg("*\n");
        log_msg("*********************************************************\n");
        log_msg("\n");
    }

    MAKE_PATH_BLOCK:
    {
        #***************************************************************************
        #
        # Validate the parameters
        #
        #***************************************************************************

        if ( $pathIn eq "" )
        {
            $statusOut = 1;
            last MAKE_PATH_BLOCK;
        }

        if ( $flushIn ne "" )
        {
            $flush = $flushIn;
        }

        #***************************************************************************
        #
        # If the path already exists then we do nothing
        #
        #***************************************************************************

        if ( ! -d $pathIn )
        {
            $flush = $FALSE;

            $path = normalize_path( $pathIn );

            #***************************************************************************
            #
            # Split out the path elements and spin through them
            #
            #***************************************************************************

            $cmd = '@elements = split( /' . $OUR_SLASH_ESCAPED . '/, $path )';                # Create Perl statement to be executed
            eval $cmd;                                                                        # Execute the Perl statement

            $elementCount = scalar( @elements );

            #***************************************************************************
            #
            # Pre-process the array and remove leading and trailing white space from
            # each element
            #
            #***************************************************************************

            for (my $i = 0; $i < $elementCount; $i++)
            {
                $element =  @elements[ $i ];
                $element =~ s/^\s+//;                  # Remove leading  white space
                $element =~ s/\s*$//;                  # Remove trailing white space
                @elements[ $i ] = $element;
            }

            #***************************************************************************
            #
            # Check for start of UNC
            #
            #***************************************************************************

            if ( $IS_W32 && substr( $pathIn, 0, 2) eq $OUR_SLASH . $OUR_SLASH && @elements[ 2 ] ne "" && @elements[ 3 ] ne "" )
            {
                $makePath = $OUR_SLASH . $OUR_SLASH . @elements[ 2 ] . $OUR_SLASH . @elements[ 3 ] . $OUR_SLASH;

                $iStart = 4;
                $elementCount -= 4;
            }

            ELEMENT_BLOCK:
            for (my $i = 0; $i < $elementCount; $i++)
            {
                $element = @elements[ $iStart + $i ];

                #***************************************************************************
                #
                # Check for null element (leading slash)
                #
                #***************************************************************************

                if ( $element eq "" )
                {
                    if ( $i > 0 )
                    {
                        log_error_msg( "VMMBI6620: make_path: Error: A null path element was encountered: $pathIn\n" );
                        $statusOut = 1;
                        last MAKE_PATH_BLOCK;
                    }

                    $makePath .= $OUR_SLASH;
                    next ELEMENT_BLOCK;
                }

                #***************************************************************************
                #
                # Check for colon in the element (leading drive letter)
                #
                #***************************************************************************

                if ( $element =~ /:/ )
                {
                    if ( $i > 0 )
                    {
                        log_error_msg( "VMMBI6640: make_path: Error: Invalid path element encountered: $pathIn\n" );
                        $statusOut = 1;
                        last MAKE_PATH_BLOCK;
                    }

                    $makePath .= $element . $OUR_SLASH;
                    next ELEMENT_BLOCK;
                }

                #***************************************************************************
                #
                # If the directory doesn't exist then create it
                #
                #***************************************************************************

                $makePath .= $element . $OUR_SLASH;

                if ( ! -d $makePath )
                {
                    $rval += run_mkdir( $makePath );
                }

                if ( $rval )
                {
                    $statusOut = $rval;
                    last MAKE_PATH_BLOCK;
                }
            }            # End of ELEMENT_BLOCK:
        }            # End of "if ( ! -d $pathIn )"

        #***************************************************************************
        #
        # Flush the final directory if specified
        #
        #***************************************************************************

        if ( $flush )
        {
            $rval += run_rmtree( $pathIn );

            $rval += run_mkdir( $pathIn );

            if ( $rval )
            {
                $statusOut = $rval;
                last MAKE_PATH_BLOCK;
            }
        }
    }            # End of MAKE_PATH_BLOCK:

    #***************************************************************************
    #
    # All done
    #
    #***************************************************************************

    if ( $DEBUG )
    {
        log_msg("\n");
        log_msg("*********************************************************\n");
        log_msg("*\n");
        log_msg("* make_path: Ending\n");
        log_msg("*\n");
        log_msg("*    Constructed path = $makePath\n");
        log_msg("*    Return code      = $statusOut\n");
        log_msg("*\n");
        log_msg("*********************************************************\n");
        log_msg("\n");
    }

    return( $statusOut );               # Return code

}   ## end -- make_path()

#***************************************************************************
#
# run_rmdir -- Run the rmdir command and adjust the return code
#
#
# Input Parameters:
#
#    $dirIn  =  Directory to be deleted
#
# Output Parameters:
#
#    $statusOut = Return status
#                 0 = Successful
#                 1 = Failed
#
#***************************************************************************

sub run_rmdir
{
    my( $dirIn )     = @_;              # Input  parameters

    my( $statusOut ) = 0;               # Output parameters

    my( $rval )      = 0;

    log_msg( "run_rmdir: rmdir $dirIn \n" );

    $rval = rmdir( $dirIn );

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

    log_msg( "run_rmdir: status = $statusOut \n" );

    return $statusOut;                       # Return code

}   ## end -- run_rmdir()

#***************************************************************************
#
# run_rmtree -- Run the the rmtree command to delete a directory tree
#
#
# Input Parameters:
#
#    $dirIn  =  Directory structure to be deleted
#
# Output Parameters:
#
#    $statusOut = Return status
#                 0 = Successful
#                 1 = Failed
#
#***************************************************************************

use File::Path;
sub run_rmtree
{
    my( $dirIn )     = @_;              # Input  parameters

    my( $statusOut ) = 0;               # Output parameters

    my( $rval )      = 0;

    log_msg( "run_rmtree: run_rmtree( $dirIn, $FALSE, $FALSE ) \n" );

    $rval = rmtree( $dirIn,          # Directory structure to delete
                    $FALSE,          # No messages
                    $FALSE           # Delete all files
                  );

    if ( -e $dirIn )
    {
        $statusOut = 1;
    }
    else
    {
        $statusOut = 0;
    }

    log_msg( "run_rmtree: status = $statusOut \n" );

    return $statusOut;                       # Return code

}   ## end -- run_rmtree()

#***************************************************************************
#
# log_error_msg -- Log an error message and also write it to the error
#                  message queue
#
#
# Input Parameters:
#
#    $messageIn     = Message
#
# Output Parameters:
#
#    none
#
#***************************************************************************

sub log_error_msg
{
    my( $messageIn ) = join "", @_;

    push @ERROR_MSG_QUEUE, $messageIn;

    log_msg( $messageIn );

    return;

}   ## end -- log_error_msg()

#***************************************************************************
#
# log_msg -- Print message to STDOUT and if we are currently redirected
#            print it to STDERR also
#
#
# Input Parameters:
#
#    $messageIn     = Message
#
# Output Parameters:
#
#    none
#
#***************************************************************************

sub log_msg
{
    my( $messageIn ) = join "", @_;
    my( $logMsg )    = "";
    my( $message )   = "";
    my( $len )       = 0;
    my( $msgCount )  = 0;

    $logMsg          = "$OUR_PROGRAM_NAME: $messageIn";

    #***************************************************************************
    #
    # If the log file is open then check to see if we have any messages in the
    # queue to print out
    #
    #
    #***************************************************************************

    if ( $BLDMAKE_PET_LOGFILE_OPEN )
    {
        #***************************************************************************
        #
        # Print messages in the queue and then clear the queue
        #
        #
        #***************************************************************************

        $msgCount = scalar( @BLDMAKE_PET_LOGFILE_MSG_QUEUE );

        if ( $msgCount > 0 )
        {
            foreach $message ( @BLDMAKE_PET_LOGFILE_MSG_QUEUE )
            {
                print STDOUT $message;
            }

            @BLDMAKE_PET_LOGFILE_MSG_QUEUE = ();
        }
    }

    #***************************************************************************
    #
    # If the log file is not open then queue up the message for when it does
    # get opened
    #
    #***************************************************************************

    else
    {
        push @BLDMAKE_PET_LOGFILE_MSG_QUEUE, $logMsg;
    }

    #***************************************************************************
    #
    # Write the message to the console if not redirecting or to the log if we
    # are redirecting
    #
    #***************************************************************************

    print STDOUT $logMsg;

    #***************************************************************************
    #
    # Write the message to the console also
    #
    #***************************************************************************

    if ( $REDIRECT )
    {
        print SAVEOUT $logMsg;
    }

    return;

}   ## end -- log_msg()

#***************************************************************************
#
# redirect_output -- Redirect the output streams to a logfile.  Save stdout
#                    and stderr for restoration later when requested.
#
#
# Input Parameters:
#
#    $logfileIn       = Logfile path
#    $saveStreamsIn   = Save stdout and stderr flag
#    $appendIn        = Append to end of log flag
#
# Output Parameters:
#
#    Dies (exits) if it fails.
#
#    $logfileOpenOut  = Log file open flag
#                       0 = Logfile is closed
#                       1 = Logfile is open
#
#***************************************************************************

sub redirect_output
{
    my( $logfileIn )     = shift;
    my( $saveStreamsIn ) = shift;
    my( $appendIn )      = shift;

    my($logfileOpenOut)  = 0;
    my($fileExists)      = 0;

    use IO::Handle;

    if ( -e $logfileIn )
    {
        $fileExists = 1;                          # Remember that the log file already exists
    }

    #***************************************************************************
    #
    # Save the current output streams
    #
    # We will also set the saved terminal output to flush after each print line.
    #
    #***************************************************************************

    if ( $saveStreamsIn )
    {
        open(SAVEOUT, ">&STDOUT");
        open(SAVEERR, ">&STDERR");

        SAVEOUT->autoflush(1);
        SAVEERR->autoflush(1);
    }
    else
    {
        close(STDOUT);
        close(STDERR);
    }

    #***************************************************************************
    #
    # Switch to the new output streams
    #
    #***************************************************************************

    if ( $appendIn )
    {
       open(STDOUT, ">>$logfileIn") || die "$OUR_PROGRAM_NAME: Error: Cannot redirect stdout\n";
       open(STDERR, ">>&STDOUT")    || die "$OUR_PROGRAM_NAME: Error: Cannot redirect stderr to stdout\n";

       if ( $fileExists )
       {
          print STDOUT "\n";
          print STDOUT "\n";
          print STDOUT "$OUR_PROGRAM_NAME: ================== VMMBI PRE-EVENT TRIGGER LOG ==================\n";
          print STDOUT "$OUR_PROGRAM_NAME: ================ APPENDING TO END OF CURRENT LOG ================\n";
          print STDOUT "$OUR_PROGRAM_NAME: ================ ", print_date(),               "================\n";
          print STDOUT "\n";
          print STDOUT "\n";
       }
       else
       {
          print STDOUT "\n";
          print STDOUT "\n";
          print STDOUT "$OUR_PROGRAM_NAME: ================== VMMBI PRE-EVENT TRIGGER LOG ==================\n";
          print STDOUT "$OUR_PROGRAM_NAME: ====================== BEGINING OF NEW LOG ======================\n";
          print STDOUT "$OUR_PROGRAM_NAME: ================ ", print_date(),               "================\n";
          print STDOUT "\n";
          print STDOUT "\n";

       }
    }
    else
    {
        open(STDOUT, ">$logfileIn") || die "$OUR_PROGRAM_NAME: Error: Cannot redirect stdout\n";
        open(STDERR, ">&STDOUT")    || die "$OUR_PROGRAM_NAME: Error: Cannot dup stdout\n";

        print STDOUT "\n";
        print STDOUT "\n";
        print STDOUT "$OUR_PROGRAM_NAME: ================== VMMBI PRE-EVENT TRIGGER LOG ==================\n";
        print STDOUT "$OUR_PROGRAM_NAME: ====================== BEGINING OF NEW LOG ======================\n";
        print STDOUT "$OUR_PROGRAM_NAME: ================ ", print_date(),               "================\n";
        print STDOUT "\n";
        print STDOUT "\n";
    }

    $REDIRECT       = $TRUE;
    $logfileOpenOut = 1;

    #***************************************************************************
    #
    # Set to unbuffered if we are saving the output streams
    #
    #***************************************************************************

    if ( $saveStreamsIn)
    {
        STDERR->autoflush(1);
        STDOUT->autoflush(1);

        select(STDOUT);
    }

    #***************************************************************************
    #
    # Set the output parameter
    #
    #***************************************************************************

    return $logfileOpenOut;

}   ## end -- redirect_output()

#***************************************************************************
#
#  reset_output -- Reset the output streams to the saved settings.
#
#
# Input Parameters:
#
#    none
#
# Output Parameters:
#
#    none
#
#***************************************************************************

sub reset_output
{

    close(STDOUT);
    close(STDERR);

    open(STDOUT, ">&SAVEOUT");
    open(STDERR, ">&SAVEERR");

    $REDIRECT = $FALSE;

    return;

}   ## end -- reset_output()

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
# strip -- Strip a sub-string from a full string
#
#
# Input Parameters:
#
#    $SubStringIn       = String to be stripped
#    $FullStringIn      = Full string
#    $ReplaceStringIn   = Replacement string (Optional)
#
# Output Parameters:
#
#    $StringOut = Date or null
#
#***************************************************************************

sub strip
{
    my( $SubStringIn )      = shift;
    my( $FullStringIn )     = shift;
    my( $ReplaceStringIn )  = shift;

    my( $StringOut )        = "";

    my( $i )                = 0;
    my( $len )              = 0;
    my( $p1 )               = "";
    my( $p2 )               = "";

    $StringOut = $FullStringIn;

    $len = length( $SubStringIn );

    if ( $len > 0 )
    {
        $i   = index( $FullStringIn, $SubStringIn );

        if ( $i >= 0 )
        {
            $p1 = substr( $FullStringIn, 0, $i );
            $p2 = substr( $FullStringIn, $i + $len );

            $StringOut = $p1 . $ReplaceStringIn . $p2;
        }
    }
    return( $StringOut );

}   ## end -- strip()

#***************************************************************************
#
# sort_by_ArchivePath() --  Sort by archive path
#
# Input Parameters:
#
#    $a   = First  object
#    $b   = Second object
#
# Output Parameters:
#
#    None
#
#***************************************************************************

sub sort_by_ArchivePath
{
    my( $ArchivePathA ) = "";
    my( $ArchivePathB ) = "";
    my( $result )       = 0;

    $ArchivePathA = $a->{ArchivePath};
    $ArchivePathB = $b->{ArchivePath};

    $result = lc( $ArchivePathA ) cmp lc( $ArchivePathB );

    return( $result );

}   ## end -- sort_by_ArchivePath()

#***************************************************************************
#
# sort_by_WorkFilePath() --  Sorts by file name
#
# Input Parameters:
#
#    $a   = First  item
#    $b   = Second item
#
# Output Parameters:
#
#    None
#
#***************************************************************************

sub sort_by_WorkFilePath
{
    my( $WorkFilePathA ) = "";
    my( $WorkFilePathB ) = "";
    my( $result )        = 0;

    $WorkFilePathA = $a->{WorkFilePath};
    $WorkFilePathB = $b->{WorkFilePath};

    $result = lc( $WorkFilePathA ) cmp lc( $WorkFilePathB );

    return( $result );

}   ## end -- sort_by_WorkFilePath()

#***************************************************************************
#
# sort_by_DateModified() --  Sorts by project name
#
# Input Parameters:
#
#    $a   = First  item
#    $b   = Second item
#
# Output Parameters:
#
#    None
#
#***************************************************************************

sub sort_by_DateModified
{
    my( $DateModifiedA ) = "";
    my( $DateModifiedB ) = "";
    my( $result )        = 0;

    $DateModifiedA = $a->{DateModified};
    $DateModifiedB = $b->{DateModified};

    $result = lc($DateModifiedA) cmp lc($DateModifiedB);

    return( $result );

}   ## end -- sort_by_DateModified()

#no strict;     # Commented out as a temporary fix for Perl Version 5.8.6

#***************************************************************************
#***************************************************************************
#
# End of VMMBI_bldmake_PET.pl
#
#***************************************************************************
#***************************************************************************
