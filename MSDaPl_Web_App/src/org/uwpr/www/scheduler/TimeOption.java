/**
 * TimeOption.java
 * @author Vagisha Sharma
 * Jan 8, 2012
 */
package org.uwpr.www.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;

public final class TimeOption {
	private int value;
	private String display = "";
	public TimeOption(int value, String display) {
		this.value = value;
		this.display = display;
	}
	public int getValue() {
		return value;
	}
	public String getDisplay() {
		return display;
	}

	public static List<TimeOption> getTimeOptions(User user) {

		boolean isAdmin = false;
		Groups groups = Groups.getInstance();
		if(groups.isMember(user.getResearcher().getID(), "administrators")) {
			isAdmin = true;
		}

		List<TimeOption> options = new ArrayList<TimeOption>();
		if(isAdmin) {
			options.add(new TimeOption(0,"12:00 am"));
			for(int i = 1; i <= 11; i++) {
				options.add(new TimeOption(i,i+":00 am"));
			}
			options.add(new TimeOption(12, "12:00 pm"));
			for(int i = 13; i <= 23; i++) {
				options.add(new TimeOption(i,(i-12)+":00 pm"));
			}
		}
		else {
			options.add(new TimeOption(9, "9:00 am"));
			options.add(new TimeOption(13, "1:00 pm"));
			options.add(new TimeOption(17, "5:00 pm"));
		}
		return options;
	}
}