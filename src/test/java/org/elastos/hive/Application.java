package org.elastos.hive;

import org.elastos.hive.exception.ActivityNotFoundException;
import org.elastos.hive.exception.ContextNotSetException;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Ignore
public class Application {

	/**
	 * 设置全局Application Context
	 */
	protected ApplicationContext applicationContext;
	private Map<String, Activity> activityCache = new HashMap<>();
	/**
	 * 配置test case运行环境
	 */
	protected Type env = Type.PRODUCTION;


	public boolean onCreate() {
		activityCache.clear();
		if(applicationContext == null) {
			throw new ContextNotSetException("Application context not set");
		}
		//TODO 配置环境：product, develop, testing

		return true;
	}

	public boolean onResume() {
		if(activityCache.isEmpty()) {
			throw new ActivityNotFoundException("Please start activity in application");
		}

		return true;
	}

	public void onDestroy() {
		activityCache.clear();
		applicationContext = null;
	}

	protected <T extends Activity> void startActivity(Class<T> activityClass) {
		Constructor<T> constructor = null;
		try {
			constructor = activityClass.getConstructor();
			T activity = constructor.newInstance();
			activity.start(applicationContext);
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

	@Test
	public void engine() {
		if(onCreate()) {
			onResume();
		}
		onDestroy();
	}

	@BeforeClass
	public static void logo() {
		Logger.hive();
	}

	public enum Type {
		CROSS,
		DEVELOPING,
		PRODUCTION,
		TESTING
	}

}
