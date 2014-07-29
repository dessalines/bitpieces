package com.bitpieces.shared.tools;

import java.io.Serializable;

import com.bitpieces.shared.tools.Tools.UserType;

/**
 * The user or creators type, id, and username
 * @author tyler
 *
 */
public class UID implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3691315536173530059L;
	UserType type;
	String id;
	String username;
	
	public UID(UserType type, String id, String username) {
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
