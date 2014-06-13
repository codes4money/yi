package com.studio.b56.im.app.ui.common;

import java.util.regex.Pattern;

public class RegisterUtil {
	private final static Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	public static boolean isEmail(String email){
		if(email == null || email.trim().length()==0) 
			return false;
	    return emailer.matcher(email).matches();
	}
	
	public static boolean validatePassword(String str){
		boolean flag = false;
		int length = str.length();
		
		if(length > 5 && length <= 16){
			flag = true;
		}
		
		return flag;
	}
	
	public static boolean validatePhone(String str){
		boolean flag = false;
		if(str.matches("^(18|13|15|14)\\d{9}$")){
			flag = true;
		}
		return flag;
	}
	
	public static boolean validate(String str1, String str2){
		boolean flag = false;
		if(str1.equals(str2)){
			flag = true;
		}
		return flag;
	}
}
