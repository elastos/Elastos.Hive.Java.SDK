package org.elastos.hive.database;

public class CountOptions extends Options<CountOptions> {
	private static final long serialVersionUID = 5243064850859749563L;

	public CountOptions(long skip, long limit) {
		skip(skip);
		limit(limit);
	}

	public CountOptions() {
	}

	public CountOptions skip(long value) {
		return setNumberOption("skip", value);
	}

	public CountOptions limit(long value) {
		return setNumberOption("limit", value);
	}

	public CountOptions maxTimeMS(int value) {
		return setNumberOption("maxTimeMS", value);
	}

	public CountOptions collation(Collation value) {
		return setObjectOption("skip", value);
	}

	public CountOptions hint(Index value) {
		return setObjectOption("hint", value);
	}

	public CountOptions hint(Index[] value) {
		return setArrayOption("hint", value);
	}
}
