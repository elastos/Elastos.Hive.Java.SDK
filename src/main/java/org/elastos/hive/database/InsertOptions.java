package org.elastos.hive.database;

public class InsertOptions extends Options<InsertOptions> {
	private static final long serialVersionUID = 3495942798606077537L;

	public InsertOptions(boolean bypassDocumentValidation, boolean ordered) {
		bypassDocumentValidation(bypassDocumentValidation);
		ordered(ordered);
	}

	public InsertOptions(boolean bypassDocumentValidation) {
		bypassDocumentValidation(bypassDocumentValidation);
	}

	public InsertOptions() {
	}

	public InsertOptions bypassDocumentValidation(boolean value) {
		return setBooleanOption("bypass_document_validation", value);
	}

	public InsertOptions ordered(boolean value) {
		return setBooleanOption("ordered", value);
	}
}
