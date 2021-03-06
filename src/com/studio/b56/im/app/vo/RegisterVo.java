package com.studio.b56.im.app.vo;

public class RegisterVo extends BaseVo{
	private static final long serialVersionUID = -3213464160250948375L;
	
	private String uid;
	
	private String username;
	private String password;
	private String repassword;
	
	private String validateCode; // 验证码
	
	public RegisterVo() {
		super();
	}
	
	public RegisterVo(String username, String password, String repassword) {
		super();
		this.username = username;
		this.password = password;
		this.repassword = repassword;
	}
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public String getRepassword() {
		return repassword;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setRepassword(String repassword) {
		this.repassword = repassword;
	}

	public String getValidateCode() {
		return validateCode;
	}

	public void setValidateCode(String validateCode) {
		this.validateCode = validateCode;
	}
	
	
	
}
