package org.elastos.hive;

import org.elastos.hive.exception.HiveException;

/**
 * This class is used to fetch some possible information from remote hive node.
 * eg. version;
 *     latest commit Id;
 *     How many DID involved;
 *     How many vault service running there;
 *     How many backup service running there;
 *     How much disk storage filled there;
 *     etc.
 */
public class Provider extends ServiceEndpoint {
	public Provider(AppContext context) throws HiveException {
		this(context, null);
	}

	public Provider(AppContext context, String providerAddress) throws HiveException {
		super(context, providerAddress);
	}

	public class Version {
		private int major;
		private int minor;
		private int hotfix;

		public int getMajor() {
			return this.major;
		}

		public int getNinor() {
			return this.minor;
		}

		public int getHotfix() {
			return this.hotfix;
		}

		public String getFullVersion() {
			return String.format("%s.%s.%s", major, minor, hotfix);
		}

		@Override
		public String toString() {
			return getFullVersion();
		}
	}
}
