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

package org.elastos.hive;

import org.elastos.hive.utils.IOUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

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
