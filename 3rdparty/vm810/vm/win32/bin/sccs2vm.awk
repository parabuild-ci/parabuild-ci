#
# $Header:   /sources/archives/dv/Vms/tools/xlate/sccs2vm.awv   1.2   10 Mar 2005 14:07:04   sschultz  $
#
# sccs2vm.awk - Awk script for converting an SCCS archive to a Merant
#                Version Manager archive.
#
# Usage: awk -f sccs2vm.awk sccsfile pvcsfile pvcspath [debug]
#
# This is an unsupported utility.  Use at your own risk.
#

#10/28/98 Corrected the vcs_cmd variable in the init_pvcs_archive function EMB.
#3/29/99 Changed the way branches are created in VM.  The numbering system start at x.x.1.1
#instead of x.x.1.0.  Basically minor numbers start at 1.1 instead of 1.0.

# 19.4.2000	Changed by Maciej so that the initial vcs -i always uses the workfile name

# 12.3.2001	Update Copyright information

# 15.3.2001	Changed by Maciej :
#		Allow for SCCS branch revisions that end with zero (1.2.1.0)
#		Turn off all VM signons

# 22.3.2001	Changed by Maciej :
#		Removed a superfluous call to remove the description file

# 27.3.2001	Changed by Maciej :
#		The original author of a revision was not being honoured in the new archive
#		Added a routine to create a vcs.cfg with the correct VCSID= setting,
#		which is then used for the current put into the archive.  The file is recreated
#		for each revision.

# 10.3.2002	Changed by Maciej :
#		The fixes for branches ending in a "0" (1.2.1.0 - see above) managed to break all "normal" archive handling
#		this script does now HANDLE BOTH x.x.x.0 AND x.x.x.1  - even inside the same FILE

# 10.9.2003	Changed by Maciej :
#		Merged in the changes above into this new archive (sccs2vm.awk)
#		Verfified the new version of the file against the standard SCCS test files I have

BEGIN {

	#
	# Sign-on
	progname = "sccs2vm";
	printf("SCCS-2-VM Converter (%s) V2.0\n", progname);  #replaced PVCS with VM
	printf("Copyright 1985-2003 Merant, Inc.  All rights reserved.\n");  #updated copyright date

	#
	# Determine operating system
	opsys = get_opsys();

	#
	# Initialize optional arguments
	debug = "N";

	#
	# Get program arguments
	if (! get_args())
		if (opsys == "UNIX")
			error_exit("usage: sccs2vm sccsfile pvcsfile pvcspath [debug]");
		else
			error_exit("usage: sccs2vm [-d] sccsfile pvcsfile pvcspath");

	#
	# Validate optional arguments
	if ((debug != "Y") && (debug != "y") && (debug != "N") && (debug != "n"))
		error_exit("\"debug\" can be Y/y or N/n");

	#
	# Set "pvcspath" terminating path separator
	if (opsys == "UNIX")
		pvcspath = pvcspath "/";
	else
		pvcspath = pvcspath "\\";

	#
	# Initialize global variables
	num_revs = 0;
	workfile = "";
	description = "";
	save_rev = "";				#	maciej
	#
	# Display what we are about to do
	printf("Converting %s to %s\n\n", sccsfile, pvcsfile);

	#
	# The strategy here is to extract the revision tree, each revision's
	# comment and date/time from the SCCS archive.  From the information
	# we get at this point, we'll assemble the PVCS revision tree.  We do
	# this because revision numbering is different between the two
	# systems.  After that we just copy from SCCS to PVCS.
	#

	#
	# Assemble the SCCS revision tree and revision comments
	if (! get_sccs_archive_info())
		error_exit("unable to get SCCS archive information");
	#
	# Build the PVCS revision tree
	create_pvcs_rev_tree();

	#
	# Create the PVCS archive
	if (! init_pvcs_archive())
		error_exit("unable to create Merant VM archive"); #replaced PVCS with Merant VM

	#
	# Replicate the revisions
	if (! replicate_revs_in_pvcs())
		error_exit("unable to complete revision replication\n");

	#
	# Complete the conversion
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
	remove_file(pvcsfile);
	remove_file(workfile);
	remove_file("*.dsc");
	if (opsys == "DOS")
		remove_file("putit.bat");
	else if (opesys == "OS/2")
		remove_file("putit.cmd");

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
	if ((debug == "Y") || (debug == "y"))
		printf("\n%s [info]: executing: %s\n", progname, string);

	#
	# If we are going to trash the output, then we modify the command
	# string in an operating system independent fashion.  For DOS & OS/2,
	# we use the PVCS supplied "RSE".  For Unix, we just do shell redirection.
	if ((debug == "N") || (debug == "n"))
		if ((opsys == "DOS") || (opsys == "OS/2"))
			string = "rse " string" >nul";
		else
			string = string " 1>/dev/null 2>&1";


	#
	# Do it
	rc = !system(string);
	if ((debug == "Y") || (debug == "y"))
		printf("Return Code = %d\n", rc);
	return rc;

}

