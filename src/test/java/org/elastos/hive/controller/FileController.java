package org.elastos.hive.controller;

import org.elastos.hive.Files;
import org.elastos.hive.Utils;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FileController extends Controller {

	private static FileController mInstance = null;
	private Files files;

	public static FileController newInstance(Files files) {
		if(mInstance == null) {
			mInstance = new FileController(files);
		}

		return mInstance;
	}

	private FileController(Files files) {
		this.files = files;
	}


	private String textLocalPath;
	private String imgLocalPath;
	private String rootLocalCachePath;

	private String remoteRootPath;
	private String remoteTextPath;
	private String remoteImgPath;
	private String remoteTextBackupPath;

	@Override
	protected void setUp() {
		super.setUp();
		String localRootPath = System.getProperty("user.dir") + "/src/test/resources/";
		textLocalPath = localRootPath + "local/test.txt";
		imgLocalPath = localRootPath + "local/big.png";
		rootLocalCachePath = localRootPath + "cache/file/";

		remoteRootPath = "hive";
		remoteTextPath = remoteRootPath + File.separator + "test.txt";
		remoteImgPath = remoteRootPath + File.separator + "big.png";
		remoteTextBackupPath = "backup" + File.separator + "test.txt";
	}

	@Override
	public void execute() {
		test01_uploadText();
		test02_uploadBin();
		test03_downloadText();
		test04_downloadBin();
		test05_list();
		test06_hash();
		test07_move();
		test08_copy();
		test09_deleteFile();
	}


	public void test01_uploadText() {
		FileReader fileReader = null;
		Writer writer = null;
		try {
			writer = files.upload(remoteTextPath, Writer.class).exceptionally(e -> {
				System.out.println(e.getMessage());
				return null;
			}).get();
			fileReader = new FileReader(new File(textLocalPath));
			char[] buffer = new char[1];
			while (fileReader.read(buffer) != -1) {
				writer.write(buffer);
			}
			System.out.println("write success");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
				if (null != fileReader) fileReader.close();
				if (null != writer) writer.close();
			} catch (Exception e) {
				fail();
			}
		}
	}


	public void test02_uploadBin() {
		try {
			OutputStream outputStream = files.upload(remoteImgPath, OutputStream.class).get();
			byte[] bigStream = Utils.readImage(imgLocalPath);
			outputStream.write(bigStream);
			outputStream.close();
			System.out.println("write success");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	public void test03_downloadText() {
		try {
			Reader reader = files.download(remoteTextPath, Reader.class).get();
			Utils.cacheTextFile(reader, rootLocalCachePath, "test.txt");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	public void test04_downloadBin() {
		try {
			InputStream inputStream = files.download(remoteImgPath, InputStream.class).get();
			Utils.cacheBinFile(inputStream, rootLocalCachePath, "big.png");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	public void test05_list() {
		CompletableFuture<Boolean> future = files.list(remoteRootPath)
				.handle((result, ex) -> {
					if(ex!=null) {
						ex.printStackTrace();
					}
					assertTrue(result.size() > 0);
					System.out.println("list size=" + result.size());
					return (ex == null);
				});

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}

	public void test06_hash() {
		CompletableFuture<Boolean> future = files.hash(remoteTextPath)
				.handle((result, ex) -> {
					if(ex!=null) {
						ex.printStackTrace();
					}
					return (ex == null);
				});

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}

	public void test07_move() {
		CompletableFuture<Boolean> future = files.delete(remoteTextBackupPath)
				.thenCompose(result -> files.move(remoteTextPath, remoteTextBackupPath))
				.handle((result, ex) -> {
					if(ex!=null) {
						ex.printStackTrace();
					}
					return (ex == null);
				});

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}

	public void test08_copy() {
		CompletableFuture<Boolean> future = files.copy(remoteTextBackupPath, remoteTextPath)
				.handle((result, ex) -> {
					if(ex!=null) {
						ex.printStackTrace();
					}
					return (ex == null);
				});

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}


	public void test09_deleteFile() {
		CompletableFuture<Boolean> future = files.delete(remoteTextPath)
				.thenCompose(result -> files.delete(remoteTextBackupPath))
				.handle((result, ex) -> {
					if(ex!=null) {
						ex.printStackTrace();
					}
					return (ex == null);
				});

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}
}
