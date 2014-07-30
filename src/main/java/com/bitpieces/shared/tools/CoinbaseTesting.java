package com.bitpieces.shared.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.joda.money.Money;

import com.bitpieces.shared.DataSources;
import com.coinbase.api.Coinbase;
import com.coinbase.api.CoinbaseBuilder;
import com.coinbase.api.entity.Account;
import com.coinbase.api.entity.AccountsResponse;
import com.coinbase.api.entity.Transaction;
import com.coinbase.api.entity.TransactionsResponse;
import com.coinbase.api.exception.CoinbaseException;

/**
 * TODO here
 * Every user/creator is linked to their own coinbase acct/wallet name within your main
 * 	Don't create one by default for users, only when they make a deposit
 * 	Don't create one by default for creators, only when they raise funds
 * Every deposit also has the coinbase transaction id
 * A scheduled job checking on the status of these transactions runs every minute, and posts them to complete if done
 * After a withdrawal, the fees get moved to a 'main' account 
 * 
 * 
 * @author tyler
 *
 */

public class CoinbaseTesting {

	public static void main(String[] args) throws Exception {
		Properties prop = Tools.loadProperties(DataSources.COINBASE_PROP);

		String acct = "bitpieces_test_wallet";
		String acctId = "53bd87d57cb6032d4600000b";

		Coinbase cb = new CoinbaseBuilder()
		.withApiKey(prop.getProperty("apiKey"), prop.getProperty("apiSecret"))
		//		.withAccountId(acctId)
		.build();


		List<Account> accts = cb.getAccounts().getAccounts();

		// Get acct id from name
		for (Account cAcct : accts) {
			String name = cAcct.getName();
			if (name.equals("jul_29")) {
				System.out.println(cAcct.getId());
				acctId = cAcct.getId();

			}
		}

//		CoinbaseTools.deleteAccountNames(cb, Arrays.asList("jul_29", "falwell"));


		WebTools.makeDepositFromCoinbaseCallback(null,null);



		//		cb.createAccount(a);


		//		System.out.println(Tools.GSON2.toJson(accts));
	}




	public static void main2(String[] args) throws Exception {

		Properties prop = Tools.loadProperties("/home/tyler/coinbase.properties");
		String acct = "bitpieces_test_wallet";
		String acctId = "53bd87d57cb6032d4600000b";

		Coinbase cb = new CoinbaseBuilder()
		.withApiKey(prop.getProperty("apiKey"), prop.getProperty("apiSecret"))
		.withAccountId(acctId)
		.build();

		System.out.println(cb.getUser());

		TransactionsResponse tr = cb.getTransactions();
		List<Transaction> txs = tr.getTransactions();

		System.out.println(txs);

		Transaction t = new Transaction();
		t.setFrom("asdf@gmail.com");
		t.setAmount(Money.parse("USD 5"));
		t.setNotes("Invoice for window derping");


		Transaction r = cb.requestMoney(t);

		System.out.println(r);
		System.out.println(r.getId());

		Tools.Sleep(3000L);
		Transaction r2 = cb.getTransaction(r.getId());
		System.out.println(r2.getStatus()); // Transaction.Status.PENDING
		System.out.println(r2.getRecipient().getEmail()); // "mpJKwdmJKYjiyfNo26eRp4j6qGwuUUnw9x"
		System.out.println("from = " + r2.getRecipient().getEmail() + " to = " + r2.getSender().getEmail());

		System.out.println(Tools.GSON2.toJson(r));
		System.out.println(Tools.GSON2.toJson(r2));





	}
}
