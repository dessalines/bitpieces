package com.bitpieces.shared.tools;

import org.jasypt.util.password.StrongPasswordEncryptor;

public class EncryptionTester {
	public static void main(String[] args) {
		StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
		String userPass = "asdf";
		String encryptedPassword = passwordEncryptor.encryptPassword(userPass);
	
		System.out.println(encryptedPassword);
		
		if (passwordEncryptor.checkPassword(userPass, encryptedPassword)) {
			System.out.println("correct pass!");
		} else {
			// bad login!
		}
	}
}
