package org.elastos.hive;

import org.elastos.hive.exception.HiveException;

/**
 * This class is used to fetch some possible information from remote hive node.
 * eg. version;
 *
 * <ul>
 * <li>Latest commit Id;</li>
 * <li>How many DID involved;</li>
 * <li>How many vault service running there;</li>
 * <li>How many backup service running there;</li>
 * <li>How much disk storage filled there;</li>
 * <li>etc.</li>
 * </ul>
 */
class Provider extends ServiceEndpoint {
	public Provider(AppContext context) throws HiveException {
		this(context, null);
	}

	/**
	 * Create by the application context and the provider address.
	 *
	 * @param context The application context
	 * @param providerAddress The provider address
	 * @throws HiveException See {@link HiveException}
	 */
	public Provider(AppContext context, String providerAddress) throws HiveException {
		super(context, providerAddress);
	}
}
