package org.elastos.hive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import org.elastos.hive.utils.IOUtil;

public class SdkVersion {
	public static final String version;

	static {
		version = loadVersion();
	}

	private static final String resourceName = "org/elastos/hive/version.txt";

	private static final class LoadException extends Exception {
		private static final long serialVersionUID = 0L;

        public LoadException(/*@Nullable*/String message) {
			super(message);
        }
	}

	private static String loadLineFromResource() throws LoadException {
		try {
			InputStream in = SdkVersion.class.getResourceAsStream(resourceName);
			if (in == null)
				throw new LoadException("Not found");

			try {
				BufferedReader bin = new BufferedReader(IOUtil.utf8Reader(in));
				String version = bin.readLine();
				if (version == null)
					throw new LoadException("No line for version");
				return version;
			}
			finally {
				IOUtil.closeInput(in);
			}
		}
		catch (IOException e) {
	            throw new LoadException(e.getMessage());
	        }
	    }

	private static String loadVersion() {
		try {
			String version = loadLineFromResource();

			Pattern regex = Pattern.compile("[0-9]+(?:\\.[0-9]+)*(?:-[-_A-Za-z0-9]+)?");
			if (!regex.matcher(version).matches()) {
				throw new LoadException("Text doesn't follow version pattern");
			}

			return version;
		}
		catch (LoadException e) {
			throw new RuntimeException("Error loading version from resource \"version.txt\": "
					+ e.getMessage());
		}
	}
}
