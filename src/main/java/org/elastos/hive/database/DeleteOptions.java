package org.elastos.hive.database;

public class DeleteOptions extends Options<DeleteOptions> {
	private static final long serialVersionUID = -5130331807240228461L;

	public DeleteOptions(Collation collation, Index hint) {
		collation(collation);
		hint(hint);
	}

	public DeleteOptions(Collation collation, Index[] hint) {
		collation(collation);
		hint(hint);
	}

	public DeleteOptions() {
	}

	public DeleteOptions collation(Collation value) {
		return setObjectOption("collation", value);
	}

	public DeleteOptions hint(Index value) {
		return setObjectOption("hint", value);
	}

	public DeleteOptions hint(Index[] value) {
		return setArrayOption("hint", value);
	}
}
