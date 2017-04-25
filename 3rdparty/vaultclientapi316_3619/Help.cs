using System;
using System.Collections;
using System.IO;
using System.Diagnostics;
using System.Reflection;



namespace VaultCmdLineClient
{

	public class CommandHelpEntry
	{
		public string Usage;
		public string ShortDetails;
		public string LongDetails;
		public Option[] OptionList;

		public CommandHelpEntry(string usage, string shortd, string longd, Option[] optlist)
		{
			Usage = usage;
			ShortDetails = shortd;
			LongDetails = longd;
			OptionList = optlist;
		}
	};

	public class OptionHelpEntry
	{
		public string Usage;
		public string Details;

		public OptionHelpEntry(string usage, string det)
		{
			Usage = usage;
			Details = det;
		}
	};

	public class Help
	{
		XMLOutputWriter _xml = null;
		Hashtable commandHelpHash = new Hashtable();
		Hashtable optionHelpHash = new Hashtable();

		public Help(XMLOutputWriter newxml)
		{
			_xml = newxml;


			// Help for commands

			commandHelpHash.Add(Command.ADD, new CommandHelpEntry(
				"repositoryfolder localpath ...",
				"Add files or folders to the repository",
				"ADD will add the files or folders specified by localpath to the repository\nat the specified repositoryfolder.",
				new Option[] { Option.COMMENT, Option.COMMIT }
				));

			commandHelpHash.Add(Command.ADDREPOSITORY, new CommandHelpEntry(
				"repositoryname",
				"Add a new repository to the server",
				"ADDREPOSITORY will create a new repository on the server named\nrepositoryname",
				new Option[] { }
				));

			commandHelpHash.Add(Command.ADDUSER, new CommandHelpEntry(
				"username password emailaddress",
				"Add a new user to the server",
				"ADDUSER will create a new user on the server, specified by username,\npassword and emailaddress.",
				new Option[] { }
				));

			// Help for options
			commandHelpHash.Add(Command.BATCH, new CommandHelpEntry(
				"- | filename",
				"Place the command line client in batch mode", 
				"Batching allows multiple vault commands to be executed within one 'run'.\n\nUse - (hyphen) to specify the batch commands can be found on standard input.\nOtherwise, provide the name of a file where the vault commands can be located.\nIn batch mode, please provide only one vault command per line.", 
				new Option[] { } 
				));

			commandHelpHash.Add(Command.BLAME, new CommandHelpEntry(
				"repositorypath linenumber [endversion]",
				"Determine the user that modified a given line",
				"BLAME will determine the last user that modified\nthe given line of a file.\nOptionally, you can specify the version number of the file that the line \nnumber references.  If no version is specified, the latest version is assumed.",
				new Option[] { }
				));

			commandHelpHash.Add(Command.BRANCH, new CommandHelpEntry(
				"repositoryfolder torepositoryfolder",
				"Create a branch for a repository folder",
				"BRANCH will create a branch for the repository folder specified\nby repositoryfolder at torepositoryfolder.",
				new Option[] { Option.COMMENT, Option.COMMIT }
				));

			commandHelpHash.Add(Command.CHECKOUT, new CommandHelpEntry(
				"repositorypath ...",
				"Checkout files from the repository",
				"CHECKOUT will checkout files from the repository.",
				new Option[] { Option.BACKUP, Option.EXCLUSIVE, Option.MERGE, Option.SETFILETIME, Option.PERFORMDELETIONS, Option.WILDCARD, Option.NORECURSIVE}
				));

			commandHelpHash.Add(Command.CLOAK, new CommandHelpEntry(
				"repositoryfolder ...",
				"Cloak repository folders",
				"CLOAK will cloak a repository folder specified by repositoryfolder,\nmeaning that folder will be ignored during recursive GET\noperations.",
				new Option[] { }
				));

			CommandHelpEntry commitHelpEntry = new CommandHelpEntry(
				"[repositorypath ...]",
				"Commit items in the pending changeset",
				"COMMIT and CHECKIN will commit the items in the pending changeset list\nspecified by repositorypath(s).  If no items are specified on the command line,\nall items in the pending changeset list are committed.  The \"unchanged\" option \nspecifies whether unmodified files are checked in.",
				new Option[] { Option.COMMENT, Option.KEEPCHECKEDOUT, Option.UNCHANGED }
				);
			commandHelpHash.Add(Command.COMMIT, commitHelpEntry );
			commandHelpHash.Add(Command.CHECKIN, commitHelpEntry );

			commandHelpHash.Add(Command.CREATEFOLDER, new CommandHelpEntry(
				"repositorypath",
				"Create a new folder in the repository",
				"CREATEFOLDER will create a folder in the repository at the point\nspecified by repositorypath.",
				new Option[] { Option.COMMENT, Option.COMMIT }
				));

			commandHelpHash.Add(Command.DELETE, new CommandHelpEntry(
				"repositorypath ...",
				"Delete files or folders from the repository",
				"DELETE will delete the files or folders specified by the\nrepositorypath(s) from the repository.",
				new Option[] { Option.COMMENT, Option.COMMIT }
				));

			commandHelpHash.Add(Command.DELETELABEL, new CommandHelpEntry(
				"repositorypath label_name",
				"Delete a label that was applied to a file or folder",
				"DELETELABEL will delete a label that was applied to the input file or folder.",
				new Option[] { }
				));

			commandHelpHash.Add(Command.DIFF, new CommandHelpEntry(
				"repositorypath", 
				"Diff a working file or folder", 
				"Diff will compare a working file or folder with another file or folder\nspecified by the -compareto option.  The working file or folder is specified\nby its repository path.", 
				new Option[] { Option.COMPARETO, Option.NORECURSIVE, Option.VAULTDIFF, Option.VAULTDIFF_OPTIONS }
				));

			commandHelpHash.Add(Command.FORGETLOGIN, new CommandHelpEntry(
				"",
				"Remove locally stored login information",
				"FORGETLOGIN will remove all login information being locally stored\nby the REMEMBERLOGIN command.",
				new Option[] {}
				));

			commandHelpHash.Add(Command.GET, new CommandHelpEntry(
				"repositorypath ...",
				"Retrieve files or folders from the repository",
				"GET will retrieve the latest version of files or folders in the repository\nto the currently defined working folder.  Use SETWORKINGFOLDER if there is\nno working folder, or -destpath to retrieve files to a non-working folder.",
				new Option[] { Option.BACKUP, Option.DESTPATH, Option.MAKEWRITABLE, Option.MAKEREADONLY, Option.MERGE, Option.NOCLOAKS, Option.NORECURSIVE, Option.PERFORMDELETIONS, Option.SETFILETIME, Option.VERBOSE }
				));

			commandHelpHash.Add(Command.GETLABEL, new CommandHelpEntry(
				"repositorypath label [labelpath]",
				"Retrieve files or folders specified by label",
				"GETLABEL will retrieve files or folders specified by repositorypath\nat the version at which they were labelled with the specified label.\nUse -labelworkingfolder (with path) to get the label to a working folder,\nuse -destpath (with path) specify a non-working folder.\nDo not set labelpath unless prompted to.",
				new Option[] { Option.BACKUP, Option.LABELWORKINGFOLDER, Option.DESTPATH, Option.MAKEWRITABLE, Option.MAKEREADONLY, Option.MERGE, Option.NORECURSIVE, Option.SETFILETIME, Option.VERBOSE }
				));

			commandHelpHash.Add(Command.GETLABELDIFFS, new CommandHelpEntry(
				"repositorypath label1 [labelpath2]",
				"Retrieve history for files changed between specified labels.",
				"GETLABELDIFFS will retrieve the histories for all files in the specified\nrepository folder between the specified labels.\n" + 
				"If only one label is specified, retrieves history from that label to present.",
				new Option[] { Option.ROWLIMIT}
				));
			
			commandHelpHash.Add(Command.GETVERSION, new CommandHelpEntry(
				"version repositorypath localfolder",
				"Retrieve files or folder at a specified version",
				"GETVERSION will retrieve a file or folder specified by repositorypath\nat the given version and place it into localfolder.",
				new Option[] { Option.BACKUP, Option.MAKEWRITABLE, Option.MAKEREADONLY, Option.MERGE, Option.NOCLOAKS, Option.NORECURSIVE, Option.SETFILETIME, Option.VERBOSE }
				));

			commandHelpHash.Add(Command.GETWILDCARD, new CommandHelpEntry(
				"repositoryfolder wildcard ...",
				"Retrieve files whose names match a wildcard",
				"GETWILDCARD will retrieve all files within the folder specified by\nrepositoryfolder whose name matches one of the wildcards specified.\nYou may use '?' to match a single character or '*' to match a range of\ncharacters.",
				new Option[] { Option.BACKUP, Option.DESTPATH, Option.MAKEWRITABLE, Option.MAKEREADONLY, Option.MERGE, Option.NOCLOAKS, Option.NORECURSIVE, Option.PERFORMDELETIONS, Option.SETFILETIME, Option.VERBOSE }
				));

			commandHelpHash.Add(Command.HELP, new CommandHelpEntry(
				"[command]",
				"Displays this page or help for a specific command",
				"HELP will display a list of all available commands, or detailed help for\na specified command.",
				new Option[] { }
				));

			commandHelpHash.Add(Command.HELPHTML, new CommandHelpEntry(
				"[command]",
				"Displays this page or help for a specific command in HTML format",
				"HELP will display a list of all available commands, or detailed help for\na specified command in HTML format.",
				new Option[] { }
				));
					
			commandHelpHash.Add(Command.HISTORY, new CommandHelpEntry(
				"repositorypath",
				"Shows history for an item in the repository",
				"HISTORY will display all committed actions to a file or folder in the\nrepository specified by repositorypath.",
				new Option[] { Option.ROWLIMIT, Option.DATESORT, Option.BEGINDATE, Option.BEGINLABEL, Option.ENDDATE, Option.ENDLABEL, Option.NORECURSIVE, Option.EXCLUDEACTIONS, Option.EXCLUDEUSERS }
				));

			commandHelpHash.Add(Command.VERSIONHISTORY, new CommandHelpEntry(
				"repositoryfolder",
				"Shows version history for a folder in the repository",
				"VERSIONHISTORY will display all versions of a folder in the\nrepository specified by repositoryfolder.",
				new Option[] { Option.ROWLIMIT, Option.BEGINDATE, Option.ENDDATE, Option.BEGINVERSION }
				));

			commandHelpHash.Add(Command.LABEL, new CommandHelpEntry(
				"repositorypath labelname [version]",
				"Applies labelname to version of repositorypath.",
				"Applies label to version of repositorypath, which can\nbe used later for GETLABEL requests.  If no version\nis specified, the current version is labelled.",
				new Option[] { Option.COMMENT }
				));

			commandHelpHash.Add(Command.LISTALLBRANCHPOINTS, new CommandHelpEntry(
				"",
				"Show all branch points in the repository",
				"LISTALLBRANCHPOINTS will display all branch points in the repository.",
				new Option[] { }
				));

			commandHelpHash.Add(Command.LISTALLTXDETAILS, new CommandHelpEntry(
				"",
				"List every transaction in the repository with details of its changeset",
				"LISTALLTXDETAILS will display all changesets in the repository.",
				new Option[] { }
				));

			commandHelpHash.Add(Command.LISTCHANGESET, new CommandHelpEntry(
				"",
				"Show the contents of the pending changeset",
				"LISTCHANGESET will display the contents of the pending changeset.",
				new Option[] { }
				));

			commandHelpHash.Add(Command.LISTCHECKOUTS, new CommandHelpEntry(
				"",
				"Show all check out items for the current repository",
				"LISTCHECKOUTS will display the check out items for the current repository.",
				new Option[] { }
				));

			commandHelpHash.Add(Command.LISTFOLDER, new CommandHelpEntry(
				"repositoryfolder",
				"Show contents and status of a repository folder",
				"LISTFOLDER will display the contents of the folder specified by\nrepositoryfolder, including the status of any working folders",
				new Option[] { Option.NORECURSIVE }
				));

			commandHelpHash.Add(Command.LISTREPOSITORIES, new CommandHelpEntry(
				"",
				"List available repositories on the server",
				"LISTREPOSITORIES will list all repositories on the server.",
				new Option[] { }
				));

			commandHelpHash.Add(Command.LISTUSERS, new CommandHelpEntry(
				"",
				"List all users on the server",
				"LISTUSERS will list all users on the server.",
				new Option[] {Option.VERBOSE, Option.REPOSITORY }
				));

			commandHelpHash.Add(Command.LISTWORKINGFOLDERS, new CommandHelpEntry(
				"",
				"List local working folder associations",
				"LISTWORKINGFOLDERS will list all current associations between repository\nfolders and local working folders.",
				new Option[] { }
				));

			commandHelpHash.Add(Command.MOVE, new CommandHelpEntry(
				"repositorypath destrepositoryfolder",
				"Move a file or folder to a different folder",
				"MOVE will move the file or folder specified by repositorypath into the\nrepository folder specified by destrepositoryfolder.",
				new Option[] { Option.COMMENT, Option.COMMIT }
				));

			commandHelpHash.Add(Command.OBLITERATE, new CommandHelpEntry(
				"repositorypath",
				"Obliterate a deleted item",
				"OBLITERATE will permanently remove a deleted folder or file (and all \nits history) from the repository.  You must be logged in as a administrator \nuser to use this command.  This command should not be used lightly, as there \nis no way to undo it.",
				new Option[] { Option.YESIAMSURE }
				));
			
			commandHelpHash.Add(Command.PIN, new CommandHelpEntry(
				"repositorypath [version]",
				"Pin a file or folder",
				"PIN will pin the file or folder specified by repositorypath at the\nspecified version, or latest version if no version specified.",
				new Option[] { Option.COMMENT, Option.COMMIT }
				));

			commandHelpHash.Add(Command.REMEMBERLOGIN, new CommandHelpEntry(
				"",
				"Store authentication information",
				"REMEMBERLOGIN will store server, repository and authentication information\non the local host so that it does not need to be reentered.",
				new Option[] {Option.REPOSITORY }
				));

			commandHelpHash.Add(Command.RENAME, new CommandHelpEntry(
				"repositorypath destrepositorypath",
				"Rename a file or folder",
				"RENAME will rename a file or folder specified by repositorypath to the new\nname destrepositorypath.",
				new Option[] { Option.COMMENT, Option.COMMIT }
				));

			commandHelpHash.Add(Command.RENAMELABEL, new CommandHelpEntry(
				"repositorypath old_label_name new_label_name",
				"Rename a file or folder label",
				"RENAMELABEL will rename a label associated with a file or folder specified\nby repositorypath.",
				new Option[] { Option.COMMENT, Option.COMMIT }
				));

			commandHelpHash.Add(Command.SETWORKINGFOLDER, new CommandHelpEntry(
				"repositoryfolder localfolder",
				"Set a working folder association",
				"SETWORKINGFOLDER will set the working folder for the specified repository\nfolder to be the local folder.  All future operations on repositoryfolder\nwill use localfolder as a working folder by default.",
				new Option[] { }
				));

			commandHelpHash.Add(Command.SHARE, new CommandHelpEntry(
				"repositorypath destrepositoryfolder",
				"Share a file or folder into a different folder",
				"SHARE will share a file or folder specified by repositorypath into the\nrepository folder specified by destrepositoryfolder",
				new Option[] { Option.COMMENT, Option.COMMIT }
				));

			commandHelpHash.Add(Command.UNCLOAK, new CommandHelpEntry(
				"repositoryfolder",
				"Uncloak a folder",
				"UNCLOAK will uncloak specified by repositoryfolder.",
				new Option[] { }
				));

			commandHelpHash.Add(Command.UNDOCHANGESETITEM, new CommandHelpEntry(
				"changeset item id", 
				"Remove an item from the change set", 
				"UNDOCHANGESETITEM will remove the change set item from the change set list.",
				new Option[] {}
				));

			commandHelpHash.Add(Command.UNDOCHECKOUT, new CommandHelpEntry(
				"repositorypath",
				"Undo checkout (revert changes)",
				"UNDOCHECKOUT will undo a checkout, reverting changes back to the data in\nthe repository.",
				new Option[] { Option.NORECURSIVE }
				));

			commandHelpHash.Add(Command.UNPIN, new CommandHelpEntry(
				"repositorypath",
				"Unpin a file or folder",
				"UNPIN will unpin the file or folder specified by repositorypath.",
				new Option[] { Option.COMMENT, Option.COMMIT }
				));

			commandHelpHash.Add(Command.UNSETWORKINGFOLDER, new CommandHelpEntry(
				"repositoryfolder",
				"Remove a working folder association",
				"UNSETWORKINGFOLDER will remove the local working folder association for\nthe specified repositoryfolder.",
				new Option[] { }
				));


			///////////////////////////////
			//  help for options
			///////////////////////////////

			optionHelpHash.Add(Option.BACKUP, new OptionHelpEntry(
				"[yes|no]",
				"Whether to backup locally modified files before overwriting.  If not\n\tspecified, the user's default value is used."
				));
			optionHelpHash.Add(Option.BEGINDATE, new OptionHelpEntry(
				"local date [ time]",
				"Date to begin history at"
				));
			optionHelpHash.Add(Option.BEGINLABEL, new OptionHelpEntry(
				"labelstring",
				"A Label that was applied to the target of the history query.  \n\tThe date of this label will be used to determine the \n\tstart point of the history query."
				));
			optionHelpHash.Add(Option.BEGINVERSION, new OptionHelpEntry(
				"versionnumber",
				"The version number from which version history should begin."
				));
			optionHelpHash.Add(Option.COMMENT, new OptionHelpEntry(
				"commentstring",
				"Checkin comment"
				));

			optionHelpHash.Add(Option.COMMIT, new OptionHelpEntry(
				"",
				"Commit this action automatically"
				));

			optionHelpHash.Add(Option.COMPARETO, new OptionHelpEntry(
				"[current|label|lastget|local|repository] [compareto_object]",
				"\nType of comparison to be made against your local working version. For\n'label', 'local', and 'repository', specify the compareto_object, which\nis a label name, local path or repository path.\n\ncurrent\t- the current repository item\nlabel\t- the version found within the given label\nlastget\t- the last version retrieved from the repository\nlocal\t- any local file system item\nrepository- any item within the repository"
				));

			optionHelpHash.Add(Option.DATESORT, new OptionHelpEntry(
				"[asc | desc]",
				"Sort the history results in ascending or descending date order."
				));

			optionHelpHash.Add(Option.DESTFOLDER, new OptionHelpEntry(
				"localfolder",
				"Use localfolder for actions instead of any existing working folder"
				));

			optionHelpHash.Add(Option.LABELWORKINGFOLDER, new OptionHelpEntry(
				"localfolder",
				"Use localfolder as the working folder for a label get"
				));

			optionHelpHash.Add(Option.DESTPATH, new OptionHelpEntry(
				"localfolder",
				"Instead of retrieving files to the currently defined working folder, use\nthis folder.  Note this does not update state information and you cannot\ncheckin files from a non-working folder."
				));

			optionHelpHash.Add(Option.ENDDATE, new OptionHelpEntry(
				"local date [ time]",
				"Date to end history display at"
				));
			optionHelpHash.Add(Option.ENDLABEL, new OptionHelpEntry(
				"labelstring",
				"A Label that was applied to the target of the history query.  \n\tThe date of this label will be used to determine the \n\tend point of the history query."
				));
			optionHelpHash.Add(Option.EXCLUDEACTIONS, new OptionHelpEntry(
				"action,action,...",
				"A comma-separated list of actions that will be excluded from \n\tthe history query.  Valid actions to exclude are: \n\tadd, branch, checkin, create, delete, label, move, obliterate, pin, \n\tpropertychange, rename, rollback, share, snapshot, undelete"
				));
			optionHelpHash.Add(Option.EXCLUDEUSERS, new OptionHelpEntry(
				"user,user,...",
				"A comma-separated list of actions that will be excluded from \n\tthe history query. "
				));
			optionHelpHash.Add(Option.EXCLUSIVE, new OptionHelpEntry(
				"",
				"Will check out items exclusively"
				));

			optionHelpHash.Add(Option.HOST, new OptionHelpEntry(
				"host",
				"Hostname of the vault server to connect to. Can also use \"-server\""
				));

			optionHelpHash.Add(Option.KEEPCHECKEDOUT, new OptionHelpEntry(
				"",
				"all files remain checked out upon commit"
				));

			optionHelpHash.Add(Option.LEAVEFILE, new OptionHelpEntry(
				"",
				"will leave the file unchanged on an undo checkout"
				));

			optionHelpHash.Add(Option.MAKEWRITABLE, new OptionHelpEntry(
				"",
				"Make all files writable after retrieval"
				));

			optionHelpHash.Add(Option.MAKEREADONLY, new OptionHelpEntry(
				"",
				"Make all files read-only after retrieval"
				));

			optionHelpHash.Add(Option.MERGE, new OptionHelpEntry(
				"[automatic|later|overwrite]",
				string.Format("The action to take when updating a local file with new content.\n\nautomatic*\t- attempt to merge changes from the server\nlater\t\t- do not overwrite an existing, modified file\noverwrite\t\t- overwrite the local file with the server's file\n\n* - only applies to {0} and {1} commands\n", Command.GET, Command.GETWILDCARD)
				));

			optionHelpHash.Add(Option.NOCLOAKS, new OptionHelpEntry(
				"",
				"Performs actions on all folders even if they were previously cloaked"
				));

			optionHelpHash.Add(Option.NORECURSIVE, new OptionHelpEntry(
				"",
				"Do not act recursively on folders"
				));

			optionHelpHash.Add(Option.NOSSL, new OptionHelpEntry(
				"",
				"Disable SSL for server connection"
				));

			optionHelpHash.Add(Option.OUT, new OptionHelpEntry(
				"filename",
				"Will write output log in XML format to filename"
				));

			optionHelpHash.Add(Option.PASSWORD, new OptionHelpEntry(
				"password",
				"Password to use when connecting to server"
				));

			optionHelpHash.Add(Option.PERFORMDELETIONS, new OptionHelpEntry(
				"[donotremoveworkingcopy|removeworkingcopy|removeworkingcopyifunmodified]",
				string.Format("When getting a folder, this option controls whether files deleted in the \nrepository are deleted in the working folder.  The default is \ndonotremoveworkingcopy.  This option only applies to {0}, {1} \nand {2} commands", Command.GET, Command.GETWILDCARD, Command.CHECKOUT)
				));

			optionHelpHash.Add(Option.PROXYSERVER, new OptionHelpEntry(
				"proxyserver",
				"Server name or url for the proxy to use when connecting."
				));

			optionHelpHash.Add(Option.PROXYPORT, new OptionHelpEntry(
				"proxyport",
				"Port to use to connect to the proxy."
				));

			optionHelpHash.Add(Option.PROXYUSER, new OptionHelpEntry(
				"proxyuser",
				"Username for proxy authentication."
				));

			optionHelpHash.Add(Option.PROXYPASSWORD, new OptionHelpEntry(
				"proxypassword",
				"Password for proxy authentication."
				));

			optionHelpHash.Add(Option.PROXYDOMAIN, new OptionHelpEntry(
				"proxydomain",
				"Domain for proxy authentication."
				));

			optionHelpHash.Add(Option.REPOSITORY, new OptionHelpEntry(
				"repositoryname",
				"Repository to connect to"
				));

			optionHelpHash.Add(Option.REQUIRECHECKOUT, new OptionHelpEntry(
				"",
				"Requires checkouts of files before commit can be performed on\nmodified files"
				));

			optionHelpHash.Add(Option.REVERTFILE, new OptionHelpEntry(
				"",
				"will revert the file to its last known version for an undo checkout"
				));

			optionHelpHash.Add(Option.ROWLIMIT, new OptionHelpEntry(
				"limitnumber",
				"Limits the number of rows returned for a history query to\nlimitnumber"
				));

			optionHelpHash.Add(Option.SERVER, new OptionHelpEntry(
				"server",
				"name of the vault server to connect to - can also use " + Option.HOST
				));

			optionHelpHash.Add(Option.SETFILETIME, new OptionHelpEntry(
				"checkin|current|modification",
				"Sets the time of the local file.\n\ncheckin\t\t- use the last checkin time\ncurrent\t\t- use the current system time\nmodification\t- use the file's last modified time"
				));

			optionHelpHash.Add(Option.SSL, new OptionHelpEntry(
				"",
				"Enables SSL for server connection"
				));

			optionHelpHash.Add(Option.UNCHANGED, new OptionHelpEntry(
				"checkin|leavecheckedout|undocheckout",
				"Action to perform on an unchanged, checked-out file during commit.\nCheck the file in unmodified, leave the file checked out, or\nundo the checkout of the file.  The default is \"undocheckout\"."
				));

			optionHelpHash.Add(Option.URL, new OptionHelpEntry(
				"serverurl",
				"URL of the server"
				));

			optionHelpHash.Add(Option.USER, new OptionHelpEntry(
				"username",
				"Username to use when connecting to server."
				));

			optionHelpHash.Add(Option.USERNAME, new OptionHelpEntry(
				"username",
				"Username to use when connecting to server - can also use " + Option.USER
				));

			optionHelpHash.Add(Option.VAULTDIFF, new OptionHelpEntry(
				"[absolute path to comparison utility]",
				"Use this comparison utility for differences."
				));

			optionHelpHash.Add(Option.VAULTDIFF_OPTIONS, new OptionHelpEntry(
				"\"[any options]\"", 
				"Options for the comparison utility.\n\tUse double quotes for multiple options."
				));

			optionHelpHash.Add(Option.WILDCARD, new OptionHelpEntry(
				"[wildcard_string]", 
				"Applies operation only to files that match the wildcard string\nUse '?' to match a single character or '*' to match a range of characters."
				));

			optionHelpHash.Add(Option.VERBOSE, new OptionHelpEntry(
				"",
				"Turn on verbose mode"
				));

			optionHelpHash.Add(Option.YESIAMSURE, new OptionHelpEntry(
				"",
				"This option is required to confirm an Obliterate command.  Obliterate is a destructive and non-reversible command that alters the history of the Vault database.  It should not be used lightly."
				));
		}

