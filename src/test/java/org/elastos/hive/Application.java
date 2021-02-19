package org.elastos.hive;

import org.elastos.did.adapter.DummyAdapter;
import org.elastos.hive.activites.Activity;
import org.elastos.hive.didhelper.DApp;
import org.elastos.hive.exception.ActivityNotFoundException;
import org.elastos.hive.exception.DAppNullException;
import org.elastos.hive.exception.DidNetTypeException;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Ignore
public class Application {

	/**
	 * 设置全局Application Context
	 */
	private Map<String, Activity> activityCache = new HashMap<>();
	public DummyAdapter adapter;
	public DApp appInstanceDid;
	public NetType netType;
	public Activity.NodeType nodeType;

	public void onCreate() {
		adapter = new DummyAdapter();
		activityCache.clear();
	}

	public boolean onResume() {
		if (activityCache.isEmpty()) {
			throw new ActivityNotFoundException("Please start activity in application");
		}

		if(netType == null) {
			throw new DidNetTypeException("Did net type is invalid");
		}

		if(appInstanceDid == null) {
			throw new DAppNullException("DApp should not be null");
		}

		return true;
	}

	public void onDestroy() {
		activityCache.clear();
		adapter = null;
		appInstanceDid = null;
	}

	protected <T extends Activity> void startActivity(Class<T> activityClass) {
		Constructor<T> constructor = null;
		try {
			constructor = activityClass.getConstructor();
			T activity = constructor.newInstance();
			activity.start(this);
			activityCache.put(activity.getClass().getSimpleName(), activity);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	public enum NetType {
		MAIN_NET,
		TEST_NET,
	}

	protected static class AppConfig {
		protected String name;
		protected String mnemonic;
		protected String phrasePass;
		protected String storepass;

		public static AppConfig create() {
			return new AppConfig();
		}

		public AppConfig setName(String name) {
			this.name = name;
			return this;
		}

		public AppConfig setMnemonic(String mnemonic) {
			this.mnemonic = mnemonic;
			return this;
		}

		public AppConfig setPhrasePass(String phrasePass) {
			this.phrasePass = phrasePass;
			return this;
		}

		public AppConfig setStorepass(String storepass) {
			this.storepass = storepass;
			return this;
		}
	}

	protected AppConfig getAppConfig(NetType type) {
		String fileName;
		switch (type) {
			case MAIN_NET:
				fileName = "MainNetApp.conf";
				break;
			case TEST_NET:
				fileName = "TestNetApp.conf";
				break;
			default:
				throw new IllegalArgumentException("App type is invalid");
		}
		Properties properties = Utils.getProperties(fileName);

		return AppConfig.create()
				.setMnemonic(properties.getProperty("appMn"))
				.setName(properties.getProperty("appName"))
				.setPhrasePass(properties.getProperty("appPhrasePass"))
				.setStorepass(properties.getProperty("appStorePass"));
	}

	@Test
	public void engine() {
		onCreate();
		onResume();
		onDestroy();
	}

	@BeforeClass
	public static void logo() {
		Logger.hive();
	}

}
