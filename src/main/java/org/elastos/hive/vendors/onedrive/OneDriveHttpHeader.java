package org.elastos.hive.vendors.onedrive;

import org.elastos.hive.AuthHelper;

class OneDriveHttpHeader {
	static final String Authorization = "Authorization";

	static String bearerValue(AuthHelper authHelper) {
		return "bearer " + authHelper.getToken().getAccessToken();
	}
}
