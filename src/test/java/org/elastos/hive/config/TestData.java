package org.elastos.hive.config;

import org.elastos.did.adapter.DummyAdapter;
import org.elastos.did.exception.DIDException;
import org.elastos.hive.Application;
import org.elastos.hive.Client;
import org.elastos.hive.Utils;
import org.elastos.hive.didhelper.DApp;
import org.elastos.hive.didhelper.DIDApp;
import org.elastos.hive.exception.HiveException;

public class TestData {

	public DApp appInstanceDid;

	public DIDApp userDid = null;

	public Client client;

	private DummyAdapter adapter;

	public Application.NetType netType;


	public void init() throws HiveException, DIDException {
		adapter = new DummyAdapter();
		netType = Application.NetType.TEST_NET;

		Client.setupResolver((netType == Application.NetType.MAIN_NET)?"http://api.elastos.io:20606":"http://api.elastos.io:21606", "data/didCache");

		AppConfig appConfig = AppConfig.deserialize(Utils.getConfigure((netType == Application.NetType.MAIN_NET)?"MainNetApp.conf":"TestNetApp.conf"));
		appInstanceDid = new DApp(appConfig.name(), appConfig.mnemonic(), adapter, appConfig.passPhrase(), appConfig.storepass());

		UserConfig userConfig = UserConfig.deserialize(Utils.getConfigure((netType == Application.NetType.MAIN_NET)?"MainNetUser.conf":"TestNetUser.conf"));
		userDid = new DIDApp(userConfig.name(), userConfig.mnemonic(), adapter, userConfig.passPhrase(), userConfig.storePass());
	}




	public enum NetType {
		MAIN_NET,
		TEST_NET,
	}


}
