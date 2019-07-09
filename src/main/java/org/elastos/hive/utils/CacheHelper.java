/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.elastos.hive.utils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CacheHelper {
	private static final String TMP 	        = ".tmp";
	private static String cachePath = null;

	public static void initialize(final String storePath) {
		cachePath = String.format("%s/%s", storePath, TMP);
	}

	public static java.io.File getCacheFile(String path) {
		java.io.File file = null;
		try {
			file = new java.io.File(getCacheFileName(path));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return file;
	}
	
	//if discard or commit successfully, delete the cache file.
	public static void deleteCache(String path) {
		try {
			java.io.File cacheFile = new java.io.File(CacheHelper.getCacheFileName(path));
			if (cacheFile.exists()) {
				cacheFile.delete();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getCacheFileName(String path) {
		String cacheFileName = null;
		try {
			MessageDigest md;
			md = MessageDigest.getInstance("MD5");
			md.update(path.getBytes());
			String md5Name =  bytes2Hex(md.digest());
			String cachePath = CacheHelper.getCachePath();
			cacheFileName = String.format("%s/%s", cachePath, md5Name);				
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return cacheFileName;
	}
	
	private static String getCachePath() {
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
	
	private static String bytes2Hex(byte[] content) {
    	final char[] HEX = "0123456789abcdef".toCharArray();
        char[] chs = new char[content.length * 2];
        for(int i = 0, offset = 0; i < content.length; i++) {
            chs[offset++] = HEX[content[i] >> 4 & 0xf];
            chs[offset++] = HEX[content[i] & 0xf];
        }
        return new String(chs);
    }
}
