package com.bitpieces.shared.tools;

import java.io.IOException;

import com.coinbase.api.Coinbase;
import com.coinbase.api.entity.Account;
import com.coinbase.api.exception.CoinbaseException;

public class CoinbaseTools {
	
	
	public static String createCoinbaseAccount(Coinbase cb, String username) {
		Account account = new Account();
		account.setName(username);

		Account cbAccountDetails = null;
		try {
			cbAccountDetails = cb.createAccount(account);
		} catch (CoinbaseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String fetchedAccountId = cbAccountDetails.getId();

		return fetchedAccountId;
	}
	
	
}
