package org.elastos.hive;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.utils.CryptoUtil;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BackupPersistentImpl implements Persistent {
	private String targetHost;
	private String targetDID;
	private String type;
	private String storePath;

	public BackupPersistentImpl(String targetHost, String targetDID, String type,String storePath) {
		this.targetHost = targetHost;
		this.targetDID = targetDID;
		this.type = type;
		this.storePath = storePath;
	}

	@Override
	public JSONObject parseFrom() throws HiveException {
		FileReader reader = null;
		try {
			initialize();
			reader = new FileReader(this.configPath);
			char[] buf = new char[128];
			int len;
			StringBuilder content = new StringBuilder();
			while ((len = reader.read(buf)) != -1) {
				content.append(new String(buf, 0, len));
			}

			if (content.length() > 0) {
				return new JSONObject(content.toString());
			}
		} catch (Exception e) {
			throw new HiveException(e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return new JSONObject();
	}

	@Override
	public void upateContent(JSONObject conetnt) throws HiveException {
		FileWriter fileWriter = null;
		try {
			initialize();
			fileWriter = new FileWriter(configPath);
			fileWriter.write(conetnt.toString());
		} catch (Exception e) {
			throw new HiveException(e.getMessage());
		} finally {
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void deleteContent() {
		String tokenPath = String.format("%s/%s", storePath,"backup");
		String fileName = CryptoUtil.getSHA256(this.targetHost+this.targetDID+this.type);
		File file = new File(tokenPath, fileName);
		if(file.exists()) {
			file.delete();
		}
	}

	private String configPath;
	private void initialize() throws IOException {
		String tokenPath = String.format("%s/%s", storePath,"token");
		File rootDir = new File(tokenPath);
		if (!rootDir.exists())
			rootDir.mkdirs();
		String fileName = CryptoUtil.getSHA256(this.targetHost+this.targetDID+this.type);
		this.configPath = String.format("%s/%s", tokenPath, fileName);
		File config = new File(configPath);
		if (!config.exists())
			config.createNewFile();
	}
}
