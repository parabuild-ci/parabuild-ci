# $Header:   /sources/archives/dv/Vms/tools/xlate/rcs2vm.awv   1.2   10 Mar 2005 14:07:02   sschultz  $
#
# Wendy Melcher 9/12/97 - Changed the array ENV to ENVIRON. ENV was used for poly Awk, We now use
#			  MKS awk so the array has to be ENVIRON.
# Wendy Melcher 9/15/97 - Added environment variables RCS_BIN and PVCS_BIN, This will allow for a
#			  fully qualified path and not rely on the PATH environment pointing to those
#			  directories. 
# Wendy Melcher 9/16/97 - Added a check to get_opsys for the presence of WinNT.
#
# Maciej Chodzko-Zajko 7/23/98 - Added a routine to warn about labels that could not be added, but 
#			  that continues with the conversion.  
#
# Maciej Chodzko-Zajko 8/7/98 - Turn off translation for archives just in case
#
# Maciej Chodzko-Zajko 18/1/2000 - Add lock handling routines
#
# Maciej Chodzko-Zajko 19/1/2000 - Abort if on Windows if no TZ variable set.  Required for -M in RCS.
#
# Maciej Chodzko-Zajko 15/8/2000 - Allow for spaces in a version label
#
#
# Maciej Chodzko-Zajko 17/5/2001 - rcs_suff added.  This variable allows for easy modification of
#			the script where there is no default RCS suffix - usually Windows.
#
# Maciej Chodzko-Zajko 14/8/2001 - fixed some NT-only bugs to do with labels and spaces.
#			Also removed some pointless messages when not in debug mode.
#
# Maciej Chodzko-Zajko 08/10/2001 - fixed bug when first line of comment starts with "="
#			


#
# rcs2vm.awk - Awk script for converting an RCS archive to a PVCS
#                Version Manager archive.
#
#
# Usage: awk -f rcs2vm.awk rcs_archive pvcs_archive [debug]
#
# This is an unsupported utility.  Use at your own risk.
#

BEGIN {

	#
	# Initialize optional arguments
	debug = "N";

 	# set these options to ON in order to display debug info
 	debug_main = "OFF";
 	debug_rcs_archive = "OFF";
 	debug_pvcs_init = "OFF";
 	debug_opsys = "OFF";
 	debug_execute = "OFF";

	#
	# Sign-on

	progname = "rcs2vm";
	printf("RCS-2-Merant VM Converter (%s) V3.1 Build(3.5)\n", progname);
	printf("Copyright 1995-2003 Merant.  All rights reserved.\n");
   #replaced PVCS with Merant VM and updated copyright date

	#
	# Get program arguments
	if (! get_args())
			error_exit("usage: rcs2vm rcs_archive pvcs_archive [debug]");

	#
	# Validate optional arguments
	if ((debug != "Y") && (debug != "y") && (debug != "N") && (debug != "n"))
		error_exit("\"debug\" can be Y/y or N/n");

	if ((debug == "Y") || (debug == "y")) {
	 	debug_main = "ON";
	 	debug_rcs_archive = "ON";
	 	debug_pvcs_init = "ON";
	 	debug_opsys = "ON";
 		debug_execute = "ON";
	        debug_create_description= "ON";		# don't know who added this, Maciej  23/10/2003
	}

	#
	# Determine operating system
	opsys = get_opsys();

	if (debug_opsys == "ON") {
		printf("Operating System = (%s) \n", opsys);
		printf("Timezone = (%s) \n", tz);
	}

	if ((opsys != "UNIX") && (tz == ""))
		error_exit("On a WINTEL platform, timezone (TZ) is required");


	#
	# Initialize global variables
	workfile = "";			# workfile name from RCS archive
	accesslist = "";			# RCS accesslist
	description = "";			# archive description
	locking = "";			# RCS archive's locking type
	commentleader = "";		# RCS archive's comment leader
	lock_num = 0;			# count of locks in archive
	rcs_suff = "-x,v "			# DO NOT delete the trailing space !

	#
	# Display what we are about to do
	printf("Converting %s to %s\n\n", rcsfile, pvcsfile);

	#
	# Get RCS archive information.  We retain the access list, file
	# description, and workfile name of the RCS archive.
	if (debug_main == "ON")  {
		printf("Getting RCS Archive on %s\n\n",rcsfile);
	}
	if (opsys == "UNIX")  {
		"uname" | getline opsysname;

	#	if (! get_unix_rcs_archive_info())
		if (! get_rcs_archive_info())
			error_exit("unable to get RCS archive information");
	}
	else
		#if (! get_pc_rcs_archive_info())
		if (! get_rcs_archive_info())
			error_exit("unable to get RCS archive information");

	# create a vcs.cfg file in the local dir
	vcs_id = "VCSID=pvcs" ;
	create_vcs_cfg(vcs_id);

	#
	# Create PVCS archive with the information we obtained from the
	# RCS archive.
	if (debug_main == "ON")  {
		printf("Initializing Merant Archive for %s\n",pvcsfile); #replaced PVCS with Merant
	}
	if (! init_pvcs_archive())
		error_exit("unable to create Merant archive");  #replaced PVCS with Merant

	#
	# Replicate the trunk
	if (debug_main == "ON")  {
		printf("Replicating Trunk\n");
	}
	if (! replicate_branch(""))
		error_exit("unable to replicate trunk revisions");

	#
	# Now that we are done, clean-up after ourselves.  We reset
	# RCS attributes we had to change and put the finishing touches
	# on the new PVCS archive.
	if (debug_main == "ON")  {
		printf("We will now finish the conversion\n");
	}
	if (! finish_conversion())
		error_exit("unable to finish conversion");

	#
	# Done
	printf("%s [info]: completed successfully\n", progname);
	exit 0;

}

