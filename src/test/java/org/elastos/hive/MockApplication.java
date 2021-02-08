package org.elastos.hive;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.activites.VaultActivity;
import org.elastos.hive.didhelper.DApp;
import org.elastos.hive.exception.HiveException;

/**
 * Application of simulation upper layer
 * test入口
 */
public class MockApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			//TODO MainNet or testNet can be set here
			netType = NetType.MAIN_NET;
			Client.setupResolver((netType == NetType.MAIN_NET)?"http://api.elastos.io:20606":"http://api.elastos.io:21606", "data/didCache");

			AppConfig appConfig = getAppConfig(netType);
			try {
				appInstanceDid = new DApp(appConfig.name, appConfig.mnemonic, adapter, appConfig.phrasePass, appConfig.storepass);
			} catch (DIDException e) {
				e.printStackTrace();
			}
		} catch (HiveException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onResume() {
		startActivity(VaultActivity.class);
		return super.onResume(); //用于check参数
	}
}
