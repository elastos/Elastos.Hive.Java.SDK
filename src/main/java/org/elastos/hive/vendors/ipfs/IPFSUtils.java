package org.elastos.hive.vendors.ipfs;

import java.util.ArrayList;
import java.util.UUID;

import org.elastos.hive.HiveException;
import org.elastos.hive.Status;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

class IPFSUtils {
	static String BASEURL           = null;
	static final String CONFIG      = "/ipfsConfig.properties";
	static final String URLFORMAT   = "http://%s:9095/api/v0/";
	static final String PREFIX      = "/ipfs/";
	static final String CONTENTTYPE = "Content-Type";
	static final String TYPE_Json   = "application/json";
	static final String UID         = "uid";
	static final String HASH        = "hash";
	static final String PATH        = "path";
	static final String SOURCE      = "source";
	static final String DEST        = "dest";

	private static final String[] IPFSIPS = {
			"52.83.159.189",
			"52.83.119.110",
			"3.16.202.140",
			"18.217.147.205",
			"18.219.53.133"
	};

	//if uid = null, use "uid/new" to get a new one and check, otherwise use stat to check. 
	static String initialize(String uid) throws HiveException {
		try {
			boolean valid = false;
			if (uid == null) {
				for (int i = 0; i < IPFSIPS.length; i++) {
					BASEURL = String.format(URLFORMAT, IPFSIPS[i]);
					uid = getNewUid(IPFSIPS[i]);
					if (uid != null) {
						valid = true;
						break;
					}
				}
			}
			else {
				for (int i = 0; i < IPFSIPS.length; i++) {
					BASEURL = String.format(URLFORMAT, IPFSIPS[i]);
					if (stat(uid, "/") != null) {
						valid = true;
						break;
					}
				}
			}

			if (!valid) {
				throw new HiveException("The ipfs' server are invalid.");
			}

			return uid;
		} catch (Exception e) {
			e.printStackTrace();
			throw new HiveException("Login failed");
		}
	}
	
	static void login(String uid) throws HiveException {
		try {
			String homeHash = getHomeHash();
			Unirest.get(BASEURL + "uid/login")
				.header(CONTENTTYPE, TYPE_Json)
				.queryString(UID, uid)
				.queryString(HASH, homeHash)
				.asJson();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new HiveException("Login failed");
		}
	}

	static Status mkdir(String uid, String path) {
		try {
			String url = String.format("%s%s", BASEURL, "files/mkdir");
			HttpResponse<JsonNode> response = Unirest.get(url)
				.header(CONTENTTYPE, TYPE_Json)
				.queryString(UID, uid)
				.queryString(PATH, path)
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

	static Status rm(String uid, String path) {
		try {
			String url = String.format("%s%s", BASEURL, "files/rm");
			HttpResponse<JsonNode> response = Unirest.get(url)
				.header(CONTENTTYPE, TYPE_Json)
				.queryString(UID, uid)
				.queryString(PATH, path)
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

	static Status createEmptyFile(String uid, String path) {
		try {
			String url = String.format("%s%s", BASEURL, "files/write");
			String type = String.format("multipart/form-data; boundary=%s", UUID.randomUUID().toString());
			HttpResponse<JsonNode> response = Unirest.post(url)
				.header(CONTENTTYPE, type)
				.queryString(UID, uid)
				.queryString(PATH, path)
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

	static String stat(String uid, String path) {
		try {
			String url = String.format("%s%s", BASEURL, "files/stat");
			HttpResponse<JsonNode> response = Unirest.get(url)
				.header(CONTENTTYPE, TYPE_Json)
				.queryString(UID, uid)
				.queryString(PATH, path)
				.asJson();
			if (response.getStatus() == 200) {
				return response.getBody().getObject().getString("Hash");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	static Status copyTo(String uid, String hash, String newPath) {
		return copyAndMove(uid, hash, newPath, "files/cp");
	}

	static Status moveTo(String uid, String hash, String newPath) {
		return copyAndMove(uid, hash, newPath, "files/mv");
	}

	private static Status copyAndMove(String uid, String hash, String newPath, String operator) {
		try {
			String url = String.format("%s%s", BASEURL, operator);
			String finalHash = PREFIX + hash;
			if (operator.equals("files/mv")) {
				finalHash = hash;
			}
			HttpResponse<JsonNode> response = Unirest.get(url)
				.header(CONTENTTYPE, TYPE_Json)
				.queryString(UID, uid)
				.queryString(SOURCE, finalHash)
				.queryString(DEST, newPath)
				.asJson();

			if (response.getStatus() == 200) {
				return new Status(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
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

	private static String getNewUid(String ip) {
		String url = BASEURL + "uid/new";
		try {
			HttpResponse<JsonNode> json = Unirest.get(url).header(CONTENTTYPE, TYPE_Json).asJson();
			return json.getBody().getObject().getString("UID");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	static ArrayList<String> getNameList(String parentPath, JSONObject baseJson) {
		JSONArray entries = null;
		try {
			entries = baseJson.getJSONArray("Entries");
		} catch (Exception e) {
			e.printStackTrace();
		}

		int len = 0;
		if (entries != null) {
			len = entries.length();
		}

		ArrayList<String> nameList = new ArrayList<String>(len);
		if (len > 0) {
			for (int i = 0; i < len; i++) {
				JSONObject itemJson = entries.getJSONObject(i);
				String name = itemJson.getString("Name");
				nameList.add(String.format("%s/%s", parentPath, name));
			}
		}

		return nameList;
	}
	
	static boolean isFolder(String uid, String path) throws HiveException {
		try {
			String url = String.format("%s%s", BASEURL, "files/stat");
			HttpResponse<JsonNode> response = Unirest.get(url)
				.header(CONTENTTYPE, TYPE_Json)
				.queryString(UID, uid)
				.queryString(PATH, path)
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
