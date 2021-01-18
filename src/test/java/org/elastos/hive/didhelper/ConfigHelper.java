package org.elastos.hive.didhelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigHelper {

	public static Config getConfigInfo(String resourcePath) {
		InputStream input = ConfigHelper.class.getClassLoader().getResourceAsStream(resourcePath);

		Properties properties = new Properties();
		try {
			properties.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Config config = new Config();
		config.setUserDid(properties.getProperty("userDid"));
		config.setUserMn(properties.getProperty("userMn"));
		config.setUserName(properties.getProperty("userName"));
		config.setUserPhrasepass(properties.getProperty("userPhrasepass"));
		config.setUserStorepass(properties.getProperty("userStorepass"));

		config.setAppMn(properties.getProperty("appMn"));
		config.setAppName(properties.getProperty("appName"));
		config.setAppPhrasepass(properties.getProperty("appPhrasepass"));
		config.setAppStorePass(properties.getProperty("appStorePass"));

		config.targetDID(properties.getProperty("targetDID"));
		config.targetHost(properties.getProperty("targetHost"));

		String cache = System.getProperty("user.dir") + File.separator + "store/";
		config.setStorePath(cache + properties.getProperty("storePath"));
		config.setProvider(properties.getProperty("provider"));
		config.setResolverUrl(properties.getProperty("resolverUrl"));
		config.setOwnerDid(properties.getProperty("ownerDid"));

		return config;
	}

}
