package org.elastos.hive;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class HiveTest {
	private static String HIVE_UID = null;
	private static final String TAG = "HiveTest";
	private static String APP_PATH;
	private final static String SLASH = "/";
	private static final String[] PUBIPS = {
			"18.217.147.205", //Hive1
			"18.219.53.133",  //Hive2
			"3.16.202.140",   //Hive3
			"52.83.119.110",  //Hive4
			"52.83.159.189",  //Hive5

//			"149.28.244.92", //private
//			"45.32.197.17",  //private
	};

	//TODO
	private static final boolean Using_different_IP = true;

	@Test
	public void testHiveFile() {
		try {
			resetPublicIP();

			//fileAdd
			String fileName = "testHiveFile.txt";
			String content = "Elastos Hive android testHiveFile 2019";
			String filePath = getFilePath(fileName, content);
			JSONObject json = Node.fileAdd(filePath);
			//"Name":"testHiveFile.txt","Hash":"Qmf44869vys9REn8ifPf2NfYbkfUWzoxdU63XRctaY9PfE","Size":"48"
			String value = json.getString("Name");
			assertEquals(fileName, value);

			String hash = json.getString("Hash");
			assertTrue(hash != null && hash.length() > 0);

			//fileCat: same ip
			value = new String(Node.fileCat(hash));
			assertEquals(content, value);

			//fileCat: different ip
			resetPublicIP();
			value = new String(Node.fileCat(hash));
			assertEquals(content, value);

			try {
				Node.fileCat(hash+"Invalid");
				fail();
			}
			catch (Exception e){
				e.printStackTrace();
			}

			//fileLs
			resetPublicIP();
			json = Node.fileLs(hash);
			try {
				Node.fileLs(hash+"Invalid");
				fail();
			}
			catch (Exception e){
				e.printStackTrace();
			}

//			{
//				"Arguments":
//				{"QmevkbR6pv3sogQCNvAQb33TamMMkY3NGgzBKGH7AGowFq":"QmevkbR6pv3sogQCNvAQb33TamMMkY3NGgzBKGH7AGowFq"},
//				"Objects":
//				{"QmevkbR6pv3sogQCNvAQb33TamMMkY3NGgzBKGH7AGowFq":
//					{"Hash":"QmevkbR6pv3sogQCNvAQb33TamMMkY3NGgzBKGH7AGowFq","Size":59,"Type":"File","Links":null}
//				}
//			}
			JSONObject objects = json.getJSONObject("Objects");
			JSONObject item = objects.getJSONObject(hash);
			value = item.getString("Hash");
			assertEquals(hash, value);

			//file size.
			value = item.getString("Size");
			assertEquals(content.length(), Integer.parseInt(value));

			//fileGet: same ip
			value = new String(Node.fileGet(hash));
			assertEquals(content, value);

			//fileGet: different ip
			resetPublicIP();
			value = new String(Node.fileGet(hash));
			assertEquals(content, value);

			try {
				Node.fileGet(hash+"Invalid");
				fail();
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testHiveFilesCp() {
		try {
			//1. Add a file
			String fileName = "testHiveFilesCp.txt";
			String content = "Elastos Hive android testHiveFilesCp 2019 time: " + System.currentTimeMillis();
			String filePath = getFilePath(fileName, content);
			JSONObject json = Node.fileAdd(filePath);
			String hash = json.getString("Hash");

			//2. mkdir
			resetPublicIPAndLogin();
			String dir = "testHiveFilesCp_" + System.currentTimeMillis();
			Node.filesMkdir(HIVE_UID, SLASH + dir, true);

			//3. copy file under the dir.
			resetPublicIPAndLogin();
			String name = "/cpFile_" + System.currentTimeMillis();
			String destPath = SLASH + dir + SLASH + name;
			Node.filesCp(HIVE_UID, hash, destPath);

			//4. check the file content.
			resetPublicIPAndLogin();
			String value = new String(Node.filesRead(HIVE_UID, destPath));
			assertEquals(content, value);

			Node.filesFlush(HIVE_UID);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Ignore
	public void testHiveFilesMv() {
		try {
			try {
				//1. Add a file
				resetPublicIP();
				String fileName = "testHiveFilesMv.txt";
				String content = "Elastos Hive android testHiveFilesMv 2019 time: " + System.currentTimeMillis();
				String filePath = getFilePath(fileName, content);
				JSONObject json = Node.fileAdd(filePath);
				String hash = json.getString("Hash");

				//2. mkdir
				resetPublicIP();
				String dir = "testHiveFilesCp_" + System.currentTimeMillis();
				Node.filesMkdir(HIVE_UID, SLASH + dir, true);

				//3. move file under the dir.
				resetPublicIP();
				String destPath = SLASH + dir;
				Node.filesMv(HIVE_UID, hash, destPath);

				//4. check the file content.
				resetPublicIP();
				String value = new String(Node.filesRead(HIVE_UID, destPath));
				assertEquals(content, value);

				//5. check the source file: no file
				try {
					resetPublicIP();
					Node.filesRead(HIVE_UID, hash);
					fail();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				fail();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testHiveFilesRm() {
		try {
			resetPublicIPAndLogin();

			//1. filesMkdir
			final int COUNT = 5;
			String[] dirs = new String[COUNT];
			for (int i = 0; i < COUNT; i++) {
				dirs[i] = "testHiveFilesRm_" + System.currentTimeMillis();
				resetPublicIPAndLogin();

				Log.d(TAG, String.format("dirs[%d]=[%s]", i, dirs[i]));
				Node.filesMkdir(HIVE_UID, SLASH + dirs[i], true);
			}

			//2.1 filesRm: same ip
			Node.filesRm(HIVE_UID, SLASH + dirs[0]);

			try {
				Node.filesRm(HIVE_UID, SLASH + dirs[0]);
				fail();
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			//2.2 filesRm: different ip
			resetPublicIPAndLogin();

			Node.filesRm(HIVE_UID, SLASH + dirs[1]);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testHiveFilesMkdir() {
		try {
			resetPublicIPAndLogin();

			//1. filesMkdir
			final int COUNT = 5;
			String[] dirs = new String[COUNT];
			for (int i = 0; i < COUNT; i++) {
				dirs[i] = "testHiveFilesMkdir_" + System.currentTimeMillis();
				resetPublicIPAndLogin();

				Log.d(TAG, String.format("dirs[%d]=[%s]", i, dirs[i]));
				Node.filesMkdir(HIVE_UID, SLASH + dirs[i], false);
			}

			resetPublicIPAndLogin();

			//Check 1.1: get all dir's name
			JSONObject json = Node.filesLs(HIVE_UID, null);
			JSONArray entries = json.getJSONArray("Entries");
			Log.d(TAG, "entries="+entries.toString());
			List<String> list = new ArrayList<>();
			for (int i = 0; i < entries.length(); i++) {
				JSONObject item = entries.getJSONObject(i);
				list.add(item.getString("Name"));
				Log.d(TAG, String.format("list[%d]=[%s]", i, item.getString("Name")));
			}

			//Check 1.2: check
			for (int i = 0; i < COUNT; i++) {
				if (!list.contains(dirs[i])) {
					fail();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testHiveFilesMkdirs() {
		try {
			resetPublicIPAndLogin();

			//1. filesMkdir
			final int COUNT = 5;
			String[] dirs = new String[COUNT];
			String[] parentDirs = new String[COUNT];

			for (int i = 0; i < COUNT; i++) {
				parentDirs[i] = "testHiveFilesMkdirMulti_" + System.currentTimeMillis();
				dirs[i] = parentDirs[i] + "/" + System.currentTimeMillis();
				resetPublicIPAndLogin();

				Log.d(TAG, String.format("dirs[%d]=[%s]", i, dirs[i]));
				Node.filesMkdir(HIVE_UID, SLASH + dirs[i], true);

				JSONObject json = Node.filesLs(HIVE_UID, SLASH + parentDirs[i]);
				Log.d(TAG, "json="+json.toString());
				json = Node.filesStat(HIVE_UID, SLASH + parentDirs[i]);
				Log.d(TAG, "json2="+json.toString());
			}

			resetPublicIPAndLogin();

			//Check 1.1: get first level dir's name
			JSONObject json = Node.filesLs(HIVE_UID, null);
			JSONArray entries = json.getJSONArray("Entries");
			Log.d(TAG, "entries="+entries.toString());
			List<String> list = new ArrayList<>();
			for (int i = 0; i < entries.length(); i++) {
				JSONObject item = entries.getJSONObject(i);
				list.add(item.getString("Name"));
				Log.d(TAG, String.format("list[%d]=[%s]", i, item.getString("Name")));
			}

			//Check 1.2: check the first level
			for (int i = 0; i < COUNT; i++) {
				if (!list.contains(parentDirs[i])) {
					fail();
				}
			}

			//Check 1.3: check the second level
			for (int i = 0; i < COUNT; i++) {
				json = Node.filesLs(HIVE_UID, SLASH + parentDirs[i]);
				entries = json.getJSONArray("Entries");
				Log.d(TAG, "entries="+entries.toString());
				JSONObject item = entries.getJSONObject(0);
				list.add(item.getString("Name"));

				String dir = parentDirs[i] + SLASH + item.getString("Name");
				boolean contains = false;
				for (int j = 0; j < COUNT; j++) {
					if (dirs[j].equals(dir)) {
						contains = true;
						break;
					}
				}

				if (!contains) {
					fail();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testHiveFilesWriteRead() {
		try {
			//1. filesWrite: file
			resetPublicIPAndLogin();

			String fileName = "testHiveFiles.txt";
			String content = "Elastos Hive android testHiveFiles 2019 time: " + System.currentTimeMillis();
			String filePath = getFilePath(fileName, content);
			String hiveFileName = "hive" + System.currentTimeMillis();
			Node.filesWrite(HIVE_UID, filePath, hiveFileName);

			//filesRead: same ip
			String value = new String(Node.filesRead(HIVE_UID, hiveFileName));
			assertEquals(content, value);

			//filesRead: different ip
			resetPublicIPAndLogin();

			value = new String(Node.filesRead(HIVE_UID, hiveFileName));
			assertEquals(content, value);

			//2. filesWrite: data
			resetPublicIPAndLogin();
			content = "Elastos Hive android testHiveFiles 2019 time: " + System.currentTimeMillis();
			hiveFileName = "hive" + System.currentTimeMillis();
			Node.filesWrite(HIVE_UID, content.getBytes(), hiveFileName);

			//filesRead: same ip
			value = new String(Node.filesRead(HIVE_UID, hiveFileName));
			assertEquals(content, value);

			//filesRead: different ip
			resetPublicIPAndLogin();

			value = new String(Node.filesRead(HIVE_UID, hiveFileName));
			assertEquals(content, value);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testHiveOtherMethods() {
		try {
			//getVersion
			JSONObject json = Node.getVersion();
			assertNotNull(json.getString("Client Version"));
			assertNotNull(json.getString("Protocol Version"));

			json = Node.uidNew();
			String uid = json.getString("UID");
			json = Node.renew(uid);
			String newUid = json.getString("UID");
			assertNotEquals(uid, newUid);

			json = Node.uidInfo(newUid);
			Log.d(TAG, "uidInfo json="+json.toString());

			try {
				// Http has error(rc: 500): [Hive error: uid-1093e335-da23-46e6-9fa3-ae91b69e23a2 does not exist.]
				Node.uidInfo(uid);
				//TODO
//				fail();
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			try {
				// Http has error(rc: 500): [Hive error: uid-1093e335-da23-46e6-9fa3-ae91b69e23a2 does not exist.]
				Node.uidInfo("Invalid UID");
				fail();
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			String fileName = "testHiveOtherMethods.txt";
			String content = "Elastos Hive android testHiveOtherMethods 2019 time: " + System.currentTimeMillis();
			String filePath = getFilePath(fileName, content);
			String hiveFileName = "hive" + System.currentTimeMillis() + ".txt";
			Node.filesWrite(newUid, filePath, hiveFileName);

			//filesStat
			json = Node.filesStat(newUid, hiveFileName);
			String value = json.getString("Size");
			assertEquals(content.length(), Integer.parseInt(value));
			Log.d(TAG, "[filesStat] json="+json.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testHiveLogin() {
		try {
			//1. Add a file
			String fileName = "testHiveLogin.txt";
			String content = "Elastos Hive android testHiveLogin 2019 time: " + System.currentTimeMillis();
			String filePath = getFilePath(fileName, content);
			JSONObject json = Node.fileAdd(filePath);
			String hash = json.getString("Hash");

			//2. mkdir
			String dir = "testHiveLogin_" + System.currentTimeMillis();
			Node.filesMkdir(HIVE_UID, SLASH + dir, true);

			//3. copy file under the dir.
			String name = "/cpFile_" + System.currentTimeMillis();
			String destPath = SLASH + dir + SLASH + name;
			Node.filesCp(HIVE_UID, hash, destPath);

			//4. check the file content.
			String value = new String(Node.filesRead(HIVE_UID, destPath));
			assertEquals(content, value);

			//5. After 30s, Change the ip and check the file content.
			json = Node.filesStat(HIVE_UID, SLASH);
			String homeHash = json.getString("Hash");
			Log.d(TAG, "[filesStat] json="+json.toString());

//			Thread.sleep(30000);
			resetPublicIP();
			json = Node.uidLogin(HIVE_UID, homeHash);
			Log.d(TAG, "[uidLogin] json="+json);
			value = new String(Node.filesRead(HIVE_UID, destPath));
			assertEquals(content, value);

			//6. Check more
			for (int i = 0; i < 100; i++) {
				content = "Elastos Hive android testHiveLogin 2019 time: " + System.currentTimeMillis();
				String hiveFileName = "hive" + System.currentTimeMillis();
				Node.filesWrite(HIVE_UID, content.getBytes(), hiveFileName);

				json = Node.filesStat(HIVE_UID, SLASH);
				homeHash = json.getString("Hash");

				resetPublicIP();
				Node.uidLogin(HIVE_UID, homeHash);

				//filesRead: same ip
				value = new String(Node.filesRead(HIVE_UID, hiveFileName));
				assertEquals(content, value);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testHiveLarge() {
		try {
			StringBuilder sb = new StringBuilder();
			final int LENGTH = 1024 * 512;
			for (int i = 0; i < LENGTH; i++) {
				sb.append('L');
			}
			String content = sb.toString();
			Log.d(TAG, "content len = " + content.length());
			for (int i = 0; i < 100; i++) {
				String hiveFileName = "hive" + System.currentTimeMillis();
				Node.filesWrite(HIVE_UID, content.getBytes(), hiveFileName);

				JSONObject json = Node.filesStat(HIVE_UID, SLASH);
				String homeHash = json.getString("Hash");

				resetPublicIP();
				Node.uidLogin(HIVE_UID, homeHash);

				//filesRead: same ip
				String value = new String(Node.filesRead(HIVE_UID, hiveFileName));
				assertEquals(content, value);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@BeforeClass
	public static void setUp() {
		try {
			resetPublicIP();
			JSONObject json = Node.uidNew();
			HIVE_UID = json.getString("UID");
			assertTrue(HIVE_UID != null && HIVE_UID.length() > 0);

			//Peer id
			String peerId = json.getString("PeerID");
			assertTrue(peerId != null && peerId.length() > 0);

			Log.i(TAG, "Hive is ready.");
			APP_PATH = InstrumentationRegistry.getTargetContext().getFilesDir().getAbsolutePath();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private static String getFilePath(String fileName, String content) {
		return getFilePath(null, fileName, content);
	}

	private static String getFilePath(String rootPath, String fileName, String content) {
		String path = APP_PATH + SLASH + fileName;
		if (rootPath != null) {
			path = rootPath + SLASH + fileName;
		}

		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)));
			out.write(content);
		}
		catch (Exception e) {
			fail();
			e.printStackTrace();
		}
		finally {
			try {
				if(out != null){
					out.close();
				}
			}
			catch (IOException e) {
				fail();
				e.printStackTrace();
			}
		}
		return path;
	}

	private static boolean jsonArrayContains(JSONArray jsonArray, String key, String check) {
		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				if (key == null) {
					jsonArray.get(i);
					if (check.equals(jsonArray.get(i))) {
						return true;
					}
				}
				else {
					JSONObject item = jsonArray.getJSONObject(i);
					Log.d(TAG, "item="+item.toString()+", names="+item.names()+", keys="+item.keys().toString());
					if (check.equals(item.getString(key))) {
						return true;
					}
				}
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}

		return false;
	}

	private static String CURRENTIP = null;
	private static void resetPublicIP() {
		if (!Using_different_IP && CURRENTIP != null) {
			return;
		}

		Random random = new Random();
		String newIP = PUBIPS[random.nextInt(PUBIPS.length)];
		if (CURRENTIP != null) {
			if (CURRENTIP.equals(newIP)) {
				while (CURRENTIP.equals(newIP = PUBIPS[random.nextInt(PUBIPS.length)])){
				}
			}
		}

		Log.d(TAG, "newIP="+newIP+", CURRENTIP="+CURRENTIP);
		Node.initialize(newIP);

		CURRENTIP = newIP;
	}

	private static void resetPublicIPAndLogin() {
		try {
			JSONObject json = Node.filesStat(HIVE_UID, SLASH);
			String homeHash = json.getString("Hash");

			resetPublicIP();
			Node.uidLogin(HIVE_UID, homeHash);
		}
		catch (Exception e) {
			fail();
		}
	}

	private boolean difference(String[] values) {
		assertTrue (values != null && values.length > 1);
		for (int i = 0; i < values.length - 1; i++) {
			for (int j = i + 1; j < values.length; j++) {
				if (values[i].equals(values[j])) {
					Log.d(TAG, String.format("valueI=[%s], valueJ=[%s]", values[i], values[j]));
					return false;
				}
			}
		}
		return true;
	}

	@Test
	public void testOtherCases() {
		_testUidNew();
		_testUidReNew();
		_testFileAdd();
	}

	private void _testUidNew() {
		try {
			resetPublicIP();
			JSONObject json = Node.uidNew();
			String uid = json.getString("UID");
			assertTrue(uid != null && uid.length() > 0);

			//Peer id
			String peerId = json.getString("PeerID");
			assertTrue(peerId != null && peerId.length() > 0);

			final int COUNT = 10;
			String[] UIDS = new String[COUNT];
			for (int i = 0; i < COUNT; i++) {
				json = Node.uidNew();
				UIDS[i] = json.getString("UID");
			}

			assertTrue(difference(UIDS));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private void _testUidReNew() {
		try {
			resetPublicIP();
			JSONObject json = Node.uidNew();
			String uid = json.getString("UID");
			assertTrue(uid != null && uid.length() > 0);

			json = Node.renew(uid);
			String newUid = json.getString("UID");
			assertNotEquals(uid, newUid);

			try {
				//Invalid uid
				Node.renew(uid);
				fail();
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			try {
				//Invalid uid
				Node.renew("Invalid");
				fail();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private void _testFileAdd() {
		try {
			resetPublicIP();

			long current = System.currentTimeMillis();
			String fileName = "_testPinAdd_"+ current +".txt";
			String content = "Elastos Hive android _testPinAdd 2019 time:"+current;
			String filePath = getFilePath(fileName, content);
			JSONObject json = Node.fileAdd(filePath);
			//"Name":"_testPinAdd.txt","Hash":"Qmf44869vys9REn8ifPf2NfYbkfUWzoxdU63XRctaY9PfE","Size":"48"
			String hash = json.getString("Hash");
			Log.d(TAG, "json="+json.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