		public void Copyright(XMLOutputWriter xml)
		{
			Assembly thisAssembly = System.Reflection.Assembly.GetExecutingAssembly();
			Version thisVersion = thisAssembly.GetName().Version;
			AssemblyCopyrightAttribute copyrightAttribute = (AssemblyCopyrightAttribute)thisAssembly.GetCustomAttributes(typeof(AssemblyCopyrightAttribute), false)[0];
			xml.WriteContent("SourceGear Vault Command Line Client " + thisVersion.ToString());
			xml.WriteContent(copyrightAttribute.Copyright.ToString());
			xml.WriteContent();
		}

		public void Write()
		{
			_xml.Begin("usage");

	
			Copyright(_xml);

			_xml.WriteContent("usage: vault commandname [options] [parameters]");
			_xml.WriteContent();
			
			_xml.WriteContent("This is a list of possible commands:");

			foreach (Command cmd in Enum.GetValues(typeof(Command)))
			{
				if(cmd == Command.NONE || cmd == Command.INVALID)
					continue;

				string cmdName = cmd.ToString().ToUpper();

				CommandHelpEntry he = (CommandHelpEntry) commandHelpHash[cmd];

				if(he != null)
				{
					_xml.WriteContent(string.Format("  {0,-22}{1}", cmdName, he.ShortDetails));
				}
				else
				{
					_xml.WriteContent(string.Format("  {0,-22}", cmdName));
				}
			}

			_xml.WriteContent();
			_xml.WriteContent("For help with a specific command, run 'vault.exe HELP commandname'.");

			_xml.End();
		}
	
