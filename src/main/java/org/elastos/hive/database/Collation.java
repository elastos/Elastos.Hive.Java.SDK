package org.elastos.hive.database;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class Collation {
	private String locale;
	private Boolean caseLevel;
	private CaseFirst caseFirst;
	private Strength strength;
	private Boolean numericOrdering;
	private Alternate alternate;
	private MaxVariable maxVariable;
	private Boolean normalization;
	private Boolean backwards;

	public String getLocale() {
		return locale;
	}

	public Collation setLocale(String locale) {
		this.locale = locale;
		return this;
	}

	public Boolean getCaseLevel() {
		return caseLevel;
	}

	public Collation setCaseLevel(Boolean caseLevel) {
		this.caseLevel = caseLevel;
		return this;
	}

	public CaseFirst getCaseFirst() {
		return caseFirst;
	}

	public Collation setCaseFirst(CaseFirst caseFirst) {
		this.caseFirst = caseFirst;
		return this;
	}

	public Strength getStrength() {
		return strength;
	}

	public Collation setStrength(Strength strength) {
		this.strength = strength;
		return this;
	}

	public Boolean getNumericOrdering() {
		return numericOrdering;
	}

	public Collation setNumericOrdering(Boolean numericOrdering) {
		this.numericOrdering = numericOrdering;
		return this;
	}

	public Alternate getAlternate() {
		return alternate;
	}

	public Collation setAlternate(Alternate alternate) {
		this.alternate = alternate;
		return this;
	}

	public MaxVariable getMaxVariable() {
		return maxVariable;
	}

	public Collation setMaxVariable(MaxVariable maxVariable) {
		this.maxVariable = maxVariable;
		return this;
	}

	public Boolean getNormalization() {
		return normalization;
	}

	public Collation setNormalization(Boolean normalization) {
		this.normalization = normalization;
		return this;
	}

	public Boolean getBackwards() {
		return backwards;
	}

	public Collation setBackwards(Boolean backwards) {
		this.backwards = backwards;
		return this;
	}

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
}
