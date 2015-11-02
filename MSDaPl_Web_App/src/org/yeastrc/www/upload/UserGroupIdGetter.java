/**
 * UserGroupIdGetter.java
 * @author Vagisha Sharma
 * Sep 22, 2010
 */
package org.yeastrc.www.upload;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;

/**
 * 
 */
public class UserGroupIdGetter {

	private UserGroupIdGetter() {}
	
	/**
	 * Returns the ID of ONE of the groups the user is a member of. 
	 * If the user is an administrator, the ID of the "administrators" group is returned.
	 * If the user is not a member of any group, 0 is returned
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public static int getOneGroupId(User user) throws SQLException {
		
		Groups groups = Groups.getInstance();
		
		// If the user is an administrator return the ID of the administrators group
		if(groups.isMember(user.getResearcher().getID(), "administrators"))
			return groups.getGroupID("administrators");
		
		// Otherwise get a list of groups that this user is a member of.  Returns the
		// ID of the first group.
		List<String> groupMemberships = groups.getUserGroups(user.getResearcher().getID());
		if(groupMemberships != null && groupMemberships.size() > 0)
			return groups.getGroupID(groupMemberships.get(0));
		
		
		// If the user is not a member of any group return 0
		return 0;
	}
	
	public static List<Integer> getAllGroupIds(User user) throws SQLException {
		
		Groups groups = Groups.getInstance();
		
		List<Integer> groupIds = new ArrayList<Integer>();
		
		// Get a list of groups that this user is a member of.  
		List<String> groupMemberships = groups.getUserGroups(user.getResearcher().getID());
		if(groupMemberships != null && groupMemberships.size() > 0)
			groupIds.add(groups.getGroupID(groupMemberships.get(0)));
		
		return groupIds;
	}
}
