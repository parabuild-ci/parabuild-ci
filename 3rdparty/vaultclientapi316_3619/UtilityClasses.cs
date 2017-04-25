using System;

using System.IO;
using System.Collections;

using VaultClientOperationsLib;
using VaultLib;


namespace VaultCmdLineClient
{

	// NOTICE: if you add a new command, make sure you add help for it below in the Help class
	public enum Command
	{
		NONE,
		INVALID,

		ADD,
		ADDREPOSITORY,
		ADDUSER,
		BATCH, 
		BLAME,
		BRANCH,
		CHECKIN,
		CHECKOUT,
		CLOAK,
		COMMIT,
		CREATEFOLDER,
		DELETE,
		DELETELABEL,
		DIFF,
		FORGETLOGIN,
		GET,
		GETLABEL,
		GETLABELDIFFS,
		GETVERSION,
		GETWILDCARD,
		HELP,
		HELPHTML,
		HISTORY,
		LABEL,
		LISTALLBRANCHPOINTS,
		LISTALLTXDETAILS,
		LISTCHANGESET,
		LISTCHECKOUTS,
		LISTFOLDER,
		LISTREPOSITORIES,
		LISTUSERS,
		LISTWORKINGFOLDERS,
		MOVE,
		OBLITERATE,
		PIN,
		REMEMBERLOGIN,
		RENAME,
		RENAMELABEL,
		SETWORKINGFOLDER,
		SHARE,
		UNCLOAK,
		UNDOCHANGESETITEM, 
		UNDOCHECKOUT,
		UNPIN,
		UNSETWORKINGFOLDER, 
		VERSIONHISTORY
	}

	// NOTICE: if you add a new option, make sure you add help for it below in the Help class
	public enum Option
	{
		INVALID,
		NONE,

		BACKUP,
		BEGINDATE,
		BEGINLABEL,
		BEGINVERSION,
		COMMENT,
		COMMIT,
		COMPARETO, 
		DATESORT, 
		DESTFOLDER,
		DESTPATH,
		ENDDATE,
		ENDLABEL,
		EXCLUSIVE,
		EXCLUDEUSERS,
		EXCLUDEACTIONS,
		HOST,
		KEEPCHECKEDOUT, 
		LABELWORKINGFOLDER,
		LEAVEFILE, 
		MAKEWRITABLE,
		MAKEREADONLY, 
		MERGE,
		NOCLOAKS,
		NORECURSIVE,
		NOSSL,
		OUT,
		PASSWORD,
		PERFORMDELETIONS,
		PROXYSERVER,
		PROXYPORT,
		PROXYUSER,
		PROXYPASSWORD,
		PROXYDOMAIN,
		REPOSITORY,
		REQUIRECHECKOUT,
		REVERTFILE, 
		ROWLIMIT,
		SETFILETIME,
		SERVER, 
		SSL,
		UNCHANGED,
		URL,
		USER,
		USERNAME, 
		VAULTDIFF, 
		VAULTDIFF_OPTIONS, 
		WILDCARD,
		VERBOSE,
		YESIAMSURE
	}

	public enum MergeOption
	{
		none, 

		auto, 
		automatic, 
		automerge, 

		later, 
		mergelater, 
		no_overwrite, 

		overwrite
	}

	public enum FileTimeOption
	{
		none, 

		checkin, 
		current, 
		modified, 
		modification
	}

	public enum CompareToOption
	{
		current, 
		label, 
		lastget, 
		local, 
		repository
	}

	public enum DateSortOption
	{
		asc,
		desc
	}

	public enum BackupOption
	{
		yes,
		no,
		usedefault
	}

	class VaultCmdLineClientDefines
	{
		public const string StdInParam = "-";
		public const string DiffEnv = "VAULTDIFF";
		public const string DiffBin = "diff";

		public const string DiffLeftItem = "%LEFT_PATH%";
		public const string DiffRightItem = "%RIGHT_PATH%";
	}

	class UsageException : System.Exception
	{
		public UsageException(string s) : base(s)
		{
		}
	}




	public class Args
	{
		#region Member Variables

		// connection/ server/ repository info
		private string _user = null;
		private string _password = null;
		private string _host = null;
		private string _url = null;
		private string _repository = "";
		private bool _ssl = false;
		private string _proxyServer = null;
		private bool _yesiamsure = false;
		private string _proxyPort = null;
		private string _proxyUser = null;
		private string _proxyPassword = null;
		private string _proxyDomain = null;
		private string _strBatchName = null;
		private TextReader _trBatchReader = null;

