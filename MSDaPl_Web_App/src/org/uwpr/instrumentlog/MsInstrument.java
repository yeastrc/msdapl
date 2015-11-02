package org.uwpr.instrumentlog;

public class MsInstrument {

	private int id;
	private String name;
	private String description;
	private boolean active;
	
	public MsInstrument(int id, String name, String description, boolean active) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.active = active;
	}

	public int getID() {
		return id;
	}

	public String getName() {
		if(active)
			return name;
		else
		{
			if("Q".equals(name))
				return "Q (inactive)"; // TODO remove this once the QExactive is active
			else
				return name + " (retired)";
		}
	}

	public String getDescription() {
		return description;
	}
	
	public boolean isActive() {
		return active;
	}
	
}
