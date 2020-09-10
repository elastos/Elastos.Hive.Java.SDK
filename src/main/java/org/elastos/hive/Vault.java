package org.elastos.hive;

import org.elastos.hive.vendor.vault.DatabaseClient;
import org.elastos.hive.vendor.vault.FileClient;
import org.elastos.hive.vendor.vault.ScriptClient;
import org.elastos.hive.vendor.vault.VaultAuthHelper;

import java.util.concurrent.ExecutionException;

public class Vault {

    private Files files;
    private Database database;
    private Scripting scripting;
    private KeyValues keyValues;

    private String vaultProvider;
    private String ownerDid;
    private VaultAuthHelper authHelper;

    public Vault(VaultAuthHelper authHelper, String vaultProvider, String ownerDid) throws ExecutionException, InterruptedException {

        this.authHelper = authHelper;
        this.files = new FileClient(authHelper);
        this.database = new DatabaseClient(authHelper);
        this.scripting = new ScriptClient(authHelper);

        this.vaultProvider = vaultProvider;
        this.ownerDid = ownerDid;

        authHelper.checkValid().get();
    }

    public String getProviderAddress() {
        return this.vaultProvider;
    }

    public String getOwnerDid() {
        return this.ownerDid;
    }

    public String getAppDid() {
        return this.authHelper.getAppId();
    }

    public String getAppInstanceDid() {
        return this.authHelper.getAppInstanceDid();
    }

    public String getUserDid() {
        return this.authHelper.getUserDid();
    }

    public Database getDatabase() {
        return this.database;
    }

    public Files getFiles() {
        return this.files;
    }

    public KeyValues getKeyValues() {
        return this.keyValues;
    }

    public Scripting getScripting() {
        return this.scripting;
    }
}