#
# get_sccs_archive_info
#
# Purpose -
#
# Runs the "prs" command and creates global arrays holding SCCS:
#
#    1) revision numbers
#    2) parent revision numbers
#    2) revision comments
#    3) revision author IDs
#    4) check-in date/time
#
# Also gets the following additional per archive items via "prs":
#
#    1) workfile name
#    2) archive description
#
# Returns -
#
# TRUE on success, FALSE otherwise
#
function get_sccs_archive_info(   state, return_val, ordinal)
{

	#
	# Initialize
	state = "NONE";
	return_val = 0;

	#
	# Tell user what we are doing
	printf("%s [info]: obtaining SCCS archive attributes\n", progname);

	#
	# Get the workfile name
	workfile = "";
	while ("sccs prs -d:M: " sccsfile | getline)
		workfile = $0;
	if (workfile == "")
		return 0;

	#
	# Get the archive description
	description = "";
	while ("sccs prs -d:FD: " sccsfile | getline)
		if (description == "")
			description = $0;
		else
			description = description "\n" $0;
	if (description == "")
		return 0;
	description = substr(description, 1, length(description) - 1);

	#
	# Run the "prs" command to get the per revision information
	while ("sccs prs " sccsfile | getline)  {

		#
		# If this line's first element is a "D", this is the
		# start of the revision record.  Get the revision number,
		# its ordinal position, and its parent's ordinal position.
		#
		# Also determine how many revisions there are by testing the
		# ordinal number against the current highest ordinal number.
		#
		# After this, we go into the mode where we are looking for
		# the start of the comment for this revision.
		if ((state == "NONE") && ($1 == "D"))  {
			ordinal = $6;
			if (ordinal > num_revs)
				num_revs = ordinal;
			sccs_rev[ordinal] = $2;
			parent[ordinal] = $7;
			date_time[ordinal] = make_datetime($3, $4);
			author[ordinal] = $5;
			state = "comment_seek";
			continue;
		}

		#
		# If we are looking for a comment and this line marks the
		# start of the comment, go into comment reading mode.
		if ((state == "comment_seek") && ($0 == "COMMENTS:"))  {
			state = "comment_read";
			continue;
		}

		#
		# If we are reading a revision's comment, accumulate it.
		# If it is a blank line, that means the end of the comment
		# and we go back to the beginning of the state machine.
		if (state == "comment_read")  {
			if ($0 == "")
				state = "NONE";
			else
				if (comment[ordinal] == "")
					comment[ordinal] = $0;
				else
					comment[ordinal] = comment[ordinal] "\n" $0;
			continue;				
		}

	}

	return 1;

}

