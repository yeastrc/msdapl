package org.yeastrc.grant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class FundingSourceType {
	
	private String name;
	private String displayName;
	private List<FundingSourceName> acceptedSourceNames;
	
	// Federal Source names
	private static final FundingSourceName[] federalSources = new FundingSourceName[] {new FundingSourceName("NASA"),
															new FundingSourceName("NIH"), 
															new FundingSourceName("NSF"), 
															new FundingSourceName("DOE"), 
															new FundingSourceName("DOD"),
															new FundingSourceName("NIST"),
															new FundingSourceName("DVA"),
															new FundingSourceName("OTHER", "Other")};
	
	// Funding source types
	public static final FundingSourceType OTHER = new FundingSourceType("OTHER", "Other");
	public static final FundingSourceType LOCGOV = new FundingSourceType("LOCGOV", "Local Gov.");
	public static final FundingSourceType PROFASSOC = new FundingSourceType("PROFASSOC", "Prof. Assoc.");
	public static final FundingSourceType INDUSTRY = new FundingSourceType("INDUSTRY", "Industry");
	public static final FundingSourceType FOUNDATION = new FundingSourceType("FOUNDATION", "Foundation");
	public static final FundingSourceType FEDERAL = new FundingSourceType("FEDERAL", "U.S. Federal", Arrays.asList(federalSources));

	private static final FundingSourceType[] sourceTypes = new FundingSourceType[]{FEDERAL, FOUNDATION, INDUSTRY, PROFASSOC, LOCGOV, OTHER};
	
	// -------------------------------------------------------------------------------------
	// Empty public constructor -- creates a federal funding source
	// -------------------------------------------------------------------------------------
	/**
	 * Creates a new Federal funding source
	 */
	public FundingSourceType() {
		this("FEDERAL", "U.S. Federal", Arrays.asList(federalSources));
	}
	
	// -------------------------------------------------------------------------------------
	// Private constructors
	// -------------------------------------------------------------------------------------
	private FundingSourceType(String type, String displayName) {
		this(type, displayName, new ArrayList<FundingSourceName>(0));
	}
	
	private FundingSourceType(String type, String displayName, List<FundingSourceName> acceptedValues) {
		this.name = type;
		this.displayName = displayName;
		this.acceptedSourceNames = acceptedValues;
	}

	// -------------------------------------------------------------------------------------
	// BEGIN Static methods
	// -------------------------------------------------------------------------------------
	public static List<FundingSourceType> getFundingSources() {
		return Arrays.asList(sourceTypes);
	}
	
	public static boolean isFederal(String sourceType) {
		return sourceType.equalsIgnoreCase(FEDERAL.getName());
	}
	
	public static boolean isValidSourceName(String sourceType, String sourceName) {
		FundingSourceType source= getSourceType(sourceType);
		if (source == null)	
			return false;
		return source.isValidSourceName(sourceName);
	}
	
	public static FundingSourceType getSourceType(String sourceType) {
		for (FundingSourceType source: sourceTypes) {
			if (source.getName().equals(sourceType))
				return source;
		}
		return null;
	}
	// -------------------------------------------------------------------------------------
	// END Static methods
	// -------------------------------------------------------------------------------------
	
	
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
	 * @return the acceptedSourceNames
	 */
	public List<FundingSourceName> getAcceptedSourceNames() {
		return acceptedSourceNames;
	}

	/**
	 * @param acceptedSourceNames the acceptedSourceNames to set
	 */
	public void setAcceptedSourceNames(List<FundingSourceName> acceptedSourceNames) {
		this.acceptedSourceNames = acceptedSourceNames;
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
	
	
	public boolean isValidSourceName(String sourceName) {
		if (acceptedSourceNames.size() == 0)
			return true;
		if (sourceName == null)
			return false;
		for (FundingSourceName source: acceptedSourceNames) {
			if (source.getName().equals(sourceName))
				return true;
		}
		return false;
	}
	
	public FundingSourceName getSourceName(String name) {
		if (acceptedSourceNames.size() == 0)
			return new FundingSourceName(name);
		for (FundingSourceName source: acceptedSourceNames) {
			if (source.getName().equals(name))
				return source;
		}
		return null;
	}
}