		private Command _cmd = Command.NONE;
		public bool Verbose = false;
		public string OutFile = null;
		public StringWriter Out = new StringWriter();
		public ArrayList items = new ArrayList();
		private string _comment = String.Empty;

		public BackupOption MakeBackup = BackupOption.usedefault;
		public bool Recursive = true;
		public string DestPath = null;
		public string LabelWorkingFolder = null;
		public bool WriteWorkingFolderStateInfo = true;
		public SetFileTimeType SetFileTime = SetFileTimeType.Current;
		public MergeType Merge = MergeType.Unspecified;
		public LocalCopyType LocalCopy = LocalCopyType.Replace;
		public PerformDeletionsType PerformDeletions = PerformDeletionsType.RemoveWorkingCopyIfUnmodified;
		public MakeWritableType MakeWritable = MakeWritableType.MakeNonMergableFilesReadOnly;
		public bool RespectCloaks = true;
		public bool AutoCommit = false;
		public bool RequireCheckOut = false;
		public bool CheckOutExclusive = false;
		public bool _bKeepCheckedOut = false;

		public UnchangedHandler Unchanged = UnchangedHandler.UndoCheckout;
		public int HistoryRowLimit = 1000;
		public DateTime HistoryBeginDate = VaultDate.EmptyDate();
		public DateTime HistoryEndDate = VaultDate.EmptyDate();
		public string HistoryBeginLabel = null;
		public string HistoryEndLabel = null;
		public string HistoryExcludedActions = null;
		public string HistoryExcludedUsers = null;
		public DateSortOption DateSort = DateSortOption.desc;

		public long VersionHistoryBeginVersion = 0;

		private bool _bBatchMode = false;

		private string _strDiffBin = null;
		private string _strDiffCompareTo = DiffAgainstType.CurrentRepositoryVersion.ToString();
		private string _strDiffArgs = string.Empty; // leave as empty... do not initialize this null.

		public string Wildcard = null;

		public bool Error = false;
		public string ErrorMessage = null;

		#endregion //Member Variables

		#region Methods

		#region ctors
		public Args(string[] args)
		{
			Parse(args);
		}
		public Args()
		{
		}
		#endregion // ctors

		public Args CreateBatchCmdArgs()
		{
			Args newarg = new Args();

			newarg.User = _user;
			newarg.Password = _password;
			newarg.Host = _host;
			newarg.RawUrl = _url;
			newarg.ProxyServer = _proxyServer;
			newarg.ProxyPort = _proxyPort;
			newarg.ProxyUser = _proxyUser;
			newarg.ProxyPassword = _proxyPassword;
			newarg.ProxyDomain = _proxyDomain;
			newarg.Repository = _repository;
			newarg.HistoryExcludedActions = HistoryExcludedActions;
			newarg.HistoryExcludedUsers = HistoryExcludedUsers;
			newarg.HistoryBeginLabel = HistoryBeginLabel;
			newarg.HistoryEndLabel = HistoryEndLabel;
			newarg.SSL = _ssl;

			newarg.Verbose = Verbose;
			newarg.OutFile = OutFile;
			newarg.Out = Out;
			newarg.Comment = _comment;
			newarg.Recursive = Recursive;
			newarg.DestPath = DestPath;
			newarg.LabelWorkingFolder = LabelWorkingFolder;
			newarg.WriteWorkingFolderStateInfo = WriteWorkingFolderStateInfo;
			newarg.SetFileTime = SetFileTime;
			newarg.Merge = Merge;
			newarg.LocalCopy = LocalCopy;
			newarg.PerformDeletions = PerformDeletions;
			newarg.MakeWritable = MakeWritable;
			newarg.RespectCloaks = RespectCloaks;
			newarg.AutoCommit = AutoCommit;
			newarg.RequireCheckOut = RequireCheckOut;
			newarg.CheckOutExclusive = CheckOutExclusive;
			newarg.KeepCheckedOut = _bKeepCheckedOut;

			newarg.Unchanged = Unchanged;
			newarg.HistoryRowLimit = HistoryRowLimit;
			newarg.HistoryBeginDate = HistoryBeginDate;
			newarg.HistoryEndDate = HistoryEndDate;
			newarg.DateSort = DateSort;

			newarg.VersionHistoryBeginVersion = VersionHistoryBeginVersion;

			newarg.InBatchMode = _bBatchMode;

			newarg.DiffBin = _strDiffBin;
			newarg.DiffCompareTo = _strDiffCompareTo;
			newarg.DiffArgs = _strDiffArgs;
			newarg.YesIAmSure = _yesiamsure;

			return newarg;
		}


