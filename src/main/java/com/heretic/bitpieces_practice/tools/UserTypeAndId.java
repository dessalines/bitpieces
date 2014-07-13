package com.heretic.bitpieces_practice.tools;

import com.heretic.bitpieces_practice.tools.Tools.UserType;

public class UserTypeAndId {
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
