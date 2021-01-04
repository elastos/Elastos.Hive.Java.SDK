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
public class MockAppContext implements HiveContext {
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

String CompletableFuture<Files> files =
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



##### 3. Upload and download file in streaming mode

With the capibility interface **files**, you can upload the specific file to your vault service, or download one file from the vault service:

Here is the simple snippet sample of uploading image:

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

While you can download it with the sample below:

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

#### Creating a specific collection

There is a **database** capability interface to the **vault** instance. Here is the sample code to describe how to create a **database** interface instance and use it to access and store the structured-data.

```java
CompletableFuture.supplyAsync(() -> {
  	Database database = vault.getDatabase();
  	database.createCollection("samples", null);
  	return database;
}).thenComposeAsync((database) -> {
  	ObjectNode node = JsonNodeFactory.instance.objectNode();
		node.put("foo", "value1");
		node.put("bar", "value2");

		InsertOptions options = new InsertOptions();
		options.bypassDocumentValidation(false)
					 .ordered(true);

  	database.inertOne("samples", node, options);
}).thenRun(() -> {
  	System.out.println("Successfull inserted the document.");
  	System.out.println("Post processing here.");
});
```