		public void WriteHTML()

		{
			try
			{
				//create a new html file
				FileInfo fi = new FileInfo("./help.htm");
  
				StreamWriter sw = fi.CreateText();
				sw.AutoFlush = true;
                    
				string heading = string.Empty;
				string text = string.Empty;
				string text1 = string.Empty;
				string text2 = string.Empty;
								                
				heading = string.Format("<h><b>SourceGear Vault Command Line Client</b></h><br>");		
				heading +=string.Format("<p>usage: vault commandname [options] [parameters]</p>");
				heading +=string.Format("<p>This is a list of possible commands:</p>");

				text += ("<table cellpadding=\"5\" border=\"1\" width=\"1000\">");
				text1 += ("<table cellpadding=\"5\" border=\"1\" width=\"1000\">");
													
				foreach (Command cmd in Enum.GetValues(typeof(Command)))

				{        
					if(cmd == Command.NONE || cmd == Command.INVALID)
						continue;
 
					string cmdName = cmd.ToString().ToUpper();

					CommandHelpEntry he = (CommandHelpEntry) commandHelpHash[cmd];

					if(he != null)
					{                  
						text += string.Format("<tr><td><a rel=index href=#{0}>{0}</a></tr><td>{1}</td></tr>", cmdName, he.ShortDetails);				
						if (he.OptionList != null)
						{
							if(he.OptionList.Length == 0)
							{
								text1 += string.Format("<tr><td><a rel=index name=#{0}><b>{0}</b></tr>", cmdName);
								text1 += string.Format("<tr><td>usage: vault.exe {0} {1}<br>", cmdName.ToUpper(), he.Usage);
								text1 += string.Format("<br>{0}<br>", he.LongDetails);
							}
							else
							{
								text1 += string.Format("<tr><td><a rel=index name=#{0}><b>{0}</b></tr>", cmdName);
								text1 += string.Format("<tr><td>usage: vault.exe {0} [options] {1}<br>", cmdName.ToUpper(), he.Usage);
								text1 += string.Format("<br>{0}<br>", he.LongDetails);
							}
						}
											
					}
					else
					{
						text += string.Format("<tr><td><a rel=index href=#{0}>{0}</a></tr><td>{1}</td></tr>", cmdName, he.ShortDetails);
					}
						
				
							
					if(VaultCmdLineClient.CommandNeedsLogin(cmd))
					{
						Option[] optionList = null;

						if(VaultCmdLineClient.CommandNeedsRepositorySpecified(cmd))
						{
							optionList = new Option[] { Option.HOST, Option.SSL, Option.USER, Option.PASSWORD, Option.REPOSITORY };
						}
						else
						{
							optionList = new Option[] { Option.HOST, Option.SSL, Option.USER, Option.PASSWORD };
						}

						text1 += string.Format("<br>Server and authentication information is specified by:");

						foreach(Option opt in optionList )
						{
														
							string optName = opt.ToString().ToLower();

							OptionHelpEntry ohe = (OptionHelpEntry) optionHelpHash[opt];

							if(ohe != null)
							{
								string details = ohe.Details.Replace("\n", "\n      ");
								text1 += string.Format("<ul>  -{0} {1}\n      <ul>{2}</ul></ul>", optName, ohe.Usage, details);
							}
							else
							{
								text1 += string.Format("<ul>  -{0}</ul>", optName);
							}
					
						}

					}

					if(he.OptionList.Length > 0)
					{
						text1 +=string.Format("This is a list of possible options:<p>");
						
						foreach(Option opt in he.OptionList)
						{
							string optName = opt.ToString().ToLower();
					
							OptionHelpEntry ohe = (OptionHelpEntry) optionHelpHash[opt];
					
							if(ohe != null)
							{
								string details = ohe.Details.Replace("\n", "\n      ");
								text1 +=string.Format("<ul>  -{0} {1}\n      <ul>{2}</ul></ul>", optName, ohe.Usage, details);
							}
							else
							{
								text1 +=string.Format("<ul>  -{0}<ul></td>", optName);
							}
						}
					}
														
				}		

				text += ("</table>");    
				text1 +=("</table>");
				text1 = text1.Replace("\n","<br>");
		
				sw.Write(heading);
				sw.Write(text);
				text2 += string.Format("<p></p>");
				sw.Write(text2);
				sw.Write(text1);	
				sw.Close();

				Process p = new Process();
				p.StartInfo.FileName = fi.Name;
				p.Start();
			}
			catch (Exception)
			{
				throw new UsageException(string.Format("Unable to process the HTML file.  Please use the 'Vault Help' command instead."));
			}

		}

