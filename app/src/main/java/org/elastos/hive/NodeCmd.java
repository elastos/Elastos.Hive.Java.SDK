package org.elastos.hive;

import org.json.JSONObject;
import java.util.Map;

class NodeCmd {
	private static String HIVE_BASE = "http://149.28.244.92:9095/";
	private static final String HIVE_BASE_FORMAT = "http://%s:9095/";
	private static final String HIVE_API  = "api/v0/";

	// uid
	static final String UID_NEW   = "uid/new";
	static final String UID_RENEW = "uid/renew";
	static final String UID_INFO  = "uid/info";
	static final String UID_LOGIN = "uid/login";

	// file
	static final String FILE_ADD = "file/add";
	static final String FILE_GET = "file/get";
	static final String FILE_LS  = "file/ls";
	static final String FILE_CAT = "file/cat";

	// files
	static final String FILES_CP     = "files/cp";
	static final String FILES_FLUSH  = "files/flush";
	static final String FILES_LS     = "files/ls";
	static final String FILES_MKDIR  = "files/mkdir";
	static final String FILES_MV     = "files/mv";
	static final String FILES_READ   = "files/read";
	static final String FILES_RM     = "files/rm";
	static final String FILES_STAT   = "files/stat";
	static final String FILES_WRITE  = "files/write";

	// version
	static final String VERSION = "version";
	static final String PREFIX = "/ipfs/";

	static void initialize(String ip) {
		HIVE_BASE = String.format(HIVE_BASE_FORMAT, ip);
	}

	static JSONObject execute(String cmd, String extra) {
		String url = HIVE_BASE + HIVE_API + cmd;

		if (VERSION.equals(cmd)) {
			url = HIVE_BASE + cmd;
		}

		if (extra != null && !extra.isEmpty()) {
			url += "?" + extra;
		}

		if (VERSION.equals(cmd)) {
			return HttpAPI.getForVersion(url);
		}

		return HttpAPI.get(url);
	}

	static byte[] executeEx(String cmd, String extra) {
		String url = HIVE_BASE + HIVE_API + cmd;

		if (extra != null && !extra.isEmpty()) {
			url += "?" + extra;
		}

		return HttpAPI.getValue(url);
	}

	static JSONObject execute(String cmd, String filePath, byte[] data, Map<String, String> params) {
		if (filePath != null && data != null) {
			throw new IllegalArgumentException("Only need filePath or data.");
		}
		String url = HIVE_BASE + HIVE_API + cmd;

		return HttpAPI.post(url, filePath, data, params);
	}
}