#
# error_exit
#
# Purpose -
#
#    Prints an error message and exits.
#
function error_exit(error_msg)
{

	#
	# Print the error
	if (pvcsfile != "")
		printf("%s [error]: %s: %s\n", progname, pvcsfile, error_msg);
	else
		printf("%s [error]: %s\n", progname, error_msg);

	#
	# Delete the PVCS VM archive (and other files).  The archive may not
	# exist in the current directory, but we can at least try to get
	# rid of it.

	exit 1;

}

#
# execute_cmd
#
# Purpose -
#
#    A friendly front-end to the system() built-in function.  Prints the
#    command and results to standard output if requested through the global
#    variable "debug" (set via command line).
#
# Returns -
#
#    TRUE if system() succeeded, FALSE otherwise
#
function execute_cmd(string)
{

	#
	# Say what we are doing
	if (debug_execute == "ON")
		printf("executing: %s\n", string);

	#
	# If we are going to trash the output, then we modify the command
	# string in an operating system independent fashion.  For DOS & OS/2,
	# we use the PVCS supplied "RSE".  For Unix, we just do shell redirection.
	if ((debug != "N") || (debug != "n"))
		if ((opsys == "DOS") || (opsys == "OS/2"))
			string = pvcs_bin "rse " string" >nul";
		else
			string = string " 1>/dev/null 2>&1";

	#
	# Do it
	ret_val = !system(string);
	if (debug_execute == "ON")
		printf("Return Value: %s\n", ret_val);
	return ret_val;

}

