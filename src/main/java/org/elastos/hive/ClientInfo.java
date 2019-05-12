package org.elastos.hive;

public class ClientInfo implements HiveItem {
	private final String userId;
	private String displayName;
	private String email;
	private String phoneNo;
	private String region;

	public ClientInfo(String userId) {
		this.userId = userId;
	}

	@Override
	public String getId() {
		return  userId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String email() {
		return email;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public String getRegion() {
		return region;
	}

	public ClientInfo setDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	public ClientInfo setEmail(String email) {
		this.email = email;
		return this;
	}

	public ClientInfo setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
		return this;
	}

	public ClientInfo setRegion(String region) {
		this.region = region;
		return this;
	}
}
