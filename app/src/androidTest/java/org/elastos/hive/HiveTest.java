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
	private static final boolean Using_different_IP = false;

	@Test
	public void testHiveFile() {
		try {
			resetPublicIP();

			//fileAdd
			String fileName = "testHiveFile.txt";
			String content = "Elastos Hive android testHiveFile 2019";
			String filePath = getFilePath(fileName, content);
			JSONObject json = Node.fileAdd(filePath, true, true, true);
			//"Name":"testHiveFile.txt","Hash":"Qmf44869vys9REn8ifPf2NfYbkfUWzoxdU63XRctaY9PfE","Size":"48"
			String value = json.getString("Name");
			assertEquals(fileName, value);

			String hash = json.getString("Hash");
			assertTrue(hash != null && hash.length() > 0);

			//fileCat: same ip
			value = Node.fileCat(hash);
			assertEquals(content, value);

			//fileCat: different ip
			resetPublicIP();
			value = Node.fileCat(hash);
			assertEquals(content, value);

			//fileLs
			resetPublicIP();
			json = Node.fileLs(hash);
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

			//fileGet: TODO
//			value = Node.fileGet(hash);
//			Log.d(TAG, "value="+value);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testHivePin() {
		try {
			resetPublicIP();

			String fileName = "testHivePin.txt";
			String content = "Elastos Hive android testHivePin 2019";
			String filePath = getFilePath(fileName, content);
			JSONObject json = Node.fileAdd(filePath);
			//"Name":"testHivePin.txt","Hash":"Qmf44869vys9REn8ifPf2NfYbkfUWzoxdU63XRctaY9PfE","Size":"48"
			String hash = json.getString("Hash");

			resetPublicIP();

			//{"Pins":["Qmatw6ZsjDY1W3JcohXS3iM6gj5Pfh8Bga36emKc5faUgt"]}
			json = Node.pinAdd(hash);
			JSONArray pins = json.getJSONArray("Pins");
			assertTrue(jsonArrayContains(pins, null, hash));

			resetPublicIP();

			//{"Keys":{"Qmatw6ZsjDY1W3JcohXS3iM6gj5Pfh8Bga36emKc5faUgt":{"Type":"recursive"}}}
			json = Node.pinLs(hash);
			JSONObject namesJson = json.getJSONObject("Keys");
			assertTrue(jsonArrayContains(namesJson.names(), null, hash));

			resetPublicIP();

			//{"Pins":["Qmatw6ZsjDY1W3JcohXS3iM6gj5Pfh8Bga36emKc5faUgt"]}
			json = Node.pinRm(hash);
			pins = json.getJSONArray("Pins");
			assertTrue(jsonArrayContains(pins, null, hash));
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
			resetPublicIP();
			String dir = "testHiveFilesCp_" + System.currentTimeMillis();
			Node.filesMkdir(HIVE_UID, SLASH + dir, true);

			//3. copy file under the dir.
			resetPublicIP();
			String name = "/cpFile_" + System.currentTimeMillis();
			String srcPath = "/ipfs/"+hash;
			String destPath = SLASH + dir + SLASH + name;
			Node.filesCp(HIVE_UID, srcPath, destPath);

			//4. check the file content.
			resetPublicIP();
			String value = Node.filesRead(HIVE_UID, destPath);
			assertEquals(content, value);
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
				String srcPath = "/ipfs/"+hash;
				String destPath = SLASH + dir;
				Node.filesMv(HIVE_UID, hash, destPath);

				//4. check the file content.
				resetPublicIP();
				String value = Node.filesRead(HIVE_UID, destPath);
				assertEquals(content, value);

				//5. check the source file: no file
				try {
					resetPublicIP();
					Node.filesRead(HIVE_UID, srcPath);
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
			resetPublicIP();

			//1. filesMkdir
			final int COUNT = 5;
			String[] dirs = new String[COUNT];
			for (int i = 0; i < COUNT; i++) {
				dirs[i] = "testHiveFilesRm_" + System.currentTimeMillis();
				resetPublicIP();

				Log.d(TAG, String.format("dirs[%d]=[%s]", i, dirs[i]));
				Node.filesMkdir(HIVE_UID, SLASH + dirs[i], true);
			}

			//2.1 filesRm: same ip
			Node.filesRm(HIVE_UID, SLASH + dirs[0], true);

			try {
				Node.filesRm(HIVE_UID, SLASH + dirs[0], true);
				fail();
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			//2.2 filesRm: different ip
			resetPublicIP();

			Node.filesRm(HIVE_UID, SLASH + dirs[1], true);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testHiveFilesMkdir() {
		try {
			resetPublicIP();

			//1. filesMkdir
			final int COUNT = 5;
			String[] dirs = new String[COUNT];
			for (int i = 0; i < COUNT; i++) {
				dirs[i] = "testHiveFilesMkdir_" + System.currentTimeMillis();
				resetPublicIP();

				Log.d(TAG, String.format("dirs[%d]=[%s]", i, dirs[i]));
				Node.filesMkdir(HIVE_UID, SLASH + dirs[i], true);
			}

			resetPublicIP();

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
	public void testHiveFilesWriteRead() {
		try {
			//1. filesWrite: file
			resetPublicIP();

			String fileName = "testHiveFiles.txt";
			String content = "Elastos Hive android testHiveFiles 2019 time: " + System.currentTimeMillis();
			String filePath = getFilePath(fileName, content);
			String hiveFileName = "hive" + System.currentTimeMillis();
			Node.filesWrite(HIVE_UID, filePath, hiveFileName);

			//filesRead: same ip
			String value = Node.filesRead(HIVE_UID, hiveFileName);
			assertEquals(content, value);

			//filesRead: different ip
			resetPublicIP();

			value = Node.filesRead(HIVE_UID, hiveFileName);
			assertEquals(content, value);

			//2. filesWrite: data
			resetPublicIP();

			content = "Elastos Hive android testHiveFiles 2019 time: " + System.currentTimeMillis();
			hiveFileName = "hive" + System.currentTimeMillis();
			Node.filesWrite(HIVE_UID, content.getBytes(), hiveFileName);

			//filesRead: same ip
			value = Node.filesRead(HIVE_UID, hiveFileName);
			assertEquals(content, value);

			//filesRead: different ip
			resetPublicIP();

			value = Node.filesRead(HIVE_UID, hiveFileName);
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

			Node.uidInfo(newUid);

			try {
				// Http has error(rc: 500): [Hive error: uid-1093e335-da23-46e6-9fa3-ae91b69e23a2 does not exist.]
				Node.uidInfo(uid);
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

			String hash = json.getString("Hash");
			//namePublish
			json = Node.namePublish(newUid, null, hash);
			Log.d(TAG, "[namePublish] json="+json.toString());
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
		String path = APP_PATH + SLASH + fileName;

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
				while (!CURRENTIP.equals(newIP = PUBIPS[random.nextInt(PUBIPS.length)])){
				}
			}
		}

		Log.d(TAG, "newIP="+newIP+", CURRENTIP="+CURRENTIP);
		Node.initialize(newIP);

		CURRENTIP = newIP;
	}
}
