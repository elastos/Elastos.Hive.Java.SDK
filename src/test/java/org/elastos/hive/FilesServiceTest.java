package org.elastos.hive;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.FilesService;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.*;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FilesServiceTest {

	@Test
	public void test01_uploadText() {
	}


	@Test
	public void test02_uploadBin() {
	}

	@Test
	public void test03_downloadText() {
	}

	@Test
	public void test04_downloadBin() {
	}

	@Test
	public void test05_list() {
	}

	@Test
	public void test06_hash() {
		try {
			String hash = filesApi.hash(remoteTextPath).exceptionally(s->null).get();
			assertNotNull(hash);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test07_move() {
	}

	@Test
	public void test08_copy() {
	}


	@Test
	public void test09_deleteFile() {
	}


	@BeforeClass
	public static void setUp() {
		try {
			filesApi = TestData.getInstance().getVault().thenApplyAsync(vault -> vault.getFilesService()).join();
		} catch (HiveException|DIDException e) {
			e.printStackTrace();
		}
	}

	private final String textLocalPath;
	private final String imgLocalPath;
	private final String rootLocalCachePath;
	@SuppressWarnings("unused")
	private final String textLocalCachePath;
	@SuppressWarnings("unused")
	private final String imgLocalCachePath;

	private final String remoteRootPath;
	private final String remoteTextPath;
	private final String remoteImgPath;
	private final String remoteTextBackupPath;

	private static FilesService filesApi;

	public FilesServiceTest() {
		String localRootPath = System.getProperty("user.dir") + "/src/test/resources/local/";
		textLocalPath = localRootPath +"test.txt";
		imgLocalPath = localRootPath +"big.png";
		rootLocalCachePath = localRootPath + "cache/file/";
		textLocalCachePath = rootLocalCachePath + "test.txt";
		imgLocalCachePath = rootLocalCachePath + "/big.png";

		remoteRootPath = "hive";
		remoteTextPath = remoteRootPath + File.separator + "test.txt";
		remoteImgPath = remoteRootPath + File.separator + "big.png";
		remoteTextBackupPath = "backup" + File.separator + "test.txt";
	}
}