#
# get_rcs_archive_info (RCS v5.6 and above)
#
# Purpose -
#
#    Runs the "rlog" command to obtain the per-archive information we will
#    be preserving.
#
#    In this pass, we get
#
#       1) the access list
#       2) locking status (strict or non-strict)
#       3) the default branch
#       4) the workfile name
#		5) all locks
#
# Returns -
#
#    TRUE if successful, FALSE otherwise
#
#function get_unix_rcs_archive_info(   state, i, ver_name, ver_rev)
function get_rcs_archive_info(   state, i, ver_name, ver_rev)
{

	#
	# Initialize
	state = "NONE";

	#
	# Tell user what we are doing
	printf("%s [info]: obtaining RCS archive attributes\n", progname);
	rlog_cmd = rcs_bin "rlog -t " rcs_suff " " rcsfile;
	printf("%s\n", rlog_cmd);

	#
	# Run the "rlog" command to get the access list and description
	while (rlog_cmd | getline)  {

		if (debug_rcs_archive == "ON") {
			printf("STATE =%s\n", state);
			printf("$0 =%s\n", $0);
			printf("$1 =%s\n", $1);
			printf("$2 =%s\n", $2);
			printf("$3 =%s\n", $3);
			printf("$4 =%s\n", $4);
			printf("$5 =%s\n", $5);
		}
		#
		# We use the line of equal signs to signal the end of
		# the report.  If we got that, then we were successful.
		if ((state == "description") && (substr($0, 1, 1) == "="))
			return 1;

		if ((state == "LOCKS") && ($1 " " $2 == "access list:")) 
			state = "NONE";

		#
		# If this is the locking status, get it.
		if ((state == "NONE") && ($1 == "locks:")) {
			if (opsysname=="HP-UX")	{
				num_parts = split($0, parts, FS);
				if (parts[num_parts] == "strict") {
					locking = parts[num_parts];
					state = "LOCKS";
				}
				continue;
			}
			else {
				if ($2 == "strict") {
					locking = $2;
					state = "LOCKS";
					continue;
				}
			}
		}


		if (state == "LOCKS") {
			lock_num++;
			lock_name[lock_num] = substr($1, 1, (length($1) -1));
			lock_rev[lock_num]  = $2;
			continue;
		}

		#
		# If this is the working file name, get it.
		if ((state == "NONE") && ($1 == "Working") && (opsysname != "HP-UX"))  {
			workfile = $3;
		printf("NOT_HP_WORKFILE = %s\n",workfile);
			continue;
		}

		#
		# If this is the working file name, get it.
		# This is an alt rcs format found on hp-ux systems
		if ((state == "NONE") && ($4 == "Working") && (opsysname == "HP-UX"))  {
			workfile = $6;
		printf("HP_WORKFILE = %s\n",workfile);
			continue;
		}

		# If this is the comment leader sequence, get it.  We also
		# get ourselves out of the version label scan mode here.
		if ((state == "version_labels") && ($1 " " $2 == "comment leader:"))  {
			if (opsysname == "HP-UX") {
				commentleader = substr($0, index($0, ":") + 3, length($0));
			}
			else {
				commentleader = substr($0, index($0, ":") + 2, length($0));
			}
			state = "NONE";
			continue;
		}

		#
		# If this is the access list, switch to access list
		# mode.
		if ((state == "NONE") && ($1 " " $2 == "access list:"))  {
			state = "access_list";
			if (opsysname == "HP-UX") {
				num_parts = split($0, parts, FS);
				for (i = 3; i <= num_parts; i += 1)
					if (accesslist == "")
						accesslist = parts[i];
					else
						accesslist = accesslist "," parts[i];
			}
			continue;
		}

		#
		# If this is the description, switch to that mode.
		if ((state == "NONE") && ($1 == "description:"))  {
			state = "description";
			continue;
		}

		#
		# We use the "symbolic names" section to signal the
		# end of the access list.  We then switch to the mode
		# of scanning for version labels.
		if ((state == "access_list") && ($1 " " $2 == "symbolic names:"))  {
			state = "version_labels";

			if (opsysname == "HP-UX") {
				num_parts = split($0, parts, FS);
				for (i = 3; i <= num_parts; i += 2)  {
					ver_name = substr(parts[i], 1, length(parts[i]) - 1);
					ver_rev = substr(parts[i + 1], 1, length(parts[i + 1]) - 1);
					versions[ver_name] = ver_rev;
				}
				continue;
			}
			continue;
		}

		#
		# If we are are getting the access list, then get this
		# name and append it to the current list.
		if (state == "access_list")  {
			if (accesslist == "")
				accesslist = $1;
			else
				accesslist = accesslist "," $1;
			continue;
		}

		#
		# If we are getting version labels, get them here.
	#	if ((state == "version_labels") && (opsysname != "HP-UX"))  
		if ((state == "version_labels") && ($1 " " $2 != "keyword substitution:"))  {
			
			x=split($0,sline,"\:");
			x=match(sline[1],/[^ \t]/);
			ver_name = "\"" substr(sline[1], RSTART) "\"";
#			ver_rev = sline[2];				Did not work on NT - we must remove spaces
			x=match(sline[1],/[^ \t]/);
			ver_rev = substr(sline[2], RSTART);
			versions[ver_name] = ver_rev;
			if (debug_rcs_archive == "ON") {
				printf("VER_NAME = %s\n",ver_name);
				printf("VER_REV = %s\n",ver_rev);
				continue;
			}
		}
		else {
			state = "NONE";
			continue;
		}	
		#
		# If we are getting the description, append to the current
		# description.
		if (state == "description")  {
			if (description == "")
				description = $0;
			else
				description = description "\n" $0;
			continue;
		}

	}

	return 1;

}

