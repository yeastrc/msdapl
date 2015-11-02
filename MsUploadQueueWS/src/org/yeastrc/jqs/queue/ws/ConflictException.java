/**
 * ConflictException.java
 * @author Vagisha Sharma
 * Jun 10, 2012
 */
package org.yeastrc.jqs.queue.ws;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * 
 */
public class ConflictException extends WebApplicationException {
	
	/**
	 * Create a HTTP 409 (Conflict) exception.
	 */
	public ConflictException() {
		super(Response.status(Response.Status.CONFLICT).build());
	}

	/**
	 * Create a HTTP 409 (Conflict) exception.
	 * @param message the String that is the entity of the 409 response.
	 */
	public ConflictException(String message) {
		super(Response.status(Response.Status.CONFLICT).
				entity(message).type("text/plain").build());
	}
}
