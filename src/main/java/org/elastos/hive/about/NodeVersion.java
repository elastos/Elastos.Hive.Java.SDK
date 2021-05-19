package org.elastos.hive.about;

public class NodeVersion {
	int major;
    int minor;
    int patch;

    public int getMajor() {
        return major;
    }

    public int getMinor() {
    	return minor;
    }

    public int getPatch() {
        return patch;
    }

    @Override
	public String toString() {
    	return String.format("%d.%d.%d", major, minor, patch);
    }
}
