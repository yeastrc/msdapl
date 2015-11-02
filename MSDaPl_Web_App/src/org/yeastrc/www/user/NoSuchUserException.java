package org.yeastrc.www.user;

/**
 * This represent an exception to be thrown when an error is encountered
 * during the loading or examining of a given user name.
 * @version 2004-01-21
 */
 public class NoSuchUserException extends Exception {

 	/** Constructs an NoSuchUserException with no detail message. */
 	public NoSuchUserException () {
 		super();
 	}

	/** Constructs an NoSuchUserException with the specified detail message. */
 	public NoSuchUserException (String message) {
 		super(message);
 	}

 }