package org.uwtech.epix.epixuhc.exceptions;

public class UhcPlayerNotOnlineException extends UhcException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1159293747235742412L;
	public UhcPlayerNotOnlineException(String name){
		super("Error : Player "+name+" is not online");
	}
}
