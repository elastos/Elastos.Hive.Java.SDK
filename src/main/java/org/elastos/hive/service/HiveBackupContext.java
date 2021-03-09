package org.elastos.hive.service;

public abstract class HiveBackupContext implements BackupContext {
	@Override
	public String getParamater(String parameter) {
		switch (parameter) {
		case "targetAddress":
			return this.getTargetProvderAddress();

		case "targetServiceDid":
			return this.getTargetServiceDid();
		}
		return null;
	}

	public abstract String getTargetProvderAddress();
	public abstract String getTargetServiceDid();
}
