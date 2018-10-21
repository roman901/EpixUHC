package org.uwtech.epix.epixuhc.utils;

import java.util.Arrays;

public class CompareUtils {
	
	public static boolean equalsToAny(Object comparable, Object... compareList){
		return Arrays.asList(compareList).contains(comparable);
	}
}
