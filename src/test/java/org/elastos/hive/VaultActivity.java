package org.elastos.hive;

import org.elastos.did.DIDDocument;
import org.elastos.did.exception.DIDException;
import org.elastos.hive.controller.FileController;
import org.elastos.hive.exception.HiveException;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class VaultActivity extends Activity {

	private Vault vault;

	@Override
	protected void onCreate(Application context) {
		super.onCreate(context);

		//初始化Application Context
		NodeConfig nodeConfig = getNodeConfig(NodeType.PRODUCTION);
		try {
			applicationContext = new ApplicationContext() {
				@Override
				public String getLocalDataDir() {
					return System.getProperty("user.dir") + File.separator + "data/store/" + File.separator + nodeConfig.storePath;
				}

				@Override
				public DIDDocument getAppInstanceDocument() {
					try {
						return context.appInstanceDid.getDocument();
					} catch (DIDException e) {
						e.printStackTrace();
					}
					return null;
				}

				@Override
				public CompletableFuture<String> getAuthorization(String jwtToken) {
					return CompletableFuture.supplyAsync(()-> userAuthorization(context, jwtToken));
				}
			};

			Client client = Client.createInstance(applicationContext);
			vault = client.getVault(nodeConfig.ownerDid, nodeConfig.provider).get();
		} catch (HiveException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}


	}

	@Override
	protected void onResume(Application context) {
		super.onResume(context);
		registerController(FileController.newInstance(vault.getFiles()));
	}

}
