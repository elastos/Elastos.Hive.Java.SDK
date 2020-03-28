## How to use APIs

In order to get developers involved to have a brief overview of APIs,  here are several examples to show the usage of Hive APIs.

### Client

#### 1. Create a Client

The first API to use Hive SDK is to create a client object to expected cloud storage service. For example,  the following paragraph is to create to a client to OneDrive:

```Java
Client.Options options = new OneDriveOptions
		                    .Builder()
		                    .setStorePath(YOUR_STORE_PATH)
		                    .setClientId(YOUR_CLIENTID)
		                    .setRedirectUrl(YOUR_REDIRECTURL)
		                    .setAuthenticator(requestUrl -> {
		                        ...
		                        ...
		                    })
		                    .build();
client = Client.createInstance(options);
```

#### 2. Connect 

When you get a client object, you need to connect backend server, example for OneDrive. 

```java
client.connect();
```

#### 3. Disconnect

When you want to disconnect from your backend , follow these instructions:

```java
client.disconnect();
```

### Files

#### 1. Get Files API

After you connect backend server, you can get files api refer the following example:

```java
Files filesApi = client.getFiles();
```


#### 2. Put data(string/buffer/InputStream/Reader)
If you want to put data to the backend, you can refer to the following example:

```java
filesApi.put(YOUR_DATA, REMOTE_FILE_NAME)
			.thenAccept(v -> {
				//Do another things after putting data to the backend.
			});
```

#### 3. Size

Get remote file size from the backend, you can refer to the following example:

```java
filesApi.size(REMOTE_FILE_NAME)
			.thenAccept(length -> {
				//length is remote file size 
				//Do another things after get remote file size from the backend.
			});
```

#### 4. Get data(string/buffer/OutputStream/Writer)

Get remote file data from the backend. For example, you can get a string of file data from a backend, you can refer to the following example:

```java
filesApi.getAsString(REMOTE_FILE_NAME)
			.thenAccept(str ->{
				//str is remote file data
				//Do another things after getting a string of file data.
			});
```

#### 5. List files

List all backend files name, refer to the following example:

```java
filesApi.list()
			.thenAccept(list -> {
				//list is files name(ArrayList<String>)
				//Do another things after list all backend files name.
			});
```


#### 6. Delete file

Delete backend file, refer to the following example:

```java
filesApi.delete(REMOTE_FILE_NAME)
			.thenAccept(v -> {
				//Do another things after delete backend file.
			});
```

### KeyValues

#### 1. Get KeyValues API

Get KeyValues api, refer to the following example:

```java
KeyValues keyValues = client.getKeyValues();
```

#### 2. Put value(string/buffer)

Put a value to the backend, refer to the following example:

```java
keyValues.putValue(YOUR_KEY, YOUR_VALUE)
			.thenAccept(v ->{
				//Do another things after put a value to the backend.
			});
```


#### 3. Get value

Get value from the backend, refer to the following example:

```java
keyValues.getValues(YOUR_KEY)
			.thenAccept(list -> {
				//list is values(ArrayList<byte[])
				//Do another things after get value from the backend
			});
```

#### 4. Set value

Set a new value with the key to the backend, refer to the following example:

```java
keyValues.setValue(YOUR_KEY, YOUR_VALUE)
			.thenAccept(v -> {
				//Do another things after set value from the backend
			});
```

#### 5. Delete key

Delete key and value from the backend, refer to the following example:

```java
keyValues.deleteKey(YOUR_KEY)
			.thenAccept(v -> {
				//Do another things after delete key and value from the backend
			});
```


### IPFS

#### 1. Get IPFS API

Get KeyValues api, refer to the following example:

```java
IPFS ipfsAPIs = client.getIPFS();
```

#### 2. Put data(string/buffer/InputStream/Reader)

If you want to put data to the backend, you can refer to the following example:

```java
ipfsAPIs.put(YOUR_DATA)
			.thenAccept(str->{
				//str is remote file CID
				//Do another things after put data to the backend
			});
```

#### 3. Get data(string/buffer/InputStream/Reader)

Get remote file data from the backend. For example, you can get a string of file data from a backend, you can refer to the following example:

```java
ipfsAPIs.getAsString(YOUR_FILE_CID)
			.thenAccept(str -> {
				//str is remote file data
				//Do another things after get remote file data 
			});
```


#### 4.Size

Get remote file size from the backend, you can refer to the following example:

```java
ipfsAPIs.size(YOUR_FILE_CID)
			.thenAccept(length->{
				//length is remote file size
				//Do another things after get remote file size 			});
```

***More guide refer to APIDoc and Sample***

&nbsp;
