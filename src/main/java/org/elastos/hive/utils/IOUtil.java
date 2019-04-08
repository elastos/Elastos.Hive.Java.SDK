package org.elastos.hive.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class IOUtil {
	
	public static Reader utf8Reader(InputStream in) {
		//TODO;
		return null;
    }
	
	public static void closeInput(InputStream in) {
        try {
            in.close();
        } catch (IOException ex) {
            // Ignore.
        }
    }
}