#
# get_pc_rcs_archive_info (RCS v5.1)
#
# Purpose -
#
#    Runs the "rlog" command to obtain the per-archive information we will
#    be preserving.
#
#    In this pass, we get
#
#       1) the access list
#       2) locking status (strict or non-strict)
#       3) the default branch
#       4) the workfile name
#		5) all locks
#
# Returns -
#
#    TRUE if successful, FALSE otherwise
#
function get_pc_rcs_archive_info(   state, i, ver_name, ver_rev, parts, num_parts)
{

	#
	# Initialize
	state = "NONE";

	#
	# Tell user what we are doing
	printf("%s [info]: obtaining RCS archive attributes\n", progname);
	printf("%s \n", rcs_bin);
	rlog_cmd = rcs_bin "rlog -t " rcs_suff " " rcsfile;
	printf("execute - %s \n", rlog_cmd);

	#
	# Run the "rlog" command to get the access list and description
		#
		# We use the line of equal signs to signal the end of
		# the report.  If we got that, then we were successful.
		if ((state == "description") && (substr($0, 1, 1) == "="))
			return 1;

		if ((state == "LOCKS") && ($1 " " $2 == "access list:")) 
			state = "NONE";

		#
		# If this is the locking status, get it.  For v5.1, "strict" would
		# be the last element on the line.
		if ((state == "NONE") && ($1 == "locks:"))  {
			num_parts = split($0, parts, FS);
			if (parts[num_parts] == "strict") {
				locking = parts[num_parts];
				state = "LOCKS";
			}
			continue;
		}

		if (state == "LOCKS") {
			lock_num++;
			lock_name[lock_num] = substr($1, 1, (length($1) -1));
			lock_rev[lock_num]  = $2;
			continue;
		}

		#
		# If this is the working file name, get it.
		if ((state == "NONE") && ($4 == "Working"))  {
			workfile = $6;
			printf("WORKFILE =%s\n", workfile);
			continue;
		}

		#
		# If this is the comment leader sequence, get it.
		if ((state == "NONE") && ($1 " " $2 == "comment leader:"))  {
			commentleader = substr($0, index($0, ":") + 3, length($0));
	#		continue;
		}

		#
		# If this is the access list, assemble the access list.
		if ((state == "NONE") && ($1 " " $2 == "access list:"))  {
			num_parts = split($0, parts, FS);
			for (i = 3; i <= num_parts; i += 1) {
				if (accesslist == "")
					accesslist = parts[i];
				else
					accesslist = accesslist "," parts[i];
				}
	#		continue;
		}

		#
		# If this is the description, switch to that mode.
		if ((state == "NONE") && ($0 == "description:"))  {
			state = "description";
	#		continue;
		}

		#
		# If this is the symbolic name section, we'll get those as version
		# labels.
		if ((state == "NONE") && ($1 " " $2 == "symbolic names:"))  {
			num_parts = split($0, parts, FS);
			for (i = 3; i <= num_parts; i += 2)  {
				ver_name = substr(parts[i], 1, length(parts[i]) - 1);
				ver_rev = substr(parts[i + 1], 1, length(parts[i + 1]) - 1);
				versions[ver_name] = ver_rev;
			}
#			continue;
		}

		#
		# If we are getting the description, append to the current
		# description.
		if (state == "description")  {
			if (description == "")
				description = $0;
			else
				description = description "\n" $0;
#			continue;
		}

	return 0;

}

#
# init_pvcs_archive
#
# Purpose -
#
#    Creates the PVCS Version Manager archive setting the archive
#    description.  We don't do the access list yet since if an archive
#    has one, it requires an access database to be present.  We also
#    turn off lock-checking (NOCHECKLOCK) so we don't have to check-out
#    revisions to do the check-ins later (it also makes this faster).
#
function init_pvcs_archive( filename, vcs_cmd)
{

	#
	# Tell user what we are doing
	printf("%s [info]: initializing Merant VM archive\n", progname); #replaced PVCS with Merant

	#
	# Write the description to a file
	filename = create_description(description);
	if (filename == "")
		return 0;

	pvcs_archive = vcs_dir pvcsfile;
	printf("pvcs_archive = %s\n",pvcs_archive);		
	#
	# Create an initial VCS command.  We might change it later.
	vcs_cmd = pvcs_bin "vcs -i -t@" filename " " pvcs_archive;

	#
	# Append to the current VCS command a workfile name
	if (opsys == "UNIX")
		vcs_cmd = vcs_cmd "\\("workfile;
	else
		vcs_cmd = vcs_cmd "(" workfile ")";


	#
	# Initialize the archive with the description we created.  If this
	# command fails, we will try again and specify the workfile name
	# with the archive name.  We do this so that the user may put the
	# new archive in the current directory.
	if (! execute_cmd(vcs_cmd))  {

		#
		# Try it again
		if (! execute_cmd(vcs_cmd))
			return 0;

	}
	remove_file(filename);


	#
	# Turn off lock-checking (NOCHECKLOCK)
	# and translation (NOTRANSLATE)
	if (! execute_cmd(pvcs_bin "vcs -pl -pt " pvcsfile))
		return 0;

	#
	# Set the workfile name
	if (! execute_cmd(pvcs_bin "vcs -w" workfile " " pvcsfile))
		return 0;

	return 1;
}