#
# create_pvcs_rev_tree
#
# Purpose -
#
# From the revision number and parent revision of each SCCS revision, this
# function builds an array containing corresponding PVCS revision numbers.
#
# Returns
#
# TRUE on success, FALSE on failure
#
function create_pvcs_rev_tree(   i, is_first_branch_rev)
{

	#
	# Tell user what we are doing
	printf("%s [info]: generating new Merant VM revision numbers\n", progname);  #replaced PVCS with Merant VM

	#
	# For each revision, we must keep track of the number of branches
	# coming from it.  Initialize that array here.
	for (i = 1; i <= num_revs; i += 1)
		branch_num[i] = 0;

	#
	# Iterate over all SCCS revisions generating a new PVCS revision
	for (i = 1; i <= num_revs; i += 1)  {

		#
		# If a revision is deleted in the SCCS file, then there
		# will be open spots in the array of SCCS revision numbers.
		# We look for those holes here and skip over them.
		if (sccs_rev[i] == "")
			continue;
		#
		# If this is a trunk revision, preserve the numbering
		if (is_trunk(sccs_rev[i]))  {
			pvcs_rev[i] = sccs_rev[i];
			continue;
		}

		#
		# We now know that we have a branch revision.  The
		# question becomes, "is this the first revision on
		# the branch?".  If it is, we need to tell the PVCS
		# revision number generator to start a branch.
		if (is_first_branch_rev(sccs_rev[i]))  {
			branch_num[parent[i]] += 1;
			is_first_branch_rev = 1;
		}
		else
			is_first_branch_rev = 0;

		#
		# Generate the new PVCS revision number
		pvcs_rev[i] = generate_pvcs_rev(i, is_first_branch_rev);

	}

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
function is_trunk(rev_num)
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

#
# is_first_branch_rev
#
# Purpose -
#
# Determines if the given SCCS revision number is the first revision on a
# branch.  In SCCS, the first branch revision always ends with a "1".
#
# Returns -
#
# TRUE if it is, FALSE if it isn't
#
function is_first_branch_rev(rev_num)
{

	#
	# Split the revision number into its parts
	num_parts = split(rev_num, parts, ".");

	#
	# If there are two parts, it is a trunk so don't bother
	if (num_parts != 4)
		return 0;

	#
	#
	# We need to be super-smart here, as the 1st revision on a branch need not be "1" ...
	# In order to cater for both conditions, we will do the following :
	#
	#	if we have a "0", then we will set first_rev_zero[] to "1" and return a "1"
	#	if we have a "1" AND first_rev_zero[] is false - we return a "1"
	#	if we have a "1" and first_rev_zero is true - we return a false and reset first_rev_zero[]
	#
	# The value of the ordinal is taken from the 2nd part of the revision number
	
	# If the last part of the revision number is a "1", then it
	# is the first revision on a branch.
	save_rev = parts[2];
	if (parts[num_parts] == "0")			# maciej/frank
		{
			first_rev_zero[save_rev] = 1;
		return 1;
	}
	if (parts[num_parts] == "1")   {

		if  (first_rev_zero[save_rev]) {
			first_rev_zero[save_rev] = 0;
			return 0;
		}
	else return 1;
	}
	
	else
		return 0;

}

#
# generate_pvcs_rev
#
# Purpose -
#
# Given an index into the SCCS revision arrays and information about whether
# the given revision is the first revision on a branch, generate a new PVCS
# revision number corresponding to the SCCS revision number.
#
# Returns -
#
# The PVCS revision number
#
function generate_pvcs_rev(i, is_first_branch_rev,   parts, num_parts, j, new_rev)
{

	#
	# Split up the parent revision number into its parts
	num_parts = split(pvcs_rev[parent[i]], parts, ".");

	#
	# If this isn't the first revision on a branch, then we get the
	# PVCS revision number corresponding to the parent revision and
	# increment the last minor number.
	if (! is_first_branch_rev)
		parts[num_parts] += 1;
	else {

		#
		# This is the first revision on a branch.  For this,
		# we get the parent PVCS revision number and append
		# to it a branch number equal to the accumulated number
		# of branches emanating from the parent and a minor
		# branch number of 0.
		parts[num_parts + 1] = branch_num[parent[i]];
		parts[num_parts + 2] = "0";			# maciej
		num_parts += 2;

	}

	#
	# Reassemble the new revision number
	new_rev = "";
	for (j = 1; j < num_parts; j += 1)
		new_rev = new_rev "." parts[j];
	new_rev = substr(new_rev, 2, length(new_rev) - 1);
	new_rev = new_rev "." parts[num_parts];

	return new_rev;

}

#
# init_pvcs_archive
#
# Purpose -
#
#    Creates the PVCS Version Manager archive.  We turn off lock-checking
#    (NOCHECKLOCK) so we don't have to check-out revisions to do the check-ins
#    later (it also makes this faster).
#
function init_pvcs_archive(   filename, vcs_cmd)
{

	#
	# Tell user what we are doing
	printf("%s [info]: initializing Merant VM archive\n", progname); #replaced PVCS with Merant 

	#
	# Write the description to a file
	filename = create_description(description);
	if (filename == "")
		return 0;

	#
	# Create an initial VCS command.  We might change it later.
	vcs_cmd = pvcspath "vcs -q -i -t@" filename " " pvcsfile;

	# Append to the current VCS command a workfile name
	# 
	# Changed by Maciej, so that we always do this ...

	if (opsys == "UNIX")
		vcs_cmd = vcs_cmd "\\(" workfile "\\)";
	else
		vcs_cmd = vcs_cmd "(" workfile ")";


	#
	# Initialize the archive with the description we created.  
	if (! execute_cmd(vcs_cmd))  {

		#
		# Try it again
		if (! execute_cmd(vcs_cmd))
			return 0;

	}
	remove_file(filename);

	#
	# Turn off lock-checking (NOCHECKLOCK)
	# and Translation (NOTRANSLATE)    Maciej 7/8/98
	if (! execute_cmd(pvcspath "vcs -q -pl -pt " pvcsfile))
		return 0;

	return 1;

}

#
# replicate_revs_in_pvcs
#
# Purpose -
#
#    When this gets called, we have arrays of SCCS and corresponding PVCS
#    revision numbers which we can simply iterate over and do the replication.
#
# Returns -
#
#    TRUE if successful, FALSE otherwise
#
function replicate_revs_in_pvcs(   i, get_cmd, put_cmd, vcsid_cmd, touch_cmd, filename, script_file)
{

	#
	# Tell user what we are doing
	printf("%s [info]: replicating SCCS revisions in Merant VM\n", progname); #replaced PVCS with Merant VM

	for (i = 1; i <= num_revs; i += 1)  {

		#
		# If a revision is deleted in the SCCS file, then there
		# will be open spots in the array of SCCS revision numbers.
		# We look for those holes here and skip over them.
		if (sccs_rev[i] == "")
			continue;

		#
		#
		# Say which revisions we are working on
		printf("   %s -> %s\n", sccs_rev[i], pvcs_rev[i]);

		#
		# Remove the workfile
		remove_file(workfile);

		#
		# Create the description file
		filename = create_description(comment[i]);
		if (filename == "")
			return 0;

		#
		# Create the commands
		get_cmd = "sccs get -k -r" sccs_rev[i] " " sccsfile;
		touch_cmd = "touch " date_time[i] " " workfile;
		put_cmd = pvcspath "put -q -y -r" pvcs_rev[i] " -m@" filename " " pvcsfile;

		#
		# Get the revision out of SCCS
		if (! execute_cmd(get_cmd))
			return 0;
		#
		# Set the modification time of the workfile to that of
		# the original delta.
		if (! execute_cmd(touch_cmd))
			return 0;

		#
		#  Changed by Maciej    27/3/2001

		# Set the VCSID for setting the author of the revision.
		# This differs based on platform.  In UNIX, we create a vcs.cfg
		# and use that for the PUT command.  On DOS and OS/2, we execute commands
		# before the PUT but must do it in a .BAT/.CMD script.
		if (opsys == "UNIX")  {

			vcs_id = "VCSID=" author[i] ;
			create_vcs_cfg(vcs_id);
			#
			#
			# Check it into PVCS
			if (! execute_cmd(put_cmd))
				return 0;

		}
		else {

			#
			# Create the script file for setting the VCSID and doing
			# the PUT command.
			print "set VCSID=" author[i] > script_file;
			print put_cmd >> script_file;
			close(script_file);

			#
			# Check it into PVCS
			if (! execute_cmd(script_file))
				return 0;

			#
			# Get rid of the script file
			remove_file(script_file);

		}

		#
		#   The call to remove the description file used to be here.
		#   As the file is removed at creation anyway, we only need to call it once
		#   at the end - like with the workfile.		Maciej 22/3/2001

	}

	#
	# Remove the final description file
	remove_file(filename);
	#
	# Remove the final workfile
	remove_file(workfile);

	# Remove the final vcs.cfg file
	remove_file("vcs.cfg");

	#
	# If we get here, we succeeded
	return 1;

}

#
# finish_conversion
#
# Purpose -
#
#    Finish conversion by doing the following to the PVCS archive
#
#       1) turn lock checking back on
#
# Returns -
#
#    TRUE if successful, FALSE otherwise
#
function finish_conversion(   i, new_rev)
{

	#
	# Turn on PVCS lock checking
	if (! execute_cmd(pvcspath "vcs -q +pl " pvcsfile))
		return 0;

	return 1;

}

#
# create_description
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
function create_description(description)
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
# make_datetime
#
# Purpose -
#
# Given a date of the format YY/MM/DD and a time of HH:MM:SS, return something
# we can give to the "touch" command.  That is how this script sets workfile
# modification times so that they are preserved in the conversion.
#
# Returns -
#
# The string or null string on error
#
function make_datetime(date, time)
{

	#
	# Split up the date and start putting together the return value
	num_parts = split(date, parts, "/");
	if (num_parts != 3)
		return "";
	return_val = parts[2] parts[3];
	year = parts[1];

	#
	# Split up the time and append it
	num_parts = split(time, parts, ":");
	if (num_parts != 3)
		return "";
	return_val = return_val parts[1] parts[2];

	#
	# Add on the year and return
	return return_val year;

}

#
# get_opsys
#
# Purpose -
#
# Determines the operating system we are running on by looking at the
# ENV global array.  This doesn't exist under Unix "awk".  This is provided
# by the PolyAWK compiler which generates the DOS and OS/2 .EXE files.
# We'll look at COMSPEC to see if we are using CMD.EXE or COMMAND.COM.
# Others (those using different command interpreters or their own "awk")
# may have trouble with this.
#
# Returns -
#
# "DOS", "OS/2", or "UNIX".  Generates error if it cannot be determined
# which we are on.
#
function get_opsys(   comspec, num_parts, parts)
{

	#
	# Get name of the command interpreter
	comspec = ENV["COMSPEC"];

	#
	# If we aren't PolyAWK, assume Unix.
	if (comspec == "")
		return "UNIX";
	else {

		#
		# Determine the actual filename of COMSPEC
		num_parts = split(comspec, parts, "\\");
		comspec = tolower(parts[num_parts]);

		#
		# If it is CMD.EXE, it is OS/2.  If it is COMMAND.COM,
		# it is DOS.  If it is neither, it is an error.
		if (comspec == "cmd.exe")
			return "OS/2";
		else if (comspec == "command.com")
			return "DOS";
		else
			error_exit("unable to determine operating system");

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
				sccsfile = ARGV[i];
			else if (count == 2)
				pvcsfile = ARGV[i];
			else if (count == 3)
				pvcspath = ARGV[i];
			else if (count == 4)
				debug = ARGV[i];
			else
				return 0;

		}

	}

	#
	# If we didn't get enough parameters we return failure
	if (count < 3)
		return 0;

	return 1;

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
#    1
#
function create_vcs_cfg(author)
{
	#
	printf("Author = %s\n",author);

	vcs_cfg = "vcs.cfg";
	#
	# Make sure it isn't there to begin with
	remove_file(vcs_cfg);


	#
	# Write the description to the file
	print "LOGIN VCSID" > vcs_cfg;
	print author > vcs_cfg;
	print vcsdir >vcs_cfg;
	close(vcs_cfg);

	return 1;
	
}
