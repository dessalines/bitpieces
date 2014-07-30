package com.bitpieces.shared.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joda.money.Money;

import com.bitpieces.shared.DataSources;
import com.bitpieces.shared.Tables.Currencies;
import com.bitpieces.shared.Tables.User;
import com.bitpieces.shared.Tables.Users_settings;
import com.coinbase.api.Coinbase;
import com.coinbase.api.entity.Account;
import com.coinbase.api.entity.Button;
import com.coinbase.api.entity.Button.Style;
import com.coinbase.api.entity.Button.Type;
import com.coinbase.api.exception.CoinbaseException;

public class CoinbaseTools {
	
	
	public static String createCoinbaseAccount(Coinbase cb, String userName) {
		Account account = new Account();
		account.setName(userName);

		Account cbAccountDetails = null;
		try {
			System.out.println("creating cb account...");
			cbAccountDetails = cb.createAccount(account);
			System.out.println("after account...");
		} catch (CoinbaseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String fetchedAccountId = cbAccountDetails.getId();

		return fetchedAccountId;
	}
	
	public static String createDepositButton(Coinbase cb, String userName) {
		
		// The acct id is stored in the users table row
		
		User user = User.findFirst("username=?", userName);
		String cbAcctId = user.getString("cb_acct_id");
		
		// Get the currency ISO code
		String currencyIso = Currencies.findById(user.getString("local_currency_id")).getString("iso");
		
		Button b = new Button();

		b.setName("Deposit");
		b.setType(Type.BUY_NOW);
		b.setPriceCurrencyIso(currencyIso);
		b.setCallbackUrl("http://" + DataSources.IP_ADDRESS + ":4567/coinbase_deposit_callback");
		b.setDescription("Make a deposit to be able to buy and bid on pieces");
		b.setStyle(Style.NONE);
		b.setIncludeEmail(true);
		b.setIncludeAddress(true);
		b.setId(cbAcctId);
		b.setPrice(Money.parse(currencyIso + " 0.01"));
		b.setChoosePrice(true);
		
		
		try {
			Button resultButton = cb.createButton(b);
			
			return resultButton.getCode();
		} catch (CoinbaseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
		
		
		
	}
	
	public static void deleteAccountNames(Coinbase cb, List<String> names) {
		try {
			// First get the account IDS
			List<String> idsToDelete = new ArrayList<String>();
			List<Account> accts = cb.getAccounts().getAccounts();

			for (Account cAcct : accts) {
				if (names.contains(cAcct.getName())) {
					idsToDelete.add(cAcct.getId());
					System.out.println(Tools.GSON2.toJson(cAcct));
				}
			}

			for (String cId : idsToDelete) {


				cb.deleteAccount(cId);

			}

		} catch (CoinbaseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	
}
