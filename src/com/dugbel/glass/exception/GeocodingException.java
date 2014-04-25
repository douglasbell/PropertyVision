package com.dugbel.glass.exception;


/**
 * 
 * Exception thrown when a class experiences an exception during geocoding
 * 
 * @author dbell
 */
public class GeocodingException extends RuntimeException {

	/** Serial Version UID */
	private static final long serialVersionUID = -1964042768879485304L;
	
	
	/**
	 * Geocoding Exception with the specified detail message.
	 * 
	 * @param message	 the detail message.
	 */
	public GeocodingException(final String message) {
		super(message);
	}

}