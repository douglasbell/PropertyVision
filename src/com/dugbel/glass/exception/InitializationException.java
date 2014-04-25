package com.dugbel.glass.exception;

/**
 * 
 * Exception thrown when a class experiences an exception during initialization
 * 
 * @author Doug Bell (douglas.bell@gmail.com)
 */
public class InitializationException extends RuntimeException {

	/** Serial Version UID */
	private static final long serialVersionUID = 1L;

	/**
	 * Initialization Exception with the specified detail message.
	 * 
	 * @param message	 the detail message.
	 */
	public InitializationException(final String message) {
		super(message);
	}

}