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
files.upload("sample.png", OutputStream.class)
				.thenApply(outputStream -> {
					try {
						byte[] imageData = Utils.readImage("sample.png");
						outputStream.write(imageData);
						outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}).thenRun(() -> {
			System.out.println("Sample.png has been uploaded.");
			System.out.println("Post processing here.");
		});
}
```

#### 2. Download file（Reader/InputStream）

Get remote file from the backend, you can refer to the following example:

```java
files.download("sample.png", InputStream.class)
				.thenRun(inputStream -> {
					try {
						FileOutputStream outputStream;
						byte[] buffer = new byte[1024];
						int len = 0;

						outputStream = new FileOutputStream(new File("sample.png"));
						while ((len = inputStream.read(buffer)) != -1) {
							outputStream.write(buffer, 0, len);
						}
						outputStream.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				})
				.thenRun(() -> {
					System.out.println("Sample.png has been downloaded.");
					System.out.println("Post processing here");
				});
```

#### 3. Delete file

Delete remote file, you can refer to the following example:

```java
files.delete("sample.png").thenRun(() -> {
    System.out.println("Sample.png has been deleted.");
    System.out.println("Post processing here.");
});
```

#### 4. Move files

Move/rename remote file, refer to the following example:

```java
files.move("sample.png", "target/sample.png").thenRun(() -> {
			System.out.println("Sample.png has been moved.");
			System.out.println("Post processing here.");
		});
```


#### 5. Copy file

Copy remote file, refer to the following example:

```java
files.copy("sample.png", "target/sample.png").thenRun(() -> {
    System.out.println("Sample.png has been copied.");
    System.out.println("Post processing here.");
});
```


#### 6. Get file hash

Get remote file hash, refer to the following example:

```java
files.hash("sample.png").thenRun(() -> {
    System.out.println("Post processing here.");
});
```


#### 7. List files

List the files in the remote directory, refer to the following example:

```java
files.list("target").thenRun(() -> {
			System.out.println("Post processing here.");
		});
```


#### 8. Get file property

List remote files, refer to the following example:

```java
filesApi.stat("sample.png").thenRun(() -> {
    System.out.println("Post processing here.");
});
```

### Database

#### 1. Create Collection

Create remote Collection, refer to the following example:

```java
database.createCollection("samples", null).thenRun(() -> {
    System.out.println("Collection has been created.");
    System.out.println("Post processing here.");
});
```


#### 2. Delete Collection

Delete remote Collection, refer to the following example:

```java
database.deleteCollection("samples").thenRun(() -> {
    System.out.println("Collection has been deleted.");
    System.out.println("Post processing here.");
});
```

#### 3. Insert(insertOne/insertMany) value

Insert value with doc and option to the backend, refer to the following example:

```java
ObjectNode docNode = JsonNodeFactory.instance.objectNode();
docNode.put("foo", "value1");
docNode.put("bar", "value2");

InsertOptions insertOptions = new InsertOptions();
insertOptions.bypassDocumentValidation(false).ordered(true);

database.insertOne("samples", docNode, insertOptions).thenRun(() -> {
    System.out.println("Successful inserted the document.");
    System.out.println("Post processing here.");
});
```

#### 4. Count Documents

Get document counts from the backend, refer to the following example:

```java
ObjectNode filter = JsonNodeFactory.instance.objectNode();
    filter.put("foo", "value");

    CountOptions options = new CountOptions();
    options.limit(1).skip(0).maxTimeMS(1000000000);

    database.countDocuments("sample", filter, options).thenRun(() -> {
        System.out.println("Post processing here.");
    });
```


#### 5. Find(findOne/findMany) Document

Get document from the backend, refer to the following example:

```java
ObjectNode query = JsonNodeFactory.instance.objectNode();
query.put("foo", "value1");
query.put("bar", "value2");

FindOptions findOptions = new FindOptions();
findOptions.skip(0)
        .allowPartialResults(false)
        .returnKey(false)
        .batchSize(0)
        .projection(jsonToMap("{\"_id\": false}"));

database.findOne("sample", query, findOptions).thenRun(() ->
        System.out.println("Post processing here."));
```


#### 6. Update(updateOne/updateMany) Document

Update remote document, refer to the following example:

```java
ObjectNode filter = JsonNodeFactory.instance.objectNode();
filter.put("author", "john doe1");

String updateJson = "{\"$set\":{\"foo\":\"value1\",\"bar\":\"value2\"}}";
ObjectMapper objectMapper = new ObjectMapper();
JsonNode update = null;
try {
    update = objectMapper.readTree(updateJson);
} catch (Exception e) {
    e.printStackTrace();
}

UpdateOptions updateOptions = new UpdateOptions();
updateOptions.upsert(true).bypassDocumentValidation(false);

database.updateOne("sample", filter, update, updateOptions).thenRun(() -> {
    System.out.println("Successful inserted the document.");
    System.out.println("Post processing here.");
});
```

#### 7. Delete(deleteOne/deleteMany) Document

Delete remote document, refer to the following example:

```java
ObjectNode filter = JsonNodeFactory.instance.objectNode();
filter.put("foo", "value1");

database.deleteOne("sample", filter, null).thenRun(() -> {
    System.out.println("Successful deleted the document.");
    System.out.println("Post processing here.");
});
```

### Scripting

#### 1. Register Scripting

Register scripting, refer to the following example:

```java
JsonNode filter = JsonUtil.deserialize("{\"friends\":\"$callScripter_did\"}");
JsonNode options = JsonUtil.deserialize("{\"projection\":{\"_id\":false,\"name\":true}}");
Executable executable = new DbFindQuery("get_groups", "groups", filter, options);

scripting.registerScript("sample", executable, false, false).thenRun(() -> {
    System.out.println("Successful register the script.");
    System.out.println("Post processing here.");
});
```


#### 2. Call Scripting(String/byte[]/JsonNode/Reader)

Call scripting, refer to the following example:

```java
scripting.callScript("sample", null, null, String.class).thenRun(new Runnable() {
    @Override
    public void run() {
        System.out.println("Successful called the script.");
        System.out.println("Post processing here.");
    }
});
```

***More guide refer to APIDoc and Sample***

&nbsp;

