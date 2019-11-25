package org.elastos.hive;

public interface IHiveConnect {
    void connect(Authenticator authenticator) throws HiveException;
    void disConnect();
    void setEncryptKey(String encryptKey);
    <T extends HiveFile> T createHiveFile(String filename);
    <T extends HiveFile> T createHiveFile();
}
