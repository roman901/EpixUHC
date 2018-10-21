package org.uwtech.epix.epixuhc.exceptions;

import org.uwtech.epix.epixuhc.languages.Lang;

public class UhcException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5868977668183366492L;

	
	public UhcException(String message){
		super(Lang.DISPLAY_MESSAGE_PREFIX+" "+message);
	}
}
