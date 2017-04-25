
SourceGear Vault Client API
2 January 2003
--------

Strictly speaking, this is currently an UNSUPPORTED API.  In
practical terms, this means:

1.  This API is not yet frozen.  It may change somewhat in
    future releases, perhaps incompatibly so.

2.  The documentation for this API is not yet complete.
    Minimal documentation exists, but for many portions of
    the API, the documentation is absent.

3.  We cannot provide private technical support for this API
    through our technical support team.  However, we do
    intend to answer questions on our public discussion
    mailing lists.


Question: "So why release this API at all if it is not
really supported?"

Many users will find this API to be helpful even with these
disclaimers.  We see no reason to prevent people from using
this API as long as we have provided enough information to
appropriately set your expectations.


Question: "Will this API be supported in the future?"

Yes.  It is certainly our plan that in a future release of
Vault, this API will be frozen, well-documented, and
supported.


Question: "Do you have a way of accessing the Vault Client
API from non-Windows platforms?"

Not yet, but we do plan to support this capability in the
future.


Question: "What are the files in this folder?"

    vault.exe

	The Vault command-line client.

    VaultClientOperationsLib.dll 
        
        The primary library for the Vault Client API.  We often call
        this library by the name "OpsLib", for short.

    VaultClientNetLib.dll
    VaultLib.dll
    VCDiff.dll
    SGDiff.dll

	These are additional libraries needed by OpsLib.

    VaultClientOperationsLib.xml
	
	This is the XML documentation file for OpsLib.  When you use
	VaultClientOperationsLib.dll within Visual Studio.NET, the IDE
	will use this file to provide Intellisense help.

    VaultCmdLineClient.cs

	The source code for the Vault command-line client.  This code
	effectively serves as sample code for how to use OpsLib.

    VaultClientOperationsLib.chm

	Documentation for the Vault Client API.

