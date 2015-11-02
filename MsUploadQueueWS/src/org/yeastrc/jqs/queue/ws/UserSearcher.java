/**
 * UserSearcher.java
 * @author Vagisha Sharma
 * Jun 10, 2012
 */
package org.yeastrc.jqs.queue.ws;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.www.user.NoSuchUserException;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import com.sun.jersey.api.NotFoundException;

/**
 * 
 */
public class UserSearcher {

	private UserSearcher() {}
	
	private static UserSearcher instance = null;
	
	private static final Logger log = Logger.getLogger(UserSearcher.class);
	
	public static synchronized UserSearcher getInstance() {
		if(instance == null)
			instance = new UserSearcher();
		
		return instance;
	}
	
	public User getUser(int researcherId) {
		
		User submitter = new User();
		try {
			submitter.load(researcherId);
			return submitter;
			
		} catch (InvalidIDException e) {
			log.error("User not found. ID: "+researcherId);
			throw new NotFoundException("User not found. ID: "+researcherId);
		} catch (SQLException e) {
			log.error("There was an error during user ID lookup.", e);
			throw new ServerErrorException("There was an error during user ID lookup. The error message was: "+e.getMessage());
		}
	}
	
	public User getUser(String username) {
		try {
			User user = UserUtils.getUser(username);
			if(user == null) {
				log.error("User not found. Name: "+username);
				throw new NotFoundException("User not found. Name: "+username);
			}
			return user;
		} catch (NoSuchUserException e) {
			log.error("User not found. Name: "+username);
			throw new NotFoundException("User not found. Name: "+username);
		} catch (SQLException e) {
			log.error("There was an error during username lookup.", e);
			throw new ServerErrorException("There was an error during username lookup. The error message was: "+e.getMessage());
		}
	}
	
	public User getUserWithEmail(String email) {
		try {
			User user = UserUtils.getUserWithEmail(email);
			if(user == null) {
				log.error("User not found. Email: "+email);
				throw new NotFoundException("User not found. Email: "+email);
			}
			return user;
		} catch (NoSuchUserException e) {
			log.error("User not found. Email: "+email);
			throw new NotFoundException("User not found. Email: "+email);
		} catch (SQLException e) {
			log.error("There was an error during user email lookup.", e);
			throw new ServerErrorException("There was an error during user email lookup. The error message was: "+e.getMessage());
		}
	}
}
