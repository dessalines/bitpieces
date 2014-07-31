package com.bitpieces.shared.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;
import org.joda.money.Money;

import com.bitpieces.shared.DataSources;
import com.bitpieces.shared.Tables.Currencies;
import com.bitpieces.shared.Tables.User;
import com.bitpieces.shared.Tables.Users_buttons;
import com.bitpieces.shared.Tables.Users_deposits;
import com.bitpieces.shared.Tables.Users_settings;
import com.coinbase.api.Coinbase;
import com.coinbase.api.entity.Account;
import com.coinbase.api.entity.Button;
import com.coinbase.api.entity.Button.Style;
import com.coinbase.api.entity.Button.Type;
import com.coinbase.api.entity.Transaction;
import com.coinbase.api.entity.TransactionsResponse;
import com.coinbase.api.exception.CoinbaseException;

public class CoinbaseTools {


	@Deprecated // because it costs to transfer between accounts, have to keep track of funds other ways
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

	public static String fetchOrCreateDepositButton(Coinbase cb, UID uid) {

		// Fetch from users_buttons, if its not there, make it
		//		Users_buttons ub = Users_buttons.findFirst("users_id=?", uid.getId());
		//		if (ub != null) {
		//			return ub.getString("button_code");
		//		} else {

		// The acct id is stored in the users table row
		User user = User.findById(uid.getId());

		String cbAcctId = user.getString("cb_acct_id");
		System.out.println(cbAcctId);
		// Get the currency ISO code
		String currencyIso = Currencies.findById(user.getString("local_currency_id")).getString("iso");

		Button b = new Button();

		b.setName("Deposit");
		b.setType(Type.BUY_NOW);
		b.setPriceCurrencyIso(currencyIso);
		b.setCallbackUrl("http://" + DataSources.IP_ADDRESS + ":4567/" + user.getId().toString()
				+ "/coinbase_deposit_callback");
		b.setDescription("Make a deposit to be able to buy and bid on pieces");
		b.setStyle(Style.NONE);
		b.setIncludeEmail(true);
		b.setIncludeAddress(true);
		b.setId(cbAcctId);


		b.setChoosePrice(true);
		b.setPrice(Money.parse(currencyIso + " 0.01"));
		b.setPriceString("52");




		try {
			Button resultButton = cb.createButton(b);

			System.out.println(Tools.GSON2.toJson(resultButton));
			System.out.println(resultButton);
			String buttonCode = resultButton.getCode();

			//				Users_buttons.createIt("users_id", uid.getId(),
			//						"button_code", buttonCode);

			return buttonCode;
		} catch (CoinbaseException | IOException e) {
			e.printStackTrace();
		}


		//		}

		return null;

	}

	public static Map<String, String> userWithdrawal(Coinbase cb, Double btcAmount, String btcOrEmailAddress) 
			throws CoinbaseException, IOException {

		Transaction t = new Transaction();
		t.setTo(btcOrEmailAddress);
		Money moneyAmount = Money.parse("BTC " + btcAmount);
		t.setAmount(moneyAmount);

		Transaction r = cb.sendMoney(t);
		
		Map<String, String> map = new HashMap<>();
		
		map.put("status", r.getStatus().toString());
		map.put("cb_tid", r.getId());
		
		return map;




	}


	public static void  getUsersDeposits(Coinbase cb, String userName) {
		try {
			// First get the user
			User user = User.findFirst("username = ?", userName);
			List<Users_deposits> deposits = Users_deposits.find("users_id = ?", user.getId());

			List<String> depositIds = new ArrayList<>();

			for (Users_deposits cDep : deposits) {
				depositIds.add(cDep.getString("cb_tid"));
			}

			TransactionsResponse res = cb.getTransactions();

			List<Transaction> transactions = res.getTransactions();

			for (Transaction cT : transactions) {
				if (depositIds.contains(cT.getId())) {
					System.out.println(cT.getAmount());
				}
			}


		} catch (IOException | CoinbaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


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
