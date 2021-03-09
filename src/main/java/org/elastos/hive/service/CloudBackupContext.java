package org.elastos.hive.service;

public abstract class CloudBackupContext implements BackupContext {
	@Override
	public String getParamater(String parameter) {
		switch(parameter) {
		case "clientId":
			return getClientId();

		case "redirectUrl":
			return getRedirectUrl();

		case "scope":
			return getAppScope();
		}

		return null;
	}

	public abstract String getClientId();
	public abstract String getRedirectUrl();
	public abstract String getAppScope();
}
