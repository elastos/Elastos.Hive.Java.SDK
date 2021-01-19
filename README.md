Elastos Hive Java SDK
===================

[![Build Status](https://travis-ci.com/elastos/Elastos.NET.Hive.Java.SDK.svg?branch=master)](https://travis-ci.com/elastos/Elastos.NET.Hive.Java.SDK)

## Summary

Elastos Hive is an essential service infrastructure as a decentralized network of Hive nodes presenting data storage capabilities to dApps. And the Java SDK provides a group of APIs for Elastos dApps to easily access and store application data to remote Vault services on Hive nodes with the following features:

- Upload/download files;
- Structured data object access and store onto MongoDB;
- Key/Values (Not supported yet);
- Customized Scripting to select other users to read or contribute the data.

Elastos Hive will keep the promise that **users remain in full control of their own data** and committing the practice on it.

## Build from source
Preparing with the developer tool **git**, and then run the following commands to clone the source:

```shell
$ git clone https://github.com/elastos/Elastos.NET.Hive.Java.SDK
$ cd Elastos.NET.Hive.Java.SDK
$ open -a Elipse .
```

Then open the **Eclipse** the build the project and run the test cases.  You also can run the instruction commands in the next each sections.

With the simple command below, build the whole project and run the test cases:

```shell
$ ./gradlew build 
```

With the option **-x** , try to build the project without running the test cases.
```shell
$ ./gradlew build -x test
```

Or just directly run the test cases:

```
$ ./gradlew test
```

## Build API Docs

Run the command below to build the APIs document, the output of documents would be generated at **doc** subdirectory. 

```shell
$ ./gradlew createjavadoc
$ ls -a doc
.	allclasses-noframe.html	help-doc.html org	overview-tree.html	serialized-form.html
..	constant-values.html	index-all.html overview-frame.html	package-list		stylesheet.css
allclasses-frame.html	deprecated-list.html index.html overview-summary.html	script.js
```

Then open **index.html** with the browser, you can see the whole APIs document there.

## How to use APIs

Here is a brief introduction about [How-to use the APIs](./HOW_TO_USE_APIS.md) , please read it carefully and feedback as issues. 

## Thanks
Sincerely thanks to all teams and projects that we relies on directly or indirectly.

## Contributing
We welcome contributions to the Elastos Hive Java Project.

## License
MIT
