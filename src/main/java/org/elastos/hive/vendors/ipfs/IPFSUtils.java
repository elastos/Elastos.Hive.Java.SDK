package org.elastos.hive.vendors.ipfs;

import java.util.UUID;

import org.elastos.hive.HiveException;
import org.elastos.hive.Status;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

class IPFSUtils {
	static final String CONFIG      = "ipfs.json";
	static final String LASTUID     = "last_uid";
	static final String UIDS        = "uids";
	static final String URLFORMAT   = "http://%s:9095/api/v0/";

	static Status mkdir(IPFSRpcHelper ipfsHelper, String path) {
		try {
			String url = String.format("%s%s", ipfsHelper.getBaseUrl(), IPFSMethod.MKDIR);
			HttpResponse<JsonNode> response = Unirest.get(url)
				.header(IPFSURL.ContentType, IPFSURL.Json)
				.queryString(IPFSURL.UID, ipfsHelper.getIpfsEntry().getUid())
				.queryString(IPFSURL.PATH, path)
				.queryString("parents", "false")
				.asJson();

			if (response.getStatus() == 200) {
				return new Status(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Status(0);
	}

	static Status rm(IPFSRpcHelper ipfsHelper, String path) {
		try {
			String url = String.format("%s%s", ipfsHelper.getBaseUrl(), IPFSMethod.RM);
			HttpResponse<JsonNode> response = Unirest.get(url)
				.header(IPFSURL.ContentType, IPFSURL.Json)
				.queryString(IPFSURL.UID, ipfsHelper.getIpfsEntry().getUid())
				.queryString(IPFSURL.PATH, path)
				.queryString("recursive", "true")
				.asJson();

			if (response.getStatus() == 200) {
				return new Status(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Status(0);
	}

	static Status createEmptyFile(IPFSRpcHelper ipfsHelper, String path) {
		try {
			String url = String.format("%s%s", ipfsHelper.getBaseUrl(), IPFSMethod.WRITE);
			String type = String.format("multipart/form-data; boundary=%s", UUID.randomUUID().toString());
			HttpResponse<JsonNode> response = Unirest.post(url)
				.header(IPFSURL.ContentType, type)
				.queryString(IPFSURL.UID, ipfsHelper.getIpfsEntry().getUid())
				.queryString(IPFSURL.PATH, path)
				.queryString("create", "true")
				.asJson();

			if (response.getStatus() == 200) {
				return new Status(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Status(0);
	}

	static String stat(IPFSRpcHelper ipfsHelper, String path) {
		try {
			String url = String.format("%s%s", ipfsHelper.getBaseUrl(), IPFSMethod.STAT);
			HttpResponse<JsonNode> response = Unirest.get(url)
				.header(IPFSURL.ContentType, IPFSURL.Json)
				.queryString(IPFSURL.UID, ipfsHelper.getIpfsEntry().getUid())
				.queryString(IPFSURL.PATH, path)
				.asJson();
			if (response.getStatus() == 200) {
				return response.getBody().getObject().getString("Hash");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	static Status copyTo(IPFSRpcHelper ipfsHelper, String hash, String newPath) {
		return copyAndMove(ipfsHelper, hash, newPath, IPFSMethod.CP);
	}

	static Status moveTo(IPFSRpcHelper ipfsHelper, String hash, String newPath) {
		return copyAndMove(ipfsHelper, hash, newPath, IPFSMethod.MV);
	}

	private static Status copyAndMove(IPFSRpcHelper ipfsHelper, String hash, String newPath, String operator) {
		try {
			String url = String.format("%s%s", ipfsHelper.getBaseUrl(), operator);
			String finalHash = IPFSURL.PREFIX + hash;
			if (operator.equals(IPFSMethod.MV)) {
				finalHash = hash;
			}
			HttpResponse<JsonNode> response = Unirest.get(url)
				.header(IPFSURL.ContentType, IPFSURL.Json)
				.queryString(IPFSURL.UID, ipfsHelper.getIpfsEntry().getUid())
				.queryString(IPFSURL.SOURCE, finalHash)
				.queryString(IPFSURL.DEST, newPath)
				.asJson();

			if (response.getStatus() == 200) {
				return new Status(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Status(0);
	}

	static boolean isFolder(IPFSRpcHelper ipfsHelper, String path) throws HiveException {
		try {
			String url = String.format("%s%s", ipfsHelper.getBaseUrl(), IPFSMethod.STAT);
			HttpResponse<JsonNode> response = Unirest.get(url)
				.header(IPFSURL.ContentType, IPFSURL.Json)
				.queryString(IPFSURL.UID, ipfsHelper.getIpfsEntry().getUid())
				.queryString(IPFSURL.PATH, path)
				.asJson();
			if (response.getStatus() == 200) {
				JSONObject jsonObject = response.getBody().getObject();
				String type = jsonObject.getString("Type");
				return isFolder(type);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		throw new HiveException(String.format("Get the file or directory:[%s] failed", path));
	}

	static boolean isFile(String type) {
		return type != null && type.equals("file");
	}

	static boolean isFolder(String type) {
		return type != null && type.equals("directory");
	}
}
