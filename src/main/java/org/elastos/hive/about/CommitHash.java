package org.elastos.hive.about;

import com.google.gson.annotations.SerializedName;

class CommitHash {
	@SerializedName("commit_hash")
    private String commitId;

    public String getCommitId() {
        return this.commitId;
    }
}
