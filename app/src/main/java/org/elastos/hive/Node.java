package org.elastos.hive;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class Node {
	public static void initialize(String pubIp) {
		NodeCmd.initialize(pubIp);
	}

	public static JSONObject uidNew() {
		return NodeCmd.execute(NodeCmd.UID_NEW, null);
	}

	public static JSONObject renew(String uid) {
		if (uid == null || uid.isEmpty()) {
			throw new IllegalArgumentException("Invalid uid.");
		}

		String arg = "uid="+uid;
		return NodeCmd.execute(NodeCmd.UID_RENEW, arg);
	}

	public static JSONObject uidInfo(String uid) {
		if (uid == null || uid.isEmpty()) {
			throw new IllegalArgumentException("Invalid uid.");
		}

		String arg = "uid="+uid;
		return NodeCmd.execute(NodeCmd.UID_INFO, arg);
	}

	public static JSONObject uidLogin(String uid, String hash) {
		if (uid == null || uid.isEmpty() || hash == null || hash.isEmpty()) {
			throw new IllegalArgumentException("Invalid uid or home hash.");
		}

		String arg = "uid="+uid;
		if (!hash.contains(NodeCmd.PREFIX)) {
			arg += "&hash="+ NodeCmd.PREFIX + hash;
		}
		else {
			arg += "&hash="+hash;
		}

		return NodeCmd.execute(NodeCmd.UID_LOGIN, arg);
	}

	public static JSONObject fileAdd(String path) {
		if (path == null || path.isEmpty()) {
			throw new IllegalArgumentException("Invalid path.");
		}

		return fileAdd(path, false, false, true);
	}

	private static JSONObject fileAdd(String path, boolean recursive, boolean hidden, boolean pin) {
		if (path == null || path.isEmpty()) {
			throw new IllegalArgumentException("Invalid path.");
		}

		Map<String, String> params = new HashMap<>();
		if (recursive) {
			params.put("recursive", "true");
		}
		else {
			params.put("recursive", "false");
		}

		if (hidden) {
			params.put("hidden", "true");
		}
		else {
			params.put("hidden", "false");
		}

		if (pin) {
			params.put("pin", "true");
		}
		else {
			params.put("pin", "false");
		}

		return NodeCmd.execute(NodeCmd.FILE_ADD, path, null, params);
	}

	public static byte[] fileGet(String hash) {
		if (hash == null || hash.isEmpty()) {
			throw new IllegalArgumentException("Invalid args.");
		}

		String args = "arg=" + hash;
		return NodeCmd.executeEx(NodeCmd.FILE_GET, args);
	}

	public static byte[] fileGet(String hash, boolean archive
			, boolean compress, int compressionLevel) {
		if (hash == null || hash.isEmpty() || compressionLevel > 9 || compressionLevel < 1) {
			throw new IllegalArgumentException("Invalid args.");
		}

		String args = "arg=" + hash;
		if (archive) {
			args += "&archive=true";
		}
		else {
			args += "&archive=false";
		}

		if (compress) {
			args += "&compress=true";
		}
		else {
			args += "&compress=false";
		}

		args += "&compression-level=" + compressionLevel;

		return NodeCmd.executeEx(NodeCmd.FILE_GET, args);
	}

	public static byte[] fileCat(String hash) {
		if (hash == null || hash.isEmpty()) {
			throw new IllegalArgumentException("Invalid args.");
		}

		String args = "arg=" + hash;
		return NodeCmd.executeEx(NodeCmd.FILE_CAT, args);
	}

	public static JSONObject fileLs(String hash) {
		if (hash == null || hash.isEmpty()) {
			throw new IllegalArgumentException("Invalid path.");
		}

		String arg = "arg=" + hash;
		return NodeCmd.execute(NodeCmd.FILE_LS, arg);
	}

	public static void filesCp(String uid, String source, String dest) {
		if (uid == null || uid.isEmpty() || source == null || source.isEmpty()) {
			throw new IllegalArgumentException("Invalid uid.");
		}

		StringBuilder sb = new StringBuilder("uid=");
		sb.append(uid);

		if (!source.contains(NodeCmd.PREFIX)) {
			sb.append("&source=").append(NodeCmd.PREFIX + source);
		}

		if (dest != null && !dest.isEmpty()) {
			sb.append("&dest=").append(dest);
		}

		NodeCmd.execute(NodeCmd.FILES_CP, sb.toString());
	}

	public static void filesFlush(String uid) {
		filesFlush(uid, "/");
	}

	private static void filesFlush(String uid, String path) {
		if (uid == null || uid.isEmpty()) {
			throw new IllegalArgumentException("Invalid uid.");
		}

		StringBuilder sb = new StringBuilder("uid=");
		sb.append(uid);
		if (path == null || path.isEmpty()) {
			path = "/";
		}

		sb.append("&path=").append(path);

		NodeCmd.execute(NodeCmd.FILES_FLUSH, sb.toString());
	}

	public static JSONObject filesLs(String uid, String path) {
		if (uid == null || uid.isEmpty()) {
			throw new IllegalArgumentException("Invalid uid.");
		}

		StringBuilder sb = new StringBuilder("uid=");
		sb.append(uid);

		//Path to show listing for. Defaults to ‘/’.
		if (path == null || path.isEmpty()) {
			path = "/";
		}

		sb.append("&path=").append(path);

		return NodeCmd.execute(NodeCmd.FILES_LS, sb.toString());
	}

	public static void filesMkdir(String uid, String path, boolean parents) {
		if (uid == null || uid.isEmpty()) {
			throw new IllegalArgumentException("Invalid uid.");
		}

		StringBuilder sb = new StringBuilder("uid=" + uid);
		sb.append("&path=").append(path);

		sb.append("&parents=");
		if (parents) {
			sb.append("true");
		}
		else {
			sb.append("false");
		}

		NodeCmd.execute(NodeCmd.FILES_MKDIR, sb.toString());
	}

	public static void filesMv(String uid, String source, String dest) {
		if (uid == null || uid.isEmpty()) {
			throw new IllegalArgumentException("Invalid uid.");
		}

		String args = "uid=" + uid;
		args += "&source=" + source;
		args += "&dest=" + dest;

		NodeCmd.execute(NodeCmd.FILES_MV, args);
	}

	public static byte[] filesRead(String uid, String path) {
		if (uid == null || uid.isEmpty()) {
			throw new IllegalArgumentException("Invalid uid.");
		}

		String args = "uid=" + uid;
		args += "&path=" + path;

		return NodeCmd.executeEx(NodeCmd.FILES_READ, args);
	}

	public static byte[] filesRead(String uid, String path, int offset, int count) {
		if (uid == null || uid.isEmpty() || count < 0) {
			throw new IllegalArgumentException("Invalid uid.");
		}

		String args = "uid=" + uid;
		args += "&path=" + path;
		args += "&offset=" + offset;
		args += "&count=" + count;

		return NodeCmd.executeEx(NodeCmd.FILES_READ, args);
	}

	public static void filesRm(String uid, String path) {
		if (uid == null || uid.isEmpty() || path == null || path.isEmpty()) {
			throw new IllegalArgumentException("Invalid uid or path");
		}

		String args = "uid=" + uid;
		args += "&path=" + path;
		args += "&recursive=true";

		NodeCmd.execute(NodeCmd.FILES_RM, args);
	}

	public static JSONObject filesStat(String uid, String path) {
		return filesStat(uid, path, null, null, null);
	}

	private static JSONObject filesStat(String uid, String path, String format, String hash, String withLocal) {
		if (uid == null || uid.isEmpty()) {
			throw new IllegalArgumentException("Invalid uid");
		}

		String args = "uid=" + uid;
		if (path == null || path.isEmpty()) {
			path = "/";
		}

		args += "&path=" + path;
		if (format != null && !format.isEmpty()) {
			args += "&format=" + format;
		}

		if (hash != null && !hash.isEmpty()) {
			args += "&hash=" + hash;
		}

		if (withLocal != null && !withLocal.isEmpty()) {
			args += "&with-local=" + withLocal;
		}

		return NodeCmd.execute(NodeCmd.FILES_STAT, args);
	}

	public static void filesWrite(String uid, byte[] data, String hivePath) {
		filesWrite(uid, data, hivePath, 0, true, true, data.length);
	}

	public static void filesWrite(String uid, byte[] data, String hivePath, int offset, boolean create,
	                             boolean truncate, int count) {
		if (uid == null || uid.isEmpty() || data == null || data.length <= 0) {
			throw new IllegalArgumentException("Invalid uid or data");
		}

		String args = "?uid=" + uid;
		args += "&path=" + hivePath;
		args += "&offset=" + offset;
		args += "&count=" + count;
		if (create) {
			args += "&create=true";
		}
		else {
			args += "&create=false";
		}

		if (truncate) {
			args += "&truncate=true";
		}
		else {
			args += "&truncate=false";
		}

		NodeCmd.execute(NodeCmd.FILES_WRITE + args, null, data, null);
	}

	public static void filesWrite(String uid, String filePath, String hivePath) {
		if (uid == null || uid.isEmpty() || filePath == null || filePath.isEmpty()) {
			throw new IllegalArgumentException("Invalid uid or filePath");
		}

		String args = "?uid=" + uid;
		args += "&path=" + hivePath;
		args += "&create=true";

		NodeCmd.execute(NodeCmd.FILES_WRITE + args, filePath, null, null);
	}

	public static void filesWrite(String uid, String filePath, String hivePath, int offset, boolean create,
	                             boolean truncate, int count) {
		if (uid == null || uid.isEmpty() || filePath == null || filePath.isEmpty()) {
			throw new IllegalArgumentException("Invalid uid or filePath");
		}

		String args = "?uid=" + uid;
		args += "&path=" + hivePath;
		args += "&offset=" + offset;
		args += "&count=" + count;
		if (create) {
			args += "&create=true";
		}
		else {
			args += "&create=false";
		}

		if (truncate) {
			args += "&truncate=true";
		}
		else {
			args += "&truncate=false";
		}

		NodeCmd.execute(NodeCmd.FILES_WRITE + args, filePath, null, null);
	}

	public static JSONObject getVersion() {
		return NodeCmd.execute(NodeCmd.VERSION, null);
	}

	public static JSONObject getVersion(boolean number, boolean commit, boolean repo, boolean all) {
		String args = "";
		if (number) {
			args += "&number=true";
		}
		else {
			args += "&number=false";
		}

		if (commit) {
			args += "&commit=true";
		}
		else {
			args += "&commit=false";
		}

		if (repo) {
			args += "&repo=true";
		}
		else {
			args += "&repo=false";
		}

		if (all) {
			args += "&all=true";
		}
		else {
			args += "&all=false";
		}

		return NodeCmd.execute(NodeCmd.VERSION, args);
	}
}
