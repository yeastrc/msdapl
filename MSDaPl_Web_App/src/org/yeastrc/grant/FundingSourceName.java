package org.yeastrc.grant;

public final class FundingSourceName {
	private String name;
	private String displayName;
	
	public FundingSourceName() {
		this("");
	}
	
	public FundingSourceName(String name) {
		this(name, name);
	}
	
	public FundingSourceName(String name, String displayName) {
		this.name = name;
		this.displayName = displayName;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	
}