		private bool checkForCommand(string s)
		{
			if (_cmd != Command.NONE)
			{
				// We've already got a command
				return false;
			}

			s = s.ToLower();
			foreach (Command c in Enum.GetValues(typeof(Command)))
			{
				if (s == c.ToString().ToLower())
				{
					_cmd = c;
					return true;
				}
			}

			// they should have specified a command here
			if( (s[0] != VaultDefine.RootName[0]) && 
				(_cmd == Command.NONE) )
			{
				Error = true;
				ErrorMessage = string.Format("unknown command: {0} - run 'vault.exe HELP' for help", s.ToUpper());
				_cmd = Command.INVALID;
			}

			return false;
		}

		public void CloseOutputStream()
		{
			Out.Close();
			if (this.OutFile == null)
			{
				Console.Out.Write(Out.GetStringBuilder().ToString());
			}
			else
			{
				TextWriter tw = new StreamWriter(OutFile);
				tw.Write(Out.GetStringBuilder().ToString());
				tw.Close();
			}
		}

		public void CloseInputBatchStream()
		{
			if ( (_trBatchReader != null) &&
				(_strBatchName != VaultCmdLineClientDefines.StdInParam) )
			{
				// only close this stream if it
				// was created by -BATCH <filename>
				_trBatchReader.Close();
			}
		}

		private bool TestForArgument(int i, int len, string option)
		{
			if(i + 1 >= len)
			{
				Error = true;
				ErrorMessage = string.Format("{0} requires an argument - run 'vault.exe HELP' for help", option);
				return false;
			}

			return true;
		}

		private string[] SplitCmdIntoArgs(string strCmd)
		{
			string[] arRet = null;
			if ( (strCmd != null) && (strCmd.Length > 0) )
			{
				System.Text.StringBuilder sb = new System.Text.StringBuilder();
				ArrayList alList = new ArrayList();

				char[] chCmd = strCmd.ToCharArray();
				char chQuoteMode = (char) 0, c = (char) 0;
				int i, nLen;
				string strArg = null;

				for (i=0, nLen=chCmd.Length; i<nLen; i++)
				{
					// get the char
					c = chCmd[i];

					if ( (c == '"') || (c == '\'') )
					{
						if ( chQuoteMode == c )
						{
							// no longer in "quote mode"
							strArg = sb.ToString().Trim();
							if ( strArg.Length > 0 )
							{
								alList.Add(strArg);
							}
							sb = new System.Text.StringBuilder();
							chQuoteMode = (char)0;
						}
						else
						{
							// if not in quote mode, go to quote mode
							if ( chQuoteMode == (char)0 )
							{
								chQuoteMode = c;
								sb = new System.Text.StringBuilder();
							}
							else
							{
								// just treat as a normal character
								sb.Append(c);
							}
						}
					}
					else
					{
						if ( char.IsWhiteSpace(c) != true )
						{
							// character is not to be ignored
							if ( c != '\\' )
							{
								// just a normal character
								sb.Append(c);
							}
							else
							{
								// move to the next character in the array
								if ( ++i < nLen )
								{
									sb.Append( chCmd[i] );
								}
							}
						}
						else 
						{
							// some white space
							// if in quote mode, just append.
							if ( chQuoteMode != (char)0 )
							{
								sb.Append(c);
							}
							else
							{
								// new parameter based on white space.
								strArg = sb.ToString().Trim();
								if ( strArg.Length > 0 )
								{
									alList.Add(strArg);
								}
								sb = new System.Text.StringBuilder();
							}
						}
					}
				}

				// finally get any remainding args
				strArg = sb.ToString().Trim();
				if ( strArg.Length > 0 )
				{
					alList.Add(strArg);
				}

				arRet = (string[]) alList.ToArray(typeof(string));
			}
			return arRet;
		}


