package org.elastos.hive;

import org.elastos.hive.vault.DatabaseClient;
import org.elastos.hive.vault.FileClient;
import org.elastos.hive.vault.ScriptClient;
import org.elastos.hive.vault.AuthHelper;

/**
 * Vault class
 *      Provide basic information of vault.
 *      Provide file, database, scripting, KeyValues instances
 */
public class Vault {

    private Files files;
    private Database database;
    private Scripting scripting;
    private KeyValues keyValues;

    private String vaultProvider;
    private String ownerDid;
    private AuthHelper authHelper;

    /**
     * Vault construction method
     *
     * @param authHelper
     *          sign in，authorize and cloud sync helper class instance
     * @param vaultProvider
     *          vault server provider address
     * @param ownerDid
     *          vault provider did
     */
    public Vault(AuthHelper authHelper, String vaultProvider, String ownerDid) {

        this.authHelper = authHelper;
        this.files = new FileClient(authHelper);
        this.database = new DatabaseClient(authHelper);
        this.scripting = new ScriptClient(authHelper);

        this.vaultProvider = vaultProvider;
        this.ownerDid = ownerDid;

        authHelper.connect();
    }

    /**
     *  get vault provider address
     * @return
     */
    public String getProviderAddress() {
        return this.vaultProvider;
    }

    /**
     * get vault owner did
     * @return
     */
    public String getOwnerDid() {
        return this.ownerDid;
    }

    /**
     * get application id
     * @return
     */
    public String getAppId() {
        return this.authHelper.getAppId();
    }

    /**
     * get application did
     * @return
     */
    public String getAppInstanceDid() {
        return this.authHelper.getAppInstanceDid();
    }

    /**
     * get user did
     * @return
     */
    public String getUserDid() {
        return this.authHelper.getUserDid();
    }

    /**
     * get vault's Database instance
     * @return
     */
    public Database getDatabase() {
        return this.database;
    }

    /**
     * get vault's Files instance
     * @return
     */
    public Files getFiles() {
        return this.files;
    }

    /**
     * get vault's KeyValues instance
     * @return
     */
    public KeyValues getKeyValues() {
        return this.keyValues;
    }

    /**
     * get vault's Scripting instance
     * @return
     */
    public Scripting getScripting() {
        return this.scripting;
    }
}

