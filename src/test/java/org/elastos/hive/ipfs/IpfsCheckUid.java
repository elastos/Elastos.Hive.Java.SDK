package org.elastos.hive.ipfs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import org.elastos.hive.Client;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class IpfsCheckUid {
	private static Client client;
	private static String uidPath = String.format("%s/%s", System.getProperty("user.dir"), "uid.txt");
	
	@Test public void testStoreAndCheckUid() {
		try {
			Client.Info info = client.getInfo().get();

			assertNotNull(info);
			assertNotNull(info.get(Client.Info.userId));

			//1. get local uid
			String uid = info.get(Client.Info.userId);
			String localUid = getLocalUid();
			
			System.out.println(String.format("uid=[%s], localUid=[%s]", uid, localUid));
			if (localUid == null) {
				stroeLocalUid(uid);
			}
			else {
				assertEquals(localUid, uid);
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("getInfo failed");
		}
	}
	
	private void stroeLocalUid(String uid) {
        FileWriter writer;
        try {
            writer = new FileWriter(uidPath);
            writer.write(uid);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private String getLocalUid() {
        File file = new File(uidPath);
		try {
		    if(!file.exists()){
		        return null;
		    }
		    FileInputStream inputStream = new FileInputStream(file);
		    int length = inputStream.available();
		    byte bytes[] = new byte[length];
		    inputStream.read(bytes);
		    inputStream.close();
		    return new String(bytes, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	@BeforeClass
	static public void setUp() throws Exception {
		client = IpfsTestBase.login();
		assertNotNull(client);
	}

    @AfterClass
    static public void tearDown() throws Exception {
    	client.logout();
    }
}
