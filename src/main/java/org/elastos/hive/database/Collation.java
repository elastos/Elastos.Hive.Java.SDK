package org.elastos.hive.database;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public class Collation {
	@JsonProperty("locale")
	private String locale;
	@JsonProperty("caseLevel")
	private Boolean caseLevel;
	@JsonProperty("caseFirst")
	private CaseFirst caseFirst;
	@JsonProperty("strength")
	private Strength strength;
	@JsonProperty("numericOrdering")
	private Boolean numericOrdering;
	@JsonProperty("alternate")
	private Alternate alternate;
	@JsonProperty("maxVariable")
	private MaxVariable maxVariable;
	@JsonProperty("normalization")
	private Boolean normalization;
	@JsonProperty("backwards")
	private Boolean backwards;

	public enum CaseFirst {
		UPPER, LOWER, OFF;

		@Override
		@JsonValue
		public String toString() {
			return name().toLowerCase();
		}

		@JsonCreator
		public static CaseFirst fromString(String name) {
			return valueOf(name.toUpperCase());
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

		@JsonValue
		public int value() {
			return value;
		}

		@JsonCreator
		public static Strength fromInt(int i) {
		    switch (i) {
		    case 1:
		    	return PRIMARY;

		    case 2:
		    	return SECONDARY;

		    case 3:
		    	return TERTIARY;

		    case 4:
		    	return QUATERNARY;

		    case 5:
		    	return IDENTICAL;

		    default:
		    	throw new IllegalArgumentException("Invalid strength");
		    }
		}

	}

	public enum Alternate {
		NON_IGNORABLE, SHIFTED;

		@Override
		@JsonValue
		public String toString() {
			return name().toLowerCase();
		}

		@JsonCreator
		public static Alternate fromString(String name) {
			return valueOf(name.toUpperCase());
		}
	}

	public enum MaxVariable {
		PUNCT, SPACE;

		@Override
		@JsonValue
		public String toString() {
			return name().toLowerCase();
		}

		@JsonCreator
		public static MaxVariable fromString(String name) {
			return valueOf(name.toUpperCase());
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
		locale = value;
		return this;
	}

	public String getLocale() {
		return locale;
	}

	public Collation caseLevel(boolean value) {
		caseLevel = value;
		return this;
	}

	public Boolean getCaseLevel() {
		return caseLevel;
	}

	public Collation caseFirst(CaseFirst value) {
		caseFirst = value;
		return this;
	}

	public CaseFirst getCaseFirst() {
		return caseFirst;
	}

	public Collation strength(Strength value) {
		strength = value;
		return this;
	}

	public Strength Strength() {
		return strength;
	}

	public Collation numericOrdering(boolean value) {
		numericOrdering = value;
		return this;
	}

	public Boolean numericOrdering() {
		return numericOrdering;
	}

	public Collation alternate(Alternate value) {
		alternate = value;
		return this;
	}

	public Alternate alternate() {
		return alternate;
	}

	public Collation maxVariable(MaxVariable value) {
		maxVariable = value;
		return this;
	}

	public MaxVariable maxVariable() {
		return maxVariable;
	}

	public Collation normalization(boolean value) {
		normalization = value;
		return this;
	}

	public Boolean normalization() {
		return normalization;
	}

	public Collation backwards(boolean value) {
		backwards = value;
		return this;
	}

	public Boolean backwards() {
		return backwards;
	}
}
