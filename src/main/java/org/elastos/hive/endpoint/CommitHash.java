package org.elastos.hive.endpoint;

import com.google.gson.annotations.SerializedName;

class CommitHash {
	@SerializedName("commit_id")
	private String commitId;

	public String getCommitId() {
		return this.commitId;
	}
}