#
# replicate_branch
#
# Purpose -
#
#    Given a branch (null string for the trunk), extract revision information
#    from the given RCS archive and replicate in the given PVCS VM archive.
#
# Returns -
#
#    TRUE if successful, FALSE otherwise
#
function replicate_branch(branch,   branches, return_val, rev_count, state, arg, rev_num, comment)
{

	#
	# Initialize
	return_val = 0;
	rev_count = 0;
	state = "start_seek";
	branches = "";

	#
	# Tell user what we are doing.  If we are doing the trunk, then
	# the "rlog" command is a bit different.
	if (branch == "")  {
		printf("%s [info]: replicating trunk revisions\n", progname);
		arg = "";
	}
	else {
		printf("%s [info]: replicating revisions on branch %s\n",
			progname, branch);
		arg = "-r" branch;
	}

	#
	# Execute the "rlog" command along the branch and gather all of
	# the information we need to replicate the revisions.
	#
	# "rlog" outputs revision information in most-recent order so
	# we stack the data we obtain in a similar fashion.  Elements
	# at the start of the arrays are most-recent and at the end
	# are least-recent.
	while (rcs_bin "rlog " rcs_suff " " arg " " rcsfile | getline)  {

		if (debug_rcs_archive == "ON") {
			printf("STATE =%s\n", state);
			printf("$0 =%s\n", $0);
			printf("$1 =%s\n", $1);
			printf("$2 =%s\n", $2);
			printf("$3 =%s\n", $3);
			printf("$4 =%s\n", $4);
			printf("$5 =%s\n", $5);
		}

		#
		# When we hit a line of equal signs, then we are done.  We
		# increment the revision count if we were reading a comment.
		if (substr($0, 1, 1) == "=")  {

			if (state == "branch_look")
				state = "comment_read";

			else {
				if (state == "comment_read")
					rev_count += 1;
				return_val = 1;
				break;
			}
		}

		#
		# If we are seeking the start of revision information
		# (delimited by the first line of hyphens), then we skip
		# until we find those hyphens.
		if ((state == "start_seek") && (substr($0, 1, 1) == "-"))  {
			state = "none";
			continue;
		}

		#
		# If this line starts with "revision", then we can
		# start accumulating information about this
		# revision.  If we are doing the trunk, then if we scan a
		# branch revision, we don't want to look at this stuff.
		if ((state == "none") && ($1 == "revision"))  {
			if ((branch == "") && !is_trunk($2))
				continue;
			rev_num[rev_count] = $2;
			comment[rev_count] = "";
			state = "date_look";
			continue;
		}

		#
		# If this line starts with "date:", then we have to note
		# this.  We do this because we aren't sure if the next
		# line is the first line of the comment or is the "branches:"
		# line.  At this line, we extract the revision author.
		if ((state == "date_look") && ($1 == "date:"))  {
			state = "branch_look";
			author[rev_count] = substr($5, 1, length($5) - 1);
			continue;
		}

		#
		# If this line starts with "branches:", then these
		# are the branches for this revision.  We'll save those
		# for later.
		#
		# If we didn't find "branches:", then this is the first line
		# of the comment.  Note how we don't skip to the next line
		# in this case.
		if (state == "branch_look")
			if ($1 == "branches:")  {
				branches = append_branches($0, branches);
				state = "comment_read";
				continue;
			}
			else
				state = "comment_read";

		#
		# If we are accumulating comments and the line starts with
		# a hyphen, we're done with this revision.
		if ((state == "comment_read") && (substr($0, 1, 1) == "-"))  {
			rev_count += 1;
			state = "none";
			continue;
		}

		#
		# If we are accumulating comments, add to this revision's
		# comment.
		if (state == "comment_read")  {
			if (comment[rev_count] == "")
				comment[rev_count] = $0;
			else
				comment[rev_count] = comment[rev_count] "\n" $0;
			continue;
		}

	}

	#
	# If we successfully got the information out of the RCS archive,
	# we can attempt to replicate it in the PVCS archive.
	if (return_val)
		if (! replicate_revs_in_pvcs(rev_count, rev_num, comment))
			error_exit("unable to replicate revisions");

	#
	# If we successfully regenerated this branch, we can then try to
	# regenerate all of the branches on our branch.
	if (return_val)
		if (! replicate_branch_list(branches))
			error_exit("unable to replicate branches");

	return return_val;

}

