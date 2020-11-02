Elastos Hive Java SDK
===================

[![Build Status](https://travis-ci.com/elastos/Elastos.NET.Hive.Java.SDK.svg?branch=master)](https://travis-ci.com/elastos/Elastos.NET.Hive.Java.SDK)

## Summary

Elastos Hive is a basic service infrastructure that provides decentralised data storage capabilities to applications. And Elastos Hive Java SDK provides a set of Java APIs for applications to access/store vault data on Hive backend servers.

Elastos Hive currently is under heavy development, and plans to support with the following data objects in the near future:

- File storage
- Database
- Key-Values
- Scripting

Anyway, Elastos Hive will keep practising the promise of **users remain in full control of their own data**.

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

## How to use APIs
Here is a brief introduction to [APIs](./HOW_TO_USE_APIS.md) usage.

## Thanks
Sincerely thanks to all teams and projects that we relies on directly or indirectly.

## Contributing
We welcome contributions to the Elastos Hive Java Project.

## License
MIT
