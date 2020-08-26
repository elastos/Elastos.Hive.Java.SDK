package org.elastos.hive.vendor.vault;

import org.elastos.hive.Authenticator;
import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.interfaces.Database;
import org.elastos.hive.interfaces.KeyValues;
import org.elastos.hive.interfaces.Scripting;
import org.elastos.hive.interfaces.Files;

public class VaultClient extends Client {

    private Authenticator authenticator;
    private VaultAuthHelper authHelper;
    private Files files;
    private Database database;
    private Scripting scripting;

    VaultClient(Client.Options options) {
        VaultOptions opts = (VaultOptions) options;
        this.authHelper = new VaultAuthHelper(opts.nodeUrl(),
                opts.authToken(),
                opts.storePath(),
                opts.clientId(),
                opts.clientSecret(),
                opts.redirectURL(),
                VaultConstance.SCOPE);
        this.authenticator = opts.authenticator();

        this.files = new ClientFile(this.authHelper);
        this.database = new ClientDatabase(this.authHelper);
        this.scripting = new ClientScript(this.authHelper);
    }


    @Override
    public void connect() throws HiveException {
        try {
            authHelper.connectAsync(authenticator).get();
        } catch (Exception e) {
            throw new HiveException(e.getLocalizedMessage());
        }
    }

    @Override
    public void disconnect() {
        authHelper.dissConnect();
    }

    @Override
    public boolean isConnected() {
        return authHelper.getConnectState();
    }

    @Override
    public Files getFiles() {
        return this.files;
    }

    @Override
    public Database getDatabase() {
        return this.database;
    }

    @Override
    public KeyValues getKeyValues() {
        return null;
    }

    @Override
    public Scripting getScripting() {
        return this.scripting;
    }

}
