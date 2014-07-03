package com.heretic.bitpieces_practice.tools;

import com.heretic.bitpieces_practice.tools.Tools.UserType;

public class UserTypeAndId {
	UserType type;
	String id;
	
	public UserTypeAndId(UserType type, String id) {
		super();
		this.type = type;
		this.id = id;
	}

	public UserType getType() {
		return type;
	}

	public String getId() {
		return id;
	}
}
