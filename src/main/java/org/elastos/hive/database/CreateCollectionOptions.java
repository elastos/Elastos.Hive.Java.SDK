package org.elastos.hive.database;

public class CreateCollectionOptions extends Options<CreateCollectionOptions> {
	private static final long serialVersionUID = -2970142120056131337L;

	public CreateCollectionOptions() {
	}

	public CreateCollectionOptions writeConcern(WriteConcern value) {
		if (value != null)
			setObjectOption("write_concern", value);
		else
			remove("write_concern");

		return this;
	}

	public CreateCollectionOptions readConcern(ReadConcern value) {
		if (value != null)
			setStringOption("read_concern", value.toString());
		else
			remove("read_concern");

		return this;
	}

	public CreateCollectionOptions readPreference(ReadPreference value) {
		if (value != null)
			setStringOption("read_preference", value.toString());
		else
			remove("read_preference");

		return this;
	}

	public CreateCollectionOptions capped(boolean value) {
		return setBooleanOption("capped", value);
	}

	public CreateCollectionOptions size(long value) {
		return setNumberOption("size", value);
	}

	public CreateCollectionOptions max(int value) {
		return setNumberOption("max", value);
	}

	public CreateCollectionOptions collation(Collation value) {
		if (value != null)
			setObjectOption("collation", value);
		else
			remove("collation");

		return this;
	}
}
