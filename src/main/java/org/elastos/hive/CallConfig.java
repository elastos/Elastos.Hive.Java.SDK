package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * scripting call config class
 */
public class CallConfig {

	/**
	 * Call parameters (params field) are meant to be used by scripts on the server side, for example as injected parameters
	 * to mongo queries. Ex: if "params" contains a field "name":"someone", then the called script is able to reference this parameter
	 * using "$params.name".
	 */
	private JsonNode params;
	/**
	 * used for cross did, optional parameter
	 */
	private String appDid;
	private Purpose purpose;
	private String filePath;

	private CallConfig(Builder builder) {
		this.params = builder.params;
		this.appDid = builder.appDid;
		this.purpose = builder.purpose;
		this.filePath = builder.filePath;
	}

	public JsonNode getParams() {
		return this.params;
	}

	public String getAppDid() {
		return this.appDid;
	}

	public Purpose getPurpose() {
		return this.purpose;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public enum Purpose {
		/**
		 * File upload scripting
		 */
		Upload,
		/**
		 * File download scripting
		 */
		Download,
		/**
		 * General scripting
		 */
		General
	}

	public static final class Builder {
		private JsonNode params;
		private String appDid;
		private Purpose purpose;
		private String filePath;

		public Builder() {
			this.params = null;
			this.appDid = null;
			this.purpose = null;
			this.filePath = null;
		}

		public Builder setParams(JsonNode params) {
			this.params = params;
			return this;
		}

		public Builder setAppDid(String appDid) {
			this.appDid = appDid;
			return this;
		}

		public Builder setPurpose(Purpose purpose) {
			this.purpose = purpose;
			return this;
		}

		public Builder setFilePath(String filePath) {
			this.filePath = filePath;
			return this;
		}

		public CallConfig build() {
			return new CallConfig(this);
		}
	}
}
