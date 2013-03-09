package com.tandon.dce.boardengine;

public class AddressResolver {
	
	public static String[] getNextTag(String boardLocation) {
		int p = boardLocation.indexOf('.');
		if (p==-1) {return new String[]{boardLocation,null};}
		
		String nextTag = boardLocation.substring(0, p);
		String subLocation = boardLocation.substring(p+1);
		
		return new String[]{nextTag, subLocation};
	}
	
	public static String[] getPropertyName(String propertyLocation) {
		int p = propertyLocation.lastIndexOf(".");
		if (p==-1) {return new String[]{propertyLocation,null};}
		
		String boardLocation = propertyLocation.substring(0, p);
		String propertyName = propertyLocation.substring(p+1);
		
		return new String[]{propertyName, boardLocation};
	}

}
