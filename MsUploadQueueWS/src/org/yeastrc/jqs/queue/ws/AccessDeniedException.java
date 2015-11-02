/**
 * AccessDeniedException.java
 * @author Vagisha Sharma
 * Jun 10, 2012
 */
package org.yeastrc.jqs.queue.ws;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * 
 */
public class AccessDeniedException extends WebApplicationException {
	
	/**
	 * Create a HTTP 403 (Forbidden) exception.
	 */
	public AccessDeniedException() {
		super(Response.status(Response.Status.FORBIDDEN).build());
	}

	/**
	 * Create a HTTP 403 (Forbidden) exception.
	 * @param message the String that is the entity of the 403 response.
	 */
	public AccessDeniedException(String message) {
		super(Response.status(Response.Status.FORBIDDEN).
				entity(message).type("text/plain").build());
	}
}