#
# Replicate_revs_in_pvcs
#
# Purpose -
#
#    Given arrays of revision numbers and comments, replicate the given
#    information in the PVCS archive.
#
# Returns -
#
#    TRUE if successful, FALSE otherwise
#
function replicate_revs_in_pvcs(rev_count, rev_num, comment,   i, get_cmd, put_cmd, filename, vcsid_cmd, script_file)
{

	#
	# Iterate backwards (remember oldest revision comes last in the array)
	# over the revisions, checking out of RCS and then checking into
	# PVCS.
	for (i = rev_count - 1; i >= 0; i -= 1)  {

		#
		# Say which revisions we are working on
		printf("   %s -> %s\n", rev_num[i], rev_num[i]);

		#
		# Remove the workfile
		remove_file(workfile);

		#
		# Create the description file
		filename = create_description(comment[i]);
		if (debug_execute == "ON")
			printf ("file_desc = %s\n", filename);
		if (filename == "")
			return 0;

		#
		# Create the commands
		get_cmd = rcs_bin "co -M " rcs_suff "  -r" rev_num[i] " " rcsfile;

		put_cmd = pvcs_bin "put -y -r" rev_num[i] " -m@" filename " " pvcsfile;

		#
		# Get the revision out of RCS
		if (! execute_cmd(get_cmd))
			return 0;

		vcs_id = "VCSID=" author[i];
		create_vcs_cfg(vcs_id);
		#
		# Set the VCSID for setting the author of the revision.
		# This differs based on platform.  In UNIX, we need to append
		# to the PUT command.  On DOS and OS/2, we execute commands
		# before the PUT but must do it in a .BAT/.CMD script.
		if (opsys == "UNIX")  {
			# Check it into PVCS
			if (! execute_cmd(put_cmd))
				return 0;
		}
		else {

			#
			# Create the script file for setting the VCSID and doing
			# the PUT command.
#			print "set VCSID=" author[i] > script_file;

			#
			# Check it into PVCS
			if (! execute_cmd(put_cmd))
				return 0;
		}

		#
		# Remove the description
		remove_file(filename);
	}

	#
	# Remove the description
	remove_file(filename);

	#
	# Remove the final workfile
	remove_file(workfile);

	#
	# If we get here, we succeeded
	return 1;

}

#
# finish_conversion
#
# Purpose -
#
#    Finish conversion by doing the following to the RCS archive
#
#       1) reset default branch
#
#    and the following to the PVCS archive
#
#       1) turn lock checking back on if it was on in the RCS archive
#       2) set the access list
#       3) set the comment prefix
#       4) add all version labels
#	5) add all locks
#
# Returns -
#
#    TRUE if successful, FALSE otherwise
#
function finish_conversion(   i)
{

	#
	# Set the PVCS comment prefix
	if (commentleader != "")  {
		printf("%s [info]: setting VM comment prefix\n", progname);
		if (! execute_cmd(pvcs_bin "vcs -ec" commentleader " " pvcsfile))
			return 0;
	}

	#
	# Set PVCS version labels
	printf("%s [info]: setting VM version labels\n", progname);
	laberr = 0;


	for (i in versions) {
		if (! execute_cmd(pvcs_bin "vcs -v" i ":" versions[i] " " pvcsfile))	{
		laberr ++;
		lablist[i] =  i;
		}
	}
	if (laberr) {
		for (label in lablist)
		printf("%s [Warning]: Could not assign label : %s\n", progname, label);
		printf("%s [Warning]: This may be because Branch revisions will usually have\n", progname);
		printf("%s [Warning]:  different numbers in VM.  VM will not allow the user\n", progname);
		printf("%s [Warning]:  to specify numbering on branches.\n", progname);

		}

	#
	# Turn on PVCS lock checking
	if (locking == "strict")  {
		printf("%s [info]: setting VM lock checking\n", progname);
		if (! execute_cmd(pvcs_bin "vcs +pl " pvcsfile))
			return 0;
	}

	# 
	#
	# Re-apply all locks

	printf("%s [info]: re-setting all locks\n", progname);

	if (debug_pvcs_end == "ON") { 
		printf("LOCK_NUM =%s\n", lock_num);
	}

	if (lock_num != 0) {

		for (i =1; i <= lock_num; i++) {

			create_vcs_cfg("VCSID=" lock_name[i]);
			lock_cmd = pvcs_bin "vcs -l" lock_rev[i]  " " pvcsfile;

			if (debug_pvcs_end == "ON") { 
				printf("LOCK_NUM =%s\n", i);
				printf("LOCK_NAME =%s\n", lock_name[i]);
				printf("LOCK_REV =%s\n", lock_rev[i]);
			}

			if (opsys == "UNIX")  {
				if (! execute_cmd(lock_cmd)) {
	printf("%s [Warning]: Could not apply lock to: %s for %s\n", progname, lock_rev[i], lock_name[i]);
				}
			}
			else {

				#
				# Create the script file for setting the VCSID and doing
				# the PUT command.
#				print "set VCSID=" lock_name[i] > script_file;
	
				if (! execute_cmd(lock_cmd)) {
	printf("%s [Warning]: Could not apply lock to: %s for %s\n", progname, lock_rev[i], lock_name[i]);
				}
			}
		}
	}	



	#
	# Set the PVCS access list
	printf("%s [info]: setting VM access list\n", progname);
	if (! execute_cmd(pvcs_bin "vcs -a" accesslist " " pvcsfile))
		return 0;

	return 1;

}

