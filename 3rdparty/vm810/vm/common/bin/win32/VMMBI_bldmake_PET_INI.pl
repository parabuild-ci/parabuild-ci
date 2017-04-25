
#**************************************************************************
#
# $Header:   Y:/archives/dv/intersolv/Merant_Build/VM/Integration/Event_Triggers/VMMBI_bldmake_PET_INI.pl-arc   1.6   29 Sep 2003 12:56:18   rogerj  $
#
# Version Manager to Merant Build Integration bldmake Pre-event Trigger INI File
#
# Copyright (c) 2003, MERANT.  All Rights Reserved.
#
#
# W A R N I N G:
#
#    This file is included directly into VMMBI_bldmake_PET_INI.pl thus
#    incorrect updates can cause the event trigger to fail.
#
#**************************************************************************

#**************************************************************************
#
# PDB Path Mapping Facility
#
#
# Description:
#
#    The "mapPDB" function is provided to allow users to map:
#
#       Windows drive letters to UNC or Unix NFS
#       Unix NFS to Windows drive letters
#       UNC to Windows drive letters
#
#    The function is a simple find and replace function limited to the
#    beginning of the Version Manager Project Data Base (PDB) path passed
#    from the Version Manager to Merant Build integration to this Merant
#    Build remote build server. A user may specify as many "mapPDB" statements
#    as desired keeping in mind that the first match is the one that will be
#    used.  A user may optionally choose to specify that the mapping
#    comparison will be performed case sensitive. By default the comparison is
#    performed case insensitive.
#
# Syntax:
#
#    mapPDB('<from_string>'[, '<to_string>'[, 'CS']]);
#
# Where:
#
#    <from_string> = The string to be searched for
#    <to_string>   = The string to be replaced. IF not specified then the
#                    <from_string> will just be deleted.  Must be specified
#                    if the 'CS' parameter is to be specified.
#                    This parameter is optional.  The default is a null
#                    string.
#    'CS'          = Perform case sensitive comparison.
#                    This parameter is optional.  The default is case
#                    insensitive.
#
# Notes:
#
#    (1) Backslashes must be escaped with a second backslash.
#    (2) Single quotes must be used as specified.
#    (3) A syntax error in a "mapPDB" command will cause all remote build
#        jobs on the associated RB server to fail.
#
# Examples:
#
#    mapPDB('x:\\', '\\\\server2\\vol1\\');
#    mapPDB('y:\\', '/buildRoot/vmProjects/');
#    mapPDB('\\\\server1\\vol2\\', 'w:\\', 'CS');
#
# Enter mapping statements following this comment:
#
#**************************************************************************

#**************************************************************************
#
# Disabling the Daylight Savings Time Adjustment Option
#
#
# Description:
#
#    If a particular Merant Build machine has the Microsoft Windows option 
#    “Automatically adjust clock for daylight saving changes”, found on the 
#    “Time Zone” tab of the “Adjust Date/Time” function, selected, then 
#    file times of files retrieved from Merant Version Manager can be off by
#    exactly one hour.  This behavior depends on whether the machine is 
#    inside or outside of daylight savings time and whether a particular 
#    file is inside or outside of daylight savings time. The Version Manager 
#    to Merant Build integration provides the functionality to automatically
#    adjust for this behavior.  If the Windows option is not selected, a 
#    Merant Build user may choose to disable this feature. 
#
# Notes:
#
#    (1) This is a Windows only behavior and thus the option is only available
#        on Windows  
#
# Syntax:
#
#    DST_ADJUSTMENT_OPTION = 0;                    
#
# Where:
#
#    0 = The numerical digit zero
#
# Enter the DST adjust option statement following this comment:
#
#**************************************************************************

#**************************************************************************
#
# This statement must remain as the last statement in this INI file
#
#**************************************************************************

$VMMBI_bldmake_PET_INI_Return = 1;          #  D O   N O T   Change or delete this statement