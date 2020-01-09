package org.elastos.hive.result;

public class Value extends Result {
    private final byte[] data;

    public Value(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
