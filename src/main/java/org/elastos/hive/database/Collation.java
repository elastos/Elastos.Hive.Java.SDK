package org.elastos.hive.database;

public class Collation {
	private String locale;
	private Boolean caseLevel;
	private String caseFirst;
	private Long strength;
	private Boolean numericOrdering;
	private String alternate;
	private String maxVariable;
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

	public String getCaseFirst() {
		return caseFirst;
	}

	public Collation setCaseFirst(String caseFirst) {
		this.caseFirst = caseFirst;
		return this;
	}

	public Long getStrength() {
		return strength;
	}

	public Collation setStrength(Long strength) {
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

	public String getAlternate() {
		return alternate;
	}

	public Collation setAlternate(String alternate) {
		this.alternate = alternate;
		return this;
	}

	public String getMaxVariable() {
		return maxVariable;
	}

	public Collation setMaxVariable(String maxVariable) {
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
}
