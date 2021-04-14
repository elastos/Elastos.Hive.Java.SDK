# Hive SDK Roles

As Hive environment design, some roles has been involved and have different responsibilities.

## Owner Use Vault

Generally, any user can subscribe to a vault on the preferred Hive node (or service), then the user can store all persistent application data in that vault while still keeping complete control of data.

The developers would utilize Hive SDKs (Java/Swift) to develop the applications to store (access) users' data onto (from) their vaults.  There are two types of application data supported:

-  Files;
-  Structured data, similar to data in JSON format.

The simple figure below describes the relationship among users, Hive SDK (application),  Vault service, and backup service.

![title](img/roles_ower_use_vault.png)

A user can have multiple DIDs, and each DID represents an avatar of user himself.  The user would subscribe to a vault via the specific DID on the remote preferred Hive node.  Therefore, we would call user the owner of that vault in the specific user DID context. 

To ensure all data in the vault have its copy at all times, the user can subscribe to a backup service on another Hive node.  The backup would be used to keep a full copy of data in the vault with recent updates periodically.

## Scripting Service

ScriptingService is used for other user (called caller) or other application access owner's vault data. It looks like this:

![title](img/roles_scripting_service.png)

For caller, owner user did and relating app did are called target user did and target app did which are required for caller to access owner's vault data.

There are some scenarios to access owner's data.

1. Caller is owner and owns permission and application is another one.
2. Caller is another user and owns permission and application is owner application.
3. Caller is another user and owns permission and application is another application.
4. Vault supports anonymous access and caller is other user and application is another one.
