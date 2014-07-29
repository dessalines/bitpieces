package com.heretic.bitpieces_practice.shared.tools;

import java.util.List;
import java.util.Properties;

import org.joda.money.Money;

import com.coinbase.api.Coinbase;
import com.coinbase.api.CoinbaseBuilder;
import com.coinbase.api.entity.Transaction;
import com.coinbase.api.entity.TransactionsResponse;

public class CoinbaseTesting {
	public static void main(String[] args) throws Exception {

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