#
# append_branches
#
# Purpose -
#
#    Given the list of branches produced by the "rlog" command, return a
#    string containing the list of branches appended to an existing list
#    of branches.  This means we have to remove the semicolon from the branch
#    names and create our own string.
#
#    The output list is a space separated list of branch numbers.
#
# Returns -
#
#    The string (the old string if no branches listed)
#
function append_branches(line, old_branches,   branch_array, i, a_branch, num_branches, new_branches)
{

	#
	# Split up the line into its constituent parts
	num_branches = split(line, branch_array, FS);
	if (num_branches == 1)
		return old_branches;

	#
	# Now generate our string
	new_branches = old_branches;
	for (i = 2; i <= num_branches; i += 1)  {
		a_branch = substr(branch_array[i], 1, length(branch_array[i]) - 1);
		if (new_branches == "")
			new_branches = a_branch;
		else
			new_branches = new_branches " " a_branch;
	}

	#
	# Return the result
	return new_branches;

}

#
# replicate_branch_list
#
# Purpose -
#
#    Given a string which is a space separated list of branch names,
#    iterate over the list and replicate each branch.
#
# Returns
#
#    TRUE if successful, FALSE otherwise
#
function replicate_branch_list(branches,   branch_array, num_branches, i)
{

	#
	# If there aren't any branches, return now
	if (length(branches) == 0)
		return 1;

	#
	# Split up the list
	num_branches = split(branches, branch_array, FS);
	if (num_branches == 0)
		return 1;

	#
	# Iterate over all branches
	for (i = 1; i <= num_branches; i += 1)
		if (! replicate_branch(branch_array[i]))
			return 0;

	#
	# If we get here, all branches are replicated
	return 1;

}

#
# Create_description
#
# Purpose -
#
#    Given a newline separated chunk of text, put the text into a uniquely
#    named file and return the name of the file.
#
# Returns -
#
#    The filename or an empty string if an error occurs.
#
function create_description(description,   return_name, parts)
{
	#
	# Create a filename based on the name of the PVCS archive.  If this
	# didn't have to run on DOS or OS/2, we could do something smart like
	# create a truly unique filename.
	split(pvcsfile, parts, ".");
	return_name = parts[1] ".dsc";

	#
	# Make sure it isn't there to begin with
	remove_file(return_name);

	#
	# Write the description to the file
	print description >return_name;
	close(return_name);

	return return_name;
	
}
#
# create_vcs_cfg
#
# Purpose -
#
#    This routine will build a config file named vcs.cfg
#    named file will contain the VCSDIR=<set equal to the 
#	 VCSDIR environment variable
#
# Returns -
#
#    The filename or an empty string if an error occurs.
#
function create_vcs_cfg(author)
{
	#
	# Create a filename based on the name of the PVCS archive.  If this
	# didn't have to run on DOS or OS/2, we could do something smart like
	# create a truly unique filename.
	vcsdir1 = "VCSDIR=" vcsdir;
	if (debug_execute == "ON") {
		printf("VCSDIR = %s\n",vcsdir1);
		printf("Author = %s\n",author);
	}

	vcs_cfg = "vcs.cfg";
	#
	# Make sure it isn't there to begin with
	remove_file(vcs_cfg);

	expandkeywords_no = "EXPANDKEYWORDS NOTOUCH";

	#
	# Write the description to the file
	print "LOGIN VCSID" > vcs_cfg;
	print author > vcs_cfg;
	print expandkeywords_no > vcs_cfg;
	print "MULTILOCK REVISION" > vcs_cfg;		# to handle locks
	print "MULTILOCK USER" > vcs_cfg		# to handle locks
	print vcsdir >vcs_cfg;
	close(vcs_cfg);

	return 1;
	
}

#
# remove_file
#
# Purpose -
#
#    Removes the given file in an operating system independent fashion.
#
function remove_file(filename)
{

	#
	# For DOS & OS/2, we use "del".  Everything else uses "rm"
	if ((opsys == "DOS") || (opsys == "OS/2"))  {
		execute_cmd("attrib -r " filename);
		execute_cmd("del " filename);
	}
	else
		execute_cmd("rm -f " filename);

}

