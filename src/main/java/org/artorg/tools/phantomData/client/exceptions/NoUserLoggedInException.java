package org.artorg.tools.phantomData.client.exceptions;

public class NoUserLoggedInException extends RuntimeException {
	private static final long serialVersionUID = -5671371006858399853L;
	
	public NoUserLoggedInException() {
		super("No user is logged in for connector");
	}

}
