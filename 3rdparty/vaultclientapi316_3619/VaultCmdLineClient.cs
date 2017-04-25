/*
	SourceGear Vault
	Copyright 2002-2003 SourceGear Corporation
	All Rights Reserved.
	
	You may not distribute this code, or any portion thereof, 
	or any derived work thereof, neither in source code form 
	nor in compiled form, to anyone outside your organization.
	
	The opslib API is currently ready only for the somewhat
	adventuresome.  The following disclaimers apply:
	
	1.  The API is not frozen.  If you write code to the opslib
		API, you may need to update your code the next time we
		release.
		
	2.  The API is not well documented.  (Corollary:  This
		sample code is not well commented.)
	
	3.  The only support we can offer is currently on our
		online forum at http://support.sourcegear.com
		
	We are continually working to improve the usability of this
	API for our customers.  Your feedback is always welcome.
	

	Special thanks to Darren Sargent for implementing the GETLABELDIFF
	command and the -verbose option on the LISTUSERS command
	
*/

using System;
using System.IO;
using System.Collections;
using System.Text;
using System.Text.RegularExpressions;


using VaultClientOperationsLib;
using VaultClientNetLib;
using VaultLib;

namespace VaultCmdLineClient
{

	class VaultCmdLineClient
	{
		const string SESSION_FILENAME = "vault_cmdline_client_session.txt";

		private ClientInstance _ci = null;
		private Args _args = null;
		private XMLOutputWriter _xml = null;

		static private string _cryptVector = "lWW1nOh5RUY=";
		static private string _cryptKey = "lXTnY5DKE9/x/5EAL98OKUqV8GA+icuF";


		string GetSessionFileName()
		{
			return Path.Combine(_ci.LocalStoreBasePath, SESSION_FILENAME);
		}
		void StoreSession(string strURLBase, string strUsername, string strPassword, string strRepos)
		{
			TextWriter tw = null;
			try
			{
				VaultLib.VaultCrypto crypt = new VaultLib.VaultCrypto(_cryptVector, _cryptKey);

				tw = new StreamWriter(new FileStream(GetSessionFileName(), FileMode.Create, FileAccess.Write, FileShare.None));
				tw.WriteLine(strURLBase);
				tw.WriteLine(crypt.Encrypt(strUsername));
				tw.WriteLine(crypt.Encrypt(strPassword));
				tw.WriteLine(strRepos);
				tw.Close();
			}
			catch (Exception)
			{
				// if anything goes wrong with the encryption, just do it without encryption
				try
				{
					if (tw != null)
						tw.Close();

					tw = new StreamWriter(new FileStream(GetSessionFileName(), FileMode.Create, FileAccess.Write, FileShare.None));
					tw.WriteLine(strURLBase);
					tw.WriteLine(strUsername);
					tw.WriteLine(strPassword);
					tw.WriteLine(strRepos);
					tw.Close();
				}
				catch (Exception)
				{
					throw;
				}
			}
		}
		void RetrieveSession(ref string strURLBase, ref string strUsername, ref string strPassword, ref string strRepos)
		{
			TextReader tw = null;
			try
			{
				VaultLib.VaultCrypto crypt = new VaultLib.VaultCrypto(_cryptVector, _cryptKey);

				tw = new StreamReader(new FileStream(GetSessionFileName(), FileMode.Open, FileAccess.Read, FileShare.Read));

				strURLBase = tw.ReadLine();
				strUsername = crypt.Decrypt(tw.ReadLine());
				strPassword = crypt.Decrypt(tw.ReadLine());
				strRepos = tw.ReadLine();

				tw.Close();
			}

			catch (Exception)
			{
				// if anything goes wrong with the encryption, just do it without encryption
				try
				{
					if (tw != null)
						tw.Close();

					tw = new StreamReader(new FileStream(GetSessionFileName(), FileMode.Open, FileAccess.Read, FileShare.Read));
					strURLBase = tw.ReadLine();
					strUsername = tw.ReadLine();
					strPassword = tw.ReadLine();
					strRepos = tw.ReadLine();
					tw.Close();
				}
				catch (Exception)
				{
				}
			}
		}
		void PurgeSession()
		{
			try
			{
				_ci = new ClientInstance();
				_ci.UseFileSystemWatchers = false;
				_ci.Init(VaultClientNetLib.VaultConnection.AccessLevelType.Client);

				string strFN = GetSessionFileName();
				if ( File.Exists(strFN) == true )
				{
					File.Delete(strFN);
				}
			}
			catch (Exception)
			{
			}
		}


