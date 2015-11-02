/**
 * ServerErrorException.java
 * @author Vagisha Sharma
 * Sep 23, 2010
 */
package org.yeastrc.jqs.queue.ws;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * 
 */
public class ServerErrorException extends WebApplicationException {

	/**
	 * Create a HTTP 500 (Internal Server Error) exception.
	 */
	public ServerErrorException() {
		super(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
	}

	/**
	 * Create a HTTP 500 (Internal Server Error) exception.
	 * @param message the String that is the entity of the 500 response.
	 */
	public ServerErrorException(String message) {
		super(Response.status(Response.Status.INTERNAL_SERVER_ERROR).
				entity(message).type("text/plain").build());
	}
}
