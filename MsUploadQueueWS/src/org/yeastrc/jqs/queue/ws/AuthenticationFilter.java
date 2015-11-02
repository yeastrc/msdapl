/**
 * AuthenticationFilter.java
 * @author Vagisha Sharma
 * Sep 23, 2010
 */
package org.yeastrc.jqs.queue.ws;

import java.security.Principal;
import java.sql.SQLException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.yeastrc.www.user.NoSuchUserException;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import com.Ostermiller.util.MD5;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * 
 */
public class AuthenticationFilter implements ContainerRequestFilter {

	@Override
	public ContainerRequest filter(ContainerRequest request) {
		
		// No authentication needed for GET requests.
		if(request.getMethod().equalsIgnoreCase("GET")) {
			return request;
		}
		
		final User user = authenticate(request);
		SecurityContext ctxt = new SecurityContext() {
			@Override
			public boolean isUserInRole(String arg0) {
				return true;
			}
			@Override
			public boolean isSecure() {
				return false;
			}
			@Override
			public Principal getUserPrincipal() {
				return new Principal() {
					
					@Override
					public String getName() {
						return user.getUsername();
					}
				};
			}
			@Override
			public String getAuthenticationScheme() {
				return SecurityContext.BASIC_AUTH;
			}
		};
		request.setSecurityContext(ctxt);
		return request;
	}
	
	private User authenticate(ContainerRequest request) {
		
		String authentication = request.getHeaderValue(ContainerRequest.AUTHORIZATION); 
		
		if(authentication == null) {
			throw new UnauthorizedException("No authentication headers found");
		}
		
		// We only support HTTP Basic authentication
		if(!authentication.startsWith("Basic")) {
			return null;
		}
		
		authentication = authentication.substring("Basic ".length());
		String[] values = new String(Base64.base64Decode(authentication)).split(":");
		if (values.length < 2) { 
            throw new UnauthorizedException("Error reading username and/or password"); 
            // "Invalid syntax for username and password" 
        } 
		
		String username = values[0]; 
        String password = values[1]; 
        if ((username == null) || (password == null)) { 
        	// "Missing username or password" 
        	throw new UnauthorizedException("Either username of password was missing"); 
        } 
        
        try {
			User user = UserUtils.getUser(username);
			password = MD5.getHashString(password);
			if(user.getPassword().equals(password)) {
				return user;
			}
			else {
				throw new UnauthorizedException("Incorrect password. Authentication failed"); 
			}
		} catch (NoSuchUserException e) {
			throw new UnauthorizedException("No user with username: "+username);
		} catch (SQLException e) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	private static final class UnauthorizedException extends WebApplicationException {
		
		/**
		 * Create a HTTP 401 (Unauthorized) exception.
		 * @param message the String that is the entity of the 401 response.
		 */
		public UnauthorizedException(String message) {
			super(Response.status(Response.Status.UNAUTHORIZED).
					entity(message).type("text/plain").build());
		}
	}
}
