package com.heretic.bitpieces_practice.tools;

import java.io.Serializable;

import com.heretic.bitpieces_practice.tools.Tools.UserType;

public class UserTypeAndId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3691315536173530059L;
	UserType type;
	String id;
	String username;
	
	public UserTypeAndId(UserType type, String id, String username) {
		super();
		this.type = type;
		this.id = id;
		this.username = username;
	}

	public UserType getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}
	
}
