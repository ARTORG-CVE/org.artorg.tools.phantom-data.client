package org.artorg.tools.phantomData.client.exceptions;

public class InvalidUIInputException extends Exception {
	private static final long serialVersionUID = -4908511201073823084L;

	public InvalidUIInputException() {
		super("Invalid input(s) in user interface");
	}
	
	public InvalidUIInputException(String message) {
		super(message);
	}

}
