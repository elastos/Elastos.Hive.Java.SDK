package org.elastos.hive.vendor.vault;

import org.elastos.hive.Client;
import org.elastos.hive.Database;
import org.elastos.hive.Files;
import org.elastos.hive.Scripting;
import org.elastos.hive.oauth.Authenticator;

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
                opts.localDataPath(),
                opts.clientId(),
                opts.clientSecret(),
                opts.redirectURL(),
                VaultConstance.SCOPE);
        //this.authenticator = opts.authenticator();

        this.files = new ClientFile(this.authHelper);
        this.database = new ClientDatabase(this.authHelper);
        this.scripting = new ClientScript(this.authHelper);
    }


}
