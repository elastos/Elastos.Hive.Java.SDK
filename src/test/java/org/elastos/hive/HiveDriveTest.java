package org.elastos.hive;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HiveDriveTest {
	@Test public void testSomeMethod() {
		DriveParameters parameters = DriveParameters.createForOneDrive("tom", "all", "https://127.0.0.1:8080");
		try {
			HiveDrive drive = HiveDrive.createInstance(parameters);
			assertTrue("someMethod should return  'true'", drive.someMethod());
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Exception", false);
		}
	}

    @Test public void testGetRootDir() {
    	//TODO;
    	System.out.print("TODO");
    	assertTrue("GetRootDir", true);
    }
}