		public void Parse(string[] args)
		{
			for (int i=0; i<args.Length; i++)
			{
				string s = args[i];
				if ( (s == null) || (s.Length == 0) )
				{
					// do not process bogus args.
					continue;
				}

				if ( (s == VaultCmdLineClientDefines.StdInParam) && 
					(_cmd == Command.BATCH) )
				{
					items.Add(s);
					continue;
				}

				if (s[0] == '-')
				{
					Option option = VaultCmdLineClient.LookupOptionByString(s.Substring(1));
					switch (option)
					{
						case Option.BACKUP:
						{
							if(TestForArgument(i, args.Length, s))
							{
								string sOpt = args[++i];

								switch (sOpt.ToLower())
								{
									case "yes":
									case "true":
										this.MakeBackup = BackupOption.yes;
										break;

									case "no":
									case "false":
										this.MakeBackup = BackupOption.no;
										break;

									default:
										if(! Error)
										{
											Error = true;
											ErrorMessage = string.Format("Invalid value for -backup: {0}.  Use \"yes\" or \"no\".", sOpt);
										}
										break;
								}
							}

							break;
						}
						case Option.BEGINDATE:
						{
							if(TestForArgument(i, args.Length, s))
							{
								string sDate = args[++i];
								try
								{
									this.HistoryBeginDate = DateTime.Parse(sDate);
								}
								catch (Exception eBegDate)
								{
									Error = true;
									ErrorMessage = string.Format("{0} could not be converted to a valid date: {1}", option, eBegDate.Message);
								}
							}

							break;
						}
						case Option.BEGINVERSION:
						{
							if ( TestForArgument(i, args.Length, s) )
							{
								string sVersion = args[++i];
								try
								{
									this.VersionHistoryBeginVersion = long.Parse(sVersion);
								}
								catch ( Exception e )
								{
									Error = true;
									ErrorMessage = string.Format("{0} could not be converted to a valid numeric version number: {1}", option, e.Message);
								}
							}
							break;
						}
						case Option.BEGINLABEL:
						{
							if(TestForArgument(i, args.Length, s))
							{
								HistoryBeginLabel = args[++i];
							}
							break;
						}
						case Option.COMMENT:
						{
							if(TestForArgument(i, args.Length, s))
							{
								_comment = args[++i];
							}
							break;
						}

						case Option.COMMIT:
						{
							AutoCommit = true;
							break;
						}

						case Option.COMPARETO:
						{
							if ( TestForArgument(i, args.Length, s))
							{
								_strDiffCompareTo = args[++i];
							}
							break;
						}

						case Option.DATESORT:
							if ( TestForArgument(i, args.Length, s))
							{
								DateSort = VaultCmdLineClient.LookupDateSortOptionByString(args[++i]);
							}
							break;

						case Option.DESTFOLDER:
						{
							if(TestForArgument(i, args.Length, s))
							{
								DestPath = args[++i];
							}
							break;
						}

						case Option.LABELWORKINGFOLDER:
						{
							if(TestForArgument(i, args.Length, s))
							{
								LabelWorkingFolder = args[++i];
							}
							break;
						}

						case Option.DESTPATH:
						{
							if(TestForArgument(i, args.Length, s))
							{
								DestPath = args[++i];
							}
							break;
						}

						case Option.ENDDATE:
						{
							if(TestForArgument(i, args.Length, s))
							{
								string sDate = args[++i];
								try
								{
									this.HistoryEndDate = DateTime.Parse(sDate);
								}
								catch (Exception eEndDate)
								{
									Error = true;
									ErrorMessage = string.Format("{0} could not be converted to a valid date: {1}", option, eEndDate.Message);
								}
							}
							break;
						}
						case Option.ENDLABEL:
						{
							if(TestForArgument(i, args.Length, s))
							{
								HistoryEndLabel = args[++i];
							}
							break;
						}

						case Option.EXCLUDEACTIONS:
						{
							if(TestForArgument(i, args.Length, s))
							{
								HistoryExcludedActions = args[++i];
							}
							break;
						}

						case Option.EXCLUDEUSERS:
						{
							if(TestForArgument(i, args.Length, s))
							{
								HistoryExcludedUsers = args[++i];
							}
							break;
						}
						case Option.EXCLUSIVE:
						{
							CheckOutExclusive = true;
							break;
						}

						case Option.SERVER:
						case Option.HOST:
						{
							if(TestForArgument(i, args.Length, s))
							{
								_host = args[++i];
							}
							break;
						}

						case Option.KEEPCHECKEDOUT:
						{
							_bKeepCheckedOut = true;
							break;
						}

						case Option.LEAVEFILE:
						{
							LocalCopy = LocalCopyType.Leave;
							break;
						}

						case Option.MAKEWRITABLE:
						{
							MakeWritable = MakeWritableType.MakeAllFilesWritable;
							break;
						}

						case Option.MAKEREADONLY:
						{
							MakeWritable = MakeWritableType.MakeAllFilesReadOnly;
							break;
						}

						case Option.MERGE:
						{
							if(TestForArgument(i, args.Length, s))
							{
								string strOpt = args[++i];
								MergeOption mo = VaultCmdLineClient.LookupMergeOptionByString( strOpt );
								switch(mo)
								{
									case MergeOption.auto:
									case MergeOption.automatic:
									case MergeOption.automerge:
										Merge = MergeType.AttemptAutomaticMerge;
										break;

									case MergeOption.later:
									case MergeOption.mergelater:
									case MergeOption.no_overwrite:
										Merge = MergeType.MergeLater;
										break;

									case MergeOption.overwrite:
										Merge = MergeType.OverwriteWorkingCopy;
										break;

									default:
										if(Error == false)
										{
											Error = true;
											ErrorMessage = string.Format("Invalid value for -{0}: {1}", Option.MERGE, strOpt);
										}
										break;
								}
							}

							break;
						}

						case Option.NOCLOAKS:
						{
							this.RespectCloaks = false;
							break;
						}

						case Option.NORECURSIVE:
						{
							this.Recursive = false;
							break;
						}

						case Option.NOSSL:
						{
							_ssl = false;
							break;
						}

						case Option.OUT:
						{
							if(TestForArgument(i, args.Length, s))
							{
								string sName = args[++i];
								this.OutFile = sName;
							}
							break;
						}

						case Option.PASSWORD:
						{
							if(TestForArgument(i, args.Length, s))
							{
								_password = args[++i];
							}
							break;
						}
						case Option.PERFORMDELETIONS:
						{
							if(TestForArgument(i, args.Length, s))
							{
								PerformDeletions = VaultCmdLineClient.LookupPerformDeletionsOptionByString( args[++i] );
							}
							break;
						}


						case Option.PROXYSERVER:
						{
							if(TestForArgument(i, args.Length, s))
							{
								_proxyServer = args[++i];
							}
							break;
						}

						case Option.PROXYPORT:
						{
							if(TestForArgument(i, args.Length, s))
							{
								_proxyPort = args[++i];
							}
							break;
						}
						case Option.PROXYUSER:
						{
							if(TestForArgument(i, args.Length, s))
							{
								_proxyUser = args[++i];
							}
							break;
						}
						case Option.PROXYPASSWORD:
						{
							if(TestForArgument(i, args.Length, s))
							{
								_proxyPassword = args[++i];
							}
							break;
						}
						case Option.PROXYDOMAIN:
						{
							if(TestForArgument(i, args.Length, s))
							{
								_proxyDomain = args[++i];
							}
							break;
						}
						case Option.REPOSITORY:
						{
							if(TestForArgument(i, args.Length, s))
								_repository = args[++i];
							break;
						}

						case Option.REQUIRECHECKOUT:
						{
							RequireCheckOut = true;
							break;
						}

						case Option.REVERTFILE:
						{
							LocalCopy = LocalCopyType.Replace;
							break;
						}

						case Option.ROWLIMIT:
						{
							if(TestForArgument(i, args.Length, s))
							{
								string sInt = args[++i];
								try
								{
									HistoryRowLimit = Int32.Parse(sInt);
								}
								catch (Exception)
								{
								}
							}
							break;
						}

						case Option.SETFILETIME:
						{
							if(TestForArgument(i, args.Length, s))
							{
								string strOpt = args[++i];
								FileTimeOption fto = VaultCmdLineClient.LookupFileTimeOptionByString(strOpt);

								switch (fto)
								{
									case FileTimeOption.current:
										this.SetFileTime = SetFileTimeType.Current;
										break;

									case FileTimeOption.checkin:
										this.SetFileTime = SetFileTimeType.CheckIn;
										break;

									case FileTimeOption.modification:
									case FileTimeOption.modified:
										this.SetFileTime = SetFileTimeType.Modification;
										break;

									default:
										if(! Error)
										{
											Error = true;
											ErrorMessage = string.Format("Invalid value for -setfiletime: {0}", strOpt);
										}
										break;
								}
							}
							break;
						}

						case Option.SSL:
						{
							_ssl = true;
							break;
						}

						case Option.UNCHANGED:
						{
							if(TestForArgument(i, args.Length, s))
							{
								string sOpt = args[++i];

								switch (sOpt.ToLower())
								{
									case "leavecheckedout":
										this.Unchanged = UnchangedHandler.LeaveCheckedOut;
										break;

									case "undocheckout":
										this.Unchanged = UnchangedHandler.UndoCheckout;
										break;

									case "checkin":
										this.Unchanged = UnchangedHandler.Checkin;
										break;

									default:
										if(! Error)
										{
											Error = true;
											ErrorMessage = string.Format("Invalid value for -unchanged: {0}", sOpt);
										}
										break;
								}
							}

							break;
						}

						case Option.URL:
						{
							if(TestForArgument(i, args.Length, s))
								_url = args[++i];
							break;
						}

						case Option.USERNAME:
						case Option.USER:
						{
							if(TestForArgument(i, args.Length, s))
							{
								_user = args[++i];
							}
							break;
						}

						case Option.VAULTDIFF:
						{
							if ( TestForArgument(i, args.Length, s) )
							{
								_strDiffBin = args[++i];
							}
							break;
						}

						case Option.VAULTDIFF_OPTIONS:
						{
							if ( TestForArgument(i, args.Length, s) )
							{
								_strDiffArgs = args[++i];
							}
							break;
						}
						case Option.WILDCARD:
						{
							if ( TestForArgument(i, args.Length, s) )
							{
								Wildcard = args[++i];
							}
							break;
						}

						case Option.VERBOSE:
						{
							Verbose = true;
							break;
						}

						case Option.YESIAMSURE:
						{
							_yesiamsure = true;
							break;
						}

						default:
						{
							if(! Error)
							{
								Error = true;
								ErrorMessage = string.Format("unknown option: {0} - run 'vault.exe HELP' for help", s);
							}
							break;
						}

					}
				}
				else
				{
					if (!checkForCommand(s))
					{
						if (
							(s[0] == VaultDefine.RootName[0])
							&& (s.EndsWith(VaultDefine.PathSeparator.ToString()))
							)
						{
							s = RepositoryPath.NormalizeFolder(s);
						}
						else if (s == VaultDefine.RootName)
						{
							s = RepositoryPath.NormalizeFolder(s);
						}

						items.Add(s);
					}
				}
			}
		}

