package org.elastos.hive;

import static org.junit.Assert.assertTrue;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.elastos.hive.exceptions.HiveException;
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

	@Test public void testLogin() {
		class TestAuthenticator extends Authenticator {
			@Override
			public void requestAuthentication(String requestUrl) {
				try {
					Desktop.getDesktop().browse(new URI(requestUrl));
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		DriveParameters parameters = DriveParameters.createForOneDrive("9021f9b8-3afc-4770-b5e2-7a92ddac3abb",
				"offline_access Files.ReadWrite",
				"http://localhost:44316");

		try {
			HiveDrive drive = HiveDrive.createInstance(parameters);
			assertTrue("Can't create drive instance.", drive != null);

			TestAuthenticator testAuth = new TestAuthenticator();

			drive.login(testAuth);
		
			//1.1 Create file at the root
			String pathName = "testCreateFile3.txt";
			drive.createFile(pathName);	

			//1.1 Create file at the root/test
			pathName = "/test/testCreateFile3.txt";
			drive.createFile(pathName);

			//2. Get the created file at the root
			HiveFile testFile = drive.getFile("testCreateFile.txt");
			
			//3. Check the HiveFile: file or directory
			//3.1 file
			boolean isFile = testFile.isFile();
			System.out.println("==========================isFile="+isFile);
			
			//3.2 directory: the "test" is an exist directory at yourself oneDrive.
			testFile = drive.getFile("test");
			boolean isDir = testFile.isDirectory();
			System.out.println("==========================isDir="+isDir);
			
			//4. make a directory
			//4.1 make a directory at the root
			HiveFile root = drive.getRootDir();
			String newDirnameString = "testMkdir008";
			HiveFile firstLevel = root.mkdir(newDirnameString);
			isDir = firstLevel.isDirectory();
			System.out.println("====================mkdir=firstLevel, isdir="+firstLevel.isDirectory());

			try {
				//Error, if re-create the directory.
				root.mkdir(newDirnameString);
			} catch (HiveException e) {
				e.printStackTrace();
			}

			String secondDir = "testMkdirChild";
			//4.2 make a directory at the sub directory.
			HiveFile secondLevel = firstLevel.mkdir(secondDir);

			try {
				//Error, if re-create the directory.
				firstLevel.mkdir(secondDir);
			} catch (HiveException e) {
				e.printStackTrace();
			}

			//5. list the folder.
			testFile.list();
			root.list();

			//6. copy the sub directory to root
			secondLevel.copyTo(root);

			//7. delete the folder
			firstLevel.delete();

			//Last; logout
			drive.logout();
		} catch (HiveException e) {
			e.printStackTrace();
			assertTrue("Test login errror", false);
		}
    }
}
