package com.heretic.bitpieces_practice.tools;

import com.heretic.bitpieces_practice.tools.Tools.Type;

public class UserTypeAndId {
	Type type;
	String id;
	
	public UserTypeAndId(Type type, String id) {
		super();
		this.type = type;
		this.id = id;
	}

	public Type getType() {
		return type;
	}

	public String getId() {
		return id;
	}
}