		public void ParseBatchCommand(string strBatchCmd)
		{
			string[] batchcommand = SplitCmdIntoArgs(strBatchCmd);
			if ( batchcommand != null )
			{
				if ( ValidBatchCommandOptions(batchcommand) == true )
				{
					Parse(batchcommand);
				}
				else
				{
					throw new UsageException(string.Format("The batch command {0} contains invalid options.  Please do not include login, server, or repository based options in the batch.", batchcommand) );
				}
			}
		}

		public void WriteLine(string s)
		{
			this.Out.WriteLine(s);
		}

		public static bool CommandNeedsAdmin(Command c)
		{
			bool bRet = false;
			switch (c)
			{
				case Command.ADDREPOSITORY:
				case Command.ADDUSER:
				case Command.LISTUSERS:
				case Command.OBLITERATE:
					bRet = true;
					break;
			}
			return bRet;
		}
		private bool ValidBatchCommandOptions(string[] batchcommand)
		{
			bool bRet = true;
			if (batchcommand != null)
			{
				foreach (string s in batchcommand)
				{
					Option o = VaultCmdLineClient.LookupOptionByString(s.Substring(1));
					switch (o)
					{
						case Option.HOST:
						case Option.NOSSL:
						case Option.OUT:
						case Option.PASSWORD:
						case Option.REPOSITORY:
						case Option.SERVER:
						case Option.SSL:
						case Option.URL:
						case Option.USER:
						case Option.USERNAME:
							bRet = false;
							break;
					}

					if (bRet == false)
					{
						break;
					}
				}
			}
			return bRet;
		}

