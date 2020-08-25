package org.elastos.hive.database;

public class Collation extends Options<Collation> {
	private static final long serialVersionUID = -5447049098908294821L;

	public enum CaseFirst {
		UPPER, LOWER, OFF;

		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}

	public enum Strength {
		PRIMARY(1),
		SECONDARY(2),
		TERTIARY(3),
		QUATERNARY(4),
		IDENTICAL(5);

		private int value;

		Strength(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}
	}

	public enum Alternate {
		NON_IGNORABLE, SHIFTED;

		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}

	public enum MaxVariable {
		PUNCT, SPACE;

		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}

	public Collation(String locale, boolean caseLevel, CaseFirst caseFirst,
			Strength strength, boolean numericOrdering, Alternate alternate,
			MaxVariable maxVariable, boolean normalization, boolean backwards) {
		locale(locale);
		caseLevel(caseLevel);
		caseFirst(caseFirst);
		strength(strength);
		numericOrdering(numericOrdering);
		alternate(alternate);
		maxVariable(maxVariable);
		normalization(normalization);
		backwards(backwards);
	}

	public Collation() {}

	public Collation locale(String value) {
		return setStringOption("locale", value);
	}

	public Collation caseLevel(boolean value) {
		return setBooleanOption("caseLevel", value);
	}

	public Collation caseFirst(CaseFirst value) {
		return setStringOption("caseFirst", value.toString());
	}

	public Collation strength(Strength value) {
		return setNumberOption("strength", value.value());
	}

	public Collation numericOrdering(boolean value) {
		return setBooleanOption("numericOrdering", value);
	}

	public Collation alternate(Alternate value) {
		return setStringOption("alternate", value.toString());
	}

	public Collation maxVariable(MaxVariable value) {
		return setStringOption("maxVariable", value.toString());
	}

	public Collation normalization(boolean value) {
		return setBooleanOption("normalization", value);
	}

	public Collation backwards(boolean value) {
		return setBooleanOption("backwards", value);
	}
}
