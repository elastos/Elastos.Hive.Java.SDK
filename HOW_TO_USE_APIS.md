Developer Guide to use Hive Java SDK
==================================

### Integrating SDK into build system

The Hive Java SDK has been published as pure jar package to jCenter maven repository, which measn users can easily integrate it into their projects with the build systems like:

#### 1. Gradle

Just add the following snippet scripts into the dependencies  items **build.gradle**:

```
implementation 'org.elastos:hive:v2.0.0'
```

#### 2. Maven

Add the following snippet of dependency declaration in their **pom** scripts:

```
<dependency>
	<groupId>org.elastos</groupId>
	<artifactId>hive</artifactId>
	<version>v2.0.0</version>
	<type>pom</type>
</dependency>
```

### Examples to use APIs

It is noticed that Java SDK has been designed as a set of asynchronous APIs to improve the performance of your programs. Therefore, the CompletableFuture object was widely be used as a return value in most of each APIs in SDK.

##### 1. Generate client instance

The user need to define a class implementing the interface **HiveContext**:

```java
public class MockAppContext implements ApplicationContext {
        private DIDDocument appInstanceDoc;
        private String hiveDataDir;
        public MockAppContext(DIDDocument appInstanceDoc, String hiveDataDir) {
            this.appInstanceDoc = appInstanceDoc;
            this.hiveDataDir = hiveDataDir;
        }
        @Override
        public String getLocalDataDir() {
            return hiveDataDir;
        }
        @Override
        public DIDDocument getAppInstanceDocument() {
            return appInstanceDoc;
        }
        @Override
        public CompletableFuture<String> getAuthorization(String authChallengeJwt) {
          	System.out.println("Implement authorization flow here.");
          	System.out.println("Try to use intent get approval from DID dApp.");
            //...
        }
    }
```

With you have the class **MocAppContext**, then can create a **Client** instance with the sample below:

```java
Client client = Client.createInstance(new MockAppContext(appInstanceDoc, localData));
```

####  2. Acquire vault instance

With **Client**, the next step is to create a specific **vault** instance, and also can acquire the captabilty interface  that you require to use it to access data on the vault in the next. For instance here to get **Files** interface:

```java
String targetDID = TARGET-USER-DID;
String targetProvider = "https://hive1.trinity-tech.io"

CompletableFuture<Files> files =
  					client.getVault(targetDID, targetProvider)
  								.thenApply(vault -> {
                  		System.out.println("Provider address: ", vault.getProviderAddress());
                  		System.out.println("vault owner DID : ", vault.getOwnerDid());
                  		System.out.println("getAppInstanceDid:", vault.getAppInstanceDID);

                  		vault.getFiles());
                	});

assertNotNull(files.get());
```

or just to get the capability interfaces with the **vault** instance:

```java
Files         files = vault.getFiles();
Database   database = vault.getDatabase();
Scripting scripting = vault.getScripting();
Payment     payment = vault.getPayment();
```



### Files

#### 1. Upload file (Writer/OutputStream)
If you want to upload file to the backend, you can refer to the following example:

```java
filesApi.upload(remoteText, Writer.class).thenAccept(writer -> {
            //Do another things.
        });
```

#### 2. Download file（Reader/InputStream）

Get remote file from the backend, you can refer to the following example:

```java
filesApi.download(remoteText, Reader.class).thenAccept(reader -> {
            //Do another things
        });
```

#### 3. Delete file

Delete remote file, you can refer to the following example:

```java
filesApi.delete(dst).thenAccept(aBoolean -> {
            //Do another things.
        });
```

#### 4. Move files

Move/rename remote file, refer to the following example:

```java
filesApi.move(src, dst).thenAccept(aBoolean -> {
            //Do another things.
        });
```


#### 5. Copy file

Copy remote file, refer to the following example:

```java
filesApi.copy(src, dst).thenAccept(aBoolean -> {
            //Do another things.
        });
```


#### 6. Get file hash

Copy remote file hash, refer to the following example:

```java
filesApi.hash(dst).thenAccept(s -> {
            // Do another things.
        });
```


#### 7. List files

List remote files, refer to the following example:

```java
filesApi.list(rootPath).thenAccept(fileInfos -> {
                //Do another things.
            });
```


#### 8. Get file property

List remote files, refer to the following example:

```java
filesApi.stat(dst).thenAccept(fileInfo -> {
                //Do another things.
            });
```

### Database

#### 1. Create Collection

Create remote Collection, refer to the following example:

```java
database.createCollection(collectionName, null).thenAccept(aBoolean -> {
                //Do another things.
            });
```


#### 2. Delete Collection

Delete remote Collection, refer to the following example:

```java
database.deleteCollection(collectionName).thenAccept(aBoolean -> {
                //Do another things.
            });
```

#### 3. Insert(insertOne/insertMany) value

Insert value with doc and option to the backend, refer to the following example:

```java
database.insertOne(collectionName, docNode, insertOptions).thenAccept(insertResult -> {
                //Do another things.
            });
```

#### 4. Count Documents

Get document counts from the backend, refer to the following example:

```java
database.countDocuments(collectionName, filter, options).thenAccept(aLong -> {
                //Do another things.
            });
```


#### 5. Find(findOne/findMany) Document

Get document from the backend, refer to the following example:

```java
database.findOne(collectionName, query, findOptions).thenAccept(jsonNode -> {
                //Do another things.
            });
```


#### 6. Update(updateOne/updateMany) Document

Update remote document, refer to the following example:

```java
database.updateOne(collectionName, filter, update, updateOptions).thenAccept(updateResult -> {
                //Do another things.
            });
```

#### 7. Delete(deleteOne/deleteMany) Document

Delete remote document, refer to the following example:

```java
database.deleteOne(collectionName, filter, deleteOptions).thenAccept(deleteResult -> {
                //Do another things.
            });
```

### Scripting

#### 1. Register Scripting

Register scripting, refer to the following example:

```java
scripting.registerScript("script_name", new RawExecutable(json)).thenAccept(aBoolean -> {
                //Do another things.
            });
```


#### 2. Call Scripting(String/byte[]/JsonNode/Reader)

Call scripting, refer to the following example:

```java
scripting.call("script_name", String.class).thenAccept(s -> {
                //Do another things.
            });
```

***More guide refer to APIDoc and Sample***

&nbsp;

