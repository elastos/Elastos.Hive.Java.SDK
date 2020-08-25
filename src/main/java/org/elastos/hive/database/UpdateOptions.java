package org.elastos.hive.database;

public class UpdateOptions extends Options<UpdateOptions> {
	private static final long serialVersionUID = -4401269049374547947L;

	public UpdateOptions upsert(boolean value) {
		return setBooleanOption("upsert", value);
	}

	public UpdateOptions bypassDocumentValidation(boolean value) {
		return setBooleanOption("bypass_document_validation", value);
	}

	public UpdateOptions collation(Collation value) {
		return setObjectOption("collation", value);
	}

	public UpdateOptions hint(Index value) {
		return setObjectOption("hint", value);
	}

	public UpdateOptions hint(Index[] value) {
		return setArrayOption("hint", value);
	}
}
