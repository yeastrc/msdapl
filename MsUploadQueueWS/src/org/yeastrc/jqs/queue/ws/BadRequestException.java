/**
 * BadRequestException.java
 * @author Vagisha Sharma
 * Sep 23, 2010
 */
package org.yeastrc.jqs.queue.ws;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * 
 */
public class BadRequestException extends WebApplicationException {

	/**
	 * Create a HTTP 400 (Bad Request) exception.
	 */
	public BadRequestException() {
		super(Response.status(Response.Status.BAD_REQUEST).build());
	}

	/**
	 * Create a HTTP 400 (Bad Request) exception.
	 * @param message the String that is the entity of the 400 response.
	 */
	public BadRequestException(String message) {
		super(Response.status(Response.Status.BAD_REQUEST).
				entity(message).type("text/plain").build());
	}
}
