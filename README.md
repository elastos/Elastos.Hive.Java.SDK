Elastos Hive Java SDK
=============================

[![Build Status](https://travis-ci.org/elastos/Elastos.NET.Hive.Java.SDK.svg?)](https://travis-ci.org/elastos/Elastos.NET.Hive.Java.SDK)

## Summary

Elastos Hive Java SDK is a set of Java APIs as well as an uniform layer that could be utilized by Elastos dApps  to access  (or store)  their files or datum from (or to) cloud driver backends, which currently includes  the list of following cloud drivers supported:

- OneDriver
- ownCloud  on WebDav
- Hive IPFS/Cluster

## Build from source

Use the following commands to download and build source code:

```shell
$ git clone https://github.com/elastos/Elastos.NET.Hive.Java.SDK
$ cd Elastos.NET.Hive.Java.SDK
$ open -a Elipse .
```

Then use the **Eclipse** to run **build** or **tests** tasks.

or run the command below:
#### Build without run test

Enter the following command to build project whithout run tests:

```
$ ./gradlew build -x test
```

#### Build & Run test

Enter the following command to build project and run tests:

```
$ ./gradlew build
```

#### Tests

Enter the following command to run tests:

```
$ ./gradlew test
```

#### Build Docs

Enter the following command to build Java doc:

```
$ ./gradlew createjavadoc
```

## How-to use APIs


#### Create a Client

Steps：

```
First create OAuthEntry,
Then create Parameter,
Finally create Client instance
```
For example:

```
//First create OAuthEntry
OAuthEntry entry = new OAuthEntry(APPID, SCOPE, REDIRECTURL);

//Then create parameter
Parameter parameter = new OneDriveParameter(entry, System.getProperty("user.dir"));

//Finally create client instance
Client client = Client.createInstance(parameter);
```

&nbsp;

#### Login

Steps:

```
First create Client instance
Second create Authenticator
Then run client.login
```

For example:

```
//First create Client instance
Client client = xxx ;

//Second create Authenticator
Authenticator authenticator = new TestAuthenticator();

//Then run client.login
client.login(authenticator);
```

&nbsp;

#### Get drive

Steps:

```
First create the Client instance by referring to the above
Then call login method
Finally call getDefaultDrive method
```

For example

```
//First create the Client instance by referring to the above
Client client = xxxxxx ;

//Then call login method
client.login(xxx);

//Finally call getDefaultDrive method
Drive drive = client.getDefaultDrive().get();
```

&nbsp;

#### Get directory
1.Get root directory from ***Drive***

Steps:

```
First create the Client instance by referring to the above
Then call login method by referring to the above
Third get Drive instance by referring to the above
Finally call drive.getRootDir method
```

For example:

```
//First create the Client instance by referring to the above
Client client = xxx ;

//Then call login method by referring to the above
client.login(xxx);

//Third get Drive instance by referring to the above
Drive drive = xxx;

//Finally call drive.getRootDir method
Directory root = drive.getRootDir().get();
```

2.Get directory with path from ***Drive***

Steps:

```
First create the Client instance by referring to the above
Then call login method by referring to the above
Third get Drive instance by referring to the above
Finally call drive.getDirectory method
```

For example:

```
//First create the Client instance by referring to the above
Client client = xxx ;

//Then call login method by referring to the above
client.login(xxx);

//Third get Drive instance by referring to the above
Drive drive = xxx;

//Finally call drive.getDirectory method
Directory directory = drive.getDirectory(path).get();
```


3.Get directory with path from ***Directory***

Steps:

```
First create the Client instance by referring to the above
Then call login method by referring to the above
Third get Drive instance by referring to the above
Fourth get Directory instance by referring to the above
Finally call directory.getDirectory method
```

For example:

```
//First create the Client instance by referring to the above
Client client = xxx ;

//Then call login method by referring to the above
client.login(xxx);

//Third get Drive by referring to the above
Drive drive = xxx;

//Fourth call drive.getRootDir or drive.getDirectory method
Directory root = drive.getRootDir().get();

//Finally call directory.getDirectory method
Directory directory = root.getDirectory(path).get();
```

&nbsp;

#### Get file
1.Get file with path from ***Drive***

Steps:

```
First create the Client instance by referring to the above
Then call login method by referring to the above
Third get Drive instance by referring to the above
Finally call drive.getFile method
```

For example:

```
//First create the Client instance by referring to the above
Client client = xxx ;

//Then call login method by referring to the above
client.login(xxx);

//Third get Drive instance by referring to the above
Drive drive = xxx;

//Finally call drive.getFile method
File file = drive.getFile(pathName).get();

```

2.Get file with path from ***Directory***

Steps:

```
First create the Client instance by referring to the above
Then call login method by referring to the above
Third create Drive instance by referring to the above
Fourth create Directory instance by referring to the above
Finally call directory.getFile method
```

For example:


```
//First create the Client instance by referring to the above
Client client = xxx ;

//Then call login method by referring to the above
client.login(xxx);

//Third get Drive by referring to the above
Drive drive = xxx;

//Fourth create Directory instance by referring to the above
Directory directory = xxx;

//Finally call directory.getFile method
File file = directory.getFile(path).get();
```

&nbsp;
#### Create directory 
1.Create directory with path from ***Drive***

Steps:

```
First create the Client instance by referring to the above
Then call login method by referring to the above
Third create Drive instance by referring to the above
Finally call drive.createDirectory method
```

For example:

```
//First create the Client instance by referring to the above
Client client = xxx ;

//Then call login method by referring to the above
client.login(xxx);

//Third get Drive by referring to the above
Drive drive = xxx;

//Finally call drive.createDirectory method
Directory child = drive.createDirectory(path).get();
```

2.Create directory with path from ***Directory***

Steps:

```
First create the Client instance by referring to the above
Then call login method by referring to the above
Third get Drive instance by referring to the above
Fourth get Directory instance by referring to the above
Finally call directory.createDirectory method
```

For example:

```
//First create the Client instance by referring to the above
Client client = xxx ;

//Then call login method by referring to the above
client.login(xxx);

//Third get Drive by referring to the above
Drive drive = xxx;

//Fourth create Directory instance by referring to the above
Directory directory = xxx;

//Finally call directory.createDirectory method
Directory child = drive.createDirectory(childDirectoryName).get();
```

&nbsp;
#### Create file

1.Create file with path from ***Drive***

Step

```
First create the Client instance by referring to the above
Then call login method by referring to the above
Third get Drive instance by referring to the above
Finally call drive.createFile method
```

For example

```
//First create the Client instance by referring to the above
Client client = xxx ;

//Then call login method by referring to the above
client.login(xxx);

//Third get Drive by referring to the above
Drive drive = xxx;

//Finally call drive.createFile method
File file = drive.createFile(pathName).get()
```


2.Create file with path from ***Directory***

Steps:

```
First create the Client instance by referring to the above
Then call login method by referring to the above
Third get Drive instance by referring to the above
Fourth get Directory instance by referring to the above
Finally call directory.createFile method
```

For example:

```
//First create the Client instance by referring to the above
Client client = xxx ;

//Then call login method by referring to the above
client.login(xxx);

//Third get Drive by referring to the above
Drive drive = xxx;

//Fourth get Directory instance by referring to the above
Directory directory = xxx;

//Finally call directory.createFile method
File file = directory.createFile(childDirectoryName).get();
```

&nbsp;

#### Copy a directory

Steps:

```
First create the Client instance by referring to the above
Then call login method by referring to the above
Third get Drive instance by referring to the above
Fourth get Directory instance by referring to the above
Finally call directory.copyTo method
```

For example:

```
//First create the Client instance by referring to the above
Client client = xxx ;

//Then call login method by referring to the above
client.login(xxx);

//Third get Drive by referring to the above
Drive drive = xxx;

//Fourth get Directory instance by referring to the above
Directory directory = xxx;

//Finally call directory.copyTo method
directory.copyTo(destPath).get();

```


&nbsp;

#### Copy a file

Steps:

```
First create the Client instance by referring to the above
Then call login method by referring to the above
Third get Drive instance by referring to the above
Fourth get File instance by referring to the above
Finally call file.copyTo method
```

For example:

```
//First create the Client instance by referring to the above
Client client = xxx ;

//Then call login method by referring to the above
client.login(xxx);

//Third get Drive by referring to the above
Drive drive = xxx;

//Fourth get File instance by referring to the above
File file = xxx;

//Finally call file.copyTo method
file.copyTo(destPath).get();
```


&nbsp;

#### Move a directory

Steps:

```
First create the Client instance by referring to the above
Then call login method by referring to the above
Third get Drive instance by referring to the above
Fourth get Directory instance by referring to the above
Finally call directory.moveTo method
```

For example:

```
//First create the Client instance by referring to the above
Client client = xxx ;

//Then call login method by referring to the above
client.login(xxx);

//Third get Drive by referring to the above
Drive drive = xxx;

//Fourth get Directory instance by referring to the above
Directory directory = xxx;

//Finally call directory.moveTo method
directory.moveTo(destPath).get();

```



&nbsp;

#### Move a file

Steps:

```
First create the Client instance by referring to the above
Then call login method by referring to the above
Third get Drive instance by referring to the above
Fourth get File instance by referring to the above
Finally call file.moveTo method
```

For example:

```
//First create the Client instance by referring to the above
Client client = xxx ;

//Then call login method by referring to the above
client.login(xxx);

//Third get Drive by referring to the above
Drive drive = xxx;

//Fourth get File instance by referring to the above
File file = xxx;

//Finally call file.copyTo method
file.moveTo(destPath).get();
```


&nbsp;

#### Delete a directory

Steps:

```
First create the Client instance by referring to the above
Then call login method by referring to the above
Third get Drive instance by referring to the above
Fourth get Directory instance by referring to the above
Finally call directory.deleteItem method
```

For example:

```
//First create the Client instance by referring to the above
Client client = xxx ;

//Then call login method by referring to the above
client.login(xxx);

//Third get Drive by referring to the above
Drive drive = xxx;

//Fourth get Directory instance by referring to the above
Directory directory = xxx;

//Finally call directory.deleteItem method
directory.deleteItem().get();
```

&nbsp;

#### Delete a file

Steps:

```
First create the Client instance by referring to the above
Then call login method by referring to the above
Third get Drive instance by referring to the above
Fourth get File instance by referring to the above
Finally call file.deleteItem method
```

For example:

```
//First create the Client instance by referring to the above
Client client = xxx ;

//Then call login method by referring to the above
client.login(xxx);

//Third get Drive by referring to the above
Drive drive = xxx;

//Fourth get File instance by referring to the above
File file = xxx;

//Finally call file.copyTo method
file.deleteItem().get();
```


&nbsp;

#### List files

Steps:

```
First create the Client instance by referring to the above
Then call login method by referring to the above
Third get Drive instance by referring to the above
Fourth get Directory instance by referring to the above
Finally call directory.getChildren method
```

For example:

```
//First create the Client instance by referring to the above
Client client = xxx ;

//Then call login method by referring to the above
client.login(xxx);

//Third get Drive by referring to the above
Drive drive = xxx;

//Fourth get Directory instance by referring to the above
Directory directory = xxx;

//Finally call directory.getChildren method
Children children = directory.getChildren().get();
```

&nbsp;

#### Read data

Steps:

```
First create the Client instance by referring to the above
Then call login method by referring to the above
Third get Drive instance by referring to the above
Fourth get File instance by referring to the above
Fifth create ByteBuffer
finally read data from backend file to ByteBuffer
```

For example:

```
//First create the Client instance by referring to the above
Client client = xxx ;

//Then call login method by referring to the above
client.login(xxx);

//Third get Drive by referring to the above
Drive drive = xxx;

//Fourth get File instance by referring to the above
File file = xxx;

//Fifth create ByteBuffer
ByteBuffer readBuf = ByteBuffer.allocate(100);

//finally read data from backend file to ByteBuffer
Length lenObj = file.read(readBuf).get();

```


&nbsp;

#### Write data

Steps:

```
First create the Client instance by referring to the above
Then call login method by referring to the above
Third get Drive instance by referring to the above
Fourth get File instance by referring to the above
Fifth create ByteBuffer
Sixth call file.write method 
Finally call file.commit method
```

For example:

```
//First create the Client instance by referring to the above
Client client = xxx ;

//Then call login method by referring to the above
client.login(xxx);

//Third get Drive by referring to the above
Drive drive = xxx;

//Fourth get File instance by referring to the above
File file = xxx;

//Fifth create ByteBuffer
ByteBuffer writeBuffer = xxx

//Sixth call file.write method 
file.write(writeBuffer).get();

//Finally call file.commit method
file.commit().get();
```



&nbsp;

#### Get info

1.Get ***Client*** info

Steps:

```
First create the Client instance by referring to the above
Then call login method by referring to the above
Finally call client.getInfo method
```

For example:

```
//First create the Client instance by referring to the above
Client client = xxx ;

//Then call login method by referring to the above
client.login(xxx);

//Finally call client.getInfo method
Client.Info info = client.getInfo().get();
```


2.Get ***Drive*** info

Steps:

```
First create the Client instance by referring to the above
Then call login method by referring to the above
Third get Drive instance by referring to the above
Finally call drive.getInfo method
```

For example:

```
//First create the Client instance by referring to the above
Client client = xxx ;

//Then call login method by referring to the above
client.login(xxx);

//Third get Drive by referring to the above
Drive drive = xxx;

//Finally call drive.getInfo method
Drive.Info info = drive.getInfo().get();
```

3.Get ***Directory*** info

Steps:

```
First create the Client instance by referring to the above
Then call login method by referring to the above
Third get Drive instance by referring to the above
Fourth get Directory instance by referring to the above
Finally call directory.getInfo method
```

For example:

```
//First create the Client instance by referring to the above
Client client = xxx ;

//Then call login method by referring to the above
client.login(xxx);

//Third get Drive by referring to the above
Drive drive = xxx;

//Fourth get Directory instance by referring to the above
Directory directory = xxx;

//Finally call directory.getInfo method
Directory.Info info = directory.getInfo().get();

```

4.Get ***File*** info

Steps:

```
First create the Client instance by referring to the above
Then call login method by referring to the above
Third get Drive by referring to the above
Fourth get File instance by referring to the above
Finally call file.getInfo method
```

For example:

```

//First create the Client instance by referring to the above
Client client = xxx ;

//Then call login method by referring to the above
client.login(xxx);

//Third get Drive by referring to the above
Drive drive = xxx;

//Fourth get File instance by referring to the above
File file = xxx;

//Finally call file.getInfo method
File.Info info = testFile.getInfo().get();
```

&nbsp;

#### Logout

Steps:

```
If client is "logined" , can call client.logout method
```

For example:

```
Client client = xxx ;

//If client is "logined" , can call client.logout method
client.logout();
```

&nbsp;

***More guide refer to APIDoc and Sample***

&nbsp;





## Thanks

Sincerely thanks to all teams and projects that we relies on directly or indirectly.

## Contributing

We welcome contributions to the Elastos Hive Java Project in many forms.

## License

MIT
