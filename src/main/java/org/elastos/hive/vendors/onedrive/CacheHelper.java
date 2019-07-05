package org.elastos.hive.vendors.onedrive;

import java.io.File;

class CacheHelper {
	private static String cachePath = null;
	static void initialize(final String storePath) {
		cachePath = String.format("%s/%s", storePath, OneDriveUtils.TMP);
	}

	static String getCachePath() {
		try {
			File cachePathFile = new File(cachePath);
			if (!cachePathFile.exists()) {
				cachePathFile.mkdir();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return cachePath;
	}
}
