package org.elastos.hive.vendors.onedrive;

import org.elastos.hive.AuthHelper;

class OneDriveHttpHeader {
	static final String Authorization	= "Authorization";
	static final String ContentType		= "Content-Type";
	static final String Json		= "application/json";
	static final String Urlencoded		= "application/x-www-form-urlencoded";

	static String bearerValue(AuthHelper authHelper) {
		if (authHelper.getToken() != null) {
			return "bearer " + authHelper.getToken().getAccessToken();			
		}
		return "";
	}
}
