The SCCAcc.zip file contains the required files to support the Microsoft
Common Source Code Control (MC-SCC) Interface Specification.  The zip
file contains the following files:

- ExPat-Copyright.txt: The copyright file to enclose with the ExPat shareware
- libexpat.dll: The ExPat shareware DLL used to parse XML
- SCCAcc.dll: The AccuRev implementation of the MC-SCC Interface
- SCCAcc-ReadMe.txt (this file): The description and installation instructions

===============================================================================

Installation:
- Unzip the contents of the SCCAcc.zip into the bin directory where AccuRev
  is installed on your local file system (Example: C:/Tools/AccuRev/bin).

- For details on choosing AccuRev as the default SCC Interface and using
  the AccuRev MC-SCC Interface, see the 'AccuRev SCC Integration' section
  of the 'AccuRev Integrations Manual'.

===============================================================================

Registry Entries:
  [HKEY_LOCAL_MACHINE\Software\SourceCodeControlProvider]
    "ProviderRegKey"="Software\AccuRev\AccuRevCM\3.0"
  [HKEY_LOCAL_MACHINE\Software\SourceCodeControlProvider\InstalledSCCProviders]
    "Accurev"="Software\AccuRev\AccuRevCM\3.0"
  [HKEY_LOCAL_MACHINE\Software\Accurev\AccuRevCM\3.0]
    "InstallPath"=<Path-Where-AccuRev-Is-Installed>
    "SCCServerName"="Accurev"
    "SCCServerPath"=<Path-Where-AccuRev-Is-Installed>\bin\SCCAcc.dll

===============================================================================

Products tested with the AccuRev 4.0 MC-SCC Interface
 - Microsoft Visual C++ 6.0
 - Microsoft Visual Basic 6.0
 - Microsoft Visual Studio .NET 2003
 - Microsoft Access 2003

Products tested with a previous version of the AccuRev MC-SCC Interface
 - Microsoft Visual C++ 4.0 and 5.0.
 - Microsoft Visual Studio 97 (C++, Basic, J++, etc.) 
 - Symantec's Visual Cafe (version 2.0 and later?) 
 - Oracle Developer 2000 (?)
 - Powerbuilder (versions 5 & 6) 
 - IBM Visual Age for Java (v2.0, at least) 
 - Allaire ColdFusion Studio and Allaire HomeSite 
 - Sun NetDynamics 

===============================================================================

Short description of Changes Made and Issues Fixed to each version

Version 4.0:
 - 7969: Reduce the number of SCM Connection tests in .NET 2003
 - 7970: Investigate the AccuRev commands used when loading a Project/Solution


Version 3.8:
 - Changed most AccuRev dialogs to support a consistent layout
 - Changed the Run AccuRev dialog to support 'Element' actions and searches
 - Changed the Get dialog to support Get Latest and Update Actions
 - Added a More button to the Get dialog that accesses the AccuRev Run dialog
 - Added a Select Issues dialog when Change Packages is enabled
 - Added a Select Issues dialog when Client Dispatch Promote is enabled
 - Added a Browser For Folder dialog when selecting the project path
 - Enhanced the messages displayed to the console
 - 2339: Support merge of overlap files
 - 3367: Project location change is handled incorrectly
 - 3679: Crashes when trying to run an SCC command while one is already running
 - 4073: Hitting cancel in the Dispatch window should cancel the command
 - 4224: Crashes when AccuRev reports 'Wrong Version'
 - 4486: Restart is required to re-enable SCC after fail to connect
 - 4487: VB 6.x - Check file status before adding the file to a project
 - 4488: VB 6.x - Rename and Promote results in a rename but not a promote
 - 4565: VB 6.x - Rename does not support VB file pairs
 - 4566: Remove From Source Control should leave file on the local file system
 - 4992: The LogOutputBox should scroll with the most recent output
 - 5096: VB 6.x - Loss of connection to server causes application to crash
 - 5242: When a project is not open - Files->Source Control->AccuRev hangs VC#
 - 6241: Don't promote files when any selected files have an overlap status
 - 6758: Hourglass needed when running long AccuRev SCC commands
 - 6778: Update release information on the splash screen to the latest version
 - 6792: .NET 2003 - Needs a more obvious way to display error messages
 - 7183: Refactoring of the Microsoft Common SCC Integration
 - 7271: SCC changes to support for Microsoft Access better
 - 7291: Support for C# Web Projects existing in the web deployment area
 - 7370: Display a dialog with a list of issues when an Issue Number is needed
 - 7475: Display the user friendly values in the Select Issues dialog