		public void Write(string cmdName)
		{
			Command cmd = Command.INVALID;
			foreach(Command testcmd in Enum.GetValues(typeof(Command)))
			{
				if(testcmd.ToString().ToLower() == cmdName.ToLower())
					cmd = testcmd;
			}

			if(cmd == Command.INVALID || cmd == Command.NONE)
			{
				throw new UsageException(string.Format("unknown command: {0} - run 'vault.exe HELP' for help", cmdName.ToUpper()));
			}

			CommandHelpEntry he = (CommandHelpEntry) commandHelpHash[cmd];

			_xml.Begin("usage");

			Copyright(_xml);

			if(he.OptionList.Length == 0)
			{
				_xml.WriteContent(string.Format("usage: vault.exe {0} {1}", cmdName.ToUpper(), he.Usage));
			}
			else
			{
				_xml.WriteContent(string.Format("usage: vault.exe {0} [options] {1}", cmdName.ToUpper(), he.Usage));
			}
			_xml.WriteContent();

			_xml.WriteContent(he.LongDetails);
			_xml.WriteContent();

			if(VaultCmdLineClient.CommandNeedsAdmin(cmd))
			{
				_xml.WriteContent("This command requires administrative privileges.");
				_xml.WriteContent();
			}

			if(VaultCmdLineClient.CommandNeedsLogin(cmd))
			{
				Option[] optionList = null;

				if(VaultCmdLineClient.CommandNeedsRepositorySpecified(cmd))
				{
					optionList = new Option[] { Option.HOST, Option.SSL, Option.USER, Option.PASSWORD, Option.PROXYSERVER, Option.PROXYPORT, Option.PROXYUSER, Option.PROXYPASSWORD, Option.PROXYDOMAIN, Option.REPOSITORY };
				}
				else
				{
					optionList = new Option[] { Option.HOST, Option.SSL, Option.USER, Option.PASSWORD, Option.PROXYSERVER, Option.PROXYPORT, Option.PROXYUSER, Option.PROXYPASSWORD, Option.PROXYDOMAIN };
				}

				_xml.WriteContent("Server and authentication information is specified by:");

				foreach(Option opt in optionList )
				{
					string optName = opt.ToString().ToLower();

					OptionHelpEntry ohe = (OptionHelpEntry) optionHelpHash[opt];

					if(ohe != null)
					{
						string details = ohe.Details.Replace("\n", "\n      ");
						_xml.WriteContent(string.Format("  -{0} {1}\n      {2}", optName, ohe.Usage, details));
					}
					else
					{
						_xml.WriteContent(string.Format("  -{0}", optName));
					}
				}

				_xml.WriteContent();
			}

			if(he.OptionList.Length > 0)
			{
				_xml.WriteContent("This is a list of possible options:");

				foreach(Option opt in he.OptionList)
				{
					string optName = opt.ToString().ToLower();

					OptionHelpEntry ohe = (OptionHelpEntry) optionHelpHash[opt];

					if(ohe != null)
					{
						string details = ohe.Details.Replace("\n", "\n      ");
						_xml.WriteContent(string.Format("  -{0} {1}\n      {2}", optName, ohe.Usage, details));
					}
					else
					{
						_xml.WriteContent(string.Format("  -{0}", optName));
					}
				}
			}

			_xml.End();
		}
	}



}
