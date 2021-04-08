package org.elastos.hive.service;

public class Version {
    private int major;
    private int minor;
    private int patch;

    public int getMajor() {
        return major;
    }

    public Version setMajor(int major) {
        this.major = major;
        return this;
    }

    public int getMinor() {
        return minor;
    }

    public Version setMinor(int minor) {
        this.minor = minor;
        return this;
    }

    public int getPatch() {
        return patch;
    }

    public Version setPatch(int patch) {
        this.patch = patch;
        return this;
    }
}
