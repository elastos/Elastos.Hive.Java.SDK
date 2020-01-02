Elastos Hive Java SDK
=============================

[![Build Status](https://travis-ci.com/elastos/Elastos.NET.Hive.Java.SDK.svg?)](https://travis-ci.com/elastos/Elastos.NET.Hive.Java.SDK)

## Summary

Elastos Hive Java SDK is a set of Java APIs as well as an uniform layer that could be utilised by Elastos dApps  to access  (or to store)  their files or datum from (or to) cloud driver backends, which currently includes  the list of following cloud drivers supported:

- OneDriver
- Hive IPFS
- ownCloud  on WebDav (Not implemented yet)

## Build from source

Use the following commands to download and build source code:

```shell
$ git clone https://github.com/elastos/Elastos.NET.Hive.Java.SDK
$ cd Elastos.NET.Hive.Java.SDK
$ open -a Elipse .
```

Then use the **Eclipse** to run **build** or **tests** tasks, or try to make build and tests with following samples:

#### 1. Build

Enter the following command to build project but ignore running tests:

```shell
$ ./gradlew build -x test
```

#### 2. Build & Run test

Enter the following command to build project and run tests:

```shell
$ ./gradlew build
```

#### 3. Run tests

Enter the following command to run tests:

```
$ ./gradlew test
```

#### 3. Build Docs

Enter the following command to build APIs document:

```
$ ./gradlew createjavadoc
```

&nbsp;

## [How to use APIs](./HOW_TO_USE_APIS.md)

&nbsp;

## Thanks

Sincerely thanks to all teams and projects that we relies on directly or indirectly.

## Contributing

We welcome contributions to the Elastos Hive Java Project.

## License

MIT
