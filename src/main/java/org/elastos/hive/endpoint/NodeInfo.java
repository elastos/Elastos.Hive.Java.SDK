package org.elastos.hive.endpoint;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.elastos.did.VerifiablePresentation;
import org.elastos.did.exception.MalformedPresentationException;

import java.util.Map;

/**
 * The information of the hive node is returned by {@link AboutController#getNodeVersion()}
 */
public class NodeInfo {
	@SerializedName("service_did")
	private String serviceDid;
	@SerializedName("owner_did")
	private String ownerDid;
	@SerializedName("ownership_presentation")
	private Map<String, Object> ownershipPresentation;
	private String name;
	private String email;
	private String description;
	private String version;
	@SerializedName("last_commit_id")
	private String lastCommitId;

	public String getServiceDid() {
		return serviceDid;
	}

	public void setServiceDid(String serviceDid) {
		this.serviceDid = serviceDid;
	}

	public String getOwnerDid() {
		return ownerDid;
	}

	public void setOwnerDid(String ownerDid) {
		this.ownerDid = ownerDid;
	}

	public VerifiablePresentation getOwnershipPresentation() {
		try {
			String vpStr = new GsonBuilder().create().toJson(this.ownershipPresentation);
			return VerifiablePresentation.parse(vpStr);
		} catch (MalformedPresentationException e) {
			throw new RuntimeException("Failed to create VerifiablePresentation from the information of the node.");
		}
	}

	public void setOwnershipPresentation(Map<String, Object> value) {
		this.ownershipPresentation = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getLastCommitId() {
		return lastCommitId;
	}

	public void setLastCommitId(String lastCommitId) {
		this.lastCommitId = lastCommitId;
	}
}
