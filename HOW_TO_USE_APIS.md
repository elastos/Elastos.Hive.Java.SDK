## How to use APIs

In order to get developers involved to have a brief overview of APIs,  here are several examples to show the usage of Hive APIs.

### Client

#### 1. Create a Client

The first API to use Hive SDK is to create a client object to expected cloud storage service. For example,  the following paragraph is to create to a client to OneDrive:

```Java
OAuthEntry entry = new OAuthEntry(YOUR-CLIENT-ID, YOUR-SCOPE, YOUR-REIRECTURL);
Paramter  params = new OneDriveParameter(entry , YOUR-DATA-DIR);
Client client = Client.createInstance(params);
```

#### 2. Login 

When you get a client object, you need to get login with user's authorisation before calling any other APIs, example for OneDrive.  And remember that an extend class has to be implement based on interface **Authenticator** before calling **login** API.

```java
// Supposed having a client object.
Authenticator authenticator = new YOUR-OWN-AUTHENTICATOR();
try {
   client.login(authenticator);
} catch (HiveException e) {
   e.printstack();
}
```

#### 3. Get drive

As long as you get user's authorisation,  then you can follow the example to get a default drive object and to handle it for your own purpose:

```java
// Supposed having a client object.
client.getDefaultDrive()
      .thenAccept(drive ->  {
        // Here is your code to use drive.
      });
```

#### 4. Logout

When you want to log out of your account after all your backend file operations, follow these instructions:

```java
// Supposed having a client object.
try {
	client.logout();
} catch (HiveException e) {
   e.printstack();
}
```

### Drive

#### 1. Get directory

After you get the drive, if you want to get the folder in the backend, you can refer to the following example:

```java
// Supposed having a drive object.
drive.getDirectory(YOUR-DIR-PATH)
	.thenAccept(directory -> {
        // Here is your code to use directory.
	});
```

#### 2. Get file
if you want to get the file in the backend, you can refer to the following example:

```java
// Supposed having a drive object.
drive.getFile(YOUR-FILE-PATH)
	.thenAccept(file -> {
        // Here is your code to use file.
	});
```

#### 3. Create directory 

You can also create a directory directly using drive, just like that:

```java
// Supposed having a drive object.
drive.createDirectory(YOUR-DIR-PATH)
	.thenAccept(directory -> {
        // Here is your code to use directory.
	});
```

#### 4. Create file

Or use drive to create a directory, just like that:

```java
// Supposed having a drive object.
drive.createFile(YOUR-FILE-PATH)
	.thenAccept(file -> {
		// Here is your code to use file.
	});
```

### Directory

#### 1. Copy a directory

After you have an instance of directory, you can copy a directory:

```java
// Supposed having a directory object.
directory.copyTo(YOUR-DIR-PATH)
	.thenAccept(v ->{
		//Do another things after copy a directory.
    });

```

#### 2. Move a directory

Or move the directory, as in the following example:

```java
// Supposed having a directory object.
directory.moveTo(YOUR-DIR-PATH)
    .thenAccept(v ->{
		//Do another things after Move a directory.
    });
```


#### 3. Delete a directory

Or refer to the implementation below to delete a directory:

```java
// Supposed having a directory object.
directory.deleteItem()
    .thenAccept(v ->{
        //Do another things after delete a directory.
    });
```

#### 4. List files

You can also use directory to list all subdirectories in the current directory:

```java
// Supposed having a directory object.
directory.getChildren()
    .thenAccept(children -> {
        //List children for current directory
    });
```

### File

When you have a file instance, you can do file related operations, such as copying a file:

#### 1. Copy a file

```java
// Supposed having a file object.
file.copyTo(YOUR-FILE-PATH)
    .thenAccept(v ->{
		//Do another things after copy a file.
    });
```



#### 2. Move a file

You can also use the following example to move files around:

```java
// Supposed having a file object.
file.moveTo(YOUR-FILE-PATH)
    .thenAccept(v ->{
		//Do another things after Move a file.
    });

```

#### 3. Delete a file

If you want to delete files, you can refer to the following example：

```java
// Supposed having a file object.
file.deleteItem()
    .thenAccept(v ->{
        //Do another things after delete a file.
    });
```


#### 4. Read data

When you want to read a file from the background, you can use the following example.

```java
// Supposed having a file object.
ByteBuffer readBuf = ByteBuffer.allocate(YOUR-BUFFER-LENGTH);

file.read(readBuf)
        .thenAccept(length -> {
			//TODO
        });

```

#### 5. Write data

If you have some data that you want to store in the backend,you need to call the file.write interface to write the data, and then call the file.commit interface to commit the changes,as shown in the following example:

```java
// Supposed having a file object.
// prepare Bytebuffer
ByteBuffer writeBuffer = YOUR-DATA-BUFFER ;

file.write(writeBuffer)
		.thenAccept(length -> {
			// Do somthing after write buffer
		});
file.commit()
		.thenAccept(v ->{
			// Do another things after commit 	
		});

```



***More guide refer to APIDoc and Sample***

&nbsp;
