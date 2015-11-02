package org.yeastrc.grant;

public class FundingSource {

	private FundingSourceType type;
	private FundingSourceName name;
	
	public FundingSource() {
		type = new FundingSourceType();
		name = new FundingSourceName();
	}
	
	public FundingSource(FundingSourceType type, FundingSourceName name) {
		this.type = type;
		this.name = name;
	}
	
	public boolean isFederal() {
		return FundingSourceType.isFederal(type.getName());
	}
	
	public FundingSourceType getSourceType() {
		return type;
	}
	
	public FundingSourceName getSourceName() {
		return name;
	}
	
	public void setSourceType(FundingSourceType type) {
		this.type = type;
	}
	
	public void setSourceName(FundingSourceName name) {
		this.name = name;
	}
	
	public static FundingSource getFundingSource(String typeName, String sourceName) {
		FundingSourceType sourceType = FundingSourceType.getSourceType(typeName);
		if (typeName != null) {
			FundingSourceName srcName = sourceType.getSourceName(sourceName);
			if (sourceName != null)
				return new FundingSource(sourceType, srcName);
		}
		return null;
	}
}