#
# get_opsys
#
# Purpose -
#
# Determines the operating system we are running on by looking at the
# ENVIRON global array.  This doesn't exist under Unix "awk".  This is provided
# by the PolyAWK compiler which generates the DOS and OS/2 .EXE files.
# We'll look at COMSPEC to see if we are using CMD.EXE or COMMAND.COM.
# Others (those using different command interpreters or their own "awk")
# may have trouble with this.
#
# Returns -
#
# "DOS", "OS/2", or "UNIX".  Generates error if it cannot be determined
# which we are on.
# Added code to check for NT (WRM).
#
function get_opsys(   comspec, num_parts, parts)
{

	#
	# Get name of the command interpreter
	comspec = ENVIRON["COMSPEC"];
	comspec1 = ENVIRON["ComSpec"];			#	NT uses ComSpec		Maciej
	rcs_bin = ENVIRON["RCS_BIN"];
	pvcs_bin = ENVIRON["PVCS_BIN"];
	oper_sys = ENVIRON["OS"];
	shell = ENVIRON["SHELL"];
	vcsdir = ENVIRON["VCSDIR"];
	tz = ENVIRON["TZ"];

	if ((comspec == "") && (comspec1 != ""))	#	NT uses ComSpec		Maciej
		comspec = comspec1;
	if (debug_opsys == "ON") {
		printf("\n");
		for (i in ENVIRON)
			printf("%s=%s\n", i, ENVIRON[i]);
	}

	#
	# If we aren't PolyAWK, assume Unix.
	if (comspec == "")
		return "UNIX";
	else { 	# we are on a NON UNIX

		if (rcs_bin != "")
			rcs_bin = rcs_bin "\\";
		if (pvcs_bin != "")
			pvcs_bin = pvcs_bin "\\";
		if (vcsdir != "")
			vcs_dir = vcsdir "\\";

		#
		# Determine the actual filename of COMSPEC
		num_parts = split(comspec, parts, "\\");
		comspec = tolower(parts[num_parts]);
		#
		# Determine the shell that is being used
		num_parts = split(shell, parts, "\\");
		shell = tolower(parts[num_parts]);

		if (debug_opsys == "ON") {
			printf("\n");
			printf("comspec=%s\n", comspec);
			printf("rcs_bin=%s\n", rcs_bin);
			printf("pvcs_bin=%s\n", pvcs_bin);
			printf("oper_sys=%s\n", oper_sys);
			printf("shell=%s\n", shell);
		}
		if ((shell == "cmd.exe") || (shell == "command.com") || (shell == "")) {
		#
		# If it is CMD.EXE, it is OS/2 or NT.  If it is COMMAND.COM,
		# it is DOS.  If it is neither, it is an error.
			if (comspec == "cmd.exe") {
				if (oper_sys == "Windows_NT") # is this NT?
					return "DOS";
				else
					return "OS/2";
			}
			else if (comspec == "command.com")
				return "DOS";
			else
				error_exit("unable to determine operating system");
		}
		else {
			printf("Current SHELL = %s\n",shell);
			error_exit("Incorrect shell! Use  cmd.exe or command.com");
		}
	}

}

#
# get_args
#
# Gets the command line arguments in an operating system dependent fashion.
# Under Unix, we cannot pass dash options (e.g. "-d") so we it do those via
# parameters.  Under DOS and OS/2, we can so we must be able to read those
# here.
#
# Returns -
#
# TRUE if successful, FALSE otherwise
#
function get_args(   i, count, tempstr)
{

	#
	# Initialize
	count = 0;

	#
	# Iterate over the arguments
	for (i = 1; i < ARGC; i += 1)  {

		#
		# Is this a "-" argument?
		if (substr(ARGV[i], 1, 1) == "-")  {

			#
			# Get rest of string
			tempstr = substr(ARGV[i], 2, length(ARGV[i]) - 1);

			#
			# -d?
			if (tempstr == "d")  {
				debug = "Y";
				continue;
			}

			#
			# Unknown option - fail now
			return 0;

		}
		else {

			#
			# Accumulate parameters
			count += 1;

			#
			# Grab parameters in the right order
			if (count == 1)
				rcsfile = ARGV[i];
			else if (count == 2)
				pvcsfile = ARGV[i];
			else if (count == 3)
				debug = ARGV[i];
			else
				return 0;

		}

	}

	#
	# If we didn't get enough parameters we return failure
	if (count < 2)
		return 0;

	return 1;

}

#
# is_trunk
#
# Purpose -
#
# Determines if the given SCCS revision number is a trunk revision.  Trunk
# revisions have two parts (major and minor revision numbers).
#
# Returns
#
# TRUE if a trunk, FALSE if something else
#
function is_trunk(rev_num,   parts, num_parts)
{

	#
	# Split the revision number into its parts
	num_parts = split(rev_num, parts, ".");

	#
	# If there are two parts, it is a trunk
	if (num_parts == 2)
		return 1;
	else
		return 0;

}