		public void SetBatchModeOperation(string strBatchModeName)
		{
			_strBatchName = strBatchModeName;
			if ( _strBatchName == VaultCmdLineClientDefines.StdInParam)
			{
				_trBatchReader = System.Console.In;
			}
			else
			{
				if ( File.Exists(_strBatchName) == true )
				{
					try
					{
						_trBatchReader = new StreamReader(new FileStream(_strBatchName, FileMode.Open, FileAccess.Read, FileShare.Read));
					}
					catch (Exception e)
					{
						_trBatchReader = null;

						Error = true;
						ErrorMessage = e.Message;
					}
				}
				else
				{
					_trBatchReader = null;

					Error = true;
					ErrorMessage = string.Format("The file: {0} could not be found for batch processing.", _strBatchName);
				}
			}

			_bBatchMode = ( _trBatchReader != null );
		}

		#endregion // Methods

		#region Accessors

		public string Comment
		{
			get { return _comment; }
			set { _comment = value; }
		}

		public string User
		{
			get { return _user; }
			set { _user = value; }
		}

		public string Repository
		{
			get { return _repository; }
			set { _repository = value; }
		}

		public string Password
		{
			get { return _password; }
			set { _password = value; }
		}

		public string Url
		{
			get 
			{
				if (_url != null)
				{
					return _url;
				}
				else
				{
					if (_host != null)
					{
						UriBuilder uri;
						int idx = _host.IndexOf(":");
						if (idx < 0)
						{
							uri = new UriBuilder((_ssl ? "https" : "http"), _host);
						} 
						else 
						{
							int iPort=80;
							try 
							{
								iPort = Int32.Parse(_host.Substring(idx+1));
							}
							catch {};
							uri = new UriBuilder((_ssl ? "https" : "http"), _host.Substring(0, idx), iPort);
						}
						uri.Path = "VaultService";
						return uri.ToString();
					}
					else
					{
						return null;
					}
				}
			}
		}

