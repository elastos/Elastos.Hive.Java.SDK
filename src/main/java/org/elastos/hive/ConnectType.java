package org.elastos.hive;

public enum ConnectType {
    IPFS,
    OneDrive,
    OwnCloud;

    public static ConnectType valueOf(int type) {
        switch (type) {
            case 1:
                return IPFS;
            case 11:
                return OneDrive;
            case 51:
                return OwnCloud;
            default:
                throw new IllegalArgumentException("Invalid Connection Type " + type);
        }
    }

    public int value() {
        switch (this) {
            case IPFS:
                return 1;
            case OneDrive:
                return 11;
            case OwnCloud:
                return 51;
            default:
                return 0;
        }
    }

    static String format(ConnectType type) {
        return String.format("%s[%d]", type.name(), type.value());
    }

    @Override
    public String toString() {
        return format(this);
    }
}
