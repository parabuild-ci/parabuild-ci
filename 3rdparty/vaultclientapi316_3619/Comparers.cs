using System;
using System.Collections;

using VaultLib;

namespace VaultCmdLineClient
{
	public class GroupComparer : IComparer
	{
		public GroupComparer ()
		{
		}

		int IComparer.Compare( Object x, Object y )  
		{
			if ( ! (x is VaultGroup) || ! (y is VaultGroup) )
				throw new InvalidCastException("One of objects supplied is not of the valid type.");

			VaultGroup item1 = (VaultGroup)x;
			VaultGroup item2 = (VaultGroup)y;

			return item1.Name.CompareTo(item2.Name);
		}

	}


	public class RightComparer : IComparer
	{
		public RightComparer  ()
		{
		}

		int IComparer.Compare( Object x, Object y )  
		{
			if ( ! (x is VaultFolderRightsItem) || ! (y is VaultFolderRightsItem) )
				throw new InvalidCastException("One of objects supplied is not of the valid type.");

			VaultFolderRightsItem item1 = (VaultFolderRightsItem)x;
			VaultFolderRightsItem item2 = (VaultFolderRightsItem)y;

			return item1.Path.CompareTo(item2.Path);
		}

	}


	public class ReverseHistoryItemComparer : IComparer
	{
		public ReverseHistoryItemComparer()
		{
		}

		int IComparer.Compare( Object x, Object y )  
		{
			if ( ! (x is VaultHistoryItem) || ! (y is VaultHistoryItem) )
				throw new InvalidCastException("One of objects supplied is not of the valid type.");

			VaultHistoryItem item1 = (VaultHistoryItem)x;
			VaultHistoryItem item2 = (VaultHistoryItem)y;

			return  item2.TxDate.CompareTo(item1.TxDate);
		}

	}

	public class UserItemComparer : IComparer
	{
		public UserItemComparer()
		{
		}

		int IComparer.Compare( Object x, Object y )  
		{
			if ( ! (x is VaultUser) || ! (y is VaultUser) )
				throw new InvalidCastException("One of objects supplied is not of the valid type.");

			VaultUser item1 = (VaultUser)x;
			VaultUser item2 = (VaultUser)y;

			return item1.Name.CompareTo(item2.Name);
		}

	}


}
