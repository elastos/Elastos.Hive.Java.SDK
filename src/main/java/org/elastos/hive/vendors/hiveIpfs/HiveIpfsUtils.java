package org.elastos.hive.vendors.hiveIpfs;

import java.util.UUID;

import org.elastos.hive.HiveException;
import org.elastos.hive.Status;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

class HiveIpfsUtils {
	static final String CONFIG      = "/ipfsConfig.properties";
	static final String BASEURL     = "http://52.83.159.189:9095/api/v0/";
	static final String PREFIX      = "/ipfs/";
	static final String CONTENTTYPE = "Content-Type";
	static final String TYPE_Json   = "application/json";
	static final String UID         = "uid";
	static final String HASH        = "hash";
	static final String PATH        = "path";

	static Status mkdir(String uid, String path) {
		try {
			String url = String.format("%s%s", HiveIpfsUtils.BASEURL, "files/mkdir");
			HttpResponse<JsonNode> response = Unirest.get(url)
				.header(HiveIpfsUtils.CONTENTTYPE, HiveIpfsUtils.TYPE_Json)
				.queryString(HiveIpfsUtils.UID, uid)
				.queryString(HiveIpfsUtils.PATH, path)
				.queryString("parents", "false")
				.asJson();

			if (response.getStatus() == 200) {
				return new Status(1);
			}
		} catch (Exception e) {
		}

		return new Status(0);
	}

	static Status rm(String uid, String path) {
		try {
			String url = String.format("%s%s", HiveIpfsUtils.BASEURL, "files/rm");
			HttpResponse<JsonNode> response = Unirest.get(url)
				.header(HiveIpfsUtils.CONTENTTYPE, HiveIpfsUtils.TYPE_Json)
				.queryString(HiveIpfsUtils.UID, uid)
				.queryString(HiveIpfsUtils.PATH, path)
				.queryString("recursive", "true")
				.asJson();

			if (response.getStatus() == 200) {
				return new Status(1);
			}
		} catch (Exception e) {
		}

		return new Status(0);
	}

	static Status createEmptyFile(String uid, String path) {
		try {
			String url = String.format("%s%s", HiveIpfsUtils.BASEURL, "files/write");
			String type = String.format("multipart/form-data; boundary=%s", UUID.randomUUID().toString());
			HttpResponse<JsonNode> response = Unirest.post(url)
				.header(HiveIpfsUtils.CONTENTTYPE, type)
				.queryString(HiveIpfsUtils.UID, uid)
				.queryString(HiveIpfsUtils.PATH, path)
				.queryString("create", "true")
				.asJson();

			if (response.getStatus() == 200) {
				return new Status(1);
			}
		} catch (Exception e) {
		}

		return new Status(0);
	}

	static String stat(String uid, String path) {
		try {
			String url = String.format("%s%s", BASEURL, "files/stat");
			HttpResponse<JsonNode> response = Unirest.get(url)
				.header(HiveIpfsUtils.CONTENTTYPE, HiveIpfsUtils.TYPE_Json)
				.queryString(HiveIpfsUtils.UID, uid)
				.queryString(HiveIpfsUtils.PATH, path)
				.asJson();
			if (response.getStatus() == 200) {
				return response.getBody().getObject().getString("Hash");
			}
		} catch (Exception e) {
		}

		return null;
	}

	static Status publish(String uid, String hash) {
		try {
			String url = String.format("%s%s", BASEURL, "name/publish");
			HttpResponse<JsonNode> response = Unirest.get(url)
				.header(HiveIpfsUtils.CONTENTTYPE, HiveIpfsUtils.TYPE_Json)
				.queryString(HiveIpfsUtils.UID, uid)
				.queryString(HiveIpfsUtils.PATH, hash)
				.asJson();
			if (response.getStatus() == 200) {
				return new Status(1);
			}
		} catch (Exception e) {
		}

		return new Status(0);
	}

	static String getHomeHash() throws HiveException {
		String url = BASEURL + "name/resolve";
		try {
			HttpResponse<JsonNode> json = Unirest.get(url).header(CONTENTTYPE, TYPE_Json).asJson();
			return json.getBody().getObject().getString("Path");
		} catch (UnirestException e) {
			e.printStackTrace();
			throw new HiveException("Get home hash failed.");
		}
	}

	static String getNewUid() throws HiveException {
		String url = BASEURL + "uid/new";
		try {
			HttpResponse<JsonNode> json = Unirest.get(url).header(CONTENTTYPE, TYPE_Json).asJson();
			return json.getBody().getObject().getString("UID");
		} catch (UnirestException e) {
			e.printStackTrace();
			throw new HiveException("Get the new uid failed.");
		}
	}

	static boolean isFile(String type) {
		return type != null && type.equals("file");
	}

	static boolean isFolder(String type) {
		return type != null && type.equals("directory");
	}
}
