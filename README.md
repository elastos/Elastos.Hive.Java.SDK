Elastos.NET.Hive.Android.SDK
=====================

## Introduction

This repository implements a set of Java Client APIs to Hive IPFS Node/Cluster for Android platforms, where Hive IPFS/Cluster is leveraged from standard IPFS/Cluster, but totally seperated from standard IPFS/Cluster network with hardcode.

## Build

### 1. Import Android project

**Prerequisite**: Android Studio must be installed (Download from https://developer.android.com/studio/ ).

Option 1: Start Android Studio and select "Check out project from Version Control", then select Git and add the URL
https://github.com/elastos/Elastos.NET.Hive.Android.SDK , click clone.

Option 2: Download the Elastos.NET.Hive.Android.SDK with Git:
```shell
$ git clone https://github.com/elastos/Elastos.NET.Hive.Android.SDK
```
then start Android Studio and import the project with the option 'Open an existing Android Studio project'.


Select 'Create project from existing sources', click next, click finish.

Wait for all the import processes to finish.

### 2. Build Hive SDK

Build the project,debug or release.

### 3. Output

After building with success, the output distribution package named **org.elastos.hive-debug(release).aar**, it will be put under the directory:
```
app/build/outputs/aar
```

## Build Docs

Open **Tools** tab in Android Studio and click the **Generate JavaDoc...** item to generate the Java API document.

## Contribution

We welcome contributions to the Elastos Hive Android SDK Project.

## Acknowledgments

A sincere thank you to all teams and projects that we rely on directly or indirectly.

## License
This project is licensed under the terms of the [MIT license](https://github.com/elastos/Elastos.NET.Hive.Android.SDK/blob/master/LICENSE).