		public bool YesIAmSure
		{
			get { return _yesiamsure; }
			set { _yesiamsure = value; }
		}

		public string ProxyServer
		{
			get { return _proxyServer; }
			set { _proxyServer = value; }
		}

		public string ProxyPort
		{
			get { return _proxyPort; }
			set { _proxyPort = value; }
		}

		public string ProxyUser
		{
			get { return _proxyUser; }
			set { _proxyUser = value; }
		}

		public string ProxyPassword
		{
			get { return _proxyPassword; }
			set { _proxyPassword = value; }
		}

		public string ProxyDomain
		{
			get { return _proxyDomain; }
			set { _proxyDomain = value; }
		}

		public string RawUrl
		{
			get { return _url; }
			set { _url = value; }
		}

		public string Host
		{
			get { return _host; }
			set { _host = value; }
		}

		public bool SSL
		{
			get { return _ssl; }
			set { _ssl = value; }
		}

		public Command Cmd 
		{
			get { return _cmd; }
		}

		public TextReader BatchTextReader
		{
			get { return _trBatchReader; }
		}

		public string BatchName
		{
			get { return _strBatchName; }
		}

		public bool InBatchMode
		{
			get { return _bBatchMode; }
			set { _bBatchMode = value; }
		}

		public string DiffBin
		{
			get { return _strDiffBin; }
			set { _strDiffBin = value; }
		}
		public string DiffCompareTo
		{
			get { return _strDiffCompareTo; }
			set { _strDiffCompareTo = value; }
		}
		public string DiffArgs
		{
			get { return _strDiffArgs; }
			set { _strDiffArgs = value; }
		}
		public bool KeepCheckedOut 
		{
			get { return _bKeepCheckedOut; }
			set { _bKeepCheckedOut = value; }
		}

		#endregion // Accessors
	}


	public class Pair
	{
		public string strName;
		public string strValue;

		public Pair(string name, string val)
		{
			strName = name;
			strValue = val;
		}
	}

	public class Element
	{
		public string name;
		public ArrayList pairs;
		public ArrayList children;
		public Element parent;
		public ArrayList content;

		public Element(string _name, Element _parent)
		{
			name = _name;
			pairs = new ArrayList();
			children = new ArrayList();
			parent = _parent;
			content = new ArrayList();
		}

		public void AddPair(string name, string val)
		{
			pairs.Add(new Pair(name, val));
		}

		public bool isEmpty()
		{
			return (children.Count == 0) && (content.Count == 0);
		}

		public void AddChild(Element e)
		{
			children.Add(e);
		}
	}


}
