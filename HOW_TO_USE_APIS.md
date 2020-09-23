### How to use APIs

In order to get developers involved to have a brief overview of APIs,  here are several examples to show the usage of Hive APIs.

### Client

#### 1. Create a Vault

The first API to use Hive SDK is to create a vault object to expected hive storage service. For example:

```Java
            Client.Options options = new Client.Options();
            options.setAuthenticationHandler(jwtToken -> {
                ...
                ...
            });
            options.setAuthenticationDIDDocument(YOUR_DID_DOC);
            options.setDIDResolverUrl(DID_RESOLVER_URL);
            options.setLocalDataPath(YOUR_STORE_PATH);
            // Optional, locally maps the given owner DID with the given vault address
            Client.setVaultProvider(OWNER_DID, PROVIDER_ADDRESS);
            client = Client.createInstance(options);

            Vault vault = client.getVault(OWNER_DID).get();
```

### Files

#### 1. Get Files API

After you get Vault instance, you can get files api refer the following example:

```java
Files filesApi = vault.getFiles();
```


#### 2. Upload file (Writer/OutputStream)
If you want to upload file to the backend, you can refer to the following example:

```java
filesApi.upload(remoteText, Writer.class).thenAccept(writer -> {
            //Do another things.
        });
```

#### 3. Download file（Reader/InputStream）

Get remote file from the backend, you can refer to the following example:

```java
filesApi.download(remoteText, Reader.class).thenAccept(reader -> {
            //Do another things
        });
```

#### 4. Delete file

Delete remote file, you can refer to the following example:

```java
filesApi.delete(dst).thenAccept(aBoolean -> {
            //Do another things.
        });
```

#### 5. Move files

Move/rename remote file, refer to the following example:

```java
filesApi.move(src, dst).thenAccept(aBoolean -> {
            //Do another things.
        });
```


#### 6. Copy file

Copy remote file, refer to the following example:

```java
filesApi.copy(src, dst).thenAccept(aBoolean -> {
            //Do another things.
        });
```


#### 7. Get file hash

Copy remote file hash, refer to the following example:

```java
filesApi.hash(dst).thenAccept(s -> {
            // Do another things.
        });
```


#### 8. List files

List remote files, refer to the following example:

```java
filesApi.list(rootPath).thenAccept(fileInfos -> {
                //Do another things.
            });
```


#### 9. Get file property

List remote files, refer to the following example:

```java
filesApi.stat(dst).thenAccept(fileInfo -> {
                //Do another things.
            });
```

### Database

#### 1. Get Database API

Get Database api, refer to the following example:

```java
Database database = vault.getDatabase();
```

#### 2. Create Collection

Create remote Collection, refer to the following example:

```java
database.createCollection(collectionName, null).thenAccept(aBoolean -> {
                //Do another things.
            });
```


#### 3. Delete Collection

Delete remote Collection, refer to the following example:

```java
database.deleteCollection(collectionName).thenAccept(aBoolean -> {
                //Do another things.
            });
```

#### 4. Insert(insertOne/insertMany) value

Insert value with doc and option to the backend, refer to the following example:

```java
database.insertOne(collectionName, docNode, insertOptions).thenAccept(insertResult -> {
                //Do another things.
            });
```

#### 5. Count Documents

Get document counts from the backend, refer to the following example:

```java
database.countDocuments(collectionName, filter, options).thenAccept(aLong -> {
                //Do another things.
            });
```


#### 6. Find(findOne/findMany) Document

Get document from the backend, refer to the following example:

```java
database.findOne(collectionName, query, findOptions).thenAccept(jsonNode -> {
                //Do another things.
            });
```


#### 7. Update(updateOne/updateMany) Document

Update remote document, refer to the following example:

```java
database.updateOne(collectionName, filter, update, updateOptions).thenAccept(updateResult -> {
                //Do another things.
            });
```

#### 8. Delete(deleteOne/deleteMany) Document

Delete remote document, refer to the following example:

```java
database.deleteOne(collectionName, filter, deleteOptions).thenAccept(deleteResult -> {
                //Do another things.
            });
```

### Script

#### 1. Get Script API

Get Script api, refer to the following example:

```java
Script script = vault.getScript();
```


#### 2. Register Script

Register Script, refer to the following example:

```java
script.registerScript("script_name", new RawExecutable(json)).thenAccept(aBoolean -> {
                //Do another things.
            });
```


#### 3. Call Script(String/byte[]/JsonNode/Reader)

Call Script, refer to the following example:

```java
script.call("script_name", String.class).thenAccept(s -> {
                //Do another things.
            });
```

***More guide refer to APIDoc and Sample***

&nbsp;