		public static bool CommandNeedsLogin(Command c)
		{
			bool bRet = true;
			switch(c)
			{
				case Command.HELP:
				case Command.NONE:
				case Command.INVALID:
				case Command.FORGETLOGIN:
					bRet = false;
					break;
			}
			return bRet;
		}
		public static bool CommandNeedsRepositorySpecified(Command c)
		{
			bool bRet = true;
			switch (c)
			{
				case Command.ADDREPOSITORY:
				case Command.ADDUSER:
				case Command.LISTUSERS:
				case Command.FORGETLOGIN:
				case Command.HELP:
				case Command.NONE:
				case Command.INVALID:
				case Command.LISTREPOSITORIES:
				case Command.REMEMBERLOGIN:
					bRet = false;
					break;
			}
			return bRet;
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


		void Login(bool bAllowAuto, bool bSaveSession)
		{
			bool bResult = false;

			// get access level req'd by the command
			VaultConnection.AccessLevelType altCommand = (CommandNeedsAdmin(_args.Cmd) == false) ? VaultConnection.AccessLevelType.Client : VaultConnection.AccessLevelType.Admin;

			if ( _ci != null ) 
			{
				if ( (_ci.ConnectionStateType == ConnectionStateType.Connected) && 
					(_ci.AccessLevel == altCommand) )
				{
					// already logged in
					return;
				}
				else
				{
					_ci.Logout();
				}
			}

			// init the client instance
			_ci = new ClientInstance();
			_ci.UseFileSystemWatchers = false;
			_ci.Init(altCommand);
			
			// get info for logging in
			string strURL = _args.Url;
			string strUser = _args.User;
			string strPassword = _args.Password;
			string strRepository = _args.Repository;
			if (
				(bAllowAuto == true) && 
				((strURL == null) || (strUser == null))
				)
			{
				RetrieveSession(ref strURL, ref strUser, ref strPassword, ref strRepository);

				// Override from cmd line 
				strURL = _args.Url != null ? _args.Url : strURL; 
				strUser = _args.User != null ? _args.User : strUser; 
				strPassword = _args.Password != null ? _args.Password : strPassword; 
				strRepository = _args.Repository != null && _args.Repository.Length > 0 ? _args.Repository : strRepository; 
			}


			if (
				(strURL != null) && 
				(strUser != null)
				)
			{
				if (strRepository == null || strRepository.Length < 1)
				{
					if ( CommandNeedsRepositorySpecified(_args.Cmd) == true )
					{
						throw new UsageException(string.Format("You must specify a repository for the {0} command", _args.Cmd));
					}
				}

				if (strPassword == null)
				{
					strPassword = "";
				}
				
				try
				{
					if (_args.ProxyServer != null && _args.ProxyServer != string.Empty)
						_ci.Connection.ResetProxy(1, _args.ProxyServer, _args.ProxyPort);
					if (_args.ProxyUser != null && _args.ProxyUser != string.Empty)
					{
						if (_args.ProxyDomain != null && _args.ProxyDomain != string.Empty)
							_ci.Connection.Proxy.Credentials = new System.Net.NetworkCredential(_args.ProxyUser, _args.ProxyPassword, _args.ProxyDomain);
						else
							_ci.Connection.Proxy.Credentials = new System.Net.NetworkCredential(_args.ProxyUser, _args.ProxyPassword );
					}
					_ci.Login(strURL, strUser, strPassword);
				}
				catch(Exception e)
				{
					string message = string.Empty;

					if (VaultClientNetLib.VaultConnection.GetSoapExceptionStatusCodeInt(e) != -1)
						message = VaultClientNetLib.Resources.SoapExceptionStrings.GetString(VaultClientNetLib.VaultConnection.GetSoapExceptionStatusCodeInt(e).ToString());
					else
						message =  string.Format("The connection to the server failed: server cannot be contacted or uses a protocol that is not supported by this client. {0}", e.Message);

					throw new Exception( message ); 
				}

				if (_ci.ConnectionStateType != ConnectionStateType.Connected)
				{
					throw new Exception("Login failed.");
				}
				else
				{
					bResult = true;

					if (strRepository != null && strRepository.Length > 0)
					{
						VaultRepositoryInfo theRep = null;

						VaultRepositoryInfo[] reps = null;
						_ci.ListRepositories(ref reps);

						foreach (VaultRepositoryInfo r in reps)
						{
							if (r.RepName.ToLower() == strRepository.ToLower())
							{
								theRep = r;
								break;
							}
						}

						if (theRep != null)
						{
							_ci.SetActiveRepositoryID(theRep.RepID, strUser, theRep.UniqueRepID, true, true);
						}
						else
						{
							throw new UsageException(string.Format("Repository {0} not found", strRepository));
						}
					}

					// Note: 9/9/04 Cautiously removing these overrides.  If the user doesn't specify
					// these options on the command line, then the clc program defaults end up overriding
					// the user options.  Explicitly specified input options should override user options from
					// the database, and CLC programatic options should not override anything.
					// set working folder options
					//_ci.WorkingFolderOptions.RequireCheckOutBeforeCheckIn = _args.RequireCheckOut;
					//_ci.WorkingFolderOptions.DefaultLocalCopyType = _args.LocalCopy;

					if ( (bAllowAuto == false ) &&
						(bSaveSession) )
					{
						StoreSession(strURL, strUser, strPassword, strRepository);
					}
				}
			}
			else
			{
				throw new UsageException("Please specify -user, -password, and -host.");
			}

			if ( _ci != null )
			{
				_ci.NewMessage += new NewMessageEventHandler(NewMessageHandler);
				_ci.NewBulkMessages += new NewBulkMessagesEventHandler(NewBulkMessagesHandler);
			}

			if (bResult == false)
			{
				throw new UsageException("Please verify that you have specified -user, -password, -host, and -repository.");
			}
		}
		void Login()
		{
			Login(true, false);
		}
		void Logout(bool bForceLogout)
		{
			if ( (_ci != null) && 
				((bForceLogout == true) || (_args.InBatchMode == false))
				)
			{
				// log out when forced or not in batch mode.
				_ci.Logout();
				_ci = null;
			}
		}

		bool ParseBool(string s)
		{
			bool bRet = false;

			switch (s.ToLower().Trim())
			{
				case "yes":
				case "true":
					bRet = true;
					break;
			}
			return bRet;
		}

		static public Option LookupOptionByString(string strOption)
		{
			Option oRet = Option.INVALID;

			foreach (Option o in Enum.GetValues(typeof(Option)))
			{
				// Note that our cmdline options are case-INsensitive.
				// Most UNIX apps have cmdline options which are case-sensitive
				bool bCaseInsensitive = true;
				if (string.Compare(strOption, o.ToString(), bCaseInsensitive) == 0)
				{
					oRet = o;
					break;
				}
			}
			return oRet;
		}
		static public Command LookupCommandByString(string strCmd)
		{
			Command cmdRet = Command.NONE;

			foreach (Command c in Enum.GetValues(typeof(Command)))
			{
				// Note that our cmdline options are case-INsensitive.
				// Most UNIX apps have cmdline options which are case-sensitive
				bool bCaseInsensitive = true;
				if (string.Compare(strCmd, c.ToString(), bCaseInsensitive) == 0)
				{
					cmdRet = c;
					break;
				}
			}
			return cmdRet;
		}
		static public PerformDeletionsType LookupPerformDeletionsOptionByString(string strPerformDeletionsOption)
		{
			PerformDeletionsType pdoRet = PerformDeletionsType.DoNotRemoveWorkingCopy;

			foreach (PerformDeletionsType pdo in Enum.GetValues(typeof(PerformDeletionsType)))
			{
				// Note that our cmdline options are case-INsensitive.
				// Most UNIX apps have cmdline options which are case-sensitive
				bool bCaseInsensitive = true;
				if (string.Compare(strPerformDeletionsOption, pdo.ToString(), bCaseInsensitive) == 0)
				{
					pdoRet = pdo;
					break;
				}
			}
			return pdoRet;
		}
		static public MergeOption LookupMergeOptionByString(string strMergeOption)
		{
			MergeOption moRet = MergeOption.none;

			foreach (MergeOption mo in Enum.GetValues(typeof(MergeOption)))
			{
				// Note that our cmdline options are case-INsensitive.
				// Most UNIX apps have cmdline options which are case-sensitive
				bool bCaseInsensitive = true;
				if (string.Compare(strMergeOption, mo.ToString(), bCaseInsensitive) == 0)
				{
					moRet = mo;
					break;
				}
			}
			return moRet;
		}
		static public FileTimeOption LookupFileTimeOptionByString(string strFileTimeOption)
		{
			FileTimeOption ftoRet = FileTimeOption.none;

			foreach (FileTimeOption fto in Enum.GetValues(typeof(FileTimeOption)))
			{
				// Note that our cmdline options are case-INsensitive.
				// Most UNIX apps have cmdline options which are case-sensitive
				bool bCaseInsensitive = true;
				if (string.Compare(strFileTimeOption, fto.ToString(), bCaseInsensitive) == 0)
				{
					ftoRet = fto;
					break;
				}
			}
			return ftoRet;
		}
		static public CompareToOption LookupCompareToOptionByString(string strCompareToOption)
		{
			CompareToOption ctoRet = CompareToOption.current;

			foreach (CompareToOption cto in Enum.GetValues(typeof(CompareToOption)))
			{
				// Note that our cmdline options are case-INsensitive.
				// Most UNIX apps have cmdline options which are case-sensitive
				bool bCaseInsensitive = true;
				if (string.Compare(strCompareToOption, cto.ToString(), bCaseInsensitive) == 0)
				{
					ctoRet = cto;
					break;
				}
			}
			return ctoRet;
		}
		static public DateSortOption LookupDateSortOptionByString(string strDateSort)
		{
			DateSortOption dsoRet = DateSortOption.desc;

			foreach (DateSortOption dso in Enum.GetValues(typeof(DateSortOption)))
			{
				// Note that our cmdline options are case-INsensitive.
				// Most UNIX apps have cmdline options which are case-sensitive
				bool bCaseInsensitive = true;
				if (string.Compare(strDateSort, dso.ToString(), bCaseInsensitive) == 0)
				{
					dsoRet = dso;
					break;
				}
			}
			return dsoRet;
		}


		private string GetStatusString(WorkingFolderFileStatus st)
		{
			return (st != WorkingFolderFileStatus.None) ? st.ToString() : string.Empty;
		}

		private void NewMessageHandler(object sender, ProgressMessage message)
		{
			_xml.WriteUserMessage(message.Message);
		}

		private void NewBulkMessagesHandler(object sender, ArrayList aProgressMessages)
		{
			if (_args.Verbose)
			{
				foreach (ProgressMessage message in aProgressMessages)
				{
					_xml.WriteUserMessage(message.Message);
				}
			}
		}

		void WriteListWorkingFolders()
		{
			string[] repos = null;
			string[] disk = null;

			_ci.TreeCache.GetWorkingFolderAssignments(ref repos, ref disk);

			_xml.Begin("listworkingfolders");
			for (int i=0; i<repos.Length; i++)
			{
				_xml.Begin("workingfolder");
				_xml.AddPair("reposfolder", repos[i]);
				_xml.AddPair("localfolder", disk[i]);
				_xml.End();
			}
			_xml.End();
		}

		void WriteFolder(VaultClientFolder vcfolder, bool recursive, int depth)
		{
			WorkingFolder wf = _ci.GetWorkingFolder(vcfolder);

			_xml.Begin("folder");
			_xml.AddPair("name", vcfolder.FullPath);
			if (wf != null)
			{
				_xml.AddPair("workingfolder", wf.GetLocalFolderPath());
			}
			foreach (VaultClientFile file in vcfolder.Files)
			{
				_xml.Begin("file");
				_xml.AddPair("name", file.Name);
				_xml.AddPair("version", file.Version);
				_xml.AddPair("length", file.FileLength);
				_xml.AddPair("objectid", file.ID);
				_xml.AddPair("objectversionid", file.ObjVerID);

				string strCheckOuts = _ci.GetCheckOuts(file);
				if (
					(strCheckOuts != null)
					&& (strCheckOuts.Length > 0)
					)
				{
					_xml.AddPair("checkouts", strCheckOuts);
				}

				if (wf != null)
				{
					WorkingFolderFileStatus st = wf.GetStatus(file);
					if (st != WorkingFolderFileStatus.None)
					{
						_xml.AddPair("status", GetStatusString(st));
					}
				}

				_xml.End();
			}

			foreach (VaultClientFolder subfolder in vcfolder.Folders)
			{
				if (recursive)
				{
					WriteFolder(subfolder, recursive, depth+1);
				}
			}
			_xml.End();
		}

		void WriteChangeSet()
		{
			_ci.UpdateKnownChanges_All(false);
			ChangeSetItemColl csic = _ci.InternalChangeSet_GetItems(true);
			WriteChangeSet(csic);
		}

		void WriteChangeSet(ChangeSetItemColl csic)
		{
			if ( _xml != null )
			{
				_xml.Begin("changeset");
			}

			try
			{
				if ( (csic != null) && (csic.Count > 0) )
				{
					int i, nCnt;
					ChangeSetItem csi = null;
					for (i = 0, nCnt = csic.Count; i<nCnt; i++)
					{
						csi = csic[i];
						switch (csi.Type)
						{
							case ChangeSetItemType.AddFile:
							{
								ChangeSetItem_AddFile it = (ChangeSetItem_AddFile) csi;
							
								_xml.Begin("AddFile");
								_xml.AddPair("id", i.ToString());
								_xml.AddPair("repospath", it.DisplayRepositoryPath);
								_xml.AddPair("localpath", it.DiskFile);
								_xml.End();
							
								break;
							}
							case ChangeSetItemType.AddFolder:
							{
								ChangeSetItem_AddFolder it = (ChangeSetItem_AddFolder) csi;

								_xml.Begin("AddFolder");
								_xml.AddPair("id", i.ToString());
								_xml.AddPair("reposfolder", it.DisplayRepositoryPath);
								_xml.AddPair("localfolder", it.DiskFolder);
								_xml.End();

								break;
							}
							case ChangeSetItemType.DeleteFile:
							{
								ChangeSetItem_DeleteFile it = (ChangeSetItem_DeleteFile) csi;

								_xml.Begin("DeleteFile");
								_xml.AddPair("id", i.ToString());
								_xml.AddPair("repospath", it.DisplayRepositoryPath);
								_xml.End();

								break;
							}
							case ChangeSetItemType.DeleteFolder:
							{
								ChangeSetItem_DeleteFolder it = (ChangeSetItem_DeleteFolder) csi;

								_xml.Begin("DeleteFolder");
								_xml.AddPair("id", i.ToString());
								_xml.AddPair("repospath", it.DisplayRepositoryPath);
								_xml.End();

								break;
							}
							case ChangeSetItemType.CreateFolder:
							{
								ChangeSetItem_CreateFolder it = (ChangeSetItem_CreateFolder) csi;

								_xml.Begin("CreateFolder");
								_xml.AddPair("id", i.ToString());
								_xml.AddPair("repospath", it.DisplayRepositoryPath);
								_xml.End();

								break;
							}
							case ChangeSetItemType.BranchCopy:
							{
								ChangeSetItem_CopyBranch it = (ChangeSetItem_CopyBranch) csi;

								_xml.Begin("CopyBranch");
								_xml.AddPair("id", i.ToString());
								_xml.AddPair("repospath", it.DisplayRepositoryPath);
								_xml.AddPair("branchpath", it.BranchPath);
								_xml.End();
							
								break;
							}
							case ChangeSetItemType.BranchShare:
							{
								ChangeSetItem_ShareBranch it = (ChangeSetItem_ShareBranch) csi;

								_xml.Begin("ShareBranch");
								_xml.AddPair("id", i.ToString());
								_xml.AddPair("repospath", it.DisplayRepositoryPath);
								_xml.AddPair("branchpath", it.BranchPath);
								_xml.End();
							
								break;
							}
							case ChangeSetItemType.Share:
							{
								ChangeSetItem_Share it = (ChangeSetItem_Share) csi;

								_xml.Begin("Share");
								_xml.AddPair("id", i.ToString());
								_xml.AddPair("repospath", it.RepositoryPath);
								_xml.AddPair("sharepath", it.NewSharePath);
								_xml.End();

								break;
							}
							case ChangeSetItemType.Pin:
							{
								ChangeSetItem_Pin it = (ChangeSetItem_Pin) csi;

								_xml.Begin("Pin");
								_xml.AddPair("id", i.ToString());
								_xml.AddPair("repospath", it.DisplayRepositoryPath);
								_xml.End();

								break;
							}
							case ChangeSetItemType.Rename:
							{
								ChangeSetItem_Rename it = (ChangeSetItem_Rename) csi;

								_xml.Begin("Rename");
								_xml.AddPair("id", i.ToString());
								_xml.AddPair("repospath", it.OldRepositoryPath);
								_xml.AddPair("newname", it.NewName);
								_xml.End();
								break;
							}
							case ChangeSetItemType.Unpin:
							{
								ChangeSetItem_Unpin it = (ChangeSetItem_Unpin) csi;

								_xml.Begin("Unpin");
								_xml.AddPair("id", i.ToString());
								_xml.AddPair("repospath", it.DisplayRepositoryPath);
								_xml.End();

								break;
							}
							case ChangeSetItemType.Move:
							{
								ChangeSetItem_Move it = (ChangeSetItem_Move) csi;

								_xml.Begin("Move");
								_xml.AddPair("id", i.ToString());
								_xml.AddPair("repospath", it.OldRepositoryPath);
								_xml.AddPair("newpath", it.NewOwnerPath);
								_xml.End();

								break;
							}
							case ChangeSetItemType.Modified:
							{
								ChangeSetItem_Modified it = (ChangeSetItem_Modified) csi;

								_xml.Begin("ModifyFile");
								_xml.AddPair("id", i.ToString());
								_xml.AddPair("repospath", it.DisplayRepositoryPath);
								_xml.AddPair("localpath", it.DiskFile);
								_xml.End();

								break;
							}
							case ChangeSetItemType.Unmodified:
							{
								ChangeSetItem_Unmodified it = (ChangeSetItem_Unmodified) csi;

								_xml.Begin("UnmodifiedFile");
								_xml.AddPair("id", i.ToString());
								_xml.AddPair("respospath", it.DisplayRepositoryPath);
								_xml.AddPair("localpath", it.DiskFile);
								_xml.End();

								break;
							}
							case ChangeSetItemType.ChangeExtProperties:
							{
								ChangeSetItem_ChangeExtProperties it = (ChangeSetItem_ChangeExtProperties) csi;

								_xml.Begin("ChangeExtProperties");
								_xml.AddPair("id", i.ToString());
								_xml.AddPair("repospath", it.DisplayRepositoryPath);
								_xml.End();

								break;
							}
							case ChangeSetItemType.CheckedOutMissing:
							{
								ChangeSetItem_CheckedOutMissing it = (ChangeSetItem_CheckedOutMissing) csi;

								_xml.Begin("CheckedOutMissing");
								_xml.AddPair("id", i.ToString());
								_xml.AddPair("respospath", it.DisplayRepositoryPath);
								_xml.End();

								break;
							}
							case ChangeSetItemType.Snapshot:
							{
								ChangeSetItem_Snapshot it = (ChangeSetItem_Snapshot)csi;
								_xml.Begin("Snapshot");
								_xml.AddPair("id", i.ToString());
								_xml.AddPair("respospath", it.RepositoryPath);
								_xml.AddPair("parentpath", it.SnapshotPath);
								_xml.End();

								break;
							}
							default:
							{
								// this should never happen.
								throw new Exception("ACK!  There is a ChangeSet item we don't recognize.");
							}
						}
					}
				}
			}
			finally
			{
				// end the xml pair.
				if ( _xml != null )
				{
					_xml.End();
				}
			}

		}

		void WriteCheckOuts(VaultClientCheckOutList checkOuts)
		{
			_xml.Begin("checkoutlist");

			if ( (checkOuts != null) && (checkOuts.Count > 0) )
			{
				foreach (VaultClientCheckOutItem item in checkOuts)
				{
					_xml.Begin("checkoutitem");
					_xml.AddPair("id", item.FileID);
					
					foreach (VaultClientCheckOutUser user in item.CheckOutUsers)
					{
						_xml.Begin("checkoutuser");
						_xml.AddPair("username", user.Name);
						_xml.AddPair("version", user.Version);
						_xml.AddPair("repositorypath", user.RepPath);

						switch (user.LockType)
						{
							case VaultCheckOutType.None:
								_xml.AddPair("locktype", "none");
								break;
							case VaultCheckOutType.CheckOut:
								_xml.AddPair("locktype", "checkout");
								break;
							case VaultCheckOutType.Exclusive:
								_xml.AddPair("locktype", "exclusive");
								break;
							default:
								_xml.AddPair("locktype", "unknown");
								break;
						}

						_xml.AddPair("comment", user.Comment);
						_xml.AddPair("hostname", user.Hostname);
						_xml.AddPair("localpath", user.LocalPath);
						_xml.AddPair("folderid", user.FolderID);
						_xml.AddPair("lockedwhen", user.LockedWhen.ToString());
						_xml.AddPair("miscinfo", user.MiscInfo);
						_xml.End();
					}
					
					_xml.End();
				}
			}

			_xml.End();
		}

		private string GetHistItemTypeString(int x)
		{
			return x.ToString();
		}

		private void ValidateReposPath(string s)
		{
			if (s[0] != '$')
			{
				throw new UsageException(string.Format("Invalid repository path: {0}", s));
			}

			if (s.Length > 1)
			{
				if (s[1] != '/')
				{
					throw new UsageException(string.Format("Invalid repository path: {0}", s));
				}
			}
		}

		private bool ValidateChangeSetItemID(int nID)
		{
			ChangeSetItemColl csic = _ci.InternalChangeSet_GetItems(true);
			return ValidateChangeSetItemID(nID, csic);
		}

		private bool ValidateChangeSetItemID(int nID, ChangeSetItemColl csic)
		{
			// get the change set.
			bool bValid = false;
			if ( csic != null )
			{
				bValid = ( (nID >= 0) && (nID < csic.Count) );
			}
			return bValid;
		}

		private void DoListUsers()
		{
			VaultUser[] users = null;
			_ci.Connection.ListUsers(ref users);

			// look up ID for repository specified
			int reposID=-1;
			try 
			{
				reposID = GetRepositoryId( _args.Repository );
			}
			catch
			{
			}

			// sort users before outputting to XML stream
			Array.Sort(users, new UserItemComparer());

			_xml.Begin("listusers");
			foreach (VaultUser u in users)
			{
				_xml.Begin("user");
				_xml.AddPair("login", u.Login);
				if ( _args.Verbose )
				{
					_xml.AddPair("email", u.Email);
					_xml.AddPair("active", u.isActive);

					// groups
					_xml.Begin("groups");
					VaultGroup[] groups = u.BelongToGroups;

					// sort groups before outputting to XML stream
					Array.Sort(groups, new GroupComparer() );

					foreach ( VaultGroup aGroup in groups )
					{
						_xml.Begin("group");
						_xml.WriteContent(aGroup.Name);
						_xml.End();
					}
					_xml.End();

					// rights
					_xml.AddPair("defaultRights", DecodeUserRights(u.DefaultRights) );
					_xml.Begin("folderRights");
					VaultFolderRightsItem[] rights = null;
					_ci.Connection.ListRightsByUser(u.UserID, ref rights);

					// sort rights before outputting XML stream
					Array.Sort(rights, new RightComparer() );

					if (rights.Length > 0)
					{
						foreach ( VaultFolderRightsItem anItem in rights )
						{
							if ( reposID == -1 || anItem.RepID == reposID )
							{
								_xml.Begin("singleRight");
								if (reposID == -1)
								{
									_xml.AddPair("repositoryID", anItem.RepID);
								}
								_xml.AddPair("folder", anItem.Path);
								_xml.AddPair("right", DecodeUserRights(anItem.FolderRights));
								_xml.End();
							}
						}
					}
					_xml.End();
				}
				_xml.End();
			}
			_xml.End();
		}

		private void x_emitOpItem(string tag, VaultClientFile f)
		{
			_xml.Begin(tag);
			_xml.AddPair("fullpath", f.FullPath);
			_xml.AddPair("version", f.Version);
			_xml.End();
		}

		private void x_emitOpItem(string tag, VaultClientFolder f)
		{
			_xml.Begin(tag);
			_xml.AddPair("fullpath", f.FullPath);
			_xml.AddPair("version", f.Version);
			_xml.End();
		}

		public bool ProcessCommand(Args curArg)
		{
			bool bSuccess = true;

			// assign the new set of arguments
			_args = curArg;

			try
			{
				switch (curArg.Cmd)
				{
					case Command.ADD:
					{
						// The first item must be the repository folder
						// All other items are local paths to be added
						if (curArg.items.Count < 2)
						{
							throw new UsageException("usage: ADD repository_folder path_to_add [...]");
						}

						string strReposFolder = (string) curArg.items[0];

						ArrayList strItemArray = new ArrayList();
						for (int i=1; i < curArg.items.Count; i++)
						{
							strItemArray.Add(curArg.items[i]);
						}

						bSuccess = ProcessCommandAdd(strReposFolder, strItemArray);
						break;
					}

					case Command.ADDREPOSITORY:
					{
						if (curArg.items.Count != 1)
						{
							throw new UsageException("usage: ADDREPOSITORY repository_folder");
						}

						string strNewReposName = (string) curArg.items[0];

						bSuccess = ProcessCommandAddRepository(strNewReposName);
						break;
					}

					case Command.ADDUSER:
					{
						if ( (curArg.items.Count == 0) || (curArg.items.Count > 3) )
						{
							throw new UsageException("usage: ADDUSER login [password] [email]");
						}

						string strLogin = (string) curArg.items[0];

						string strPassword = string.Empty;
						if ( curArg.items.Count > 1 )
						{
							strPassword = (string) curArg.items[1];
						}

						string strEmail = null;
						if ( curArg.items.Count == 3 )
						{
							strEmail = (string) curArg.items[2];
						}

						bSuccess = ProcessCommandAddUser(strLogin, strPassword, strEmail);
						break;
					}
					case Command.BATCH:
					{
						throw new Exception( string.Format("{0} cannot be called as a batch command", curArg.Cmd) );
					}
					case Command.BLAME:
					{
						if (curArg.items.Count != 2 && curArg.items.Count != 3)
						{
							throw new UsageException("usage: BLAME path linenumber [endversion]");
						}
						string strReposPath = (string) curArg.items[0];
						ValidateReposPath(strReposPath);

						int linenumber = int.Parse((string)curArg.items[1]);
						int endversion = -1;
						if (curArg.items.Count == 3)
							endversion = int.Parse((string)curArg.items[2]);

						bSuccess = ProcessCommandBlame(strReposPath, linenumber, endversion);
						break;
					}

					case Command.BRANCH:
					{
						if (curArg.items.Count != 2)
						{
							throw new UsageException("usage: BRANCH from_path to_path");
						}

						string strReposPath_From = (string) curArg.items[0];
						ValidateReposPath(strReposPath_From);

						string strReposPath_To = (string) curArg.items[1];
						ValidateReposPath(strReposPath_To);

						bSuccess = ProcessCommandBranch(strReposPath_From, strReposPath_To);
						break;
					}

					case Command.CHECKOUT:
					{
						if (curArg.items.Count < 1)
						{
							throw new UsageException("usage: CHECKOUT item [...]");
						}

						ArrayList strItemArray = new ArrayList();
						foreach (string strReposItem in curArg.items)
						{
							ValidateReposPath(strReposItem);
							strItemArray.Add(strReposItem);
						}

						bSuccess = ProcessCommandCheckout(strItemArray);
						break;
					}
					case Command.CLOAK:
					{
						if (curArg.items.Count < 1)
						{
							throw new UsageException("usage: CLOAK item [...]");
						}
						
						ArrayList strItemArray = new ArrayList();
						foreach (string strReposItem in curArg.items)
						{
							ValidateReposPath(strReposItem);
							strItemArray.Add(strReposItem);
						}

						bSuccess = ProcessCommandCloak(strItemArray);
						break;
					}

					case Command.COMMIT:
					case Command.CHECKIN:
					{
						ArrayList strItemArray = new ArrayList();
						foreach (string strReposItem in curArg.items)
						{
							strItemArray.Add(strReposItem);
						}

						bSuccess = ProcessCommandCommit(strItemArray);
						break;
					}

					case Command.CREATEFOLDER:
					{
						if (curArg.items.Count != 1)
						{
							throw new UsageException("usage: CREATEFOLDER foldername");
						}

						string strReposFolder = (string) curArg.items[0];
						ValidateReposPath(strReposFolder);

						bSuccess = ProcessCommandCreateFolder(strReposFolder);
						break;
					}

					case Command.DELETE:
					{
						if (curArg.items.Count < 1)
						{
							throw new UsageException("usage: DELETE item [...]");
						}
						
						ArrayList strItemArray = new ArrayList();
						foreach (string strReposItem in curArg.items)
						{
							ValidateReposPath(strReposItem);
							strItemArray.Add(strReposItem);
						}

						bSuccess = ProcessCommandDelete(strItemArray);
						break;
					}

					case Command.DELETELABEL:
					{
						if (curArg.items.Count != 2)
						{
							throw new UsageException("usage: DELETELABEL item label_name");
						}
						
						string strReposPath = RepositoryPath.NormalizeFolder((string) curArg.items[0]);
						ValidateReposPath(strReposPath);

						string strLabelName = (string) curArg.items[1];

						bSuccess = ProcessCommandDeleteLabel(strReposPath, strLabelName);
						break;
					}

					case Command.DIFF:
					{
						string strRepItem = null, strRepLbl = null, 
							strDiffAgainstItem = null, strDiffAgainstLbl = null, 
							strErrMsg = null;

						// the "compare to" operator
						DiffAgainstType datDiffChoice = DiffAgainstType.CurrentRepositoryVersion;
						CompareToOption cto = VaultCmdLineClient.LookupCompareToOptionByString(curArg.DiffCompareTo);
						switch (cto)
						{
							case CompareToOption.current:

								if ( curArg.items.Count == 1 )
								{
									datDiffChoice = DiffAgainstType.CurrentRepositoryVersion;
									strRepItem = (string)curArg.items[0];
									// TODO - move into resource for globalization
									strRepLbl = "Working: {0}";
									strDiffAgainstLbl = "Repository: {0}";
								}
								else
								{
									strErrMsg = "usage: DIFF repository_path [...]";
								}
								break;

							case CompareToOption.label:

								if ( curArg.items.Count == 2 )
								{
									datDiffChoice = DiffAgainstType.Label;
									strRepItem = (string)curArg.items[0];
									strDiffAgainstItem = (string)curArg.items[1];

									// TODO - move into resource for globalization
									strRepLbl = "Label: {0}";
									strDiffAgainstLbl = "Working: {0}";
								}
								else
								{
									strErrMsg = "usage: DIFF repository_path label_name [...]";
								}
								break;

							case CompareToOption.lastget:

								if ( curArg.items.Count == 1 )
								{
									datDiffChoice = DiffAgainstType.PreviousRepositoryVersion;
									strRepItem = (string)curArg.items[0];

									// TODO - move into resource for globalization
									strRepLbl = "Baseline: {0}";
									strDiffAgainstLbl = "Working Version: {0}";
								}
								else
								{
									strErrMsg = "usage: DIFF repository_path [...]";
								}
								break;

							case CompareToOption.local:

								if ( curArg.items.Count == 2 )
								{
									datDiffChoice = DiffAgainstType.AnyLocalItem;
									strRepItem = (string)curArg.items[0];
									strDiffAgainstItem = (string)curArg.items[1];

									// TODO - move into resource for globalization
									strRepLbl = "{0}";
									strDiffAgainstLbl = "Working: {0}";
								}
								else
								{
									strErrMsg = "usage: DIFF repository_path local_path [...]";
								}
								break;

							case CompareToOption.repository:

								if ( curArg.items.Count == 2 )
								{
									datDiffChoice = DiffAgainstType.AnyRepositoryItem;
									strRepItem = (string)curArg.items[0];
									strDiffAgainstItem = (string)curArg.items[1];

									// TODO - move into resource for globalization
									strRepLbl = "Repository: {0}";
									strDiffAgainstLbl = "Working: {0}";
								}
								else
								{
									strErrMsg = "usage: DIFF repository_path other_repository_path [...]";
								}
								break;

							default:

								if ( curArg.items.Count == 1 )
								{
									datDiffChoice = DiffAgainstType.CurrentRepositoryVersion;
									strRepItem = (string)curArg.items[0];

									// TODO - move into resource for globalization
									strRepLbl = "Working: {0}";
									strDiffAgainstLbl = "Repository: {0}";
								}
								else
								{
									strErrMsg = "usage: DIFF repository_path [...]";
								}
								break;
						}

						if ( strErrMsg != null )
						{
							throw new UsageException(strErrMsg);
						}

						bSuccess = ProcessCommandDiff(curArg.DiffBin, curArg.DiffArgs, datDiffChoice, 
							curArg.Recursive, strRepItem, strRepLbl, strDiffAgainstItem, strDiffAgainstLbl);
						break;
					}

					case Command.LISTALLTXDETAILS:
					{
						if (curArg.items.Count != 0)
						{
							throw new UsageException("usage: LISTALLTXDETAILS");
						}

						bSuccess = ProcessCommandListAllTxDetails();
						break;
					}

					case Command.FORGETLOGIN:
					{
						if (curArg.items.Count != 0)
						{
							throw new UsageException("usage: FORGETLOGIN");
						}

						PurgeSession();
						break;
					}

					case Command.GET:
					{
						if (curArg.items.Count < 1)
						{
							throw new UsageException("usage: GET item [...]");
						}
												
						ArrayList strItemArray = new ArrayList();
						foreach (string strReposItem in curArg.items)
						{
							ValidateReposPath(strReposItem);
							strItemArray.Add(strReposItem);
						}

						bSuccess = ProcessCommandGet(strItemArray);
						break;
					}

					case Command.GETLABEL:
					{
						if(curArg.items.Count < 2 || curArg.items.Count > 3)
						{
							throw new UsageException("usage: GETLABEL repository_item label [labelpath]");
						}

						string strReposItem = (string) curArg.items[0];
						ValidateReposPath(strReposItem);
						
						string strLabel = (string) curArg.items[1];

						string strLabelPath = (curArg.items.Count == 3) ? (string)curArg.items[2] : null;

						if (_args.DestPath == null && _args.LabelWorkingFolder == null)
						{
							throw new UsageException("usage: GETLABEL requires either -destpath or -labelworkingfolder to be set");
						}

						bSuccess = ProcessCommandGetLabel(strReposItem, strLabel, strLabelPath);
						break;
					}

					case Command.GETLABELDIFFS:
					{
						if(curArg.items.Count < 2 || curArg.items.Count > 3)
						{
							throw new UsageException("usage: GETLABELDIFFS repository_item label1 [label2]");
						}

						string strReposPath = (string) curArg.items[0];
						ValidateReposPath(strReposPath);

						string strLabel1 = (string) curArg.items[1];
						string strLabel2 = "";

						if ( curArg.items.Count > 2 )
							strLabel2 = (string)curArg.items[2];

						bSuccess = ProcessCommandGetLabelDiffs(strReposPath, strLabel1, strLabel2);
						break;
					}


					case Command.GETVERSION:
					{
						if (curArg.items.Count != 3)
						{
							throw new UsageException("usage: GETVERSION version item destination_folder");
						}

						int version = Int32.Parse((string) curArg.items[0]);
						string strReposItem = (string) curArg.items[1];
						ValidateReposPath(strReposItem);
						string strDestFolder = (string) curArg.items[2];
						if (!Directory.Exists(strDestFolder))
						{
							Directory.CreateDirectory(strDestFolder);
						}
						if (!Directory.Exists(strDestFolder))
						{
							throw new Exception(string.Format("{0} does not exist and could not be created", strDestFolder));
						}

						bSuccess = ProcessCommandGetVersion(version, strReposItem, strDestFolder);
						break;
					}

					case Command.GETWILDCARD:
					{
						if(curArg.items.Count < 2)
						{
							throw new UsageException("usage: GETWILDCARD repospath wildcard [...]");
						}

						string strReposItem = (string) curArg.items[0];
						ValidateReposPath(strReposItem);

						ArrayList strWildcardArray = new ArrayList();
						for(int i = 1; i < curArg.items.Count; i++)
						{
							strWildcardArray.Add((string) curArg.items[i]);
						}

						bSuccess = ProcessCommandGetWildcard(strReposItem, strWildcardArray);
						break;
					}

					case Command.HELPHTML:
					{
						Help help = new Help(_xml);

						help.WriteHTML();
						break;

					}

					case Command.HELP:
					{
						Help help = new Help(_xml);

						if(curArg.items.Count < 1)
							help.Write();
						else
							help.Write((string) curArg.items[0]);

						break;
					}

					case Command.HISTORY:
					{
						if (curArg.items.Count != 1)
						{
							throw new UsageException("usage: HISTORY repository_path");
						}

						string strReposPath = (string) curArg.items[0];
						ValidateReposPath(strReposPath);

						bSuccess = ProcessCommandHistory(strReposPath);
						break;
					}

					case Command.VERSIONHISTORY:
					{
						if (curArg.items.Count != 1)
						{
							throw new UsageException("usage: VERSIONHISTORY repository_folder");
						}

						string strReposPath = (string) curArg.items[0];
						ValidateReposPath(strReposPath);

						bSuccess = ProcessCommandVersionHistory(strReposPath);
						break;
					}

					case Command.LABEL:
					{
						const string usageErrMsg = "usage: LABEL repositorypath labelname [version]";
						if(curArg.items.Count != 2 && curArg.items.Count != 3)
						{
							throw new UsageException(usageErrMsg);
						}

						string strReposPath = (string) curArg.items[0];
						ValidateReposPath(strReposPath);

						string labelName = (string) curArg.items[1];

						long version = VaultDefine.Latest;
						if ( curArg.items.Count == 3 )
						{
							try
							{
								version = long.Parse((string)curArg.items[2]);
							}
							catch ( FormatException )
							{
								throw new UsageException(usageErrMsg);
							}
						}

						bSuccess = ProcessCommandLabel(strReposPath, labelName, version);
						break;
					}

					case Command.LISTALLBRANCHPOINTS:
					{
						if (curArg.items.Count != 0)
						{
							throw new UsageException("usage: LISTALLBRANCHPOINTS");
						}

						bSuccess = ProcessCommandListAllBranchPoints();
						break;
					}

					case Command.LISTCHANGESET:
					{
						if (curArg.items.Count != 0)
						{
							throw new UsageException("usage: LISTCHANGESET");
						}

						bSuccess = ProcessCommandListChangeSet();
						break;
					}

					case Command.LISTCHECKOUTS:
					{
						if (curArg.items.Count != 0)
						{
							throw new UsageException("usage: LISTCHECKOUTS");
						}

						bSuccess = ProcessCommandListCheckOuts();
						break;
					}

					case Command.LISTFOLDER:
					{
						if (curArg.items.Count != 1)
						{
							throw new UsageException("usage: LISTFOLDER repository_folder");
						}

						string strReposFolder = (string) curArg.items[0];
						ValidateReposPath(strReposFolder);

						bSuccess = ProcessCommandListFolder(strReposFolder);
						break;
					}

					case Command.LISTREPOSITORIES:
					{
						if (curArg.items.Count != 0)
						{
							throw new UsageException("usage: LISTREPOSITORIES");
						}

						bSuccess = ProcessCommandListRepositories();
						break;
					}

					case Command.LISTUSERS:
					{
						if (curArg.items.Count != 0)
						{
							throw new UsageException("usage: LISTUSERS");
						}

						Login();
						DoListUsers();

						break;
					}

					case Command.LISTWORKINGFOLDERS:
					{
						if (curArg.items.Count != 0)
						{
							throw new UsageException("usage: LISTWORKINGFOLDERS");
						}

						Login();	
						WriteListWorkingFolders();

						break;
					}

					case Command.MOVE:
					{
						if (curArg.items.Count != 2)
						{
							throw new UsageException("usage: MOVE path_from path_to");
						}

						string strReposPath_From = (string) curArg.items[0];
						ValidateReposPath(strReposPath_From);

						string strReposPath_To = (string) curArg.items[1];
						ValidateReposPath(strReposPath_To);

						bSuccess = ProcessCommandMove(strReposPath_From, strReposPath_To);
						break;
					}
					case Command.OBLITERATE:
					{
						if (curArg.items.Count != 1)
						{
							throw new UsageException("usage: OBLITERATE path_to_deleted_item");
						}

						string strReposPath = (string) curArg.items[0];
						ValidateReposPath(strReposPath);

						if (_args.YesIAmSure == false)
						{
							_xml.WriteUserMessage("You have not provided the -yesiamsure option to the OBLITERATE command.  \nOBLITERATE is a destructive and non-reversible command, which should not be \nused lightly.  If you are still sure that you would like to permanently \ndestroy " + strReposPath + " and all of its children, \nthen add the -yesiamsure flag to your command.");
							break;
						}

						bSuccess = ProcessCommandObliterate(strReposPath);
						break;
					}
					case Command.REMEMBERLOGIN:
					{
						if (curArg.items.Count != 0)
						{
							throw new UsageException("This command accepts no arguments.");
						}

						Login(false, true);
						break;
					}

					case Command.PIN:
					{
						if (curArg.items.Count < 1 || curArg.items.Count > 2)
						{
							throw new UsageException("usage: PIN repository_path [version]");
						}

						string strReposPath = RepositoryPath.NormalizeFolder((string) curArg.items[0]);
						ValidateReposPath(strReposPath);

						int version=0;
						if (curArg.items.Count == 2)
						{
							string strVersion = (string) curArg.items[1];
							version = int.Parse(strVersion);
						} 
						else 
						{
							version = VaultDefine.Latest;
						}

						bSuccess = ProcessCommandPin(strReposPath, version);
						break;
					}

					case Command.RENAME:
					{
						if (curArg.items.Count != 2)
						{
							throw new UsageException("usage: RENAME from_name to_name");
						}

						string strReposPath = RepositoryPath.NormalizeFolder((string) curArg.items[0]);
						ValidateReposPath(strReposPath);

						string strNewName = (string) curArg.items[1];

						bSuccess = ProcessCommandRename(strReposPath, strNewName);
						break;
					}

					case Command.RENAMELABEL:
					{
						if (curArg.items.Count != 3)
						{
							throw new UsageException("usage: RENAMELABEL item from_label_name to_label_name");
						}

						string strReposPath = RepositoryPath.NormalizeFolder((string) curArg.items[0]);
						ValidateReposPath(strReposPath);

						string strOldLabelName = (string) curArg.items[1];
						string strNewLabelName = (string) curArg.items[2];

						bSuccess = ProcessCommandRenameLabel(strReposPath, strOldLabelName, strNewLabelName);
						break;
					}

					case Command.SETWORKINGFOLDER:
					{
						if (curArg.items.Count != 2)
						{
							throw new UsageException("usage: SETWORKINGFOLDER repository_folder local_folder");
						}

						string strReposFolder = RepositoryPath.NormalizeFolder((string) curArg.items[0]);
						ValidateReposPath(strReposFolder);

						string strDiskFolder = (string) curArg.items[1];
						if (!Directory.Exists(strDiskFolder))
						{
							Directory.CreateDirectory(strDiskFolder);
						}
						if (!Directory.Exists(strDiskFolder))
						{
							throw new Exception(string.Format("{0} does not exist and could not be created", strDiskFolder));
						}

						Login();	
						VaultClientTreeObject obj = _ci.TreeCache.Repository.Root.FindTreeObjectRecursive(strReposFolder);
						if (obj == null)
						{
							throw new Exception(string.Format("{0} does not exist in the repository", strReposFolder));
						}

						_ci.TreeCache.SetWorkingFolder(obj.FullPath, strDiskFolder);
						
						WriteListWorkingFolders();

						break;
					}

					case Command.SHARE:
					{
						if (curArg.items.Count != 2)
						{
							throw new UsageException("usage: SHARE repository_path_from repository_path_to");
						}

						string strReposPath_From = (string) curArg.items[0];
						ValidateReposPath(strReposPath_From);

						string strReposPath_To = (string) curArg.items[1];
						ValidateReposPath(strReposPath_To);

						bSuccess = ProcessCommandShare(strReposPath_From, strReposPath_To);
						break;
					}

					case Command.UNCLOAK:
					{
						if (curArg.items.Count < 1)
						{
							throw new UsageException("usage: CLOAK item [...]");
						}

						ArrayList strItemArray = new ArrayList();
						foreach (string strReposItem in curArg.items)
						{
							ValidateReposPath(strReposItem);
							strItemArray.Add(strReposItem);
						}

						bSuccess = ProcessCommandUncloak(strItemArray);
						break;
					}

					case Command.UNDOCHANGESETITEM:
					{
						if ( curArg.items.Count != 1 )
						{
							throw new UsageException("usage: UNDOCHANGESETITEM changesetitem_id");
						}

						int nChgSetItemID = -1;
						try
						{
							nChgSetItemID = Convert.ToInt32(curArg.items[0]);
						}
						catch
						{
							nChgSetItemID = -1;
						}

						bSuccess = ProcessCommandUndoChangeSetItem(nChgSetItemID);
						break;
					}

					case Command.UNDOCHECKOUT:
					{
						if (curArg.items.Count != 1)
						{
							throw new UsageException("usage: UNDOCHECKOUT repository_path");
						}

						string strReposPath = RepositoryPath.NormalizeFolder((string) curArg.items[0]);
						ValidateReposPath(strReposPath);

						bSuccess = ProcessCommandUndoCheckout(strReposPath);
						break;
					}

					case Command.UNPIN:
					{
						if (curArg.items.Count != 1)
						{
							throw new UsageException("usage: UNPIN repository_path");
						}

						string strReposPath = RepositoryPath.NormalizeFolder((string) curArg.items[0]);
						ValidateReposPath(strReposPath);

						bSuccess = ProcessCommandUnPin(strReposPath);
						break;
					}

					case Command.UNSETWORKINGFOLDER:
					{
						if (curArg.items.Count != 1)
						{
							throw new UsageException("The UNSETWORKINGFOLDER command requires 1 argument.");
						}

						string strReposFolder = (string) curArg.items[0];
						ValidateReposPath(strReposFolder);

						Login();	

						_ci.TreeCache.RemoveWorkingFolder(strReposFolder);

						WriteListWorkingFolders();

						break;
					}

					default:
					{
						throw new UsageException("no command specified - run 'vault.exe HELP' for help");
						//break;
					}
				}
			}
			catch (Exception)
			{
				throw;
			}
			finally
			{
				// force a logout if an error occurred.
				Logout(bSuccess == false);
			}

			return bSuccess;
		}

		public bool PreProcessCommand(Args arg)
		{
			bool bSuccess = true;
			if (arg.Cmd == Command.BATCH)
			{
				if (arg.items.Count == 1)
				{
					ProcessCommandBatch(arg);
				}
				else
				{
					arg.Error = true;
					arg.ErrorMessage = string.Format("usage: {0} file name | -", arg.Cmd.ToString());
				}

				bSuccess = (arg.Error == false);
			}
			return bSuccess;
		}

		bool ProcessCommandAdd(string strReposFolder, ArrayList strItemArray)
		{
			bool bSuccess = true;

			string strFolderNormalized = RepositoryPath.NormalizeFolder(strReposFolder);
			ValidateReposPath(strFolderNormalized);

			Login();

			_ci.Refresh();

			VaultClientFolder folder = _ci.TreeCache.Repository.Root.FindFolderRecursive(strFolderNormalized);
			if (folder == null)
			{
				throw new Exception(string.Format("{0} does not exist", strFolderNormalized));
			}

			ChangeSetItemColl csic = new ChangeSetItemColl();

			for (int i=0; i < strItemArray.Count; i++)
			{
				string strItem = (string) strItemArray[i];
				string strReposPath = strFolderNormalized + "/" + Path.GetFileName(strItem);

				if (File.Exists(strItem))
				{
					ChangeSetItem_AddFile csaf = new ChangeSetItem_AddFile(
						DateTime.Now, 
						_args.Comment, 
						String.Empty, 
						strItem,
						strReposPath);
					csic.Add(csaf);
				}
				else if (Directory.Exists(strItem))
				{
					ChangeSetItem_AddFolder csaf = new ChangeSetItem_AddFolder(
						DateTime.Now, 
						_args.Comment, 
						String.Empty, 
						strItem,
						strReposPath);
					csic.Add(csaf);
				}
				else
				{
					throw new Exception(string.Format("{0} does not exist", strItem));
				}
			}

			_ci.InternalChangeSet_Append(csic);

			if (_args.AutoCommit)
			{
				_ci.InternalChangeSet_SetComment(_args.Comment);
				WriteChangeSet(csic);
				bSuccess = _ci.Commit(csic);
			}
			else
			{
				WriteChangeSet();
			}
			return bSuccess;
		}

		bool ProcessCommandAddRepository(string strNewReposName)
		{
			Login();

			_ci.Connection.AddRepository(strNewReposName, true);

			_xml.WriteUserMessage(string.Format("Added repository: {0}", strNewReposName));

			return true;
		}
		
		bool ProcessCommandAddUser(string strLogin, string strPassword, string strEmail)
		{
			bool bRet = true;
			ArrayList groupList = new ArrayList();

			Login();

			VaultUser newUser = new VaultUser();
			newUser.Login = strLogin;
			newUser.Password = VaultLib.VaultUserCrypt.HashPassword(strLogin, strPassword);
			newUser.Name = strLogin;
			newUser.Email = strEmail;
			newUser.isActive = true;
			newUser.DefaultRights = 7;		
			newUser.BelongToGroups = (VaultGroup[]) groupList.ToArray(typeof(VaultGroup));				

			try
			{
				_ci.Connection.AddUser(ref newUser);
			}
			catch (Exception e)
			{
				string strMsg = null;
				if ( newUser.UserID == 0 )
				{
					strMsg = string.Format("{0} was not created - {1}", strLogin, e.Message);
					bRet = false;
				}
				else
				{
					strMsg = string.Format("{0} was created.  But, there was a small problem - {1}", strLogin, e.Message);
				}
				_xml.WriteUserMessage(strMsg);
			}

			if ( bRet == true )
			{
				DoListUsers();
			}

			return bRet;
		}

		void ProcessCommandBatch(Args arg)
		{
			arg.SetBatchModeOperation((string)arg.items[0]);
		}

		bool ProcessCommandBlame(string strReposPath, int linenumber, long endversion)
		{
			bool bSuccess = true;

			Login();
			if (endversion == -1)
				endversion = _ci.TreeCache.Repository.Root.FindFileRecursive(strReposPath).Version;

			bool bFound = false;
			long howfarback = -20;
			while (bFound == false && (endversion + howfarback ) > -20)
			{
				VaultBlameNode[] blames = null;
				_xml.WriteUserMessage("Performing blame on the previous " + Math.Abs(howfarback) + " versions from " + endversion);
				_ci.Connection.Blame(_ci.ActiveRepositoryID, strReposPath, howfarback, endversion, ref blames);

				foreach (VaultBlameNode bn in blames)
				{
					if ((bn.FirstLine+1) <= linenumber && ((bn.FirstLine+1) + bn.CountLines - 1) >= linenumber)
					{
						if (bn.UserName != string.Empty)
						{
					
							_xml.WriteUserMessage(string.Format("User {0} last changed line {1} of {2} with the comment:\r\n {3}", bn.UserName, linenumber, strReposPath, bn.Comment));
							_xml.Begin("blame");
							_xml.AddPair("user", bn.UserName);
							_xml.AddPair("version", bn.OriginatingVersion);
							_xml.AddPair("comment", bn.Comment);
							_xml.End();
							bFound = true;
						}
					}
				}
				if (bFound == false)
					howfarback -= 20;
			}
			return bSuccess;
		}

		bool ProcessCommandBranch(string strReposPath_From, string strReposPath_To)
		{
			bool bSuccess = true;

			Login();

			ChangeSetItemColl csic = new ChangeSetItemColl();

			VaultClientFolder vcfolder = null;
			VaultClientFile vcfile = null;

			vcfolder = _ci.TreeCache.Repository.Root.FindFolderRecursive(strReposPath_From);
			if (vcfolder != null)
			{
				// ok, this is a folder
				ChangeSetItem_CopyBranch csi = new ChangeSetItem_CopyBranch(
					DateTime.Now,
					_args.Comment,
					String.Empty,
					vcfolder.FullPath,
					strReposPath_To,
					vcfolder.ObjVerID);
				csic.Add(csi);
			}
			else
			{
				vcfile = _ci.TreeCache.Repository.Root.FindFileRecursive(strReposPath_From);
				if (vcfile != null)
				{
					throw new UsageException(string.Format("{0} exists, but this command may not be used to branch individual files.", strReposPath_From));
				}
				else
				{
					throw new Exception(string.Format("{0} does not exist", strReposPath_From));
				}
			}

			_ci.InternalChangeSet_Append(csic);

			if (_args.AutoCommit)
			{
				_ci.InternalChangeSet_SetComment(_args.Comment);
				WriteChangeSet(csic);
				bSuccess = _ci.Commit(csic);
			}
			else
			{
				WriteChangeSet();
			}

			return bSuccess;
		}

		bool ProcessCommandCheckout(ArrayList strItemArray)
		{
			bool bRet = true;

			string strReposItem = null;

			VaultClientFolder vcfolder = null;
			VaultClientFile vcfile = null;

			VaultResponseItem[] vriResponses = null;
			VaultResponseItem vri = null;

			int j, nLen;

			Login();
			_ci.Refresh();

			if(_args.Merge == MergeType.Unspecified)
			{
				// use a default merge type if one was not specified
				_args.Merge = MergeType.AttemptAutomaticMerge;
			}

			// if the backup option is specified, set it for this checkout/get only
			bool saveMakeBackupOption = _ci.WorkingFolderOptions.MakeBackups;
			if (_args.MakeBackup == BackupOption.yes)
			{
				_ci.WorkingFolderOptions.MakeBackups = true;
			} 
			else if (_args.MakeBackup == BackupOption.no)
			{
				_ci.WorkingFolderOptions.MakeBackups = false;
			}


			byte checkOutType = (_args.CheckOutExclusive ? VaultCheckOutType.Exclusive : VaultCheckOutType.CheckOut);

			try
			{
				VaultClientFileColl theFiles = new VaultClientFileColl();

				// Collect all the specified files/folders into a single list of files
				bool bFolder = false;
				for (int i=0; i < strItemArray.Count; i++)
				{
					strReposItem = (string) strItemArray[i];
					
					vcfolder = _ci.TreeCache.Repository.Root.FindFolderRecursive(strReposItem);
					if (vcfolder != null)
					{
						bFolder = true;
						if (CheckForWorkingFolder(vcfolder, true) == false)
							return false;
						// if it is a folder, check whether there is a wildcard to be matched
						if (_args.Wildcard != null)
						{
							// If there is a wildcard, checkout only files and folders that match the wildcard
							VaultClientFolderColl foundFolders = null;
							VaultClientFileColl foundFiles = null;
							ArrayList RegexArray = new ArrayList();

							Wildcard wildcard = new Wildcard(_args.Wildcard);
							Regex regex = new Regex(wildcard.ToRegex(true));
							RegexArray.Add(regex);

							// Note that we are not regexing on the folders, just the files, and this function will
							// add the files that match we regex to the collection.
							_ci.GetFileAndFolderListsByRegex(vcfolder, RegexArray, _args.Recursive, out foundFiles, out foundFolders);

							if (foundFiles != null)
							{
								theFiles.AddRange(foundFiles);
							}

						} 
						else 
						{
							// add all files in the folder to the list
							theFiles.AddRange(vcfolder.Files);
							if (_args.Recursive)
							{
								foreach (VaultClientFolder subfolder1 in vcfolder.Folders)
									subfolder1.GetFilesRecursive(ref theFiles, _ci.TreeCache.Cloaks);
							}
						}
					}
					else
					{
						vcfile = _ci.TreeCache.Repository.Root.FindFileRecursive(strReposItem);
						if (vcfile != null)
						{
							if (CheckForWorkingFolder(vcfile, true) == false)
								return false;
							theFiles.Add(vcfile);
						}
						else
						{
							throw new Exception(string.Format("{0} does not exist.", strReposItem));
						}
					}
				}

				VaultClientFile[] files = (VaultClientFile[]) theFiles.ToArray(typeof(VaultClientFile));

				vriResponses = _ci.CheckOut(files, checkOutType, string.Empty);
				if ( vriResponses == null )
				{
					throw new Exception(string.Format("The checkout on {0} did not return a response.", strReposItem));
				}

				// process the responses
				for (j = 0, nLen = vriResponses.Length; j < nLen; j++)
				{

					vri = vriResponses[j];
					if ( (vri.Status == VaultStatusCode.Success) || 
						(vri.Status == VaultStatusCode.SuccessRequireFileDownload) ) 
					{
						_xml.WriteUserMessage(string.Format("Checked out: {0}", files[j].FullPath));
					}
					else
					{
						bRet = false;
						_xml.WriteUserMessage(string.Format("Failed Checkout for {0}: {1}", files[j].FullPath, VaultConnection.GetSoapExceptionMessage(vri.Status)) );
					}
				}

				// Get the items that were checked out.
				_ci.Get(files, false, MakeWritableType.MakeAllFilesWritable, _args.SetFileTime, _args.Merge, null);
				if (bFolder == true)
					_ci.PerformPendingLocalDeletions(strReposItem, _args.PerformDeletions);

			}

			catch (Exception)
			{
				throw;
			}

			// reset prev MakeBackup value
			if (_args.MakeBackup != BackupOption.usedefault)
			{
				_ci.WorkingFolderOptions.MakeBackups = saveMakeBackupOption;
			} 

			return bRet;
		}

		bool ProcessCommandCloak(ArrayList strItemArray)
		{
			Login();
			_ci.Refresh();

			for (int i=0; i < strItemArray.Count; i++)
			{
				string strReposItem = (string) strItemArray[i];

				VaultClientFolder vcfolder = null;

				vcfolder = _ci.TreeCache.Repository.Root.FindFolderRecursive(strReposItem);
				if (vcfolder == null)
				{
					throw new UsageException(string.Format("{0} exists, but the CLOAK command can only be used on folders.", strReposItem));
				}
				else
				{
					_ci.CloakOrUnCloak(vcfolder.FullPath, true);
					_xml.WriteUserMessage(string.Format("Cloaked: {0}", vcfolder.FullPath));
				}
			}

			return true;
		}

		bool ProcessCommandCommit(ArrayList strItemArray)
		{
			ChangeSetItem csiItem = null;
			bool bRet = false;

			// login to the server.
			Login();

			if ( _ci.WorkingFolderOptions.RequireCheckOutBeforeCheckIn == false )
			{
				// do a scan to update the change set list
				_ci.UpdateKnownChanges_All(false);
			}

			// get the internal change set
			ChangeSetItemColl csic = _ci.InternalChangeSet_GetItems(true);
			if ( (csic != null) && (csic.Count > 0) )
			{
				// a sub set of the change set is requested... build that collection
				if ( strItemArray.Count > 0 )
				{
					int nPos = 0;

					// set the old change set list.
					ChangeSetItemColl csicOld = csic;

					// the new list of change set items
					csic = new ChangeSetItemColl();
					
					// find the subset of change sets to use
					for (int i = 0; i < strItemArray.Count; i++)
					{
						string strReposItem = (string) strItemArray[i];

						// see if the item is numeric based
						try
						{
							nPos = Convert.ToInt32(strReposItem);
						}
						catch
						{
							nPos = -1;
						}

						if ( nPos == -1 )
						{
							// a string based subset item
							ValidateReposPath(strReposItem);
							
							// find this item in the old change set
							bool bFoundItem = false;
							for (int j = 0; j < csicOld.Count; j++)
							{
								csiItem = csicOld[j];
								if (csiItem.DisplayRepositoryPath.ToLower().Equals(strReposItem.ToLower()) ||
									csiItem.DisplayRepositoryPath.ToLower().StartsWith(strReposItem.ToLower() + "/") )
								{
									// if not already there, add the item
									if ( csic.Contains(csiItem) == false )
									{
										csic.Add(csiItem);
									}

									bFoundItem = true;
									//break;
								}
							}

							if ( bFoundItem == false )
							{
								// throw here
								throw new Exception(string.Format("{0} does not exist in the current change set.", strReposItem));
							}
						}
						else 
						{
							if ( ValidateChangeSetItemID(nPos, csicOld) == true )
							{
								csiItem = csicOld[nPos];
								// if not already there, add the item
								if ( csic.Contains(csiItem) == false )
								{
									csic.Add(csiItem);
								}
							}
							else
							{
								throw new UsageException(string.Format("Invalid ChangeSetItem ID: {0}.  Please use the LISTCHANGESET command to retrieve a valid ID.", nPos));
							}
						}
					}
				}


				// of the finalized change set, locate files which need to be "un-checked out"
				VaultClientFileColl vcfcUndoCheckouts = new VaultClientFileColl();
				VaultClientFile vcfile = null;
				ChangeSetItemColl csiRemove = new ChangeSetItemColl();

				for (int i = 0; i < csic.Count; i++)
				{
					csiItem = csic[i];

					vcfile = _ci.TreeCache.Repository.Root.FindFileRecursive(csiItem.DisplayRepositoryPath);

					if ( csiItem.Type == ChangeSetItemType.Unmodified )
					{
						if ( 
							(vcfile != null) && 
							( ((ChangeSetItem_Unmodified)csiItem).FileID == vcfile.ID )
							)
						{
							// this file is unmodified, what do we do about it?
							// default is to leave checked out

							// maybe we checkin unchanged files
							if (_args.Unchanged == UnchangedHandler.LeaveCheckedOut)
							{
								// this change set item needs to be 
								// removed from the items to commit
								// since it should be left checked out.
								csiRemove.Add(csiItem);
							}
							else if (_args.Unchanged == UnchangedHandler.UndoCheckout) 
							{
								// regardless of .KeepChecked out, this 
								// change set item needs to be removed 
								// from the items to commit
								csiRemove.Add(csiItem);

								// when keep checked out has not been specified...
								if ( _args.KeepCheckedOut == false )
								{
									// ...we undo checkouts
									if(_ci.GetWorkingFolder(vcfile.Parent) == null)
									{
										throw new Exception(string.Format("{0} does not have a working folder set", vcfile.Name));
									}
									vcfcUndoCheckouts.Add(vcfile);
								}
							}
						}
						else
						{
							throw new Exception(String.Format("{0} does not exist", csiItem.DisplayRepositoryPath));
						}
					} 
					else if (csiItem.Type == ChangeSetItemType.CheckedOutMissing)
					{
						// Can't check in a file if it is missing
						csiRemove.Add(csiItem);
					} 
					else if (csiItem.Type == ChangeSetItemType.Modified)
					{
						// Don't check in file if it is Needs Merge, or is Renegade
						if (((ChangeSetItem_Modified) csiItem).NeedsMerge ||
							(!_ci.IsCheckedOutByMeOnThisMachine(vcfile) && _ci.WorkingFolderOptions.RequireCheckOutBeforeCheckIn))
						{
							csiRemove.Add(csiItem);
						}
					}
				}

				// set the comment
				_ci.InternalChangeSet_SetComment(_args.Comment);

				// remove any change set items which will not be committed
				// with the changeset.
				for (int i = 0; i < csiRemove.Count; i++)
				{
					csic.Remove( csiRemove[i] );
				}

				// write out the change set after the items have been removed.
				WriteChangeSet(csic);

				// commit the transaction
				bRet = _ci.Commit(csic, _args.KeepCheckedOut, false);
				if ( (bRet == true) && (vcfcUndoCheckouts.Count > 0) )
				{
					// the commit was successful, now undo checkouts.
					_ci.UndoCheckOut( (VaultClientFile[]) vcfcUndoCheckouts.ToArray( typeof(VaultClientFile) ), _args.LocalCopy);
				}
			}
			else
			{
				// nothing to do but... 
				bRet = true;

				// write out the change set
				WriteChangeSet(csic);

			}

			return bRet;
		}

		bool ProcessCommandCreateFolder(string strReposFolder)
		{
			bool bSuccess = true;

			Login();

			ChangeSetItem_CreateFolder csaf = new ChangeSetItem_CreateFolder(
				DateTime.Now, 
				_args.Comment, 
				String.Empty, 
				strReposFolder);
			ChangeSetItemColl csic = new ChangeSetItemColl();
			csic.Add(csaf);

			_ci.InternalChangeSet_Append(csic);

			if (_args.AutoCommit)
			{
				_ci.InternalChangeSet_SetComment(_args.Comment);

				WriteChangeSet(csic);
				bSuccess = _ci.Commit(csic);
			}
			else
			{
				WriteChangeSet();
			}

			return bSuccess;
		}

		bool ProcessCommandDelete(ArrayList strItemArray)
		{
			bool bSuccess = true;
			
			Login();
			_ci.Refresh();

			ChangeSetItemColl csic = new ChangeSetItemColl();

			for (int i=0; i < strItemArray.Count; i++)
			{
				string strReposItem = (string) strItemArray[i];

				VaultClientFolder vcfolder = null;
				VaultClientFile vcfile = null;

				vcfolder = _ci.TreeCache.Repository.Root.FindFolderRecursive(strReposItem);
				if (vcfolder != null)
				{
					// OK, this is a folder
					ChangeSetItem_DeleteFolder csdf = new ChangeSetItem_DeleteFolder(
						DateTime.Now,
						_args.Comment,
						String.Empty,
						vcfolder.ID,
						vcfolder.FullPath);
					csic.Add(csdf);
				}
				else
				{
					vcfile = _ci.TreeCache.Repository.Root.FindFileRecursive(strReposItem);
					if (vcfile != null)
					{
						// OK, this is a file
						ChangeSetItem_DeleteFile csdf = new ChangeSetItem_DeleteFile(
							DateTime.Now,
							_args.Comment,
							String.Empty,
							vcfile.ID,
							vcfile.FullPath);
						csic.Add(csdf);
					}
					else
					{
						throw new Exception(string.Format("{0} does not exist", strReposItem));
					}
				}
			}

			_ci.InternalChangeSet_Append(csic);

			if (_args.AutoCommit)
			{
				_ci.InternalChangeSet_SetComment(_args.Comment);

				WriteChangeSet(csic);
				bSuccess = _ci.Commit(csic);
			}
			else
			{
				WriteChangeSet();
			}

			return bSuccess;
		}

		bool ProcessCommandDeleteLabel(string strReposPath, string strLabelName)
		{
			bool bSuccess = true;
			long labelID = 0;
			long rootID = 0;
			string[] discoveredPaths;
			VaultClientTreeObject labelStructure=null;

			
			Login();
			_ci.Refresh();

			VaultClientTreeObject reposTreeObj = _ci.TreeCache.Repository.Root.FindTreeObjectRecursive(strReposPath);

			if(reposTreeObj == null)
			{
				throw new Exception(string.Format("Item \"{0}\" was not found in the repository", strReposPath));
			}

			try 
			{
				// There isn't a good API to get a label ID based on a label name, so just get the whole structure
				_ci.GetByLabel_GetStructure(strReposPath, strLabelName, ref labelID, "", out discoveredPaths, out labelStructure, out rootID);

				if (reposTreeObj.ID == rootID && labelID != 0)
				{
					int iRet = _ci.DeleteLabel(strReposPath, labelID);

					if (iRet == VaultStatusCode.Success)
					{
						bSuccess = true;
					} 
					else 
					{
						throw new Exception("Delete Label error: " + VaultConnection.GetSoapExceptionMessage(iRet));
					}
				} 
				else 
				{
					throw new Exception(string.Format("Could not find label \"{0}\" created at item \"{1}\".  ", strLabelName, strReposPath));
				}
			}
			catch (Exception e)
			{
				if (labelStructure == null)
				{
					throw new Exception(string.Format("Could not find label \"{0}\" created at item \"{1}\".  ", strLabelName, strReposPath));
				} 
				else 
				{
					throw e;
				}
			}

			return bSuccess;
		}

		bool ProcessCommandDiff 
			( 
			string strArgDiffBin, 
			string strArgDiffArgs, 
			DiffAgainstType datDiffChoice, 
			bool bRecursive, 
			string strLeft, 
			string strLeftLbl, 
			string strRight, 
			string strRightLbl
			)
		{
			int nDoDiffError = DoDiffError.Success;

			// login to the server.
			Login();
			_ci.Refresh();

			// determine the diff command
			string strDiffBin = strArgDiffBin;
			if ( (strDiffBin == null) || (strDiffBin.Length == 0) )
			{
				// nothing specified on the command line, what about the environment variable.
				strDiffBin = Environment.GetEnvironmentVariable(VaultCmdLineClientDefines.DiffEnv);
				
				if ( strDiffBin == null )
				{
					// just try plain diff
					strDiffBin = VaultCmdLineClientDefines.DiffBin;
				}
			}

			// add parameters for left/right to the args
			string strFullDiffArgs = strArgDiffArgs;
			if ( strFullDiffArgs.IndexOf(VaultCmdLineClientDefines.DiffLeftItem) < 0 )
			{
				strFullDiffArgs += string.Format("\"{0}\" ", VaultCmdLineClientDefines.DiffLeftItem);
			}
			if ( strFullDiffArgs.IndexOf(VaultCmdLineClientDefines.DiffRightItem) < 0 )
			{
				strFullDiffArgs += string.Format("\"{0}\" ", VaultCmdLineClientDefines.DiffRightItem);
			}

			string strLItem = null, strRItem = null;
			Exception e = null;

			// get the repository item.
			VaultClientTreeObject to = _ci.TreeCache.Repository.Root.FindTreeObjectRecursive(strLeft);
			if ( to != null )
			{
				if ( to is VaultClientFile )
				{
					VaultClientFile vcfile = (VaultClientFile)to;
					// the working folder must be set
					if ( _ci.TreeCache.GetBestWorkingFolder(vcfile.Parent) != null )
					{
						nDoDiffError = _ci.DoWorkingFileDiff(vcfile, datDiffChoice, strRight, 
							strDiffBin, strFullDiffArgs, strLeftLbl, strRightLbl, out strLItem, out strRItem, out e);
					}
					else
					{
						nDoDiffError = DoDiffError.NoValidWorkingFolder;
					}
				}
				else
				{
					VaultClientFolder vcfolder = (VaultClientFolder)to;
					if ( _ci.TreeCache.GetBestWorkingFolder(vcfolder) != null )
					{
						nDoDiffError = _ci.DoWorkingFolderDiff(vcfolder, bRecursive, datDiffChoice, strRight, 
							strDiffBin, strFullDiffArgs, strLeftLbl, strRightLbl, out strLItem, out strRItem, out e);
					}
					else
					{
						nDoDiffError = DoDiffError.NoValidWorkingFolder;
					}
				}
			}
			else
			{
				// the repository item could not be found.
				nDoDiffError = DoDiffError.RepositoryItemNotFound;
			}

			// handle the diff error
			if ( nDoDiffError != DoDiffError.Success )
			{
				if ( strLeft == null )
				{
					strLeft = "Unknown Item";
				}
				if ( strRight == null )
				{
					strRight = "Unknown Item";
				}

				string strMessage = null;
				switch ( nDoDiffError )
				{
					case DoDiffError.RepositoryItemNotFound:
					case DoDiffError.LeftItemDoesNotExist:
						strMessage = string.Format("Item {0} could not be found.", strLeft);
						break;
					case DoDiffError.RightItemDoesNotExist:
						strMessage = string.Format("Item {0} could not be found.", strRight);
						break;
					case DoDiffError.DiffBinaryError:
						strMessage = "The Diff utility encountered an error during execution.  Please verify the use of VAULTDIFF or the \"diff\" utility.";
						break;
					case DoDiffError.TempPathNotFound:
						strMessage = "Could not find temp path.";
						break;
					case DoDiffError.TempFileNotFound:
						strMessage = "Could not find temp file.";
						break;
					case DoDiffError.LabelNotRetrieved:
						strMessage = string.Format("Could not retrieve label {0}.", strRight);
						break;
					case DoDiffError.NoValidWorkingFolder:
						strMessage = string.Format("The working folder has not been set for {0}.", strLeft);
						break;
					default:
						if ( e != null )
						{
							strMessage = e.Message;
						}
						else
						{
							// some other error
							// TODO - move this to a resource.
							strMessage = "An unknown error occurred executing the diff utility.";
						}
						break;
				}

				throw new Exception(strMessage);
			}

			return (nDoDiffError == DoDiffError.Success);
		}

		bool ProcessCommandGet(ArrayList strItemArray)
		{
			bool bExists = true;
			bool bSuccess = true;
			bool bResetCloaks = false;
			VaultNameValueCollection cloaks = null;
			string strErrorItem = null;
			
			// get the merge option.
			MergeType mt = _args.Merge;

			// this argument specifies a get to a non-working folder
			// in this case, there are only two valid options.
			if (_args.DestPath != null)
			{
				// on a non working folder only valid options :
				//	a) overwrite OR b) do not overwrite (later)
				switch (mt)
				{
					case MergeType.AttemptAutomaticMerge:
						// in this case, automatic merge is not possible, switch to do not overwrite
						mt = MergeType.MergeLater;
						break;
					case MergeType.OverwriteWorkingCopy:
					case MergeType.MergeLater:
						// do nothing - set correctly
						break;
					default:
						// the default value
						mt = MergeType.OverwriteWorkingCopy;
						break;
				}
			}
			if(mt == MergeType.Unspecified)
			{
				// use a default merge type if a valid merge type was not set
				mt = MergeType.AttemptAutomaticMerge;
			}

			// login to the server.
			Login();
			_ci.Refresh();

			// after login - check for flag to use/not use cloak 
			if(_args.RespectCloaks == false)
			{
				cloaks = _ci.TreeCache.Cloaks;
				bResetCloaks = true;
				_ci.TreeCache.Cloaks = null;
			}

			// if the backup option is specified, set it for these gets only
			bool saveMakeBackupOption = _ci.WorkingFolderOptions.MakeBackups;
			if (_args.MakeBackup == BackupOption.yes)
			{
				_ci.WorkingFolderOptions.MakeBackups = true;
			} 
			else if (_args.MakeBackup == BackupOption.no)
			{
				_ci.WorkingFolderOptions.MakeBackups = false;
			}

			VaultGetResponse[] gresponses=null;

			for (int i=0; i < strItemArray.Count; i++)
			{
				string strReposItem = (string) strItemArray[i];

				VaultClientFile vcfile = null;
				VaultClientFolder vcfolder = _ci.TreeCache.Repository.Root.FindFolderRecursive(strReposItem);
				if (vcfolder != null)
				{
					// OK, this is a folder
					if (_args.DestPath != null)
					{
						gresponses = _ci.GetToNonWorkingFolder(vcfolder, _args.Recursive, true, (mt == MergeType.OverwriteWorkingCopy) ? true : false, _args.MakeWritable, _args.SetFileTime, _args.DestPath, null);
					}
					else
					{
						if (CheckForWorkingFolder(vcfolder, false) == false)
							return false;
						// a get to a working folder
						gresponses = _ci.Get(vcfolder, _args.Recursive, true, _args.MakeWritable, _args.SetFileTime, mt, null);
						_ci.PerformPendingLocalDeletions(strReposItem, _args.PerformDeletions);
					}
				}
				else
				{
					vcfile = _ci.TreeCache.Repository.Root.FindFileRecursive(strReposItem);
					if (vcfile != null)
					{
						// OK, this is a file
						if (_args.DestPath != null)
						{
							gresponses = _ci.GetToNonWorkingFolder(vcfile, true, (mt == MergeType.OverwriteWorkingCopy) ? true : false, _args.MakeWritable, _args.SetFileTime, vcfile.Parent.FullPath, _args.DestPath, null);
						}
						else
						{
							if (CheckForWorkingFolder(vcfile, false) == false)
								return false;
							gresponses = _ci.Get(vcfile, true, _args.MakeWritable, _args.SetFileTime, mt, null);
						}
					}
					else
					{
						bExists = false;
						strErrorItem = strReposItem;
					}
				}
			}

			// reset prev cloak value
			if ( 
				(_args.RespectCloaks == false) && 
				(bResetCloaks == true) 
				)
			{
				_ci.TreeCache.Cloaks = cloaks;
			}

			// reset prev MakeBackup value
			if (_args.MakeBackup != BackupOption.usedefault)
			{
				_ci.WorkingFolderOptions.MakeBackups = saveMakeBackupOption;
			} 

			if(bExists == false)
				throw new Exception(string.Format("{0} does not exist", strErrorItem));

			if (gresponses != null)
			{
				foreach (VaultGetResponse vgr in gresponses)
				{
					if (vgr.Response.Status != VaultStatusCode.Success && vgr.Response.Status != VaultStatusCode.SuccessRequireFileDownload)
					{
						return bSuccess = false;
					}
				}
			}

			return bSuccess;
		}

		bool CheckForWorkingFolder(VaultClientTreeObject obj, bool isCheckout)
		{
			WorkingFolder wf = null;
			if (obj is VaultClientFolder )
				wf = _ci.GetWorkingFolder((VaultClientFolder)obj);
			else
				wf = _ci.GetWorkingFolder((VaultClientFile)obj);
			if (wf == null)
				if (isCheckout)
					_xml.WriteUserMessage("There is no working folder specified for " + obj.FullPath + "\nPlease specify one using " + Command.SETWORKINGFOLDER + ".");
				else
					_xml.WriteUserMessage("There is no working folder specified for " + obj.FullPath + "\nPlease specify one using " + Command.SETWORKINGFOLDER + ", or use the " + Option.DESTPATH + " option to get \nto a nonworking folder");
			return (wf != null);
		}
		bool ProcessCommandGetLabel(string reposItem, string label, string labelSubItem)
		{
			bool bSuccess = true;
			string[] discoveredPaths;
			VaultClientTreeObject labelStructure = null;
			long labelSubItemId = 0;
			long labelID = 0;

			// retrieve the merge option.
			MergeType mt = _args.Merge;
			// TODO - SEE IF YOU CAN ACTUALLY SET AUTOMATIC MERGE FOR A LABEL GET
			//			if(_args.DestPath != null)
			//			{
			// a get to a non-working folder
			switch (mt)
			{
				case MergeType.AttemptAutomaticMerge:
					// in this case, automatic merge is not possible, switch to do not overwrite
					mt = MergeType.MergeLater;
					break;
				case MergeType.OverwriteWorkingCopy:
				case MergeType.MergeLater:
					// do nothing - set correctly
					break;
				default:
					// the default value
					mt = MergeType.OverwriteWorkingCopy;
					break;
			}
			//			}
			//			if ( mt == MergeType.Unspecified )
			//			{
			//				// use a default merge type if a valid merge type was not set
			//				mt = MergeType.AttemptAutomaticMerge;
			//			}

			Login();
			_ci.Refresh();

			VaultClientTreeObject reposTreeObj = _ci.TreeCache.Repository.Root.FindTreeObjectRecursive(reposItem);

			if(reposTreeObj == null)
			{
				throw new Exception(string.Format("Item \"{0}\" was not found in the repository", reposItem));
			}

			labelSubItemId = reposTreeObj.ID;

			// if the backup option is specified, set it for these gets only
			bool saveMakeBackupOption = _ci.WorkingFolderOptions.MakeBackups;
			if (_args.MakeBackup == BackupOption.yes)
			{
				_ci.WorkingFolderOptions.MakeBackups = true;
			} 
			else if (_args.MakeBackup == BackupOption.no)
			{
				_ci.WorkingFolderOptions.MakeBackups = false;
			}


			// This is the ID of the file/folder where the label structure was
			// created.
			long rootID = 0;

			try 
			{

				bSuccess = _ci.GetByLabel_GetStructure(reposItem, label, ref labelID, labelSubItem, out discoveredPaths, out labelStructure, out rootID);
			}
			catch (Exception e)
			{
				if (labelStructure == null)
				{
					throw new Exception(string.Format("Could not find label \"{0}\" created at item \"{1}\".  ", label, reposItem));
				} 
				else 
				{
					throw e;
				}
			}


			if(bSuccess == true)
			{
				VaultClientTreeObjectColl treeObjects = new VaultClientTreeObjectColl();

				if (labelStructure is VaultClientFile)
				{
					if (((VaultClientFile) labelStructure).ID == labelSubItemId)
						treeObjects.Add(labelStructure);
				}
				else
				{
					((VaultClientFolder) labelStructure).FindTreeObjectsRecursive(labelSubItemId, ref treeObjects);
				}

				if(treeObjects.Count < 1)
				{
					throw new Exception("The specified item could not be found in the label.");
				}
				else if(treeObjects.Count > 1)
				{
					throw new Exception("The specified item was not specific within the label.");
				}

				VaultClientTreeObject treeObject = (VaultClientTreeObject)treeObjects[0];
				labelSubItem = discoveredPaths[0];

				// get to non-working folder
				if(_args.DestPath != null)
				{
					if (labelStructure is VaultClientFolder)
					{
						_ci.GetByLabelToNonWorkingFolder_GetData(
							(VaultClientFolder) labelStructure, 
							_args.Recursive, 
							(mt == MergeType.OverwriteWorkingCopy) ? true : false, 
							_args.MakeWritable, 
							_args.SetFileTime, 
							_args.DestPath,
							null, 
							labelID,
							reposItem,
							labelSubItem);
					}
					else
					{
						// We have to invent a parent for the file in the label structure so the
						// get works correctly.

						VaultClientFolder parent = new VaultClientFolder();
						parent.Name = Guid.NewGuid().ToString();
						parent.Files.Add((VaultClientFile) labelStructure);
						labelStructure.Parent = parent;

						_ci.GetByLabelToNonWorkingFolder_GetData(
							(VaultClientFile) labelStructure, 
							(mt == MergeType.OverwriteWorkingCopy) ? true : false, 
							_args.MakeWritable, 
							_args.SetFileTime, 
							_args.DestPath, 
							null,
							labelID,
							reposItem,
							labelSubItem);
					}
				}
					// get to working folder
				else
				{
					VaultClientFolder labelRootFolder;

					if(treeObject == null)
					{
						throw new Exception(string.Format("{0} does not exist in the label structure for {1}", reposItem, label));
					}

					if(labelStructure is VaultClientFolder)
					{				
						labelRootFolder = (VaultClientFolder)treeObjects[0];
					}
					else
					{
						labelRootFolder = GetFakeLabelParent((VaultClientFile) labelStructure, reposItem, label);
					}

					_ci.TreeCache.SetLabelWorkingFolder(labelRootFolder.FullPath, _args.LabelWorkingFolder);
					
					if(labelStructure is VaultClientFolder)
					{
						_ci.GetByLabel_GetData((VaultClientFolder) labelStructure,
							_args.Recursive,
							_args.MakeWritable,
							_args.SetFileTime,
							mt,
							null,
							labelID,
							reposItem,
							labelSubItem);
					}
					else
					{
						_ci.GetByLabel_GetData((VaultClientFile) labelStructure,
							_args.MakeWritable,
							_args.SetFileTime,
							mt,
							null,
							labelID,
							reposItem,
							labelSubItem);
					}
				}
			}
			else
			{
				string subItemOptions = String.Empty;
				
				foreach(string item in discoveredPaths)
				{
					subItemOptions += string.Format("   {0}{1}", item, Environment.NewLine);
				}

				throw new Exception(
					string.Format("The specified item is shared to multiple places in the label structure.{0}" +
					"Please use \"vault.exe GETLABEL '{1}' '{2}' labelpath\", where labelpath is one of:{0}{0}{3}", 
					Environment.NewLine, reposItem, label, subItemOptions)
					);
			}

			// reset prev MakeBackup value
			if (_args.MakeBackup != BackupOption.usedefault)
			{
				_ci.WorkingFolderOptions.MakeBackups = saveMakeBackupOption;
			} 


			return bSuccess;
		}

		bool ProcessCommandGetVersion(int version, string strReposItem, string strDestFolder)
		{
			bool bExists = true;
			bool bSuccess = true;
			bool bResetCloaks = false;
			VaultNameValueCollection cloaks = null;

			// retrieve the merge option.
			MergeType mt = _args.Merge;

			// this always overwrites, unless specified to auto merge
			switch (mt)
			{
				case MergeType.AttemptAutomaticMerge:
					// in this case, automatic merge is not possible, switch to do not overwrite
					mt = MergeType.MergeLater;
					break;
				case MergeType.OverwriteWorkingCopy:
				case MergeType.MergeLater:
					// do nothing - set correctly
					break;
				default:
					// the default value
					mt = MergeType.OverwriteWorkingCopy;
					break;
			}
			
			Login();
			_ci.Refresh();

			VaultClientFolder vcfolder = null;
			VaultClientFile vcfile = null;

			// temporarily blow away cloaks if that's what the user wanted
			// do after login
			if(_args.RespectCloaks == false)
			{
				cloaks = _ci.TreeCache.Cloaks;
				bResetCloaks = true;
				_ci.TreeCache.Cloaks = null;
			}

			// if the backup option is specified, set it for these gets only
			bool saveMakeBackupOption = _ci.WorkingFolderOptions.MakeBackups;
			if (_args.MakeBackup == BackupOption.yes)
			{
				_ci.WorkingFolderOptions.MakeBackups = true;
			} 
			else if (_args.MakeBackup == BackupOption.no)
			{
				_ci.WorkingFolderOptions.MakeBackups = false;
			}

			vcfolder = _ci.TreeCache.Repository.Root.FindFolderRecursive(strReposItem);

			if(strDestFolder == null)
				throw new Exception("destination folder is null, you can only checkout historical versions to a non-working folder");

			if (vcfolder != null)
			{
				vcfolder.Version = version;

				VaultFolderDelta vfDelta = new VaultFolderDelta();
				_ci.Connection.GetBranchStructure(_ci.ActiveRepositoryID, strReposItem, vcfolder.ID, version, ref vfDelta, false);

				vcfolder = new VaultClientFolder(vfDelta, vcfolder.Parent);

				_ci.GetByDisplayVersionToNonWorkingFolder(vcfolder, _args.Recursive, _args.MakeWritable, _args.SetFileTime, strDestFolder, null);				
			}
			else
			{
				vcfile = new VaultClientFile(_ci.TreeCache.Repository.Root.FindFileRecursive(strReposItem));
				if (vcfile != null)
				{
					// Set the version.
					vcfile.Version = version;
					_ci.GetByDisplayVersionToNonWorkingFolder(vcfile, _args.MakeWritable, _args.SetFileTime, vcfile.Parent.FullPath, strDestFolder, null);
				}
				else
				{
					bExists = false;
				}
			}

			if (
				(_args.RespectCloaks == false) && 
				(bResetCloaks == true) 
				)
			{
				_ci.TreeCache.Cloaks = cloaks;
			}

			// reset prev MakeBackup value
			if (_args.MakeBackup != BackupOption.usedefault)
			{
				_ci.WorkingFolderOptions.MakeBackups = saveMakeBackupOption;
			} 

			if(bExists == false)
				throw new Exception(string.Format("{0} does not exist", strReposItem));

			return bSuccess;
		}

		private bool ProcessCommandGetLabelDiffs(string strReposPath, string strLabel1, string strLabel2)
		{
			bool bSuccess = true;

			Login();

			VaultHistoryQueryRequest hq = new VaultHistoryQueryRequest();
			hq.Recursive = true;
			
			DateTime beginDate = VaultDate.EmptyDate(), endDate = VaultDate.EmptyDate();
			GetLabelTimeStamps(strReposPath, strLabel1, strLabel2, out beginDate, out endDate);

			hq.BeginDate = beginDate;

			if ( endDate != VaultDate.EmptyDate() )
				hq.EndDate = endDate;
			else hq.EndDate = System.DateTime.Now;
			
			hq.RepID = _ci.ActiveRepositoryID;

			VaultClientFolder vcfolder = null;
			VaultClientFile vcfile = null;

			vcfolder = _ci.TreeCache.Repository.Root.FindFolderRecursive(strReposPath);
			if (vcfolder != null)
			{
				hq.TopName = vcfolder.FullPath;
				hq.TopID = vcfolder.ID;
				hq.IsFolder = true;
			}
			else
			{
				vcfile = _ci.TreeCache.Repository.Root.FindFileRecursive(strReposPath);
				if (vcfile != null)
				{
					hq.TopName = vcfile.FullPath;
					hq.TopID = vcfile.ID;
					hq.IsFolder = false;
				}
				else
				{
					throw new Exception(string.Format("{0} does not exist", strReposPath));
				}
			}

			int nRowsRetrieved = 0;
			string strQryToken = null;

			_ci.Connection.HistoryBegin(hq, _args.HistoryRowLimit, ref nRowsRetrieved, ref strQryToken);
			VaultHistoryItem[] histitems = null;
			_ci.Connection.HistoryFetch(strQryToken, 0, nRowsRetrieved-1, ref histitems);
			_ci.Connection.HistoryEnd(strQryToken);

			_xml.Begin("history");

			// write header
			_xml.AddPair("fromLabel", strLabel1);
			_xml.AddPair("fromDate", beginDate.ToString());
			if ( strLabel2.Length > 0 )
			{
				_xml.AddPair("toLabel", strLabel2);
				_xml.AddPair("toDate", endDate.ToString());
			}
			else
			{
				_xml.AddPair("toLabel", "");
				_xml.AddPair("toDate", "");
			}

			// sort history items before outputting as XML
			Array.Sort(histitems, new ReverseHistoryItemComparer());		

			// dump history items to XML stream
			foreach (VaultHistoryItem hi in histitems)
			{
				// skip label history - not interested in that.
				if ( hi.HistItemType == VaultHistoryType.Label )
					continue;
				_xml.Begin("item");
				_xml.AddPair("txid", hi.TxID);
				_xml.AddPair("date", hi.TxDate.ToString());
				_xml.AddPair("name", hi.Name);
				_xml.AddPair("version", hi.Version);
				_xml.AddPair("user", hi.UserLogin);
				_xml.AddPair("action", TranslateActionToString(hi) );
				if (
					(hi.Comment != null)
					&& (hi.Comment.Length > 0)
					)
				{
					_xml.AddPair("comment", hi.Comment);
				}
				_xml.End();
			}
			_xml.End();
			return bSuccess;
		}

		
		bool ProcessCommandGetWildcard(string strReposPath, ArrayList strWildcardArray)
		{
			bool bSuccess = true;
			bool bResetCloaks = false;
			VaultNameValueCollection cloaks = null;
			ArrayList RegexArray = new ArrayList();

			// get the merge option.
			MergeType mt = _args.Merge;
			
			// this argument specifies a get to a non-working folder
			// in this case, there are only two valid options.
			if (_args.DestPath != null)
			{
				// on a non working folder only valid options :
				//	a) overwrite OR b) do not overwrite (later)
				switch (mt)
				{
					case MergeType.AttemptAutomaticMerge:
						// in this case, automatic merge is not possible, switch to do not overwrite
						mt = MergeType.MergeLater;
						break;
					case MergeType.OverwriteWorkingCopy:
					case MergeType.MergeLater:
						// do nothing - set correctly
						break;
					default:
						// the default value
						mt = MergeType.OverwriteWorkingCopy;
						break;
				}
			}
			if(mt == MergeType.Unspecified)
			{
				// use a default merge type if a valid merge type was not set
				mt = MergeType.AttemptAutomaticMerge;
			}
			
			Login();
			_ci.Refresh();

			// reset cloaks - must come after login
			if(_args.RespectCloaks == false)
			{
				cloaks = _ci.TreeCache.Cloaks;
				bResetCloaks = true;
				_ci.TreeCache.Cloaks = null;
			}

			// if the backup option is specified, set it for these gets only
			bool saveMakeBackupOption = _ci.WorkingFolderOptions.MakeBackups;
			if (_args.MakeBackup == BackupOption.yes)
			{
				_ci.WorkingFolderOptions.MakeBackups = true;
			} 
			else if (_args.MakeBackup == BackupOption.no)
			{
				_ci.WorkingFolderOptions.MakeBackups = false;
			}


			//OperatingSystem os = Environment.OSVersion;
			//bool bCaseInsensitive = ( (os.Platform == PlatformID.Win32NT) || 
			//									(os.Platform == PlatformID.Win32S) || 
			//									(os.Platform == PlatformID.Win32Windows) ||
			//									(os.Platform == PlatformID.WinCE) );
			
			// for consistency's sake, currently doing case insensitve regex for wildcard
			bool bCaseInsensitive = true; 

			for(int i = 0; i < strWildcardArray.Count; i++)
			{
				Wildcard wildcard = new Wildcard((string) strWildcardArray[i]);
				Regex regex = new Regex(wildcard.ToRegex(bCaseInsensitive));
				RegexArray.Add(regex);
			}
			
			VaultClientFolder vcfolder = _ci.TreeCache.Repository.Root.FindFolderRecursive(strReposPath);

			if(vcfolder != null)
			{
				if(_args.DestPath != null)
				{
					_ci.GetByRegexToNonWorkingFolder(vcfolder, RegexArray, _args.Recursive, true, (mt == MergeType.OverwriteWorkingCopy) ? true : false, _args.MakeWritable, _args.SetFileTime, _args.DestPath, null);
				}
				else
				{
					if (CheckForWorkingFolder(vcfolder, false) == false)
						return false;
					_ci.GetByRegex(vcfolder, RegexArray, _args.Recursive, true, _args.MakeWritable, _args.SetFileTime, mt, null);
					_ci.PerformPendingLocalDeletions(strReposPath, _args.PerformDeletions);
				}
			}

			if (
				(_args.RespectCloaks == false) && 
				(bResetCloaks == true) 
				)
			{
				_ci.TreeCache.Cloaks = cloaks;
			}

			// reset prev MakeBackup value
			if (_args.MakeBackup != BackupOption.usedefault)
			{
				_ci.WorkingFolderOptions.MakeBackups = saveMakeBackupOption;
			} 

			if(vcfolder == null && _ci.TreeCache.Repository.Root.FindFileRecursive(strReposPath) != null)
				throw new Exception(string.Format("{0} is a file, not a directory", strReposPath));
			else if(vcfolder == null)
				throw new Exception(string.Format("{0} does not exist", strReposPath));

			return bSuccess;
		}

		bool ProcessCommandListAllTxDetails()
		{
			bool bSuccess = true;

			Login();
			_ci.Refresh();

			long txidEnd = _ci.Repository.RevID;

			_xml.Begin("txdetails");
			for (long txid=1; txid<=txidEnd; txid++)
			{
				_xml.Begin("tx");

				VaultTxDetailHistoryItem[] items = null;
				string comment = null;
				_ci.Connection.GetTxDetail(_ci.ActiveRepositoryID, txid, ref comment, ref items);

				_xml.AddPair("txid", txid.ToString());

				if (items.Length > 0) // and it ALWAYS should be...
				{
					VaultTxDetailHistoryItem hi = items[0];
					_xml.AddPair("UserLogin", hi.UserLogin);
					_xml.AddPair("TxDate", hi.TxDate.ToString());
				}
				_xml.AddPair("comment", comment);

				foreach (VaultTxDetailHistoryItem hi in items)
				{
					_xml.Begin("txitem");
					_xml.AddPair("RequestType", VaultRequestType.GetRequestTypeName(hi.RequestType));
					_xml.AddPair("ID", hi.ID);
					_xml.AddPair("ObjVerID", hi.ObjVerID);
					_xml.AddPair("Version", hi.Version);
					_xml.AddPair("ItemPath1", hi.ItemPath1);
					_xml.AddPair("ItemPath2", hi.ItemPath2);
					_xml.AddPair("Name", hi.Name);
					_xml.AddPair("Comment", hi.Comment);
					_xml.End();
				}

				_xml.End();
			}
			_xml.End();

			return bSuccess;
		}

		bool ProcessCommandVersionHistory(string strReposPath)
		{
			bool bSuccess = true;

			Login();

			VaultClientFolder vcfolder = null;
			vcfolder = _ci.TreeCache.Repository.Root.FindFolderRecursive(strReposPath);
			if (vcfolder == null)
			{
				throw new Exception(string.Format("Folder {0} does not exist.  (Note that versionhistory can only be used on folders, not on files.)", strReposPath));
			}
			
			
			int rowsRetrieved = 0;
			string strQryToken = null;
			VaultTxHistoryItem[] histitems = new VaultTxHistoryItem[0];

			DateTime actualBeginDate = _args.HistoryBeginDate;
			DateTime actualEndDate = _args.HistoryEndDate;
			if ( VaultDate.IsEmptyDate(_args.HistoryBeginDate) || VaultDate.IsEmptyDate(_args.HistoryEndDate) )
			{
				actualBeginDate = _args.HistoryEndDate;
				actualEndDate = _args.HistoryBeginDate;
			}
			_ci.Connection.VersionHistoryBegin(_args.HistoryRowLimit, _ci.ActiveRepositoryID, vcfolder.ID, actualBeginDate,
				actualEndDate, _args.VersionHistoryBeginVersion, ref rowsRetrieved, ref strQryToken);
			if ( rowsRetrieved > 0 )
			{
				_ci.Connection.VersionHistoryFetch(strQryToken, 0, rowsRetrieved-1, ref histitems);
			}
			_ci.Connection.VersionHistoryEnd(strQryToken);
			
			// produce results into the xml item.
			_xml.Begin("history");
			foreach (VaultTxHistoryItem hi in histitems)
			{
				_xml.Begin("item");
				_xml.AddPair("version", hi.Version);
				_xml.AddPair("date", hi.TxDate.ToString());
				_xml.AddPair("user", hi.UserLogin);
				if ( hi.Comment != null && hi.Comment.Length > 0 )
				{
					_xml.AddPair("comment", hi.Comment);
				}
				_xml.AddPair("txid", hi.TxID);
				_xml.End();
			}
			_xml.End();

			return bSuccess;
			
		}

		bool ProcessCommandHistory(string strReposPath)
		{
			bool bSuccess = true;

			Login();

			// prepare the object used to filter the history results
			VaultHistoryQueryRequest hq = new VaultHistoryQueryRequest();
			hq.RepID = _ci.ActiveRepositoryID;

			hq.Recursive = _args.Recursive;
			
			VaultClientFolder vcfolder = null;
			VaultClientFile vcfile = null;
			vcfolder = _ci.TreeCache.Repository.Root.FindFolderRecursive(strReposPath);
			if (vcfolder != null)
			{
				hq.TopName = vcfolder.FullPath;
				hq.TopID = vcfolder.ID;
				hq.IsFolder = true;
			}
			else
			{
				vcfile = _ci.TreeCache.Repository.Root.FindFileRecursive(strReposPath);
				if (vcfile != null)
				{
					hq.TopName = vcfile.FullPath;
					hq.TopID = vcfile.ID;
					hq.IsFolder = false;
				}
				else
				{
					throw new Exception(string.Format("{0} does not exist", strReposPath));
				}
			}

			// set the sort order
			hq.Sorts = new long[1];
			hq.Sorts[0] = (_args.DateSort == DateSortOption.asc) ? 
				(long)(VaultQueryRequestSort.DateSort | VaultQueryRequestSort.AscSort) : 
				(long)(VaultQueryRequestSort.DateSort | VaultQueryRequestSort.DescSort);

			if (_args.HistoryExcludedUsers != null && _args.HistoryExcludedUsers != string.Empty)
			{
				VaultUser[] serverUsers = null;
				_ci.Connection.GetUserList( ref serverUsers );
				if (serverUsers == null)
				{
					_xml.WriteUserMessage("Could not get user list for history query.");
					return false;
				}
				ArrayList serverUsersAL = new ArrayList(serverUsers);
					
				string[] excludeUsers = _args.HistoryExcludedUsers.Split(",".ToCharArray());
				foreach (string excludedUser in excludeUsers)
				{
					for (int i = serverUsers.Length -1; i >= 0; i--)
					{
						if (serverUsers[i].Login.ToLower() == excludedUser.ToLower().Trim())
						{
							serverUsersAL.Remove(serverUsers[i]);
						}
					}
				}
				hq.Users = (VaultUser[])serverUsersAL.ToArray(typeof(VaultLib.VaultUser));
			}
			// set the date ranges.
			bool bBegDateNull = VaultDate.IsEmptyDate(_args.HistoryBeginDate);
			bool bEndDateNull = VaultDate.IsEmptyDate(_args.HistoryEndDate);

#region "Use the Label Dates if no begin or end dates were specified"
			
			if (bBegDateNull == true && _args.HistoryBeginLabel != null && _args.HistoryBeginLabel != string.Empty)
			{//Try to find the label that specified.
				VaultClientTreeObject vaultObject = ((vcfolder != null) ? (VaultClientTreeObject)vcfolder : (VaultClientTreeObject)vcfile);
				DateTime dtBegin = DateTime.MinValue;
				string strQryTokenBegin = "";
				int nRowsRecursive = 0, nRowsInherited = 0;
				_ci.BeginLabelQuery(vaultObject.FullPath, vaultObject.ID, false, true, false, true, int.MaxValue, out nRowsInherited, out nRowsRecursive, out strQryTokenBegin);

				VaultLabelItemX[] vlx = null;
				int current = 0;
				while (dtBegin == DateTime.MinValue && current < nRowsInherited)
				{
					_ci.GetLabelQueryItems_Main(strQryTokenBegin, current, current + 5, out vlx);

					if (vlx != null)
						foreach (VaultLabelItemX vli in vlx)
						{
							if (string.Compare(vli.Label, _args.HistoryBeginLabel, true) ==0)
							{
								if (vli.LabelType == VaultLabelResultType.MainLabel 
									|| vli.LabelType == VaultLabelResultType.InheritedLabel)
								{
									//This is a hack, looking only at the date the label is applied.
									//You can break this by labeling a historical version of an object.
									dtBegin = vli.LabelDate;
								}
							}
						}
					current = current + 5;
				}
				_ci.EndLabelQuery(strQryTokenBegin);
				if (dtBegin == DateTime.MinValue)
				{
					_xml.WriteUserMessage("The label " + _args.HistoryBeginLabel + " could not be found.");
					return false;
				}
				else
				{
					bBegDateNull = false;
					_args.HistoryBeginDate = dtBegin;
				}
			}
			if (bEndDateNull == true && _args.HistoryEndLabel != null && _args.HistoryEndLabel != string.Empty)
			{//Try to find the label that specified.
				VaultClientTreeObject vaultObject = ((vcfolder != null) ? (VaultClientTreeObject)vcfolder : (VaultClientTreeObject)vcfile);
				DateTime dtEnd = DateTime.MinValue;
				string strQryTokenEnd = "";
				int nRowsRecursive = 0, nRowsInherited = 0;
				_ci.BeginLabelQuery(vaultObject.FullPath, vaultObject.ID, false, true, false, true, int.MaxValue, out nRowsInherited, out nRowsRecursive, out strQryTokenEnd);

				VaultLabelItemX[] vlx = null;
				int current = 0;
				while (dtEnd == DateTime.MinValue && current < nRowsInherited)
				{
					_ci.GetLabelQueryItems_Main(strQryTokenEnd, current, current + 5, out vlx);

					if (vlx != null)
						foreach (VaultLabelItemX vli in vlx)
						{
							if (string.Compare(vli.Label, _args.HistoryEndLabel, true) ==0)
							{
								if (vli.LabelType == VaultLabelResultType.MainLabel 
									|| vli.LabelType == VaultLabelResultType.InheritedLabel)
								{
									//This is a hack, looking only at the date the label is applied.
									//You can break this by labeling a historical version of an object.
									dtEnd = vli.LabelDate;
								}
							}
						}
					current = current + 5;
				}
				_ci.EndLabelQuery(strQryTokenEnd);
				if (dtEnd == DateTime.MinValue)
				{
					_xml.WriteUserMessage("The label " + _args.HistoryEndLabel + " could not be found.");
					return false;
				}
				else
				{
					bEndDateNull = false;
					_args.HistoryEndDate = dtEnd;
				}
			}
#endregion

			if ( (bBegDateNull == true) && 
				(bEndDateNull == true) )
			{
				// no date range
				hq.DateFilterMask = VaultQueryRequestDates.DoNotFilter;
				hq.BeginDate = hq.EndDate = VaultDate.EmptyDate();
			}
			else if ( (bBegDateNull == false) && 
				(bEndDateNull == false) )
			{
				// a range of dates has been requested
				hq.DateFilterMask = VaultQueryRequestDates.HistoryBefore | VaultQueryRequestDates.HistoryAfter;
				hq.BeginDate = _args.HistoryBeginDate;
				hq.EndDate = _args.HistoryEndDate;
			}
			else if (bBegDateNull == false)
			{
				// when -begindate (floor) has been specified,
				// and -enddate has not, the user
				// is asking for all dates after the  date.

				// q query of this type should be defined so the
				// end date is valid and the begindate is empty.
				hq.DateFilterMask = VaultQueryRequestDates.HistoryAfter;
				hq.BeginDate = VaultDate.EmptyDate();
				hq.EndDate = _args.HistoryBeginDate;
			}
			else // bEndDateNull will be false
			{
				// when -enddate (ceiling) has been specified,
				// and -begindate has not, the user
				// is asking for all dates before the date.

				// q query of this type should be defined so the
				// begin date is valid and the enddate is empty.
				hq.DateFilterMask = VaultQueryRequestDates.HistoryBefore;
				hq.BeginDate = _args.HistoryEndDate;
				hq.EndDate = VaultDate.EmptyDate();
			}

#region Exclude Actions
			
			if (_args.HistoryExcludedActions != null && _args.HistoryExcludedActions != string.Empty)
			{
				ArrayList allActions = new ArrayList();
				//There are 24 actions to filter on.
				for (long action = 1; action <= 24; action++)
				{
					allActions.Add(action);
				}
				string[] excludeActions = _args.HistoryExcludedActions.Split(",".ToCharArray());
				foreach (string excludedAction in excludeActions)
				{
					switch (excludedAction.ToLower().Trim())
					{
						case "add":
						case "create":
							allActions.Remove((long)VaultRequestType.AddFile);
							allActions.Remove((long)VaultRequestType.AddFolder);
							break;
						case "branch":
							allActions.Remove((long)VaultRequestType.CopyBranch);
							allActions.Remove((long)VaultRequestType.ShareBranch);
							break;
						case "checkin":
							allActions.Remove((long)VaultRequestType.CheckIn);
							break;
						case "delete":
							allActions.Remove((long)VaultRequestType.Delete);
							break;
						case "label":
							allActions.Remove((long)VaultRequestType.LabelItem);
							break;
						case "move":
							allActions.Remove((long)VaultRequestType.Move);
							break;
						case "obliterate":
							allActions.Remove((long)VaultRequestType.Obliterate);
							break;
						case "pin":
							allActions.Remove((long)VaultRequestType.Pin);
							allActions.Remove((long)VaultRequestType.Unpin);
							break;
						case "propertychange":
							allActions.Remove((long)VaultRequestType.PropertyChanged);
							allActions.Remove((long)VaultRequestType.ExtPropertyChanged);
							break;
						case "rename":
							allActions.Remove((long)VaultRequestType.Rename);
							break;
						case "rollback":
							allActions.Remove((long)VaultRequestType.Rollback);
							break;
						case "share":
							allActions.Remove((long)VaultRequestType.Share);
							break;
						case "snapshot":
							allActions.Remove((long)VaultRequestType.Snapshot);
							break;
						case "undelete":
							allActions.Remove((long)VaultRequestType.Undelete);
							break;
					}
				}
				hq.Actions = (long[]) allActions.ToArray(typeof(long));
			}
#endregion
			/////////////////////////////////
			///  execute the query and get results.
			int nRowsRetrieved = 0;
			string strQryToken = null;
			VaultHistoryItem[] histitems = new VaultHistoryItem[0];

			_ci.Connection.HistoryBegin(hq, _args.HistoryRowLimit, ref nRowsRetrieved, ref strQryToken);
			if ( nRowsRetrieved > 0 )
			{
				_ci.Connection.HistoryFetch(strQryToken, 0, nRowsRetrieved-1, ref histitems);
			}
			_ci.Connection.HistoryEnd(strQryToken);


			// produce results into the xml item.
			_xml.Begin("history");
			foreach (VaultHistoryItem hi in histitems)
			{
				_xml.Begin("item");
				_xml.AddPair("txid", hi.TxID);
				_xml.AddPair("date", hi.TxDate.ToString());
				_xml.AddPair("name", hi.Name);
				_xml.AddPair("type", GetHistItemTypeString(hi.HistItemType));
				_xml.AddPair("version", hi.Version);
				_xml.AddPair("user", hi.UserLogin);
				if (
					(hi.Comment != null)
					&& (hi.Comment.Length > 0)
					)
				{
					_xml.AddPair("comment", hi.Comment);
				}
				_xml.AddPair("actionString", hi.GetActionString());
				_xml.End();
			}
			_xml.End();

			return bSuccess;
		}

		bool ProcessCommandLabel(string strReposPath, string labelName, long versionID)
		{
			bool bSuccess = false;
			int ret = 0;
			VaultClientTreeObject vctreeobj;
			VaultLabelResult labelResult = null;
			long objVerID = -1;

			Login();

			if((vctreeobj = _ci.TreeCache.Repository.Root.FindTreeObjectRecursive(strReposPath)) == null)
			{
				throw new Exception(string.Format("{0} does not exist in the repository.", strReposPath));
			}

			VaultObjectVersionInfo[] ovis = null;
			_ci.Connection.GetObjectVersionList(vctreeobj.ID, ref ovis, false);

			if ( (ovis != null) && (ovis.Length > 0) )
			{
				if ( versionID == VaultDefine.Latest )
				{
					// use the last version, since the list is in ascending order.
					objVerID = ovis[ovis.Length - 1].ObjVerID;
				}
				else
				{
					// find the version number specified for this tree object
					foreach(VaultObjectVersionInfo ovi in ovis)
					{
						if(versionID == ovi.Version)
						{
							objVerID = ovi.ObjVerID;
							break;
						}
					}
				}
			}

			if(objVerID == -1)
			{
				throw new Exception(string.Format("{0} does not exist at version {1}", strReposPath, versionID));
			}


			ret = _ci.AddLabel(strReposPath, objVerID, labelName, _args.Comment, ref labelResult);
			switch(ret)
			{
				case VaultStatusCode.Success:
					bSuccess = true;
					break;

				case VaultStatusCode.FailDuplicateLabel:
					string strErrorMsg = null;
					if(String.Compare(labelResult.ExistingRootPath, strReposPath, true) == 0)
					{
						strErrorMsg = string.Format("{0} already has the label {1} applied", strReposPath, labelName);
					}
					else
					{
						strErrorMsg = string.Format("{0} has inherited the label {1} already", strReposPath, labelName);
					}
					throw new Exception(strErrorMsg);

				default:
					throw new Exception("Label error: " + VaultConnection.GetSoapExceptionMessage(ret));
			}

			return bSuccess;
		}

		bool ProcessCommandListAllBranchPoints()
		{
			bool bSuccess = true;
			
			Login();

			VaultBranchPointInfo[] points = null;

			_ci.ListAllBranchPoints(ref points);

			_xml.Begin("listallbranchpoints");
			foreach (VaultBranchPointInfo pt in points)
			{
				if ((pt._objprops & 4) == 4)
				{
					_xml.Begin("branch");
				}
				else
				{
					_xml.Begin("label");
				}
				VaultClientFolder vcfolder_trunk = _ci.TreeCache.Repository.Root.FindFolderRecursive(pt._baselineobjid);
				_xml.AddPair("trunk", vcfolder_trunk.FullPath);
				_xml.AddPair("trunk_versionid", pt._baselineobjverid);

				VaultClientFolder vcfolder_branch = _ci.TreeCache.Repository.Root.FindFolderRecursive(pt._objid);
				_xml.AddPair("branch", vcfolder_branch.FullPath);
				_xml.AddPair("branch_versionid", pt._rootobjverid);

				_xml.AddPair("txid", pt._origintxid);

				_xml.End();
			}
			_xml.End();

			return bSuccess;
		}

		bool ProcessCommandListChangeSet()
		{
			Login();

			_ci.UpdateKnownChanges_All(true);

			ChangeSetItemColl csic = _ci.InternalChangeSet_GetItems(true);

			WriteChangeSet(csic);

			return true;
		}

		bool ProcessCommandListCheckOuts()
		{
			Login();

			_ci.Refresh();

			WriteCheckOuts(_ci.TreeCache.CheckOuts);

			return true;
		}

		bool ProcessCommandListFolder(string strReposFolder)
		{
			Login();

			_ci.Refresh();

			VaultClientFolder vcfolder = _ci.TreeCache.Repository.Root.FindFolderRecursive(strReposFolder);
			if (vcfolder == null)
			{
				throw new Exception(string.Format("{0} does not exist", strReposFolder));
			}

			WriteFolder(vcfolder, _args.Recursive, 0);

			return true;
		}

		bool ProcessCommandListRepositories()
		{
			Login();

			VaultRepositoryInfo[] reps = null;
			_ci.ListRepositories(ref reps);
					
			_xml.Begin("listrepositories");
			foreach (VaultRepositoryInfo r in reps)
			{
				_xml.Begin("repository");
				_xml.AddPair("name", r.RepName);
				_xml.AddPair("files", r.FileCount);
				_xml.AddPair("folders", r.FolderCount);
				_xml.AddPair("dbsize", r.DbSize);
				_xml.End();
			}
			_xml.End();
					
			return true;
		}

		bool ProcessCommandMove(string strReposPath_From, string strReposPath_To)
		{
			bool bSuccess = true;
			
			Login();

			ChangeSetItemColl csic = new ChangeSetItemColl();

			VaultClientFolder vcfolder = null;
			VaultClientFile vcfile = null;

			vcfolder = _ci.TreeCache.Repository.Root.FindFolderRecursive(strReposPath_From);
			if (vcfolder != null)
			{
				// ok, this is a folder
				ChangeSetItem_Move csi = new ChangeSetItem_Move(
					DateTime.Now,
					_args.Comment,
					String.Empty,
					vcfolder.ID,
					vcfolder.FullPath,
					strReposPath_To);
				csic.Add(csi);
			}
			else
			{
				vcfile = _ci.TreeCache.Repository.Root.FindFileRecursive(strReposPath_From);
				if (vcfile != null)
				{
					// ok, this is a file
					ChangeSetItem_Move csi = new ChangeSetItem_Move(
						DateTime.Now,
						_args.Comment,
						String.Empty,
						vcfile.ID,
						vcfile.FullPath,
						strReposPath_To);
					csic.Add(csi);
				}
				else
				{
					throw new Exception(string.Format("{0} does not exist", strReposPath_From));
				}
			}

			_ci.InternalChangeSet_Append(csic);

			if (_args.AutoCommit)
			{
				_ci.InternalChangeSet_SetComment(_args.Comment);
				WriteChangeSet(csic);
				bSuccess = _ci.Commit(csic);
			}
			else
			{
				WriteChangeSet();
			}

			return bSuccess;
		}

		bool ProcessCommandObliterate(string strReposPath)
		{
			bool bSuccess = true;
			
			Login();
			ArrayList fileList = new ArrayList();
			string txtID = null;
			VaultDeletedObject[] vDeletedObjects = null;
			_ci.Connection.ListDeletedObjects(_ci.ActiveRepositoryID, "$/", true, ref vDeletedObjects);

			VaultRequestItem[] vRequests = new VaultRequestItem[1];
			VaultRequestObliterate r = null;
			if(vDeletedObjects!= null && vDeletedObjects.Length > 0)
			{	
				foreach(VaultDeletedObject item in vDeletedObjects)
				{
					if (String.Compare(item.FullPath, strReposPath, true) == 0)
					{
						if (r != null)
						{
							_xml.WriteUserMessage("There are multiple deleted objects at the specified path.  Please use the admin tool to choose between the items.");
							return false;
						}
						r = new VaultRequestObliterate();
						r.ObjID = item.ID;
						r.ItemPath = item.FullPath;
						r.DeletionID = item.DeletionID;
					}
				}
			}
			else
			{
				_xml.WriteUserMessage("There are no deleted items in the repository.");
				return false;
			}
			if (r == null)
			{
				_xml.WriteUserMessage("No deleted item was found at " + strReposPath);
				return false;
			}
			else
				vRequests[0] = r;

			_ci.Connection.BeginTx(_ci.ActiveRepositoryID, ref vRequests, ref txtID, "");	
			VaultResponseObliterate resp = null;

			foreach (VaultRequestItem req in vRequests)
			{
				if (req.Response.Status == VaultLib.VaultStatusCode.Success)
				{
					resp = (VaultResponseObliterate)req.Response;
					if (resp != null && resp.ObliteratedObjects != null)
					{
						foreach (string s in resp.ObliteratedObjects)
						{
							_xml.WriteUserMessage("Obliterating: " + s);
						}
					}
				}
				else if (req.Response.Status == VaultLib.VaultStatusCode.FailObliterateBranchExists)
				{
					string conflictlist= "";
					resp = (VaultResponseObliterate)req.Response;
					foreach (string s in resp.BranchedConflicts)
					{
						conflictlist += s + "\n";
					}
					_xml.WriteUserMessage(string.Format("Unabled to Obliterate item: {0} All branches of an item must be obliterated before the item itself can be obliterated. It is possible that some of the branched items have been deleted, but not obliterated. You must obliterate all branches before you can obliterate this item. This item has branches at: {1}", req.ItemPath + "\n\n", "\n\n"+ conflictlist + "\n")); 
							
					//If one of the requests failed, the the TxID isn't valid, and we don't need to worry about
					// aborting the operation.
					return false;
				}
				else if (req.Response.Status == VaultLib.VaultStatusCode.FailObliterateItemShared)
				{
					string conflictlist= "";
					resp = (VaultResponseObliterate)req.Response;
					foreach (string s in resp.ObliteratedObjects)
					{
						conflictlist += s + "\n";
					}
						
					_xml.WriteUserMessage(string.Format("Unabled to Obliterate item: {0} because the following item is shared:  {1}", req.ItemPath + "\n\n", "\n\n"+ conflictlist + "\n"));
					//If one of the requests failed, the the TxID isn't valid, and we don't need to worry about
					// aborting the operation.
					return false;
				}
				else
				{
					_xml.WriteUserMessage("Unable to obliterate objects" + " " + VaultClientNetLib.VaultConnection.GetSoapExceptionMessage(req.Response.Status));
					//If one of the requests failed, the the TxID isn't valid, and we don't need to worry about
					// aborting the operation.
					return false;
				}
			}
				
			VaultResponseItem[] responses = new VaultResponseItem[vRequests.Length];
			for (int i = 0; i < vRequests.Length; i++)
				responses[i] = vRequests[i].Response;
					
			long newRevision = 0;
			DateTime serverCheckInTime = DateTime.Now;
			int action =VaultLib.VaultTxAction.Commit;

			_ci.Connection.EndTx(txtID, ref newRevision, ref responses, action , ref serverCheckInTime);
			
			return bSuccess;
		}

		bool ProcessCommandPin(string strReposPath, int version)
		{
			bool bSuccess = true;

			Login();

			ChangeSetItemColl csic = new ChangeSetItemColl();

			VaultClientFolder vcfolder = null;
			VaultClientFile vcfile = null;

			vcfolder = _ci.TreeCache.Repository.Root.FindFolderRecursive(strReposPath);
			if (vcfolder != null)
			{
				// ok, this is a folder
				long objverid = version;
				if (objverid != VaultDefine.Latest)
				{
					VaultObjectVersionInfo[] ovis = null;
					_ci.Connection.GetObjectVersionList(vcfolder.ID, ref ovis, false);
					bool bFound = false;
					foreach (VaultObjectVersionInfo ovi in ovis)
					{
						if (ovi.Version == version)
						{
							objverid = ovi.ObjVerID;
							bFound = true;
							break;
						}
					}
					if (!bFound)
					{
						throw new Exception(string.Format("Version {0} of {1} does not exist", version, vcfolder.FullPath));
					}
				}


				ChangeSetItem_Pin csi = new ChangeSetItem_Pin(
					DateTime.Now,
					_args.Comment,
					String.Empty,
					objverid,
					vcfolder.FullPath);
				csic.Add(csi);
			}
			else
			{
				vcfile = _ci.TreeCache.Repository.Root.FindFileRecursive(strReposPath);
				if (vcfile != null)
				{
					long objverid = version;
					if (objverid != VaultDefine.Latest)
					{
						bool bFound = false;
						// ok, this is a file
						VaultObjectVersionInfo[] ovis = null;
						_ci.Connection.GetObjectVersionList(vcfile.ID, ref ovis, false);
						foreach (VaultObjectVersionInfo ovi in ovis)
						{
							if (ovi.Version == version)
							{
								objverid = ovi.ObjVerID;
								bFound = true;
								break;
							}
						}
						if (!bFound)
						{
							throw new Exception(string.Format("Version {0} of {1} does not exist", version, vcfile.FullPath));
						}
					}

					ChangeSetItem_Pin csi = new ChangeSetItem_Pin(
						DateTime.Now,
						_args.Comment,
						String.Empty,
						objverid,
						vcfile.FullPath);
					csic.Add(csi);
				}
				else
				{
					throw new Exception(string.Format("{0} does not exist", strReposPath));
				}
			}

			_ci.InternalChangeSet_Append(csic);

			if (_args.AutoCommit)
			{
				_ci.InternalChangeSet_SetComment(_args.Comment);
				WriteChangeSet(csic);
				bSuccess = _ci.Commit(csic);
			}
			else
			{
				WriteChangeSet();
			}

			return bSuccess;
		}

		bool ProcessCommandRename(string strReposPath, string strNewName)
		{
			bool bSuccess = true;

			Login();

			ChangeSetItemColl csic = new ChangeSetItemColl();

			VaultClientFolder vcfolder = null;
			VaultClientFile vcfile = null;

			vcfolder = _ci.TreeCache.Repository.Root.FindFolderRecursive(strReposPath);
			if (vcfolder != null)
			{
				// ok, this is a folder
				ChangeSetItem_Rename csi = new ChangeSetItem_Rename(
					DateTime.Now,
					_args.Comment,
					String.Empty,
					vcfolder.ID,
					vcfolder.FullPath,
					strNewName);
				csic.Add(csi);
			}
			else
			{
				vcfile = _ci.TreeCache.Repository.Root.FindFileRecursive(strReposPath);
				if (vcfile != null)
				{
					// ok, this is a file
					ChangeSetItem_Rename csi = new ChangeSetItem_Rename(
						DateTime.Now,
						_args.Comment,
						String.Empty,
						vcfile.ID,
						vcfile.FullPath,
						strNewName);
					csic.Add(csi);
				}
				else
				{
					throw new Exception(string.Format("{0} does not exist", strReposPath));
				}
			}

			_ci.InternalChangeSet_Append(csic);

			if (_args.AutoCommit)
			{
				_ci.InternalChangeSet_SetComment(_args.Comment);
				WriteChangeSet(csic);
				bSuccess = _ci.Commit(csic);
			}
			else
			{
				WriteChangeSet();
			}

			return bSuccess;
		}

		bool ProcessCommandRenameLabel(string strReposPath, string strOldLabelName, string strNewLabelName)
		{
			bool bSuccess = true;
			long labelID = 0;
			long rootID = 0;
			string[] discoveredPaths;
			VaultClientTreeObject labelStructure=null;

			Login();
			_ci.Refresh();

			VaultClientTreeObject reposTreeObj = _ci.TreeCache.Repository.Root.FindTreeObjectRecursive(strReposPath);

			if(reposTreeObj == null)
			{
				throw new Exception(string.Format("\"{0}\" was not found in the repository", strReposPath));
			}

			try 
			{
				// There isn't a good API to get a label ID based on a label name, so just get the whole structure
				_ci.GetByLabel_GetStructure(strReposPath, strOldLabelName, ref labelID, "", out discoveredPaths, out labelStructure, out rootID);

				if (reposTreeObj.ID == rootID && labelID != 0)
				{
					// We found the label ID.  Now rename it.
					DateTime lastModified = DateTime.Now;
					int indexFailed;
					string rootPathConflict;

					int ret = _ci.PromoteLabelItems(strReposPath, labelID, strNewLabelName, ref lastModified, 
						null, out indexFailed, out rootPathConflict);

					if (ret == VaultStatusCode.Success)
					{
						bSuccess=true;
					} 
					else if  (ret == VaultStatusCode.FailDuplicateLabel)
					{
						// FailDuplicateLabel requires some string formatting.
						throw new Exception(
							String.Format(VaultConnection.GetSoapExceptionMessage(ret), rootPathConflict));
					} 
					else 
					{
						throw new Exception(VaultConnection.GetSoapExceptionMessage(ret));
					}

				} 
				else 
				{
					throw new Exception(string.Format("Could not find label \"{0}\" created at item \"{1}\".  ", strOldLabelName, strReposPath));
				}
			}
			catch (Exception e)
			{
				if (labelStructure == null)
				{
					throw new Exception(string.Format("Could not find label \"{0}\" created at item \"{1}\".  ", strOldLabelName, strReposPath));
				} 
				else 
				{
					throw e;
				}
			}

			return bSuccess;
		}

		bool ProcessCommandShare(string strReposPath_From, string strReposPath_To)
		{
			bool bSuccess = true;
			
			Login();

			ChangeSetItemColl csic = new ChangeSetItemColl();

			VaultClientFolder vcfolder = null;
			VaultClientFile vcfile = null;

			vcfolder = _ci.TreeCache.Repository.Root.FindFolderRecursive(strReposPath_From);
			if (vcfolder != null)
			{
				// ok, this is a folder
				ChangeSetItem_Share csi = new ChangeSetItem_Share(
					DateTime.Now,
					_args.Comment,
					String.Empty,
					vcfolder.ID,
					vcfolder.FullPath,
					strReposPath_To);
				csic.Add(csi);
			}
			else
			{
				vcfile = _ci.TreeCache.Repository.Root.FindFileRecursive(strReposPath_From);
				if (vcfile != null)
				{
					// ok, this is a file
					ChangeSetItem_Share csi = new ChangeSetItem_Share(
						DateTime.Now,
						_args.Comment,
						String.Empty,
						vcfile.ID,
						vcfile.FullPath,
						strReposPath_To);
					csic.Add(csi);
				}
				else
				{
					throw new Exception(string.Format("{0} does not exist", strReposPath_From));
				}
			}

			_ci.InternalChangeSet_Append(csic);

			if (_args.AutoCommit)
			{
				_ci.InternalChangeSet_SetComment(_args.Comment);
				WriteChangeSet(csic);
				bSuccess = _ci.Commit(csic);
			}
			else
			{
				WriteChangeSet();
			}

			return bSuccess;
		}

		bool ProcessCommandUncloak(ArrayList strItemArray)
		{
			bool bSuccess = true;
			
			Login();
			_ci.Refresh();

			for (int i=0; i < strItemArray.Count; i++)
			{
				string strReposItem = (string) strItemArray[i];

				VaultClientFolder vcfolder = null;

				vcfolder = _ci.TreeCache.Repository.Root.FindFolderRecursive(strReposItem);
				if (vcfolder == null)
				{
					throw new UsageException(string.Format("{0} exists, but the UNCLOAK command can only be used on folders.", strReposItem));
				}
				else
				{
					_ci.CloakOrUnCloak(vcfolder.FullPath, false);
					_xml.WriteUserMessage(string.Format("Cloaked: {0}", vcfolder.FullPath));
				}
			}

			return bSuccess;
		}

		private bool ProcessCommandUndoChangeSetItem(int nChgSetID)
		{
			bool bSuccess = true;

			Login();

			if ( ValidateChangeSetItemID(nChgSetID) == false )
			{
				throw new UsageException(string.Format("Invalid ChangeSetItem ID: {0}.  Please use the LISTCHANGESET command to retrieve a valid ID.", nChgSetID));
			}

			// get the change set item by index - note
			// error checking should have been done at this point.
			ChangeSetItemColl csic = _ci.InternalChangeSet_GetItems(true);
			ChangeSetItem csi = csic[nChgSetID];

			// remove this item from the set.
			_ci.InternalChangeSet_Undo(csi);

			// write the new change set.
			WriteChangeSet();

			return bSuccess;
		}

		bool ProcessCommandUndoCheckout(string strReposPath)
		{
			bool bSuccess = true;

			Login();

			VaultClientFolder vcfolder = null;
			VaultClientFile vcfile = null;

			vcfolder = _ci.TreeCache.Repository.Root.FindFolderRecursive(strReposPath);
			if (vcfolder != null)
			{
				_ci.UndoCheckOut(vcfolder, _args.Recursive, _args.LocalCopy);
			}
			else
			{
				vcfile = _ci.TreeCache.Repository.Root.FindFileRecursive(strReposPath);

				if (vcfile != null)
				{
					if(_ci.GetWorkingFolder(vcfile.Parent) == null)
					{
						throw new Exception(string.Format("{0} does not have a working folder set", strReposPath));
					}

					_ci.UndoCheckOut(vcfile, _args.LocalCopy);
				}
				else
				{
					throw new Exception(string.Format("{0} does not exist", strReposPath));
				}
			}
			return bSuccess;
		}

		bool ProcessCommandUnPin(string strReposPath)
		{
			bool bSuccess = true;

			Login();

			ChangeSetItemColl csic = new ChangeSetItemColl();

			VaultClientFolder vcfolder = null;
			VaultClientFile vcfile = null;

			vcfolder = _ci.TreeCache.Repository.Root.FindFolderRecursive(strReposPath);
			if (vcfolder != null)
			{
				// ok, this is a folder
				ChangeSetItem_Unpin csi = new ChangeSetItem_Unpin(
					DateTime.Now,
					_args.Comment,
					String.Empty,
					vcfolder.ID,
					vcfolder.FullPath);
				csic.Add(csi);
			}
			else
			{
				vcfile = _ci.TreeCache.Repository.Root.FindFileRecursive(strReposPath);
				if (vcfile != null)
				{
					// ok, this is a file
					ChangeSetItem_Unpin csi = new ChangeSetItem_Unpin(
						DateTime.Now,
						_args.Comment,
						String.Empty,
						vcfile.ID,
						vcfile.FullPath);
					csic.Add(csi);
				}
				else
				{
					throw new Exception(string.Format("{0} does not exist", strReposPath));
				}
			}

			_ci.InternalChangeSet_Append(csic);

			if (_args.AutoCommit)
			{
				_ci.InternalChangeSet_SetComment(_args.Comment);
				WriteChangeSet(csic);
				bSuccess = _ci.Commit(csic);
			}
			else
			{
				WriteChangeSet();
			}

			return bSuccess;
		}

		public VaultCmdLineClient(Args args, XMLOutputWriter xml)
		{
			_args = args;
			_xml = xml;
		}

		VaultClientFolder GetFakeLabelParent(VaultClientFile file, string currentPath, string labelName)
		{
			VaultClientFolder fakeParent = new VaultClientFolder();

			System.Security.Cryptography.MD5 md5 = new System.Security.Cryptography.MD5CryptoServiceProvider();
			ASCIIEncoding enc = new ASCIIEncoding();
			
			byte[] currentPathBytes = enc.GetBytes(currentPath.ToLower());
			byte[] labelBytes = enc.GetBytes(labelName.ToLower());

			byte[] combinedBytes = new byte[currentPathBytes.Length + labelBytes.Length + 1];

			byte[] currentPathHash = md5.ComputeHash(currentPathBytes);
			byte[] labelHash = md5.ComputeHash(labelBytes);

			string currentPathMD5 = Convert.ToBase64String(currentPathHash);
			string labelMD5 = Convert.ToBase64String(labelHash);

			fakeParent.Name = string.Format("label:{0}:{1}", currentPathMD5, labelMD5);

			fakeParent.Files.Add(file);
			file.Parent = fakeParent;

			return fakeParent;
		}


		private void GetLabelTimeStamps(string item, string label1, string label2, out DateTime timestamp1, 
			out DateTime timestamp2)
		{

			// TODO: This is a very inefficient way to get the label timestamps (it is currently doing a
			// query on the folder and searching on the client side for labels that match).  It should
			// use ClientInstance.BeginLabelQuery() in order to get the dates the labels were applied on.


			timestamp1 = VaultDate.EmptyDate(); // rogue value
			timestamp2 = VaultDate.EmptyDate(); // rogue value

			VaultHistoryQueryRequest hq = new VaultHistoryQueryRequest();
			hq.Recursive = true;

			// search all dates
			hq.BeginDate = VaultDate.EmptyDate();
			hq.EndDate = VaultDate.EmptyDate();
			
			hq.RepID = _ci.ActiveRepositoryID;

			VaultClientFolder vcfolder = null;
			VaultClientFile vcfile = null;

			vcfolder = _ci.TreeCache.Repository.Root.FindFolderRecursive(item);
			if (vcfolder != null)
			{
				hq.TopName = vcfolder.FullPath;
				hq.TopID = vcfolder.ID;
				hq.IsFolder = true;
			}
			else
			{
				vcfile = _ci.TreeCache.Repository.Root.FindFileRecursive(item);
				if (vcfile != null)
				{
					hq.TopName = vcfile.FullPath;
					hq.TopID = vcfile.ID;
					hq.IsFolder = false;
				}
				else
				{
					throw new Exception(string.Format("{0} does not exist", item));
				}
			}

			int nRowsRetrieved = 0;
			string strQryToken = null;

			//_ci.Connection.HistoryBegin(hq, _args.HistoryRowLimit, ref nRowsRetrieved, ref strQryToken);
			_ci.Connection.HistoryBegin(hq, 10000, ref nRowsRetrieved, ref strQryToken);
			VaultHistoryItem[] histitems = null;
			_ci.Connection.HistoryFetch(strQryToken, 0, nRowsRetrieved-1, ref histitems);
			_ci.Connection.HistoryEnd(strQryToken);

			foreach (VaultHistoryItem hi in histitems)
			{
				// want labels only
				if ( hi.HistItemType == VaultHistoryType.Label )
				{
					if ( hi.MiscInfo1.ToLower().Equals(label1.ToLower()))
					{
						timestamp1 = hi.TxDate;
					}
					else if ( label2.Length > 0 && hi.MiscInfo1.ToLower().Equals(label2.ToLower()))
					{
						timestamp2 = hi.TxDate;
					}

					// bail out if we've found both already (or if we've found the first one 
					// and only 1 label requested )
					if ( timestamp1 != VaultDate.EmptyDate() && 
						(timestamp2 != VaultDate.EmptyDate() || label2.Length == 0 ) )
						break;
				}
			}

			// make sure we found all relevant labels
			if ( timestamp1 == VaultDate.EmptyDate() )
				throw new ArgumentException("Invalid label: " + label1);
			else if ( label2.Length > 0 && timestamp2 == VaultDate.EmptyDate() )
				throw new ArgumentException("Invalid label: " + label2);

			return;
		}

		private string TranslateActionToString(VaultHistoryItem item)
		{
			byte itemnum = (byte)item.HistItemType;
			string descr = "";
			switch (itemnum)
			{
					#region actions
				case VaultHistoryType.Added:
					descr = "Add";
					break;
				case VaultHistoryType.BranchedFrom:
					descr = "Branch";
					break;
				case VaultHistoryType.BranchedFromShare:
					descr = "BranchedFromShare";
					break;
				case VaultHistoryType.BranchedFromItem:
					descr = "BranchFromItem";
					break;
				case VaultHistoryType.BranchedFromShareItem:
					descr = "BranchFromShareItem";
					break;
				case VaultHistoryType.CheckIn:
					descr = "CheckIn";
					break;
				case VaultHistoryType.Created:
					descr = "Create";
					break;
				case VaultHistoryType.Deleted:
					descr = "Delete";
					break;
				case VaultHistoryType.Label:
					descr = "Label";
					break;
				case VaultHistoryType.MovedFrom:
					descr = "MoveFrom";
					break;
				case VaultHistoryType.MovedTo:
					descr = "MoveTo";
					break;
				case VaultHistoryType.Obliterated:
					descr = "Obliterate";
					break;
				case VaultHistoryType.Pinned:
					descr = "Pin";
					break;
				case VaultHistoryType.PropertyChange:
					#region PropertyChange
					//we get this in the form E:x,M:y where E: marks the change for eol id, and M: marks the change for mergeable. 
					//x will be the new value of the eolid and y will be the new value of the mergeable setting.
					//x or y of 0 means no change and -1 means the value wasn't exp
					try
					{
						string s = item.MiscInfo1;
						string[] props = item.MiscInfo1.Split(',');
						string propName = string.Empty;
						string propValue = string.Empty;
						string[] propNames = null;
						string[] propValues = null;
						descr = "ChangeProperty";

						if (props != null)
						{
							propNames = new string[props.Length];
							propValues = new string[props.Length];
							for (int j=0; j<props.Length; j++)
							{
								string[] tmp = props[j].Split(':');
								if (int.Parse(tmp[1]) == -100)
								{
									propNames[j] = null;
									propValues[j] = null;
								}
								else 
								{
									if (tmp[0] == "E")
									{
										propNames[j] = "EOLConversion";
										switch (int.Parse(tmp[1]))
										{
											case VaultLib.VaultEOLForm.CR:
												propValues[j] = "CR";
												break;
											case VaultLib.VaultEOLForm.CRLF:
												propValues[j] = "CRLF";
												break;
											case VaultLib.VaultEOLForm.LF:
												propValues[j] = "LF";
												break;
											case VaultLib.VaultEOLForm.Native:
												propValues[j] = "Native";
												break;
											case VaultLib.VaultEOLForm.None:
												propValues[j] = "None";
												break;
										}										
									}

									else if (tmp[0] == "M")
									{
										propNames[j] = "FileType";
										switch (int.Parse(tmp[1]))
										{
											case VaultLib.VaultMergeable.Binary:
												propValues[j] = "Binary";
												break;
											case VaultLib.VaultMergeable.Mergeable:
												propValues[j] = "Mergeable";
												break;
											case VaultLib.VaultMergeable.UseSystemDefault:
												propValues[j] = "UseSystemDefault";
												break;														
										}										
									}								
								}
							}						
						}
							
						for (int n = 0; n < propNames.Length; n++)
						{
							if (propNames[n] != null)
							{
								if (descr.Length > 0)
									descr += ", ";

								descr += string.Format("{0} {1} {2}",  propNames[n], "changedTo", propValues[n]);
							}
						}
					}
					catch
					{
						descr = "PropertyChanged";
					}
					
					break;
					#endregion
				case VaultHistoryType.Renamed:
					descr = "RenameItem";
					break;
				case VaultHistoryType.RenamedItem:
					descr = "Rename";
					break;
				case VaultHistoryType.Rollback:
					descr = "Rollback";
					break;
				case VaultHistoryType.SharedTo:
					descr = "Share";
					break;
				case VaultHistoryType.Undeleted:
					descr = "Undelete";
					break;
				case VaultHistoryType.UnPinned:
					descr = "UnPin";
					break;
				case VaultHistoryType.Snapshot:
					descr = "Snapshot";
					break;
				case VaultHistoryType.SnapshotFrom:
					string snapshotName = string.Empty;
					//display only the name of the labeled folder, not the full path
					if (item.MiscInfo2 != null)
					{
						string[] pathItems = item.MiscInfo2.Split('/');
						snapshotName = pathItems[pathItems.Length-1];
					}
					descr = "Snapshot";
					break;
				case VaultHistoryType.SnapshotItem:
					descr = String.Format("{0} {1} {2} ", "Snapshot_", "From", item.MiscInfo1);
					break;
				default:
					descr = "Unknown";
					break;
					#endregion
			}
			return descr;
		}


		private int GetRepositoryId(string repositoryName)
		{
			VaultRepositoryInfo[] reps = null;
			//List all the repositories on the server.
			_ci.ListRepositories(ref reps);

			int repositoryId = -1;

			//Search for the one that we want.
			foreach (VaultRepositoryInfo r in reps)
			{
				if (String.Compare(r.RepName,repositoryName, true) == 0)
				{
					//This will load up the client side cache files and refresh the repository structure.
					//See http://support.sourcegear.com/viewtopic.php?t=6 for more on client side cache files.
					repositoryId = r.RepID;
					break;
				}
			}
			if (repositoryId == -1)
				throw new ArgumentException(string.Format("Repository {0} not found", repositoryName));

			return repositoryId;
		}

		private string DecodeUserRights(uint rights)
		{
			StringBuilder rightsString = new StringBuilder("---");
			if ( (rights & 1) != 0 )
				rightsString[0] = 'R';
			if ( (rights & 2) != 0 )
				rightsString[1] = 'C';
			if ( (rights & 4) != 0 )
				rightsString[2] = 'A';
			return rightsString.ToString();
		}



		[STAThread]
		static int Main(string[] args)
		{
			SimpleLogger.Log.ConfigureFromAppSettings("VaultCLC");

			int nRetCode = 0;
			bool bOK = false;

			// create the args.
			Args cmdlineargs = new Args(args);

			// create the output writer - based on the OUT option
			XMLOutputWriter xml = new XMLOutputWriter(cmdlineargs.Out);

			// create the cmd line client object
			VaultCmdLineClient cmdlineclient = new VaultCmdLineClient(cmdlineargs, xml);

			// pre process check on the original command
			if ( cmdlineargs.Error == false )
			{
				cmdlineclient.PreProcessCommand(cmdlineargs);
			}

			cmdlineargs.Out.WriteLine("<vault>");
			if(cmdlineargs.Error)
			{
				xml.Begin("error");
				xml.WriteContent(cmdlineargs.ErrorMessage);
				xml.End();
			}
			else
			{
				try
				{
					// if there is a batch, read each line until no more commands
					if ( cmdlineargs.InBatchMode == false )
					{
						// process the one command.
						bOK = cmdlineclient.ProcessCommand(cmdlineargs);
					}
					else
					{
						// while there are commands on the batch input stream
						// keep processing.
						string strCmd = null;
						while ( null != (strCmd = cmdlineargs.BatchTextReader.ReadLine()) )
						{
							if ( strCmd.Length > 0 )
							{
								Args batch_args = cmdlineargs.CreateBatchCmdArgs();

								// parse / and process the batch command
								batch_args.ParseBatchCommand(strCmd);
								bOK = cmdlineclient.ProcessCommand(batch_args);
								if ( bOK == false )
								{
									break;
								}
							}
						}
					}
				}
				catch (UsageException e)
				{
					xml.Begin("error");
					xml.WriteContent(e.Message);
					xml.End();
					bOK = false;
				}
				catch (Exception e)
				{
					xml.Begin("error");
					xml.WriteContent(e.Message);
					xml.End();
					xml.Begin("exception");
					xml.WriteContent(e.ToString());
					xml.End();
					bOK = false;
				}
				finally
				{
					// always force a logout here.
					cmdlineclient.Logout(true);
				}
			}

			xml.Begin("result");
			xml.AddPair("success", bOK);
			xml.End();

			cmdlineargs.Out.WriteLine("</vault>");

			// clean up any left over streams
			cmdlineargs.CloseOutputStream();
			cmdlineargs.CloseInputBatchStream();

			if ( bOK == false )
			{
				nRetCode = -1;
			}

			return nRetCode;
		}

	}


	public enum UnchangedHandler
	{
		LeaveCheckedOut,
		Checkin,
		UndoCheckout
	};




}
